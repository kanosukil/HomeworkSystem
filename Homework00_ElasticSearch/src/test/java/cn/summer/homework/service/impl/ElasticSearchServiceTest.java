package cn.summer.homework.service.impl;

import cn.summer.homework.QO.ElasticSearchQO;
import cn.summer.homework.service.ElasticSearchService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author VHBin
 * @date 2022/8/4-15:05
 */


@SpringBootTest
class ElasticSearchServiceTest {

    private static final Logger logger = LoggerFactory.getLogger(ElasticSearchServiceTest.class);
    private final String index = "ess-test";
    private final String value = "string";
    @Resource
    private ElasticSearchService ess;

    @Test
    void test1() throws IOException {
        if (ess.createDoc(index, 1, new ElasticSearchQO(value))) {
            logger.info("CreateDoc Result: {}", ess.findDocByID(index, 1));
        } else {
            logger.error("CreateDoc Fail.");
        }
        if (ess.deleteIndex(index)) {
            logger.info("Delete Successfully");
        } else {
            logger.error("Delete Fail");
        }
    }

    @Test
    void test2() throws IOException {
        logger.info("{}", ess.isExistIndex("EEEE"));
    }

    @Test
    void test3() throws IOException, InterruptedException {
        if (ess.createIndex(index)) {
            logger.info("Create Index Successfully");
        } else {
            logger.error("Create Index Fail");
            return;
        }
        if (ess.createDoc(index, new ElasticSearchQO(value))) {
            logger.info("CreateDoc Result(term): {}", ess.termSearch(index, value));
            logger.info("CreateDoc Result(wild): {}", ess.wildCardSearch(index, value));
            logger.info("CreateDoc Result(match): {}", ess.matchSearch(index, value));
            logger.info("CreateDoc Result(matchAll): {}", ess.matchAll(index));
        } else {
            logger.error("CreateDoc Fail.");
        }
        logger.info("");
        Thread.sleep(1000L);
        logger.info("");
        term();
        logger.info("");
        wildCard();
        logger.info("");
        match();
        if (ess.deleteIndex(index)) {
            logger.info("Delete Successfully");
        } else {
            logger.error("Delete Fail");
        }
    }

    @Test
    void match() throws IOException {
        logger.info("Match: {}", ess.matchSearch(index, value));
    }

    @Test
    void term() throws IOException {
        logger.info("Term: {}", ess.termSearch(index, value));
    }

    @Test
    void wildCard() throws IOException {
        logger.info("WildCard: {}", ess.wildCardSearch(index, value));
    }

    @Test
    void matchAll() throws IOException {
        logger.info("Match All: {}", ess.matchAll("test2"));
    }
}