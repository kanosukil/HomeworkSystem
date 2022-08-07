package cn.summer.homework.controller;

import cn.summer.homework.BO.HomeworkOpBO;
import cn.summer.homework.DTO.NewResultDTO;
import cn.summer.homework.service.ResultIOService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author VHBin
 * @date 2022/8/6-22:20
 */

@RestController
@RequestMapping("respondent")
public class ResultHandleController {
    @Resource
    private ResultIOService service;

    @PostMapping("new")
    public HomeworkOpBO create(@RequestBody NewResultDTO newResult) {
        return service.insertResult(newResult);
    }

    @PostMapping("update")
    public HomeworkOpBO update(@RequestBody NewResultDTO updateResult) {
        return service.updateResult(updateResult);
    }

    @PostMapping("delete")
    public HomeworkOpBO delete(@RequestBody NewResultDTO deleteResult) {
        return service.deleteResult(deleteResult);
    }
}
