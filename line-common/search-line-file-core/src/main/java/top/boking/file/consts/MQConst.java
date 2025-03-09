package top.boking.file.consts;

public class MQConst {
    public static final String FILE_SYN_2_ES_TOPIC = "FILE_SYN_2_ES_TOPIC";
    public static final String FILE_DELETE_2_ES_TOPIC = "FILE_DELETE_2_ES_TOPIC";

    public static final String FILE_TRANSACTION_GROUP = "file-transaction-group";
    public static final String FILE_TRANSACTION_TAG = "file-transaction-tag";
    public static final String FILE_TRANSACTION_GROUP_TAG = FILE_TRANSACTION_GROUP + ":" + FILE_TRANSACTION_TAG;
    public static final String DLQ_PREFIX = "%DLQ%";
    public static final String ROCKETMQ_TOPIC = "rocketmq_TOPIC";
}
