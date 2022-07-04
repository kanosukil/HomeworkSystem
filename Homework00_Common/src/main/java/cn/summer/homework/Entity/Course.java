package cn.summer.homework.Entity;

import lombok.Data;

import java.util.Date;

/**
 * @author VHBin
 * @date 2022/7/3-14:31
 */

@Data
public class Course {
    private Integer id = 0;
    private String name;
    private Integer student_num;
    private Date create_time;
}
