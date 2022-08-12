package cn.summer.homework.mqGetter;

import cn.summer.homework.DTO.CourseSTDTO;
import cn.summer.homework.DTO.QuestionResultDTO;
import cn.summer.homework.DTO.ResultQuestionDTO;
import cn.summer.homework.DTO.UserRoleDTO;
import cn.summer.homework.Util.IndexUtil;
import cn.summer.homework.Util.RabbitMQUtil;
import cn.summer.homework.Util.TypeUtil;
import cn.summer.homework.config.InputMessageConsumer;
import cn.summer.homework.service.ElasticSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author VHBin
 * @date 2022/8/8-23:03
 */

@Component
@EnableBinding(InputMessageConsumer.class)
public class ElasticSearchDirectExchangeGetter {
    private static final Logger logger =
            LoggerFactory.getLogger(ElasticSearchDirectExchangeGetter.class);
    @Resource
    private ElasticSearchService ess;

    private Map<String, Object> getInfo(Object obj) {
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
            put("id", id);
        }};
    }

    //    @RabbitListener(queuesToDeclare = @Queue(RabbitMQUtil.SAVE_QUEUE))
//    @RabbitHandler
    @StreamListener(RabbitMQUtil.SAVE_IN)
    public void save(Message<Object> obj) {
        Object payload = obj.getPayload();
        logger.info("ElasticSearch 服务接收对象: {}-OpType:{}",
                payload, obj.getHeaders().get("type"));
        int id;
        String index;
        Map<String, Object> map;
        try {
            if (obj instanceof List) {
                List<Object> objects = TypeUtil.objToList(payload);
                index = getInfo(objects.get(0)).get("index").toString();
                ess.createDocs(index, objects);
            } else {
                map = getInfo(payload);
                index = map.get("index").toString();
                id = Integer.parseInt(map.get("id").toString());
                ess.createDoc(index, id, payload);
            }
        } catch (IOException io) {
            logger.error("RabbitMQ ES 插入异常", io);
        } catch (RuntimeException run) {
            logger.error("Runtime 异常", run);
        } catch (Exception ex) {
            logger.error("其他异常", ex);
        }
    }

    //    @RabbitListener(queuesToDeclare = @Queue(RabbitMQUtil.DELETE_QUEUE))
//    @RabbitHandler
    @StreamListener(RabbitMQUtil.DELETE_IN)
    public void delete(Message<Object> obj) {
        Object payload = obj.getPayload();
        logger.info("ElasticSearch 服务接收对象: {}-OpType:{}",
                payload, obj.getHeaders().get("type"));
        int id;
        String index;
        try {
            Map<String, Object> map = getInfo(payload);
            index = map.get("index").toString();
            id = Integer.parseInt(map.get("id").toString());
            ess.deleteDoc(index, id);
        } catch (IOException io) {
            logger.error("RabbitMQ ES 删除异常", io);
        } catch (RuntimeException run) {
            logger.error("Runtime 异常", run);
        } catch (Exception ex) {
            logger.error("其他异常", ex);
        }
    }

    //    @RabbitListener(queuesToDeclare = @Queue(RabbitMQUtil.UPDATE_QUEUE))
//    @RabbitHandler
    @StreamListener(RabbitMQUtil.UPDATE_IN)
    public void update(Message<Object> obj) {
        Object payload = obj.getPayload();
        logger.info("ElasticSearch 服务接收对象: {}-OpType:{}",
                payload, obj.getHeaders().get("type"));
        int id;
        String index;
        try {
            Map<String, Object> map = getInfo(payload);
            index = map.get("index").toString();
            id = Integer.parseInt(map.get("id").toString());
            ess.updateDoc(index, id, payload);
        } catch (IOException io) {
            logger.error("RabbitMQ ES 更新异常", io);
        } catch (RuntimeException run) {
            logger.error("Runtime 异常", run);
        } catch (Exception ex) {
            logger.error("其他异常", ex);
        }
    }
}
