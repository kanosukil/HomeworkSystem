package cn.summer.homework.service.impl;

import cn.summer.homework.Util.RabbitMQUtil;
import cn.summer.homework.config.OutputChannelProcessor;
import cn.summer.homework.service.ElasticSearchDirectExchangeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;

/**
 * @author VHBin
 * @date 2022/8/12-18:09
 */
@Service
@EnableBinding(OutputChannelProcessor.class)
public class OutputMessageProducer implements ElasticSearchDirectExchangeService {
    private static final Logger logger = LoggerFactory.getLogger(OutputMessageProducer.class);
    @Resource
    @Output(RabbitMQUtil.SAVE_OUT)
    private MessageChannel saveChannel;
    @Resource
    @Output(RabbitMQUtil.DELETE_OUT)
    private MessageChannel deleteChannel;
    @Resource
    @Output(RabbitMQUtil.UPDATE_OUT)
    private MessageChannel updateChannel;

    @Override
    public Boolean save(Object doc) {
        try {
            MessageHeaders header = new MessageHeaders(new HashMap<>(3, 1f) {{
                put("content-type", "UTF-8");
                put("send-time", new Date().toString());
                put("type", "save");
            }});
            Message<Object> message = MessageBuilder.createMessage(doc, header);
            if (saveChannel.send(message)) {
                logger.info("Save Message 发送成功");
                return true;
            } else {
                logger.error("save Message 发送失败");
                return false;
            }
        } catch (Exception ex) {
            logger.error("Save Exception", ex);
            return false;
        }
    }

    @Override
    public Boolean delete(Object doc) {
        try {
            MessageHeaders header = new MessageHeaders(new HashMap<>(3, 1f) {{
                put("content-type", "UTF-8");
                put("send-time", new Date().toString());
                put("type", "delete");
            }});
            Message<Object> message = MessageBuilder.createMessage(doc, header);
            if (deleteChannel.send(message)) {
                logger.info("delete Message 发送成功");
                return true;
            } else {
                logger.error("delete Message 发送失败");
                return false;
            }
        } catch (Exception ex) {
            logger.error("Delete Exception", ex);
            return false;
        }
    }

    @Override
    public Boolean update(Object doc) {
        try {
            MessageHeaders header = new MessageHeaders(new HashMap<>(3, 1f) {{
                put("content-type", "UTF-8");
                put("send-time", new Date().toString());
                put("type", "update");
            }});
            Message<Object> message = MessageBuilder.createMessage(doc, header);
            if (updateChannel.send(message)) {
                logger.info("update Message 发送成功");
                return true;
            } else {
                logger.error("update Message 发送失败");
                return false;
            }
        } catch (Exception ex) {
            logger.error("Update Exception", ex);
            return false;
        }
    }
}
