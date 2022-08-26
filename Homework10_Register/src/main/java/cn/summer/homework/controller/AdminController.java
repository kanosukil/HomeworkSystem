package cn.summer.homework.controller;

import cn.summer.homework.BO.ESOpBO;
import cn.summer.homework.DTO.CourseInDTO;
import cn.summer.homework.DTO.ElasticSearchDTO;
import cn.summer.homework.DTO.QuestionInDTO;
import cn.summer.homework.DTO.ResultInDTO;
import cn.summer.homework.Util.IndexUtil;
import cn.summer.homework.VO.AdminVO;
import cn.summer.homework.feignClient.AdminClient;
import cn.summer.homework.feignClient.ESCRUDClient;
import cn.summer.homework.service.ElasticSearchDirectExchangeService;
import cn.summer.homework.service.FindService;
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
 * @date 2022/8/7-17:00
 */

@RestController
@RequestMapping("/operation/admin")
public class AdminController {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    @Resource
    private AdminClient admin;
    @Resource
    private FindService find;
    @Resource
    private ESCRUDClient esCUD;
    @Resource
    private ElasticSearchDirectExchangeService es;

    private void esIndexDelete(String index) {
        ElasticSearchDTO dto = new ElasticSearchDTO();
        dto.setOption(4);
        dto.setIndex(index);
        ESOpBO esOpBO = esCUD.indexDelete(dto);
        if (!esOpBO.getIsSuccess()) {
            logger.warn("ad: ES 删除目录{}失败", index);
        }
    }

    private void isAdmin(HttpServletRequest request) throws IOException {
        int userid = Integer.parseInt(request.getAttribute("userid").toString());
        if (!find.user(userid).getRoles().contains("Admin")) {
            throw new IOException("无效操作: 非 Admin");
        }
    }

    /*
        course
     */

    /**
     * 新建课程
     *
     * @param in      tid+course
     * @param request 检测是否为管理员操作
     * @return AdminVO statusCode+message(新建cid)+info(cid值)
     */
    @PostMapping("/c/course")
    public AdminVO createCourse(@RequestBody CourseInDTO in, HttpServletRequest request) {
        logger.info("Administer Operation");
        try {
            isAdmin(request);
            AdminVO course = admin.createCourse(in);
            if (course.getCode() == 200) {
                esIndexDelete(IndexUtil.COURSE);
            }
            return course;
        } catch (IOException io) {
            logger.error("ad:[Create Course]SQL Course 获取异常", io);
            esIndexDelete(IndexUtil.COURSE);
            return new AdminVO(500, "CreateCourse: SQL Course 获取异常", io.toString());
        }
    }

    /**
     * 更新课程信息
     *
     * @param in      tid+course
     * @param request 检测是否为管理员
     * @return AdminVO statusCode+message(course)+info(course对象字符串)
     */
    @PostMapping("/u/course")
    public AdminVO updateCourse(@RequestBody CourseInDTO in, HttpServletRequest request) {
        logger.info("Administer Operation");
        try {
            isAdmin(request);
            AdminVO course = admin.updateCourse(in);
            if (course.getCode() == 200) {
                esIndexDelete(IndexUtil.COURSE);
            }
            return course;
        } catch (IOException io) {
            logger.error("ad:[Update Course]SQL Course 获取异常", io);
            esIndexDelete(IndexUtil.COURSE);
            return new AdminVO(500, "UpdateCourse: SQL Course 获取异常", io.toString());
        }
    }

    /**
     * 删除课程
     *
     * @param in      tid+cid
     * @param request 检测是否为管理员
     * @return AdminVO statusCode+message+info(原课程信息)
     */
    @PostMapping("/d/course")
    public AdminVO deleteCourse(@RequestBody CourseInDTO in, HttpServletRequest request) {
        logger.info("Administer Operation");
        try {
            isAdmin(request);
        } catch (IOException io) {
            logger.error("ad:[Delete Course]", io);
            return new AdminVO(400, "DeleteCourse: 无效操作", io.getMessage());
        }
        AdminVO course = admin.deleteCourse(in);
        if (course.getCode() == 200) {
            esIndexDelete(IndexUtil.QUESTION);
            esIndexDelete(IndexUtil.RESULT);
            esIndexDelete(IndexUtil.COURSE);
        }
        return course;
    }

    /*
        question
     */

    /**
     * 创建问题
     *
     * @param in      tid+cid+question+type
     * @param request 检测是否为管理员
     * @return AdminVO statusCode+message(qid)+info(qid值)
     */
    @PostMapping("/c/question")
    public AdminVO createQuestion(@RequestBody QuestionInDTO in, HttpServletRequest request) {
        logger.info("Administer Operation");
        try {
            isAdmin(request);
            AdminVO question = admin.createQuestion(in);
            if (question.getCode() == 200) {
                esIndexDelete(IndexUtil.QUESTION);
            }
            return question;
        } catch (IOException io) {
            logger.error("ad:[Create Question]SQL Question 获取异常", io);
            return new AdminVO(500, "CreateQuestion: SQL Question 获取异常", io.toString());
        }
    }

