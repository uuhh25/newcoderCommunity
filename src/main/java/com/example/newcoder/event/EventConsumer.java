package com.example.newcoder.event;

import com.alibaba.fastjson.JSONObject;
import com.example.newcoder.entity.Event;
import com.example.newcoder.entity.Message;
import com.example.newcoder.service.MessageService;
import com.example.newcoder.util.CommunityConstant;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class EventConsumer implements CommunityConstant {

    private static final Logger logger = LoggerFactory.getLogger(EventConsumer.class);

    @Autowired
    private MessageService messageService;

    // 因为主题的内容比较类似，可以用一个方法消费3个主题
    // 在一个Kafka监听器定义3个主题
    @KafkaListener(topics = {TOPIC_COMMENT, TOPIC_LIKE, TOPIC_FOLLOW})
    public void handleCommentMessage(ConsumerRecord record) {
        if (record == null || record.value() == null) {
            logger.error("消息的内容为空!");
            return;
        }

        // 把消息，转到JSON格式
        Event event = JSONObject.parseObject(record.value().toString(), Event.class);
        if (event == null) {
            logger.error("消息格式错误!");
            return;
        }

        // 发送站内通知
        Message message = new Message();
        message.setFromId(SYSTEM_USER_ID);  // 系统发布的，所以用一个定义的常量
        message.setToId(event.getEntityUserId());   // 这个实体主人就收到实体相关的通知
        message.setConversationId(event.getTopic());
        message.setCreateTime(new Date());

        // 用map，存 通知 需要的数据
        Map<String, Object> content = new HashMap<>();
        content.put("userId", event.getUserId());
        content.put("entityType", event.getEntityType());
        content.put("entityId", event.getEntityId());

        // 把event.data 放到map中，即保存已有的消息
        if (!event.getData().isEmpty()) {
            for (Map.Entry<String, Object> entry : event.getData().entrySet()) {
                content.put(entry.getKey(), entry.getValue());
            }
        }
        // 把新的 data 放回到 Event实体中
        message.setContent(JSONObject.toJSONString(content));
        messageService.addMessage(message);
    }
}
