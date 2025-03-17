package top.boking.escore.syn;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.Refresh;
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@Component
@Slf4j
public class ESBatchPushTest {

    @Autowired
    private ElasticsearchClient elasticsearchClient;

    private Syn<Comment> syn;

    @PostConstruct
    public void init() throws Exception {

        Connection connection = SynDataSourceConfig.getSynDataSource(
                "jdbc:mysql://10.12.103.14:3306/ebuilder_form?characterEncoding=utf8&useSSL=false&autoReconnect=true&failOverReadOnly=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true&"
                , "database"
                , "wJDCojuAYr6BbEQr"
        ).getConnection();

        CommentDao commentDao = new CommentDao(connection);

        syn = new Syn<>(comsumer(), commentDao);

        syn.startPoll();

    }

    private @NotNull Consumer<Comment> comsumer() {
        AtomicInteger count = new AtomicInteger(0);
        AtomicReference<BulkRequest.Builder> br = new AtomicReference<>(new BulkRequest.Builder());

        return comment -> {

            br.get().operations(op -> op
                            .create(idx -> idx
                                    .index("comment2")
                                    .id(String.valueOf(comment.getId()))
                                    .document(comment)
                            )
                    )
                    .refresh(Refresh.True)
            ;

            if (count.incrementAndGet() % 1000 == 0) {
                try {
                    //todo 批量写入部分失败场景需要解决！！ 这里先按照统一失败处理
                    //todo 这里的批处理逻辑可能会丢失末尾的少于一百的数据
                    BulkResponse bulkResponse = elasticsearchClient.bulk(br.get().build());
                    if (bulkResponse.errors()) {
                        log.error("批量写入出现错误：{}", bulkResponse.items().stream()
                                .filter(item -> item.error() != null)
                                .map(item -> String.format("id: %s, error: %s", item.id(), item.error().reason()))
                        );
                        // 重试
                        br.set(new BulkRequest.Builder());

                    } else {
                        log.info("批量写入成功，共当前写入{}条数据", count.get());
                        br.set(new BulkRequest.Builder());
                    }
                } catch (IOException e) {
                    log.error("批量写入出现错误", e);
                    // 重试
                }
            }
        };
    }


}
