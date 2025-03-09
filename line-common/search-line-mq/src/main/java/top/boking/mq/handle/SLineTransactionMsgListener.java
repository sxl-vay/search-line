package top.boking.mq.handle;

import org.apache.rocketmq.spring.annotation.RocketMQTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionListener;
import org.apache.rocketmq.spring.core.RocketMQLocalTransactionState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.framework.AopProxyUtils;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.messaging.Message;
import top.boking.mq.annotation.SLineTransactionListener;

import java.util.Map;

@RocketMQTransactionListener
public class SLineTransactionMsgListener implements RocketMQLocalTransactionListener, SmartInitializingSingleton {

    private final static Logger log = LoggerFactory.getLogger(SLineTransactionMsgListener.class);
    private static final String ROCKETMQ_TOPIC_HEADER = "rocketmq_TOPIC";
    private Map<String, RocketMQLocalTransactionListener> transactionMsgListenerMap;
    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public RocketMQLocalTransactionState executeLocalTransaction(Message msg, Object arg) {
        return handleTransactionMsg(msg).executeLocalTransaction(msg, arg);
    }

    @Override
    public RocketMQLocalTransactionState checkLocalTransaction(Message msg) {
        return handleTransactionMsg(msg).checkLocalTransaction(msg);
    }

    private RocketMQLocalTransactionListener handleTransactionMsg(Message msg) {
        String topic = msg.getHeaders().get(ROCKETMQ_TOPIC_HEADER).toString();
        RocketMQLocalTransactionListener rocketMQLocalTransactionListener = transactionMsgListenerMap.get(topic);
        if (rocketMQLocalTransactionListener == null) {
            throw new IllegalStateException("topic " + topic + " is do not have a listener");
        }
        return rocketMQLocalTransactionListener;
    }

    @Override
    public void afterSingletonsInstantiated() {
        transactionMsgListenerMap = applicationContext.getBeansOfType(RocketMQLocalTransactionListener.class);
        Map<String, Object> beansWithAnnotation = applicationContext.getBeansWithAnnotation(SLineTransactionListener.class);
        for (Object value : beansWithAnnotation.values()) {
            registerTransactionListener(value);
        }
        log.info("transactionMsgListenerMap:{}", transactionMsgListenerMap);
    }

    private void registerTransactionListener(Object bean) {
        Class<?> clazz = AopProxyUtils.ultimateTargetClass(bean);
        if (!RocketMQLocalTransactionListener.class.isAssignableFrom(bean.getClass())) {
            throw new IllegalStateException(clazz + " is not instance of " + RocketMQLocalTransactionListener.class.getName());
        }
        SLineTransactionListener annotation = clazz.getAnnotation(SLineTransactionListener.class);
        if (annotation != null) {
            String topic = annotation.topic();
            if (topic == null) {
                throw new IllegalStateException(clazz + " is  annotation with " + SLineTransactionListener.class + " but topic is null; give it a 'topic'");
            }
            transactionMsgListenerMap.put(topic, (RocketMQLocalTransactionListener) bean);
        }

    }
}
