package cn.summer.homework.service;

/**
 * @author VHBin
 * @date 2022/8/8-22:52
 */

public interface ElasticSearchDirectExchangeService {
    <T> Boolean save(T doc);

    <T> Boolean delete(T doc);

    <T> Boolean update(T doc);
}
