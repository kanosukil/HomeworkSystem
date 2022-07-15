package cn.summer.homework.service.impl;

import cn.summer.homework.service.ElasticSearchService;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.DeleteIndexResponse;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import com.alibaba.fastjson.JSON;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author VHBin
 * @date 2022/7/15-18:20
 */

@Service
public class ElasticSearchServiceImpl implements ElasticSearchService {
    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchServiceImpl.class);
    @Resource
    private ElasticsearchClient client;

    @Override
    public boolean createIndex(String index_name)
            throws IOException {
        CreateIndexResponse res
                = client.indices().create(
                e -> e.index(index_name));
        logger.info("Result: {}", res.toString());
        return Boolean.TRUE.equals(res.acknowledged());
    }

    @Override
    public boolean deleteIndex(String index_name)
            throws IOException {
        DeleteIndexResponse res
                = client.indices().delete(
                e -> e.index(index_name));
        logger.info("Result: {}", res.toString());
        return res.acknowledged();
    }

    @Override
    public boolean isExistIndex(String index_name)
            throws IOException {
        BooleanResponse res = client.indices()
                .exists(e -> e.index(index_name));
        logger.info("Result: {}", res.toString());
        return res.value();
    }

    @Override
    public boolean createDoc(String index_name, Integer id, Object doc)
            throws IOException {
        IndexResponse res = client
                .index(e -> e.index(index_name)
                        .id(id.toString())
                        .document(JSON.toJSONString(doc)));
        logger.info("Result: {}", res.toString());
        // ...
        return false;
    }

    @Override
    public boolean createDoc(String index_name, Object doc)
            throws IOException {
        IndexResponse res = client
                .index(e -> e.index(index_name)
                        .document(JSON.toJSONString(doc)));
        logger.info("Result: {}", res.toString());
        // ...
        return false;
    }

    @Override
    public boolean createDocs(String index_name, List<Object> docs)
            throws IOException {
        List<BulkOperation> bulk = new ArrayList<>();
        docs.forEach(e -> {
            bulk.add(
                    BulkOperation.of(
                            _1 -> _1.index(
                                    _2 -> _2.document(
                                            JSON.toJSONString(e)))));
        });
        BulkResponse res = client
                .bulk(e -> e.index(index_name).operations(bulk));
        logger.info("Result: {}", res.toString());
        // ...
        return !res.errors();
    }

    @Override
    public boolean updateDoc(String index_name, Integer id, Object updateDoc)
            throws IOException {
        UpdateResponse<String> res = client
                .update(e -> e.index(index_name)
                                .id(id.toString())
                                .doc(JSON.toJSONString(updateDoc)),
                        String.class);
        logger.info("Result: {}", res.toString());
        // ...
        return false;
    }

    @Override
    public boolean deleteDoc(String index_name, Integer id)
            throws IOException {
        DeleteResponse res = client
                .delete(e -> e.index(index_name)
                        .id(id.toString()));
        logger.info("Result: {}", res.toString());
        // ...
        return false;
    }

    @Override
    public String findDocByID(String index_name, Integer id)
            throws IOException {
        GetResponse<String> res = client
                .get(e -> e.index(index_name).id(id.toString()), String.class);
        logger.info("Result: {}", res.toString());
        // ...
        return res.source();
    }

    @Override
    public String termSearch(String index_name, String value) {
        return null;
    }

    @Override
    public String wildCardSearch(String index_name, String value) {
        return null;
    }

    @Override
    public String matchSearch(String index_nane, String value) {
        return null;
    }
}
