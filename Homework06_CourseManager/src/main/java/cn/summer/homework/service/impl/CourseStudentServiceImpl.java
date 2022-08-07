package cn.summer.homework.service.impl;

import cn.summer.homework.BO.CourseOpBO;
import cn.summer.homework.PO.StudentCourse;
import cn.summer.homework.Util.OpBOUtil;
import cn.summer.homework.feignClient.CourseClient;
import cn.summer.homework.service.CourseStudentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author VHBin
 * @date 2022/8/6-23:58
 */

@Service
public class CourseStudentServiceImpl implements CourseStudentService {
    @Resource
    private CourseClient client;

    @Override
    public CourseOpBO dropStudent(StudentCourse sc) {
        if (sc == null || sc.getCid() == null || sc.getSid() == null) {
            return OpBOUtil.generateCOB("学生退课传入数据无效");
        }
        return client.dropStudent(sc);
    }

    @Override
    public CourseOpBO addStudent(StudentCourse sc) {
        if (sc == null || sc.getCid() == null || sc.getSid() == null) {
            return OpBOUtil.generateCOB("学生选课传入数据无效");
        }
        return client.addStudent(sc);
    }
}
