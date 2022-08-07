package cn.summer.homework.feignClient;

import cn.summer.homework.BO.HomeworkOpBO;
import cn.summer.homework.DTO.NewResultDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author VHBin
 * @date 2022/8/7-15:26
 */

@FeignClient("RespondentService")
public interface ResultHandleClient {
    @PostMapping("/respondent/new")
    HomeworkOpBO create(@RequestBody NewResultDTO newResult);

    @PostMapping("/respondent/update")
    HomeworkOpBO update(@RequestBody NewResultDTO updateResult);

    @PostMapping("/respondent/delete")
    HomeworkOpBO delete(@RequestBody NewResultDTO deleteResult);
}
