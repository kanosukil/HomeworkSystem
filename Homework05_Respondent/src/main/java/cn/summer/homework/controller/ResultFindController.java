package cn.summer.homework.controller;

import cn.summer.homework.DTO.ResultQuestionDTO;
import cn.summer.homework.service.ResultSelectService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * @author VHBin
 * @date 2022/8/6-23:53
 */

@RestController
@RequestMapping("/query/result")
public class ResultFindController {
    @Resource
    private ResultSelectService service;

    @GetMapping("all")
    public List<ResultQuestionDTO> getAll() {
        return service.getAResult();
    }

    @GetMapping("id")
    public ResultQuestionDTO get(@RequestParam("id") Integer rid)
            throws IOException {
        return service.getResult(rid);
    }

    @GetMapping("course")
    public List<ResultQuestionDTO> getCourseResult(@RequestParam("cid") Integer cid)
            throws IOException {
        return service.getCourseResult(cid);
    }
}
