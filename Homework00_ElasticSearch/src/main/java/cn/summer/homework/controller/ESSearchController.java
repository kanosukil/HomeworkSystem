package cn.summer.homework.controller;

import cn.summer.homework.DTO.ElasticSearchDTO;
import cn.summer.homework.service.ElasticSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author VHBin
 * @date 2022/7/29-15:52
 */

@RestController
@RequestMapping("es-search")
public class ESSearchController {
    private static final Logger logger = LoggerFactory.getLogger(ESSearchController.class);
    @Resource
    private ElasticSearchService elasticSearchService;

    @GetMapping("match-all")
    public List<Integer> matchAll(@RequestBody ElasticSearchDTO es) {
        return searchAll(es);
    }

    @GetMapping("search-all")
    public List<Integer> searchAll(@RequestBody ElasticSearchDTO es) {
        List<Integer> res = new ArrayList<>();
        try {
            if (es.getOption() != 7) {
                logger.error("操作码错误");
                throw new IOException("ElasticSearchDTO Option 与操作不符(SearchAll)");
            }
            if (es.getFrom() == null) {
                elasticSearchService.matchAll(es.getIndex())
                        .forEach((k, v) -> {
                            res.add(Integer.parseInt(k));
                            logger.info("Key:{}, Value:{}", k, v);
                        });
            } else {
                elasticSearchService.matchAll(es.getIndex(),
                                es.getFrom(),
                                es.getSize())
                        .forEach((k, v) -> {
                            res.add(Integer.parseInt(k));
                            logger.info("Key:{}, Value:{}", k, v);
                        });
            }
        } catch (Exception ex) {
            logger.error("SearchAll 异常: {}", ex.getMessage());
            res.clear();
            res.add(-1);
        }
        return res;
    }

    @GetMapping("term-search")
    public List<Integer> termSearch(@RequestBody ElasticSearchDTO es) {
        List<Integer> res = new ArrayList<>();
        try {
            if (es.getOption() != 7) {
                logger.error("操作码错误");
                throw new IOException("ElasticSearchDTO Option 与操作不符(TermSearch)");
            }
            if (es.getFrom() == null) {
                elasticSearchService.termSearch(es.getIndex(), es.getValue())
                        .forEach((k, v) -> {
                            res.add(Integer.parseInt(k));
                            logger.info("Key:{}, Value:{}", k, v);
                        });
            } else {
                elasticSearchService.termSearch(es.getIndex(),
                                es.getValue(),
                                es.getFrom(),
                                es.getSize())
                        .forEach((k, v) -> {
                            res.add(Integer.parseInt(k));
                            logger.info("Key:{}, Value:{}", k, v);
                        });
            }
        } catch (Exception ex) {
            logger.error("TermSearch 异常: {}", ex.getMessage());
            res.clear();
            res.add(-1);
        }
        return res;
    }

    @GetMapping("wildCard-search")
    public List<Integer> wildCardSearch(@RequestBody ElasticSearchDTO es) {
        List<Integer> res = new ArrayList<>();
        try {
            if (es.getOption() != 7) {
                logger.error("操作码错误");
                throw new IOException("ElasticSearchDTO Option 与操作不符(WildCardSearch");
            }
            if (es.getFrom() == null) {
                elasticSearchService.wildCardSearch(es.getIndex(), es.getValue())
                        .forEach((k, v) -> {
                            res.add(Integer.parseInt(k));
                            logger.info("Key:{}, Value:{}", k, v);
                        });
            } else {
                elasticSearchService.wildCardSearch(es.getIndex(),
                                es.getValue(),
                                es.getFrom(),
                                es.getSize())
                        .forEach((k, v) -> {
                            res.add(Integer.parseInt(k));
                            logger.info("Key:{}, Value:{}", k, v);
                        });
            }
        } catch (Exception ex) {
            logger.error("WildCardSearch 异常: {}", ex.getMessage());
            res.clear();
            res.add(-1);
        }
        return res;
    }

    @GetMapping("match-search")
    public List<Integer> matchSearch(@RequestBody ElasticSearchDTO es) {
        List<Integer> res = new ArrayList<>();
        try {
            if (es.getOption() != 7) {
                logger.error("操作码错误");
                throw new IOException("ElasticSearchDTO Option 与操作不符(MatchSearch)");
            }
            if (es.getFrom() == null) {
                elasticSearchService.matchSearch(es.getIndex(), es.getValue())
                        .forEach((k, v) -> {
                            res.add(Integer.parseInt(k));
                            logger.info("Key:{}, Value:{}", k, v);
                        });
            } else {
                elasticSearchService.matchSearch(es.getIndex(),
                                es.getValue(),
                                es.getFrom(),
                                es.getSize())
                        .forEach((k, v) -> {
                            res.add(Integer.parseInt(k));
                            logger.info("Key:{}, Value:{}", k, v);
                        });
            }
        } catch (Exception ex) {
            logger.error("MatchSearch 异常: {}", ex.getMessage());
            res.clear();
            res.add(-1);
        }
        return res;
    }
}
