package cn.summer.homework.Entity;

import lombok.Data;

import java.util.Date;

/**
 * @author VHBin
 * @date 2022/7/3-14:41
 */

@Data
public class Result {
    private Integer id;
    private String content;
    private Boolean isFile;
    private Boolean isCheck;
    private Integer score;
    private String comment;
    private Date create_time;
}
