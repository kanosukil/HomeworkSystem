package cn.summer.homework.Entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author VHBin
 * @date 2022/7/3-14:31
 */

@Data
public class Course implements Serializable {
    private Integer id = 0;
    private String name;
    private Integer teacher_num;
    private Integer student_num;
    private Date create_time;
}
