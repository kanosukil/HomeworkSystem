package cn.summer.homework.Entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author VHBin
 * @date 2022/7/3-14:34
 */

@Data
public class Question implements Serializable {
    private Integer id = 0;
    private String title;
    private String extension;
    private Integer score;
    private Boolean isFile;
    private String answer;
    private String comment;
    private Date create_time;
}
