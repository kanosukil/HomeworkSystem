package cn.summer.homework.controller;

import cn.summer.homework.BO.ESOpBO;
import cn.summer.homework.DTO.CourseInDTO;
import cn.summer.homework.DTO.ElasticSearchDTO;
import cn.summer.homework.DTO.ResultInDTO;
import cn.summer.homework.DTO.UserRoleDTO;
import cn.summer.homework.Util.IndexUtil;
import cn.summer.homework.VO.StudentVO;
import cn.summer.homework.feignClient.ESCRUDClient;
import cn.summer.homework.feignClient.StudentClient;
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
import java.util.Objects;

/**
 * @author VHBin
 * @date 2022/8/7-17:00
 */

@RestController
@RequestMapping("/operation/student")
public class StudentController {
    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);
    @Resource
    private ElasticSearchDirectExchangeService es;
    @Resource
    private StudentClient student;
    @Resource
    private FindService find;
    @Resource
    private ESCRUDClient esCUD;

    private void esIndexDelete(String index) {
        ElasticSearchDTO dto = new ElasticSearchDTO();
        dto.setOption(4);
        dto.setIndex(index);
        ESOpBO esOpBO = esCUD.indexDelete(dto);
        if (!esOpBO.getIsSuccess()) {
            logger.warn("ES 删除目录{}失败", index);
        }
    }

    private void judge(Integer id, HttpServletRequest request)
            throws IOException {
        UserRoleDTO tmp = find.user(
                Integer.parseInt(request.getAttribute("userid").toString()));
        if (!tmp.getRoles().contains("Student")) {
            throw new IOException("无效操作: 非 Student");
        }
        if (!Objects.equals(id, tmp.getUser().getId())) {
            throw new IOException("无效操作: 不允许操作其他用户");
        }
    }

    /**
     * 新建回答
     *
     * @param in      qid+cid+sid+result{content+isFile}
     * @param request 检测是否为本人
     * @return Student code+msg(rid)+info(rid值)
     */
    @PostMapping("/c/result")
    public StudentVO createResult(@RequestBody ResultInDTO in, HttpServletRequest request) {
        try {
            judge(in.getSid(), request);
            StudentVO result = student.createResult(in);
            if (result.getCode() == 200) {
                esIndexDelete(IndexUtil.RESULT);
            }
            return result;
        } catch (IOException io) {
            logger.error("[Create Result]SQL Result 获取异常", io);
            return new StudentVO(500, "CreateResult: SQL Result 获取异常", io.toString());
        }
    }

    /**
     * 更新回答
     *
     * @param in      sid+result(批改后不能修改)
     * @param request 检测是否为本人
     * @return StudentVO code+msg(result)+info(更新后result值)
     */
    @PostMapping("/u/result")
    public StudentVO updateResult(@RequestBody ResultInDTO in, HttpServletRequest request) {
        try {
            judge(in.getSid(), request);
            StudentVO result = student.updateResult(in);
            if (result.getCode() == 200) {
                esIndexDelete(IndexUtil.RESULT);
            }
            return result;
        } catch (IOException io) {
            logger.error("[Update Result]SQL Result 获取异常", io);
            return new StudentVO(500, "UpdateResult: SQL Result 获取异常", io.toString());
        }
    }

    /**
     * 删除回答
     *
     * @param in      sid+rid
     * @param request 检测是否为本人
     * @return StudentVO code+msg(result)+info(删除前result值)
     */
    @PostMapping("/d/result")
    public StudentVO deleteResult(@RequestBody ResultInDTO in, HttpServletRequest request) {
        try {
            judge(in.getSid(), request);
        } catch (IOException e) {
            logger.error("[Delete Result]", e);
            return new StudentVO(400, "DeleteResult: 无效操作", e.getMessage());
        }
        StudentVO result = student.deleteResult(in);
        if (result.getCode() == 200) {
            esIndexDelete(IndexUtil.RESULT);
        }
        return result;
    }

    /**
     * 选修一门课
     *
     * @param in      cid+sid
     * @param request 检测是否为本人
     * @return StudentVO code+msg(course)+info(course值)
     */
    @PostMapping("/ao/add/course")
    public StudentVO addCourse(@RequestBody CourseInDTO in, HttpServletRequest request) {
        try {
            judge(in.getSid(), request);
            StudentVO course = student.addCourse(in);
            if (course.getCode() == 200) {
                esIndexDelete(IndexUtil.COURSE);
            }
            return course;
        } catch (IOException io) {
            logger.error("[Add Course]SQL Course 获取异常", io);
            return new StudentVO(500, "AddCourse: SQL Course 获取异常", io.toString());
        }
    }

    /**
     * 退修一门课
     *
     * @param in      cid+sid
     * @param request 检测是否为本人
     * @return StudentVO code+msg(course)+info(course值)
     */
    @PostMapping("/ao/drop/course")
    public StudentVO dropCourse(@RequestBody CourseInDTO in, HttpServletRequest request) {
        try {
            judge(in.getSid(), request);
            StudentVO course = student.dropCourse(in);
            if (course.getCode() == 200) {
                esIndexDelete(IndexUtil.COURSE);
            }
            return course;
        } catch (IOException io) {
            logger.error("[Drop Course]SQL Course 获取异常", io);
            return new StudentVO(500, "DropCourse: SQL Course 获取异常", io.toString());
        }
    }
}