    /**
     * 更新问题
     *
     * @param in      tid+qid+question+type
     * @param request 检测是否为管理员
     * @return AdminVO statusCode+message(question)+info(question字符串)
     */
    @PostMapping("/u/question")
    public AdminVO updateQuestion(@RequestBody QuestionInDTO in, HttpServletRequest request) {
        logger.info("Administer Operation");
        try {
            isAdmin(request);
            AdminVO question = admin.updateQuestion(in);
            if (question.getCode() == 200) {
                esIndexDelete(IndexUtil.QUESTION);
            }
            return question;
        } catch (IOException io) {
            logger.error("ad:[Update Question]SQL Question 获取异常", io);
            return new AdminVO(500, "UpdateQuestion: SQL Question 获取异常", io.toString());
        }
    }

    /**
     * 删除问题
     *
     * @param in      tid+qid
     * @param request 检测是否为管理员
     * @return AdminVO statusCode+message(question)+info(question字符串)
     */
    @PostMapping("/d/question")
    public AdminVO deleteQuestion(@RequestBody QuestionInDTO in, HttpServletRequest request) {
        logger.info("Administer Operation");
        try {
            isAdmin(request);
        } catch (IOException io) {
            logger.error("ad:[Delete Question]", io);
            return new AdminVO(400, "DeleteQuestion: 无效操作", io.getMessage());
        }
        AdminVO question = admin.deleteQuestion(in);
        if (question.getCode() == 200) {
            esIndexDelete(IndexUtil.RESULT);
            esIndexDelete(IndexUtil.QUESTION);
        }
        return question;
    }

    /*
        type
     */

    /**
     * 新建问题类型
     *
     * @param in      tid+type
     * @param request 检测是否为管理员
     * @return AdminVO statusCode+message(type)+info(successful)
     */
    @PostMapping("/c/type")
    public AdminVO createType(@RequestBody QuestionInDTO in, HttpServletRequest request) {
        logger.info("Administer Operation");
        try {
            isAdmin(request);
        } catch (IOException io) {
            logger.error("ad:[Create Type]", io);
            return new AdminVO(400, "CreateType: 无效操作", io.getMessage());
        }
        return admin.createType(in);
    }

    /**
     * 删除问题类型
     *
     * @param in      tid+typeid/type
     * @param request 检测是否为管理员
     * @return AdminVO statusCode+message(type)+info(successful)
     */
    @PostMapping("/d/type")
    public AdminVO deleteType(@RequestBody QuestionInDTO in, HttpServletRequest request) {
        logger.info("Administer Operation");
        try {
            isAdmin(request);
        } catch (IOException io) {
            logger.error("ad:[Delete Type]", io);
            return new AdminVO(400, "DeleteType: 无效操作", io.getMessage());
        }
        return admin.deleteType(in);
    }

    /*
        result
     */

    /**
     * 创建回答
     *
     * @param in      qid+cid+sid+result
     * @param request 检测是否为管理员
     * @return AdminVO statusCode+message(rid)+info(rid值)
     */
    @PostMapping("/c/result")
    public AdminVO createResult(@RequestBody ResultInDTO in, HttpServletRequest request) {
        logger.info("Administer Operation");
        try {
            isAdmin(request);
            AdminVO result = admin.createResult(in);
            if (result.getCode() == 200) {
                esIndexDelete(IndexUtil.RESULT);
            }
            return result;
        } catch (IOException io) {
            logger.error("ad:[Create Result]SQL Result 获取异常", io);
            return new AdminVO(500, "CreateResult: SQL Result 获取异常", io.toString());
        }
    }

    /**
     * 更新回答
     *
     * @param in      qid+cid+sid+result
     * @param request 检测是否为管理员
     * @return AdminVO statusCode+message(result)+info(result字符串)
     */
    @PostMapping("/u/result")
    public AdminVO updateResult(@RequestBody ResultInDTO in, HttpServletRequest request) {
        logger.info("Administer Operation");
        try {
            isAdmin(request);
            AdminVO result = admin.updateResult(in);
            if (result.getCode() == 200) {
                esIndexDelete(IndexUtil.RESULT);
            }
            return result;
        } catch (IOException io) {
            logger.error("ad:[Update Result]SQL Result 获取异常", io);
            return new AdminVO(500, "UpdateResult: SQL Result 获取异常", io.toString());
        }
    }

    /**
     * 删除回答
     *
     * @param in      rid+sid
     * @param request 检测是否为管理员
     * @return AdminVO statusCode+message(question)+info(question字符串)
     */
    @PostMapping("/d/result")
    public AdminVO deleteResult(@RequestBody ResultInDTO in, HttpServletRequest request) {
        logger.info("Administer Operation");
        try {
            isAdmin(request);
        } catch (IOException io) {
            logger.error("ad:[Delete Result]", io);
            return new AdminVO(400, "DeleteUpdate: 无效操作", io.getMessage());
        }
        AdminVO result = admin.deleteResult(in);
        if (result.getCode() == 200) {
            esIndexDelete(IndexUtil.RESULT);
        }
        return result;
    }
}
