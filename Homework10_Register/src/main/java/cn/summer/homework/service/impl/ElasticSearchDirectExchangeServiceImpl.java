package cn.summer.homework.service.impl;

import cn.summer.homework.Util.RabbitMQUtil;
import cn.summer.homework.service.ElasticSearchDirectExchangeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author VHBin
 * @date 2022/8/8-22:57
 */
@Service
public class ElasticSearchDirectExchangeServiceImpl
        implements ElasticSearchDirectExchangeService {
    private static final Logger logger
            = LoggerFactory.getLogger(ElasticSearchDirectExchangeServiceImpl.class);
    @Resource
    private RabbitTemplate template;

    @Override
    public Boolean sendMessage(Object doc) {
        try {
            template.convertAndSend(
                    RabbitMQUtil.RabbitDirectExchange,
                    RabbitMQUtil.RabbitDirectRouting,
                    doc);
            return true;
        } catch (Exception ex) {
            logger.error("RabbitMQException: {}", ex.getMessage());
            return false;
        }
    }
}
