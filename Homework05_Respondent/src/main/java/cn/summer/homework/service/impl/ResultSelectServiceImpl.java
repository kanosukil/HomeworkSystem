package cn.summer.homework.service.impl;

import cn.summer.homework.DTO.ResultQuestionDTO;
import cn.summer.homework.feignClient.ResultClient;
import cn.summer.homework.service.ResultSelectService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * @author VHBin
 * @date 2022/8/6-23:56
 */

@Service
public class ResultSelectServiceImpl implements ResultSelectService {
    @Resource
    private ResultClient client;

    @Override
    public List<ResultQuestionDTO> getAResult() {
        return client.getAResult();
    }

    @Override
    public ResultQuestionDTO getResult(Integer rid)
            throws IOException {
        if (rid == null) {
            throw new IOException("传入 ResultID 为空");
        }
        return client.getResult(rid);
    }

    @Override
    public List<ResultQuestionDTO> getCourseResult(Integer cid)
            throws IOException {
        if (cid == null) {
            throw new IOException("传入 CourseID 为空");
        }
        return client.getCourseResult(cid);
    }

    @Override
    public List<ResultQuestionDTO> getStudentResult(Integer sid)
            throws IOException {
        if (sid == null) {
            throw new IOException("传入 StudentID 为空");
        }
        return client.getStudentResult(sid);
    }

    @Override
    public List<ResultQuestionDTO> getQuestionResult(Integer qid)
            throws IOException {
        if (qid == null) {
            throw new IOException("传入 QuestionID 为空");
        }
        return client.getQuestionResult(qid);
    }
}
