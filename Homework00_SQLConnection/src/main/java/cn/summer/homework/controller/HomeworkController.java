package cn.summer.homework.controller;

import cn.summer.homework.BO.HomeworkOpBO;
import cn.summer.homework.DTO.NewQuestionDTO;
import cn.summer.homework.DTO.QuestionResultDTO;
import cn.summer.homework.DTO.ResultQuestionDTO;
import cn.summer.homework.PO.TeacherQuestion;
import cn.summer.homework.service.HomeworkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author VHBin
 * @date 2022/7/13-18:37
 */

@RefreshScope
@RestController
@RequestMapping("homework")
public class HomeworkController {
    private static final Logger logger = LoggerFactory.getLogger(HomeworkController.class);
    @Resource
    private HomeworkService homeworkService;

    /*
        老师
     */
    // 查询
    @GetMapping("homework-questions-get")
    public List<QuestionResultDTO> getAQuestion() {
        return homeworkService.selectAllHK_T();
    }

    @GetMapping("homework-question-get")
    public QuestionResultDTO getQuestion(@RequestParam("qid") Integer qid) {
        try {
            return homeworkService.selectHKByQID(qid);
        } catch (Exception ex) {
            logger.error("指定问题查询异常: {}", ex.getMessage());
            return null;
        }
    }

    @GetMapping("homework-questions-get-tid")
    public List<QuestionResultDTO> getTeacherQuestion(@RequestParam("tid") Integer tid) {
        try {
            return homeworkService.selectHKByTID(tid);
        } catch (Exception ex) {
            logger.error("查询指定教师教授的问题异常: {}", ex.getMessage());
            return null;
        }
    }

    @GetMapping("homework-question-get-type")
    public List<QuestionResultDTO> getTypeQuestion(@RequestParam("type") String type) {
        try {
            return homeworkService.selectHKByQuestionType(type);
        } catch (Exception ex) {
            logger.error("查询指定类型问题异常: {}", ex.getMessage());
            return null;
        }
    }

    @GetMapping("homework-question-get-course")
    public List<QuestionResultDTO> getCourseQuestion(@RequestParam("cid") Integer cid) {
        try {
            return homeworkService.selectHKByCID_T(cid);
        } catch (Exception ex) {
            logger.error("查询指定课程的问题异常: {}", ex.getMessage());
            return null;
        }
    }

    // 插入
    @PostMapping("homework-question-create")
    public HomeworkOpBO createNewQuestion(@RequestBody NewQuestionDTO newQuestion) {
        HomeworkOpBO res = new HomeworkOpBO();

        return res;
    }

    // 更新
    @PostMapping("homework-question-update")
    public HomeworkOpBO updateQuestion(@RequestBody NewQuestionDTO updateQuestion) {
        HomeworkOpBO res = new HomeworkOpBO();

        return res;
    }


    // 删除
    @PostMapping("homework-question-delete")
    public HomeworkOpBO deleteQuestion(@RequestBody TeacherQuestion tq) {
        HomeworkOpBO res = new HomeworkOpBO();
        return res;
    }

    /*
        学生
     */
    // 查询
    @GetMapping("homework-result-get")
    public List<ResultQuestionDTO> getAResult() {
        return homeworkService.selectAllHK_S();
    }

    // 插入

    // 更新

    // 删除

}
