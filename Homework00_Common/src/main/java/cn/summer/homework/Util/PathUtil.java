package cn.summer.homework.Util;

/**
 * @author VHBin
 * @date 2022/8/10-23:47
 */
public class PathUtil {
    public static String pathJudge(String origin) {
        if (!origin.endsWith("/")) {
            origin = origin.concat("/");
        }
        return origin;
    }
}
