package cn.summer.homework.Util;

/**
 * @author VHBin
 * @date 2022/8/10-23:47
 */
public class PathUtil {
    public static String pathJudge(String origin) {
        if (!origin.endsWith("/")) {
            origin = origin.concat("/");
        } else if (!origin.endsWith("\\")) {
            origin = origin.concat("\\");
        }
        return origin;
    }

    public static String pathCombine(String name, String path) {
        return pathJudge(path).concat(name);
    }

    public static boolean isPath(String path) {
        return path.contains("/") || path.contains("\\");
    }
}
