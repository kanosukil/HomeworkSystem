package cn.summer.homework.feignClient;

import cn.summer.homework.BO.ESOpBO;
import cn.summer.homework.DTO.ElasticSearchDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author VHBin
 * @date 2022/8/4-18:44
 */

@FeignClient("ESService")
public interface ESCRUDClient {
    @GetMapping("/es-search/match-all")
    List<Integer> matchAll(@RequestBody ElasticSearchDTO es);

    @GetMapping("/es-search/search-all")
    List<Integer> searchAll(@RequestBody ElasticSearchDTO es);

    @GetMapping("/es-search/term-search")
    List<Integer> termSearch(@RequestBody ElasticSearchDTO es);

    @GetMapping("/es-search/wildCard-search")
    List<Integer> wildCardSearch(@RequestBody ElasticSearchDTO es);

    @GetMapping("/es-search/match-search")
    List<Integer> matchSearch(@RequestBody ElasticSearchDTO es);

    @PostMapping("/es-data/index-create")
    ESOpBO indexCreate(@RequestBody ElasticSearchDTO es);

    @PostMapping("/es-data/doc-create")
    ESOpBO documentCreate(@RequestBody ElasticSearchDTO es);

    @PostMapping("/es-data/docs-create")
    ESOpBO documentsCreate(@RequestBody ElasticSearchDTO es);

    @PostMapping("/es-data/index-delete")
    ESOpBO indexDelete(@RequestBody ElasticSearchDTO es);

    @PostMapping("/es-data/doc-delete")
    ESOpBO documentDelete(@RequestBody ElasticSearchDTO es);

    @PostMapping("/es-data/doc-update")
    ESOpBO documentUpdate(@RequestBody ElasticSearchDTO es);
}
