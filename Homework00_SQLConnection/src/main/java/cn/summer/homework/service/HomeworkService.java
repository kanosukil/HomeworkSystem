package cn.summer.homework.service;

import cn.summer.homework.BO.HomeworkOpBO;
import cn.summer.homework.DTO.QuestionResultDTO;
import cn.summer.homework.DTO.ResultQuestionDTO;
import cn.summer.homework.Entity.Question;
import cn.summer.homework.Entity.Result;
import cn.summer.homework.exception.SQLRWException;

import java.util.List;

/**
 * @author VHBin
 * @date 2022/7/4-15:06
 */
public interface HomeworkService {
    /*
    老师
     */
    List<QuestionResultDTO> selectAllHK_T();

    QuestionResultDTO selectHKByQID(Integer qid)
            throws Exception;
    // 根据问题

    List<QuestionResultDTO> selectHKByTID(Integer tid)
            throws Exception;
    // 根据老师

    List<QuestionResultDTO> selectHKByQuestionType(String type)
            throws Exception;
    // 根据题型

    List<QuestionResultDTO> selectHKByCID_T(Integer cid)
            throws Exception;
    // 根据课程

    HomeworkOpBO createQuestion(Integer tid, Integer cid, Question question, String type)
            throws SQLRWException;
    // 创建问题

    // 删除问题
    HomeworkOpBO deleteQuestion(Integer tid, Integer qid)
            throws SQLRWException;

    // 更新问题内容
    HomeworkOpBO updateQuestion(Integer tid, Question question)
            throws SQLRWException;

    // 更新问题类型
    HomeworkOpBO updateQuestion(Integer tid, Integer qid, String type)
            throws SQLRWException;

    // 更新问题内容+类型
    HomeworkOpBO updateQuestion(Integer tid, Question question, String type)
            throws SQLRWException;

    HomeworkOpBO correctResult(Integer tid, Integer qid, Result result)
            throws SQLRWException;

    List<String> getAllType();

    boolean createType(Integer uid, String typeName);

    boolean deleteType(Integer uid, String typeName);

    boolean deleteType(Integer uid, Integer id);

    /*
    学生
     */
    List<ResultQuestionDTO> selectAllHK_S();

    ResultQuestionDTO selectHKByRID(Integer rid)
            throws Exception;
    // 根据答案

    List<ResultQuestionDTO> selectHKBySID(Integer sid)
            throws Exception;
    // 根据学生

    List<ResultQuestionDTO> selectHKByCID_S(Integer cid)
            throws Exception;
    // 根据课程

    List<ResultQuestionDTO> selectResultByQID(Integer qid)
            throws Exception;

    HomeworkOpBO answerQuestion(Integer sid, Integer cid, Integer qid, Result result)
            throws SQLRWException;
    // 回答问题, 创建答案

    // 删除答案
    HomeworkOpBO deleteResult(Integer sid, Integer rid)
            throws SQLRWException;

    HomeworkOpBO updateResult(Integer sid, Result result)
            throws SQLRWException;
    // 更新答案
}
