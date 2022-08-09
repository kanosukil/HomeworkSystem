package cn.summer.homework.controller;

import cn.summer.homework.BO.HomeworkOpBO;
import cn.summer.homework.DTO.NewResultDTO;
import cn.summer.homework.DTO.ResultInDTO;
import cn.summer.homework.VO.StudentVO;
import cn.summer.homework.feignClient.ResultHandleClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author VHBin
 * @date 2022/8/8-16:12
 */

@RestController
@RequestMapping("s")
public class CUDController {
    @Resource
    private ResultHandleClient result;

    private StudentVO getCause(Map<String, Object> map) {
        return new StudentVO(500, "Cause", map.get("Cause").toString());
    }

    @PostMapping("/c/result")
    public StudentVO createResult(@RequestBody ResultInDTO in) {
        HomeworkOpBO res = result.create(new NewResultDTO(
                in.getSid(), in.getCid(), in.getQid(), in.getResult()));
        if (res.getIsQuestion()) {
            return new StudentVO(500, "创建回答异常", "问题标识:true");
        } else {
            Map<String, Object> map = res.getInfo();
            if (res.getIsSuccess()) {
                return new StudentVO(200, "rid", map.get("rid").toString());
            } else {
                return getCause(map);
            }
        }
    }

    @PostMapping("/u/result")
    public StudentVO updateResult(@RequestBody ResultInDTO in) {
        HomeworkOpBO res = result.update(new NewResultDTO(
                in.getSid(), 0, 0, in.getResult()));
        if (res.getIsQuestion()) {
            return new StudentVO(500, "更新回答异常", "问题标识:true");
        } else {
            Map<String, Object> map = res.getInfo();
            if (res.getIsSuccess()) {
                return new StudentVO(200, "result", map.get("Result").toString());
            } else {
                return getCause(map);
            }
        }
    }

    @PostMapping("/d/result")
    public StudentVO deleteResult(@RequestBody ResultInDTO in) {
        HomeworkOpBO res = result.delete(
                new NewResultDTO(in.getSid(), 0, in.getRid(), null));
        if (res.getIsQuestion()) {
            return new StudentVO(500, "删除回答异常", "问题标识:true");
        } else {
            Map<String, Object> map = res.getInfo();
            if (res.getIsSuccess()) {
                return new StudentVO(200, "result", map.get("Result").toString());
            } else {
                return getCause(map);
            }
        }
    }
}
