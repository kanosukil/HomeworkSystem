package cn.summer.homework.service;

/**
 * @author VHBin
 * @date 2022/8/8-22:52
 */

public interface ElasticSearchDirectExchangeService {
    Boolean save(Object doc);

    Boolean delete(Object doc);

    Boolean update(Object doc);
}
