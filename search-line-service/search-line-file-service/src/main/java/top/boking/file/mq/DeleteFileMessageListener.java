package top.boking.file.mq;

import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.Message;
import top.boking.file.consts.MQConst;
import top.boking.file.domain.entity.SLineFile;
import top.boking.file.utils.MinioUtils;
import top.boking.mq.annotation.SLineTransactionListener;

import java.util.List;

@SLineTransactionListener(topic = MQConst.FILE_DELETE_2_ES_TOPIC)
public class DeleteFileMessageListener implements RocketMQLocalTransactionListener {

    @Autowired
    private MinioUtils minioUtils;

    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        @SuppressWarnings("unchecked")
        List<SLineFile> sLineFiles = (List<SLineFile>) arg;
        List<String> fileNames = sLineFiles.stream().map(SLineFile::getStoreFileName).toList();
        //删除文件
        minioUtils.deleteFiles(fileNames);
        return RocketMQLocalTransactionState.COMMIT;
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
        return null;
    }
}
