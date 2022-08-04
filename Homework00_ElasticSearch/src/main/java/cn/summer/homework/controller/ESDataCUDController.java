package cn.summer.homework.controller;

import cn.summer.homework.BO.ESOpBO;
import cn.summer.homework.DTO.ElasticSearchDTO;
import cn.summer.homework.service.ElasticSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashMap;

/**
 * @author VHBin
 * @date 2022/8/4-17:59
 */

@RestController
@RequestMapping("es-data")
public class ESDataCUDController {
    private static final Logger logger = LoggerFactory.getLogger(ESDataCUDController.class);
    @Resource
    private ElasticSearchService elasticSearchService;

    @PostMapping("index-create")
    public ESOpBO indexCreate(@RequestBody ElasticSearchDTO es) {
        ESOpBO res = new ESOpBO();
        try {
            if (es.getOption() != 1) {
                throw new IOException("Option 代码与操作不符(目录创建)");
            }
            if (elasticSearchService.createIndex(es.getIndex())) {
                logger.info("目录{}创建完成", es.getIndex());
                res.setIsSuccess(true);
            }
        } catch (Exception ex) {
            logger.error("目录创建异常: {}", ex.getMessage());
            res.setMap(new HashMap<>(1, 1f) {{
                put("exception", ex);
            }});
        }
        return res;
    }

    @PostMapping("doc-create")
    public ESOpBO documentCreate(@RequestBody ElasticSearchDTO es) {
        ESOpBO res = new ESOpBO();
        try {
            if (es.getOption() != 2) {
                throw new IOException("Option 代码与操作不符(文档创建)");
            }
            if (elasticSearchService.createDoc(es.getIndex(),
                    es.getSize(),
                    es.getObjects().get(0))) {
                res.setIsSuccess(true);
            }
        } catch (Exception ex) {
            logger.error("文档创建异常: {}", ex.getMessage());
            res.setMap(new HashMap<>(1, 1f) {{
                put("exception", ex);
            }});
        }
        return res;
    }

    @PostMapping("docs-create")
    public ESOpBO documentsCreate(@RequestBody ElasticSearchDTO es) {
        ESOpBO res = new ESOpBO();
        try {
            if (es.getOption() != 3) {
                throw new IOException("Option 代码与操作不符(文档批量创建)");
            }
            if (elasticSearchService.createDocs(es.getIndex(), es.getObjects())) {
                res.setIsSuccess(true);
            }
        } catch (Exception ex) {
            logger.error("文档批量创建异常: {}", ex.getMessage());
            res.setMap(new HashMap<>(1, 1f) {{
                put("exception", ex);
            }});
        }
        return res;
    }

    @PostMapping("index-delete")
    public ESOpBO indexDelete(@RequestBody ElasticSearchDTO es) {
        ESOpBO res = new ESOpBO();
        try {
            if (es.getOption() != 4) {
                throw new IOException("Option 代码与操作不符(目录删除)");
            }
            if (elasticSearchService.deleteIndex(es.getIndex())) {
                res.setIsSuccess(true);
            }
        } catch (Exception ex) {
            logger.error("目录删除异常: {}", ex.getMessage());
            res.setMap(new HashMap<>(1, 1f) {{
                put("exception", ex);
            }});
        }
        return res;
    }

    @PostMapping("doc-delete")
    public ESOpBO documentDelete(@RequestBody ElasticSearchDTO es) {
        ESOpBO res = new ESOpBO();
        try {
            if (es.getOption() != 5) {
                throw new IOException("Option 代码与操作不符(文档删除)");
            }
            if (elasticSearchService.deleteDoc(es.getIndex(), es.getSize())) {
                res.setIsSuccess(true);
            }
        } catch (Exception ex) {
            logger.error("文档删除异常: {}", ex.getMessage());
            res.setMap(new HashMap<>(1, 1f) {{
                put("exception", ex);
            }});
        }
        return res;
    }

    @PostMapping("doc-update")
    public ESOpBO documentUpdate(@RequestBody ElasticSearchDTO es) {
        ESOpBO res = new ESOpBO();
        try {
            if (es.getOption() != 6) {
                throw new IOException("Option 代码与操作不符(文档更新)");
            }
            if (elasticSearchService.updateDoc(es.getIndex(),
                    es.getSize(),
                    es.getObjects().get(0))) {
                res.setIsSuccess(true);
            }
        } catch (Exception ex) {
            logger.error("文档更新异常: {}", ex.getMessage());
            res.setMap(new HashMap<>(1, 1f) {{
                put("exception", ex);
            }});
        }
        return res;
    }

}
