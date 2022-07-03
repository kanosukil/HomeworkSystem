package cn.summer.homework.Entity;

import lombok.Data;

import java.util.Date;

/**
 * @author VHBin
 * @date 2022/7/3-14:34
 */

@Data
public class Question {
    private Integer id;
    private String title;
    private String extension;
    private Integer score;
    private Boolean isFile;
    private String answer;
    private String comment;
    private Date create_time;
}
