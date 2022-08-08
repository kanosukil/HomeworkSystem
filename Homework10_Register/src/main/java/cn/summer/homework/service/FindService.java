package cn.summer.homework.service;

import cn.summer.homework.DTO.CourseSTDTO;
import cn.summer.homework.DTO.QuestionResultDTO;
import cn.summer.homework.DTO.ResultQuestionDTO;
import cn.summer.homework.DTO.UserRoleDTO;

import java.io.IOException;
import java.util.List;

/**
 * @author VHBin
 * @date 2022/8/8-18:05
 */

public interface FindService {
    List<UserRoleDTO> users();

    UserRoleDTO user(Integer uid) throws IOException;

    List<CourseSTDTO> courses();

    CourseSTDTO course(Integer cid) throws IOException;

    List<CourseSTDTO> courseBName(String name) throws IOException;

    List<CourseSTDTO> courseBTeacher(Integer tid) throws IOException;

    List<CourseSTDTO> courseBStudent(Integer sid) throws IOException;

    List<QuestionResultDTO> questions();

    QuestionResultDTO question(Integer qid) throws IOException;

    List<QuestionResultDTO> questionBTeacher(Integer tid) throws IOException;

    List<QuestionResultDTO> questionBType(String type) throws IOException;

    List<QuestionResultDTO> questionBCourse(Integer cid) throws IOException;

    List<String> type();

    List<ResultQuestionDTO> results();

    ResultQuestionDTO result(Integer rid) throws IOException;

    List<ResultQuestionDTO> resultBCourse(Integer cid) throws IOException;

    List<ResultQuestionDTO> resultBStudent(Integer sid) throws IOException;
}
