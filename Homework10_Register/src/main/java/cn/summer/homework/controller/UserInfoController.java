package cn.summer.homework.controller;

import cn.summer.homework.BO.ESOpBO;
import cn.summer.homework.BO.UserOpBO;
import cn.summer.homework.DTO.ElasticSearchDTO;
import cn.summer.homework.DTO.URoleDTO;
import cn.summer.homework.DTO.UserRoleDTO;
import cn.summer.homework.DTO.UserUpdateDTO;
import cn.summer.homework.Util.IndexUtil;
import cn.summer.homework.VO.UserVO;
import cn.summer.homework.feignClient.ESCRUDClient;
import cn.summer.homework.service.ElasticSearchDirectExchangeService;
import cn.summer.homework.service.UserIOService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

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
    @Resource
    private ESCRUDClient esCUD;
    @Resource
    private ElasticSearchDirectExchangeService ess;

    private void isUserSelf(Integer id, HttpServletRequest request)
            throws IOException {
        if (Integer.parseInt(request.getAttribute("userid").toString()) != id) {
            throw new IOException("无效操作: 不允许操作其他用户");
        }
    }

    private void esIndexDelete() {
        ElasticSearchDTO dto = new ElasticSearchDTO();
        dto.setOption(4);
        dto.setIndex(IndexUtil.USER);
        ESOpBO esOpBO = esCUD.indexDelete(dto);
        if (!esOpBO.getIsSuccess()) {
            logger.warn("ES 删除目录{}失败", IndexUtil.USER);
        }
    }

    @PostMapping("/update/all")
    public UserVO<String> update(@RequestBody UserUpdateDTO update, HttpServletRequest request) {
        try {
            isUserSelf(update.getUid(), request);
        } catch (IOException e) {
            logger.error("[Update User]", e);
            return new UserVO<>(400, "UpdateUser: 无效操作", e.getMessage());
        }
        logger.info("User: {}", update);
        UserOpBO res = userIO.update(
                new UserRoleDTO(update.getUser(), update.getRoles()));
        try {
            if (ess.update(userIO.login(update.getUser().getEmail()))) {
                logger.info("<user>MQ ES Update 成功");
            } else {
                logger.warn("<user>MQ ES Update 异常, 未更新 ES");
                esIndexDelete();
            }
        } catch (IOException io) {
            esIndexDelete();
            logger.warn("[User Update]ES更新异常", io);
        }
        return getStringUserVO(res);
    }

    @PostMapping("/update/info")
    public UserVO<String> infoUpdate(@RequestBody UserUpdateDTO info, HttpServletRequest request) {
        try {
            isUserSelf(info.getUid(), request);
        } catch (IOException e) {
            logger.error("[Update UserInfo]", e);
            return new UserVO<>(400, "UpdateUserInfo: 无效操作", e.getMessage());
        }
        logger.info("User: {}", info);
        UserOpBO res = userIO.infoUpdate(info.getUser());
        try {
            if (ess.update(userIO.login(info.getUser().getEmail()))) {
                logger.info("<user-info>MQ ES Update 成功");
            } else {
                logger.warn("<user-info>MQ ES Update 异常, 未更新 ES");
                esIndexDelete();
            }
        } catch (IOException io) {
            esIndexDelete();
            logger.warn("[UserInfo Update]ES更新异常", io);
        }
        return getStringUserVO(res);
    }

    @PostMapping("/update/role")
    public UserVO<String> roleUpdate(@RequestBody UserUpdateDTO role, HttpServletRequest request) {
        try {
            isUserSelf(role.getUid(), request);
        } catch (IOException e) {
            logger.error("[Update UserRole]", e);
            return new UserVO<>(400, "UpdateUserRole: 无效操作", e.getMessage());
        }
        logger.info("User: {}", role);
        UserOpBO res = userIO.roleUpdate(
                new URoleDTO(role.getUid(), role.getRoles()));
        esIndexDelete();
        return getStringUserVO(res);
    }

    private UserVO<String> getStringUserVO(UserOpBO res) {
        if (res.getIsSuccess()) {
            logger.info("用户数据更新完成");
            logger.info("Source User: {}", res.getInfo().get("srcUser"));
            return new UserVO<>(200, "OK", "更新完成");
        } else {
            logger.error("用户数据更新异常");
            return new UserVO<>(500, "Exception",
                    res.getInfo().get("Cause").toString());
        }
    }
}
