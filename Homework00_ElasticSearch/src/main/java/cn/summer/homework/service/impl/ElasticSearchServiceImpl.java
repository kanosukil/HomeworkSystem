package cn.summer.homework.service.impl;

import cn.summer.homework.DTO.*;
import cn.summer.homework.QO.ElasticSearchQO;
import cn.summer.homework.service.ElasticSearchService;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.ElasticsearchException;
import co.elastic.clients.elasticsearch.core.*;
import co.elastic.clients.elasticsearch.core.bulk.BulkOperation;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.elasticsearch.core.search.HitsMetadata;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.DeleteIndexResponse;
import co.elastic.clients.transport.endpoints.BooleanResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        boolean res = false;
        try {
            CreateIndexResponse response = client.indices().create(
                    e -> e.index(index_name));
            logger.info("创建 ES index 结果: {}", response.toString());
            res = Boolean.TRUE.equals(response.acknowledged());
        } catch (ElasticsearchException ese) {
            logger.error("创建 ES index 失败: {}", ese.toString());
        }
        return res;
    }

    @Override
    public boolean deleteIndex(String index_name)
            throws IOException {
        boolean res = false;
        try {
            DeleteIndexResponse response
                    = client.indices().delete(
                    e -> e.index(index_name));
            logger.info("删除 ES index 结果: {}", response.toString());
            res = response.acknowledged();
        } catch (ElasticsearchException ese) {
            logger.error("删除 ES index 失败: {}", ese.toString());
        }
        return res;
    }

    @Override
    public boolean isExistIndex(String index_name)
            throws IOException {
        BooleanResponse res = client.indices()
                .exists(e -> e.index(index_name));
        logger.info("ES Index {} 是否存在: {}", index_name, res.toString());
        return res.value();
    }

    /*
    index 不存在, 将自动创建 index 后, 再插入 doc
    */
    @Override
    public boolean createDoc(String index_name, Integer id, Object doc)
            throws IOException {
        IndexResponse response = client
                .index(e -> e.index(index_name)
                        .id(id.toString())
                        .document(new ElasticSearchQO(doc.toString())));
        String res = response.result().jsonValue().trim();
        logger.info("创建 ES document 结果: {}", res);
        return res.equals("created") || res.equals("updated");
    }

    /*
    创建不指定 id 文档
     */
    @Override
    public boolean createDoc(String index_name, Object doc)
            throws IOException {
        IndexResponse response = client
                .index(e -> e.index(index_name)
                        .document(new ElasticSearchQO(doc.toString())));
        String res = response.result().jsonValue().trim();
        logger.info("创建 ES document 结果: {}", res);
        return res.equals("created") || res.equals("updated");
    }

    @Override
    public boolean createDocs(String index_name, List<Object> docs)
            throws IOException {
        List<BulkOperation> bulk = new ArrayList<>();
        String id;
        Object o = docs.get(0);
        if (o instanceof CourseSTDTO) {
            id = ((CourseSTDTO) o).getCourse().getId().toString();
        } else if (o instanceof NewCourseDTO) {
            id = ((NewCourseDTO) o).getCourse().getId().toString();
        } else if (o instanceof NewQuestionDTO) {
            id = ((NewQuestionDTO) o).getQuestion().getId().toString();
        } else if (o instanceof NewResultDTO) {
            id = ((NewResultDTO) o).getResult().getId().toString();
        } else if (o instanceof QuestionResultDTO) {
            id = ((QuestionResultDTO) o).getQuestion().getId().toString();
        } else if (o instanceof ResultQuestionDTO) {
            id = ((ResultQuestionDTO) o).getResult().getId().toString();
        } else if (o instanceof URoleDTO) {
            id = ((URoleDTO) o).getUid().toString();
        } else if (o instanceof UserRoleDTO) {
            id = ((UserRoleDTO) o).getUser().getId().toString();
        } else {
            throw new IOException("未知对象<文档批量插入>");
        }
        String finalId = id;
        docs.forEach(e -> bulk.add(
                BulkOperation.of(
                        _1 -> _1.index(
                                _2 -> _2.id(finalId)
                                        .document(new ElasticSearchQO(e.toString()))))));
        BulkResponse res = client
                .bulk(e -> e.index(index_name).operations(bulk));
        logger.info("批量插入文档是否出错: {}", res.errors());
        for (BulkResponseItem item : res.items()) {
            logger.info("-----------------------------------");
            logger.info("文档插入 ID: {}", item.id());
            logger.info("文档所在目录: {}", item.index());
            logger.info("文档插入结果: {}", item.result());
            logger.info("文档操作类型: {}", item.operationType());
        }
        return !res.errors();
    }

    @Override
    public boolean updateDoc(String index_name, Integer id, Object updateDoc)
            throws IOException {
        boolean res = false;
        try {
            UpdateResponse<ElasticSearchQO> response = client
                    .update(e -> e.index(index_name)
                                    .id(id.toString())
                                    .doc(new ElasticSearchQO(e.toString())),
                            ElasticSearchQO.class);
            String s = response.result().jsonValue().trim();
            if (s.equals("noop")) {
                logger.debug("未更新 ES document: {}", s);
                res = true;
            } else if (s.equals("updated")) {
                logger.info("更新 ES document 成功: {}", s);
                res = true;
            } else {
                logger.debug("更新 ES document 未知状态: {}", s);
            }
        } catch (ElasticsearchException ese) {
            logger.error("更新 ES document 失败: {}", ese.toString());
        }
        return res;
    }

    @Override
    public boolean deleteDoc(String index_name, Integer id)
            throws IOException {
        // not_found deleted
        boolean res = false;
        try {
            DeleteResponse response = client
                    .delete(e -> e.index(index_name)
                            .id(id.toString()));
            String s = response.result().jsonValue().trim();
            if (s.equals("deleted")) {
                logger.info("删除 ES document 成功: {}", s);
                res = true;
            } else if (s.equals("not_found")) {
                logger.debug("删除 ES document 未找到: {}", s);
                res = true;
            } else {
                logger.debug("删除 ES document 未知状态: {}", s);
            }
        } catch (ElasticsearchException ese) {
            logger.error("删除 ES document 失败: {}", ese.toString());
        }
        return res;
    }

    @Override
    public String findDocByID(String index_name, Integer id)
            throws IOException {
        String res = null;
        try {
            GetResponse<ElasticSearchQO> response = client
                    .get(e -> e.index(index_name)
                                    .id(id.toString()),
                            ElasticSearchQO.class);
            if (response.found()) {
                logger.info("已找到 ID {} 的文档", id);
                ElasticSearchQO source = response.source();
                if (source == null) {
                    res = "指定ID文档为空";
                } else {
                    res = source.getValue();
                }
            } else {
                logger.debug("ID {} 的文档不存在", id);
                res = "未找到指定ID文档";
            }
        } catch (ElasticsearchException ese) {
            logger.error("通过 ID 查询文档失败: {}", ese.toString());
        }
        return res;
    }

    @Override
    public Map<String, String> termSearch(String index_name, String value)
            throws IOException {
        return termSearch(index_name, value, 0, 10);
    }

    @Override
    public Map<String, String> wildCardSearch(String index_name, String value)
            throws IOException {
        return wildCardSearch(index_name, value, 0, 10);
    }

    @Override
    public Map<String, String> matchSearch(String index_nane, String value)
            throws IOException {
        return matchSearch(index_nane, value, 0, 10);
    }

    private Map<String, String> resultOp(HitsMetadata<ElasticSearchQO> hits) {
        logger.info("查询完毕");
        if (hits.total() == null) {
            logger.debug("查询无结果");
            return null;
        } else {
            Map<String, String> res = new HashMap<>();
            for (Hit<ElasticSearchQO> hit : hits.hits()) {
                String id = hit.id();
                if (hit.source() == null) {
                    logger.debug("ID={} 无内容", id);
                    res.put(id, null);
                } else {
                    res.put(id, hit.source().getValue());
                }
                logger.info("查询处理完毕");
            }
            return res;
        }
    }

    @Override
    public Map<String, String> termSearch(String index_name, String value, int from, int size)
            throws IOException {
        HitsMetadata<ElasticSearchQO> hits = client.search(_1 -> _1.index(index_name)
                        .query(_2 -> _2
                                .term(_3 -> _3
                                        .field("value")
                                        .value(_4 -> _4
                                                .stringValue(value))))
                        .from(from)
                        .size(size),
                ElasticSearchQO.class).hits();
        logger.info("Term 查询完成. Index={}, Value={}", index_name, value);
        return resultOp(hits);
    }

    @Override
    public Map<String, String> wildCardSearch(String index_name, String value, int from, int size)
            throws IOException {
        HitsMetadata<ElasticSearchQO> hits = client.search(_1 -> _1.index(index_name)
                        .query(_2 -> _2
                                .wildcard(_3 -> _3
                                        .field("value.keyword")
                                        .value("*" + value + "*")))
                        .from(from)
                        .size(size),
                ElasticSearchQO.class).hits();
        logger.info("WildCard 查询完成. Index={}, Value={}", index_name, value);
        return resultOp(hits);
    }

    @Override
    public Map<String, String> matchSearch(String index_name, String value, int from, int size)
            throws IOException {
        HitsMetadata<ElasticSearchQO> hits = client.search(_1 -> _1.index(index_name)
                        .query(_2 -> _2
                                .match(_3 -> _3
                                        .field("value")
                                        .query(value)))
                        .from(from)
                        .size(size),
                ElasticSearchQO.class).hits();
        logger.info("Match 查询完成. Index={}, Value={}", index_name, value);
        return resultOp(hits);
    }
}
