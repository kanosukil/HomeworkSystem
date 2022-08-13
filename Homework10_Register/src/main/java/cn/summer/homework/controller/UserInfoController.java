package cn.summer.homework.controller;

import cn.summer.homework.BO.UserOpBO;
import cn.summer.homework.DTO.URoleDTO;
import cn.summer.homework.DTO.UserRoleDTO;
import cn.summer.homework.DTO.UserUpdateDTO;
import cn.summer.homework.VO.UserVO;
import cn.summer.homework.service.UserIOService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author VHBin
 * @date 2022/8/13-12:06
 */
@RestController
@RequestMapping("user")
public class UserInfoController {
    private static final Logger logger = LoggerFactory.getLogger(UserInfoController.class);
    @Resource
    private UserIOService userIO;

    @PostMapping("/update/all")
    public UserVO<String> update(@RequestBody UserUpdateDTO update) {
        UserOpBO res = userIO.update(
                new UserRoleDTO(update.getUser(), update.getRoles()));
        return getStringUserVO(res);
    }

    @PostMapping("/update/info")
    public UserVO<String> infoUpdate(@RequestBody UserUpdateDTO info) {
        UserOpBO res = userIO.infoUpdate(info.getUser());
        return getStringUserVO(res);
    }

    @PostMapping("/update/role")
    public UserVO<String> roleUpdate(@RequestBody UserUpdateDTO role) {
        UserOpBO res = userIO.roleUpdate(
                new URoleDTO(role.getUid(), role.getRoles()));
        return getStringUserVO(res);
    }

    private UserVO<String> getStringUserVO(UserOpBO res) {
        if (res.getIsSuccess()) {
            logger.info("用户数据更新完成");
            logger.info("Source User: {}", res.getInfo().get("SrcUser"));
            logger.info("Update User: {}", res.getInfo().get("UpdateUser"));
            return new UserVO<>(200, "OK", "更新完成");
        } else {
            logger.error("用户数据更新异常");
            return new UserVO<>(500, "Exception",
                    res.getInfo().get("Cause").toString());
        }
    }
}
