package cn.summer.homework.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author VHBin
 * @date 2022/7/15-17:48
 */

public interface ElasticSearchService {
    /*
        索引
     */
    // 创建
    boolean createIndex(String index_name) throws IOException;

    // 删除
    boolean deleteIndex(String index_name) throws IOException;

    // 存在
    boolean isExistIndex(String index_name) throws IOException;

    /*
        文档
     */
    // 创建
    boolean createDoc(String index_name, Integer id, Object doc) throws IOException; // 指定id

    boolean createDoc(String index_name, Object doc) throws IOException; // 随机id

    boolean createDocs(String index_name, List<Object> docs) throws IOException; // 批量插入

    // 更新
    boolean updateDoc(String index_name, Integer id, Object updateDoc) throws IOException;

    // 删除
    boolean deleteDoc(String index_name, Integer id) throws IOException;

    // 根据 ID 查询
    String findDocByID(String index_name, Integer id) throws IOException;

    /*
        搜索
     */
    Map<String, String> termSearch(String index_name, String value) throws IOException;

    Map<String, String> wildCardSearch(String index_name, String value) throws IOException;

    Map<String, String> matchSearch(String index_nane, String value) throws IOException;

    Map<String, String> termSearch(String index_name, String value, int from, int size) throws IOException;

    Map<String, String> wildCardSearch(String index_name, String value, int from, int size) throws IOException;

    Map<String, String> matchSearch(String index_nane, String value, int from, int size) throws IOException;

    Map<String, String> matchAll(String index_name) throws IOException;

    Map<String, String> matchAll(String index_name, int from, int size) throws IOException;
}
