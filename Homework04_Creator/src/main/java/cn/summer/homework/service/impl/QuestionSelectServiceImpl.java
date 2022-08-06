package cn.summer.homework.service.impl;

import cn.summer.homework.DTO.QuestionResultDTO;
import cn.summer.homework.feignClient.QuestionClient;
import cn.summer.homework.service.QuestionSelectService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * @author VHBin
 * @date 2022/8/6-23:42
 */

@Service
public class QuestionSelectServiceImpl implements QuestionSelectService {
    @Resource
    private QuestionClient client;

    @Override
    public List<QuestionResultDTO> getAQuestion() {
        return client.getAQuestion();
    }

    @Override
    public QuestionResultDTO getQuestion(Integer qid)
            throws IOException {
        if (qid == null) {
            throw new IOException("传入的 QuestionID 为空");
        }
        return client.getQuestion(qid);
    }

    @Override
    public List<QuestionResultDTO> getTeacherQuestion(Integer tid)
            throws IOException {
        if (tid == null) {
            throw new IOException("传入的 TeacherID 为空");
        }
        return client.getTeacherQuestion(tid);
    }

    @Override
    public List<QuestionResultDTO> getTypeQuestion(String type)
            throws IOException {
        if (type == null) {
            throw new IOException("传入的 Type 为空");
        }
        return client.getTypeQuestion(type);
    }

    @Override
    public List<QuestionResultDTO> getCourseQuestion(Integer cid)
            throws IOException {
        if (cid == null) {
            throw new IOException("传入的 CourseID 为空");
        }
        return client.getCourseQuestion(cid);
    }

    @Override
    public List<String> getAType() {
        return client.getAType();
    }
}
