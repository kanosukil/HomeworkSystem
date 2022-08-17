package cn.summer.homework.service.impl;

import cn.summer.homework.DTO.CourseSTDTO;
import cn.summer.homework.DTO.QuestionResultDTO;
import cn.summer.homework.DTO.ResultQuestionDTO;
import cn.summer.homework.DTO.UserRoleDTO;
import cn.summer.homework.Util.IndexUtil;
import cn.summer.homework.Util.RabbitMQUtil;
import cn.summer.homework.service.ElasticSearchDirectExchangeService;
import com.alibaba.fastjson2.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author VHBin
 * @date 2022/8/12-18:09
 */
@Service
//@EnableBinding(OutputChannelProcessor.class)
public class OutputMessageProducer implements ElasticSearchDirectExchangeService {
    private static final Logger logger = LoggerFactory.getLogger(OutputMessageProducer.class);
    /**
     * Spring Cloud Stream 3.2.2 Output/Input/StreamListener 等注解官方不建议使用并被弃用
     * <p>
     * &#064;Resource<br>
     * &#064;Output(RabbitMQUtil.SAVE_OUT)  private MessageChannel saveChannel;<br>
     * &#064;Resource<br>
     * &#064;Output(RabbitMQUtil.DELETE_OUT) private MessageChannel deleteChannel;<br>
     * &#064;Resource<br>
     * &#064;Output(RabbitMQUtil.UPDATE_OUT) private MessageChannel updateChannel;<br>
     * </p>
     */


    @Resource
    private StreamBridge streamBridge;

    //    private <T> Message<T> getMessage(T doc, String index, String id, Integer isList) {
    private <T> Message<String> getMessage(T doc, String index, String id, Integer isList) {
        logger.info("Doc: {}", doc);
//        logger.info("Doc: {}", JSON.toJSONString(doc));
        MessageHeaders header =
                new MessageHeaders(new HashMap<>(6, 1f) {{
                    put("content-type", "UTF-8");
                    put("send-time", new Date().toString());
                    put("isList", isList.toString());
                    put("class", isList == 1 ?
                            ((List<?>) doc).get(0).getClass().toString() :
                            doc.getClass().toString());
//            put("class", doc.getClass().toString());
                    put("index", index);
                    put("out-id", id);
                }});
        return MessageBuilder.createMessage(JSON.toJSONString(doc), header);
//        return MessageBuilder.createMessage(doc, header);
    }

    private Map<String, String> getInfo(Object obj) {
        String index;
        Integer id;
        if (obj instanceof CourseSTDTO) {
            id = ((CourseSTDTO) obj).getCourse().getId();
            index = IndexUtil.COURSE;
        } else if (obj instanceof QuestionResultDTO) {
            id = ((QuestionResultDTO) obj).getQuestion().getId();
            index = IndexUtil.QUESTION;
        } else if (obj instanceof ResultQuestionDTO) {
            id = ((ResultQuestionDTO) obj).getResult().getId();
            index = IndexUtil.RESULT;
        } else if (obj instanceof UserRoleDTO) {
            id = ((UserRoleDTO) obj).getUser().getId();
            index = IndexUtil.USER;
        } else {
            throw new RuntimeException("未知对象<RabbitMQ文档插入>");
        }
        return new HashMap<>(2, 1f) {{
            put("index", index);
            put("id", id.toString());
        }};
    }

    @Override
    public <T> Boolean save(T doc) {
        try {
            Map<String, String> info;
            int isList = 0;
            if (doc instanceof List) {
                info = getInfo(((List<?>) doc).get(0));
                isList = 1;
            } else {
                info = getInfo(doc);
            }
//            Message<T> message = getMessage(doc, info.get("index"), info.get("id"));
            if (streamBridge.send(RabbitMQUtil.SAVE_OUT,
                    getMessage(doc, info.get("index"), info.get("id"), isList))) {
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
    public <T> Boolean delete(T doc) {
        try {
            Map<String, String> info = getInfo(doc);
//            Message<T> message = getMessage(doc, info.get("index"), info.get("id"));
            if (streamBridge.send(RabbitMQUtil.DELETE_OUT,
                    getMessage(doc, info.get("index"), info.get("id"), 0))) {
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
    public <T> Boolean update(T doc) {
        try {
            logger.info("Doc:{}", doc);
            Map<String, String> info = getInfo(doc);
//            Message<T> message = getMessage(doc, info.get("index"), info.get("id"));
            if (streamBridge.send(RabbitMQUtil.UPDATE_OUT,
                    getMessage(doc, info.get("index"), info.get("id"), 0))) {
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
