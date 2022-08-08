package cn.summer.homework.mqGetter;

import cn.summer.homework.DTO.CourseSTDTO;
import cn.summer.homework.DTO.QuestionResultDTO;
import cn.summer.homework.DTO.ResultQuestionDTO;
import cn.summer.homework.DTO.UserRoleDTO;
import cn.summer.homework.Util.RabbitMQUtil;
import cn.summer.homework.service.ElasticSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author VHBin
 * @date 2022/8/8-23:03
 */

@Component
@RabbitListener(queuesToDeclare = @Queue(RabbitMQUtil.RabbitDirectQueue))
public class ElasticSearchDirectExchangeGetter {
    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchDirectExchangeGetter.class);
    @Resource
    private ElasticSearchService ess;

    @RabbitHandler
    public void get(Object obj) {
        logger.info("ElasticSearch 服务接收对象: {}", obj);
        Integer id;
        String index;
        try {
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
            ess.createDoc(index, id, obj);
        } catch (IOException ex) {
            logger.error("RabbitMQ ES 插入异常", ex);
        } catch (RuntimeException run) {
            logger.error("Runtime 异常", run);
        }
    }
}
