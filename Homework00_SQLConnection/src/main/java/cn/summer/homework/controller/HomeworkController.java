package cn.summer.homework.controller;

import cn.summer.homework.BO.HomeworkOpBO;
import cn.summer.homework.DTO.NewQuestionDTO;
import cn.summer.homework.DTO.NewResultDTO;
import cn.summer.homework.DTO.QuestionResultDTO;
import cn.summer.homework.DTO.ResultQuestionDTO;
import cn.summer.homework.Entity.Question;
import cn.summer.homework.PO.TeacherQuestion;
import cn.summer.homework.service.HomeworkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
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

    private void setRes_Q(HomeworkOpBO res, Boolean isSuccess, String key, Object value) {
        res.setIsSuccess(isSuccess);
        res.setIsQuestion(true);
        res.setInfo(new HashMap<>(1, 1f) {{
            put(key, value);
        }});
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
        try {
            Integer tid = updateQuestion.getTid();
            Question question = updateQuestion.getQuestion();
            Integer qid = updateQuestion.getId();
            String type = updateQuestion.getType();
            HomeworkOpBO op;
            if (type.equals("")) {
                op = homeworkService.updateQuestion(tid, question);
            } else if (question == null) {
                op = homeworkService.updateQuestion(tid, qid, type);
            } else {
                op = homeworkService.updateQuestion(tid, question, type);
            }
            if (!op.getIsSuccess()) {
                throw new Exception("问题更新失败");
            }
            logger.info("更新问题完成");
            setRes_Q(res, true, "Question",
                    op.getInfo().get("updateQuestion"));
        } catch (Exception ex) {
            logger.error("更新问题异常: {}", ex.getMessage());
            setRes_Q(res, false, "Cause", ex.getCause());
        }
        return res;
    }

    // 删除
    @PostMapping("homework-question-delete")
    public HomeworkOpBO deleteQuestion(@RequestBody TeacherQuestion tq) {
        HomeworkOpBO res = new HomeworkOpBO();
        return res;
    }

    // 老师批改
    @PostMapping("homework-result-correct")
    public HomeworkOpBO correctResult(@RequestBody NewResultDTO correctResult) {
        HomeworkOpBO res = new HomeworkOpBO();
        return res;
    }

    // 类型操作
    @GetMapping("homework-question-types-get")
    public List<String> getAType() {
        return homeworkService.getAllType();
    }

    @PostMapping("homework-question-type-add")
    public Boolean createType(@RequestBody NewQuestionDTO questionType) {
        return homeworkService.createType(questionType.getTid(),
                questionType.getType());
    }

    @PostMapping("homework-question-type-delete")
    public Boolean deleteType(@RequestBody NewQuestionDTO questionType) {
        if (questionType.getId() == 0) {
            return homeworkService.deleteType(questionType.getTid(),
                    questionType.getType());
        } else {
            return homeworkService.deleteType(questionType.getTid(),
                    questionType.getId());
        }
    }

    /*
        学生
     */
    // 查询
    @GetMapping("homework-result-get")
    public List<ResultQuestionDTO> getAResult() {
        return homeworkService.selectAllHK_S();
    }

    @GetMapping("homework-result-get")
    public ResultQuestionDTO getResult(@RequestParam("rid") Integer rid) {
        try {
            return homeworkService.selectHKByRID(rid);
        } catch (Exception ex) {
            return null;
        }
    }

    @GetMapping("homework-result-get-course")
    public List<ResultQuestionDTO> getCourseResult(@RequestParam("cid") Integer cid) {
        try {
            return homeworkService.selectHKByCID_S(cid);
        } catch (Exception ex) {
            return null;
        }
    }

    // 插入
    @PostMapping("homework-result-create")
    public HomeworkOpBO insertResult(@RequestBody NewResultDTO newResult) {
        HomeworkOpBO res = new HomeworkOpBO();
        return res;
    }

    // 更新
    @PostMapping("homework-result-update")
    public HomeworkOpBO updateResult(@RequestBody NewResultDTO deleteResult) {
        HomeworkOpBO res = new HomeworkOpBO();
        return res;
    }

    // 删除
    @PostMapping("homework-result-delete")
    public HomeworkOpBO deleteResult(@RequestBody NewResultDTO updateResult) {
        HomeworkOpBO res = new HomeworkOpBO();
        return res;
    }
}
