package cn.summer.homework.Util;

import cn.summer.homework.BO.CourseOpBO;
import cn.summer.homework.BO.HomeworkOpBO;
import cn.summer.homework.BO.UserOpBO;

import java.util.HashMap;

/**
 * @author VHBin
 * @date 2022/8/7-11:48
 */


public class OpBOUtil {
    public static HomeworkOpBO generateHOBq(String value) {
        HomeworkOpBO res = new HomeworkOpBO();
        res.setIsQuestion(true);
        res.setIsSuccess(false);
        res.setInfo(new HashMap<>(1, 1f) {{
            put("Cause", value);
        }});
        return res;
    }

    public static HomeworkOpBO generateHOBr(String value) {
        HomeworkOpBO res = new HomeworkOpBO();
        res.setIsQuestion(false);
        res.setIsSuccess(false);
        res.setInfo(new HashMap<>(1, 1f) {{
            put("Cause", value);
        }});
        return res;
    }

    public static CourseOpBO generateCOB(String value) {
        CourseOpBO res = new CourseOpBO();
        res.setIsSuccess(false);
        res.setMap(new HashMap<>(1, 1f) {{
            put("Cause", value);
        }});
        return res;
    }

    public static UserOpBO generateUOB(String value) {
        UserOpBO res = new UserOpBO();
        res.setIsSuccess(false);
        res.setInfo(new HashMap<>(1, 1f) {{
            put("Cause", value);
        }});
        return res;
    }
}
