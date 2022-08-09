package cn.summer.homework.mqGetter;

import cn.summer.homework.DTO.CourseSTDTO;
import cn.summer.homework.DTO.QuestionResultDTO;
import cn.summer.homework.DTO.ResultQuestionDTO;
import cn.summer.homework.DTO.UserRoleDTO;
import cn.summer.homework.Util.RabbitMQUtil;
import cn.summer.homework.Util.TypeUtil;
import cn.summer.homework.service.ElasticSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
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
            index = "course";
        } else if (obj instanceof QuestionResultDTO) {
            id = ((QuestionResultDTO) obj).getQuestion().getId();
            index = "question";
        } else if (obj instanceof ResultQuestionDTO) {
            id = ((ResultQuestionDTO) obj).getResult().getId();
            index = "result";
        } else if (obj instanceof UserRoleDTO) {
            id = ((UserRoleDTO) obj).getUser().getId();
            index = "user";
        } else {
            throw new RuntimeException("未知对象<RabbitMQ文档插入>");
        }
        return new HashMap<>(2, 1f) {{
            put("index", index);
            put("id", id);
        }};
    }

    @RabbitListener(queuesToDeclare = @Queue(RabbitMQUtil.SAVE_QUEUE))
    @RabbitHandler
    public void save(Object obj) {
        logger.info("ElasticSearch 服务接收对象: {}", obj);
        int id;
        String index;
        Map<String, Object> map;
        try {
            if (obj instanceof List) {
                List<Object> objects = TypeUtil.objToList(obj);
                index = getInfo(objects.get(0)).get("index").toString();
                ess.createDocs(index, objects);
            } else {
                map = getInfo(obj);
                index = map.get("index").toString();
                id = Integer.parseInt(map.get("id").toString());
                ess.createDoc(index, id, obj);
            }
        } catch (IOException io) {
            logger.error("RabbitMQ ES 插入异常", io);
        } catch (RuntimeException run) {
            logger.error("Runtime 异常", run);
        } catch (Exception ex) {
            logger.error("其他异常", ex);
        }
    }

    @RabbitListener(queuesToDeclare = @Queue(RabbitMQUtil.DELETE_QUEUE))
    @RabbitHandler
    public void delete(Object obj) {
        logger.info("ElasticSearch 服务接收对象: {}", obj);
        int id;
        String index;
        try {
            Map<String, Object> map = getInfo(obj);
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

    @RabbitListener(queuesToDeclare = @Queue(RabbitMQUtil.UPDATE_QUEUE))
    @RabbitHandler
    public void update(Object obj) {
        logger.info("ElasticSearch 服务接收对象: {}", obj);
        int id;
        String index;
        try {
            Map<String, Object> map = getInfo(obj);
            index = map.get("index").toString();
            id = Integer.parseInt(map.get("id").toString());
            ess.updateDoc(index, id, obj);
        } catch (IOException io) {
            logger.error("RabbitMQ ES 更新异常", io);
        } catch (RuntimeException run) {
            logger.error("Runtime 异常", run);
        } catch (Exception ex) {
            logger.error("其他异常", ex);
        }
    }
}
