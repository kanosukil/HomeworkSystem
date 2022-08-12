package cn.summer.homework.mqGetter;

import cn.summer.homework.Util.RabbitMQUtil;
import cn.summer.homework.Util.TypeUtil;
import cn.summer.homework.config.InputMessageConsumer;
import cn.summer.homework.service.ElasticSearchService;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

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

    //    @RabbitListener(queuesToDeclare = @Queue(RabbitMQUtil.SAVE_QUEUE))
//    @RabbitHandler
    @StreamListener(RabbitMQUtil.SAVE_IN)
//    public void save(Message<?> obj) {
    public void save(Message<String> obj) {
        try {
            String className = Objects.requireNonNull(obj.getHeaders().get("class"))
                    .toString().split(" ")[1].trim();
            Object object;
            if (Objects.equals(obj.getHeaders().get("isList"), "1")) {
                object = JSON.parseArray(obj.getPayload(), Class.forName(className));
            } else {
                object = JSONObject.parseObject(obj.getPayload(), Class.forName(className));
            }
//            Object object = obj.getPayload();
            logger.info("ElasticSearch save 服务接收对象: {}-class:{}",
                    object, className);
            int id = Integer.parseInt(Objects.requireNonNull(obj.getHeaders().get("out-id")).toString());
            String index = Objects.requireNonNull(obj.getHeaders().get("index")).toString();
            if (object instanceof List) {
                logger.info("List");
                List<Object> objects = TypeUtil.objToList(object);
                ess.createDocs(index, objects);
            } else {
                logger.info("{}", className);
                ess.createDoc(index, id, object);
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
//    public void delete(Message<?> obj) {
    public void delete(Message<String> obj) {
        try {
            String className = Objects.requireNonNull(obj.getHeaders().get("class"))
                    .toString().split(" ")[1].trim();
            Object object = JSONObject.parseObject(obj.getPayload(), Class.forName(className));
//            Object object = obj.getPayload();
            logger.info("ElasticSearch delete 服务接收对象: {}-class:{}",
                    object, className);
            int id = Integer.parseInt(Objects.requireNonNull(obj.getHeaders().get("out-id")).toString());
            String index = Objects.requireNonNull(obj.getHeaders().get("index")).toString();
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
//    public void update(Message<?> obj) {
    public void update(Message<String> obj) {
        try {
            String className = Objects.requireNonNull(obj.getHeaders().get("class"))
                    .toString().split(" ")[1].trim();
            Object object = JSONObject.parseObject(obj.getPayload(), Class.forName(className));
//            Object object = obj.getPayload();
            logger.info("ElasticSearch update 服务接收对象: {}-class:{}",
                    object, className);
            int id = Integer.parseInt(Objects.requireNonNull(obj.getHeaders().get("out-id")).toString());
            String index = Objects.requireNonNull(obj.getHeaders().get("index")).toString();
            ess.updateDoc(index, id, object);
        } catch (IOException io) {
            logger.error("RabbitMQ ES 更新异常", io);
        } catch (RuntimeException run) {
            logger.error("Runtime 异常", run);
        } catch (Exception ex) {
            logger.error("其他异常", ex);
        }
    }
}
