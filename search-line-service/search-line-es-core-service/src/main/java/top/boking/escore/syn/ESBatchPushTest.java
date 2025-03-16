package top.boking.escore.syn;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.BulkResponse;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import top.boking.escore.syn.dao.CommentDao;
import top.boking.escore.syn.entity.Comment;

import java.io.IOException;
import java.sql.Connection;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Component
@Slf4j
public class ESBatchPushTest {

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    private ArrayBlockingQueue<Comment> queue = new ArrayBlockingQueue<>(10000);

    private Syn<Comment> syn;

    @PostConstruct
    public void init() throws Exception {

        Connection connection = SynDataSourceConfig.getSynDataSource(
                "jdbc:mysql://10.12.102.19:3306/ebuilder_form?characterEncoding=utf8&useSSL=false&autoReconnect=true&failOverReadOnly=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&allowMultiQueries=true"
                , "database"
                , "GGyasDd6sdTR7e"
        ).getConnection();

        CommentDao commentDao = new CommentDao(connection);

        syn = new Syn<>(comsumer(), commentDao);

        syn.startPoll();

        pushSchedule();

    }

    private @NotNull Consumer<Comment> comsumer() {
        return comment -> {
            try {
                boolean offer = queue.offer(comment, 1000, TimeUnit.MILLISECONDS);
                while (!offer) {
                    offer = queue.offer(comment, 1000, TimeUnit.MILLISECONDS);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        };
    }


    public void pushSchedule() {

        CompletableFuture.runAsync(() -> {
            BulkRequest.Builder br = new BulkRequest.Builder();
            for (int i = 0; !syn.isFinish() || queue.size() != 0; i++) {
                if (i % 100 == 0 && i != 0) {
                    try {
                        //todo 批量写入部分失败场景需要解决！！ 这里先按照统一失败处理
                        BulkResponse bulkResponse = elasticsearchClient.bulk(br.build());
                        if (bulkResponse.errors()) {
                            log.error("批量写入出现错误：{}", bulkResponse.items().stream()
                                    .filter(item -> item.error() != null)
                                    .map(item -> String.format("id: %s, error: %s", item.id(), item.error().reason()))
                            );
                            // 重试
                            i--;
                            continue;
                        } else {
                            log.info("批量写入成功，共当前写入{}条数据", i);
                            br = new BulkRequest.Builder();
                        }
                    } catch (IOException e) {
                        log.error("批量写入出现错误", e);
                        // 重试
                        i--;
                        continue;
                    }
                }
                Comment comment = queue.poll();
                if (comment == null) {
                    try {
                        Thread.sleep(500);
                        i--;
                        continue;
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                br.operations(op -> op
                        .create(idx -> idx
                                .index("comment")
                                .id(String.valueOf(comment.getId()))
                                .document(comment)
                        )
                );
            }
        });

    }

}
