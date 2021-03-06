package cn.summer.homework.Entity;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author VHBin
 * @date 2022/7/3-14:24
 */

@Data
public class User implements Serializable {
    private Integer id = 0;
    private String name;
    private String password_hash;
    private String head_image;
    private String introduction;
    private String email;
    private Date create_time;
}
