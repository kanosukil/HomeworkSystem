package cn.summer.homework.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author VHBin
 * @date 2022/8/9-14:28
 */

public class TypeUtil {
    /**
     * Object 转 List&lt;Object&gt;
     *
     * @param obj 可能是 List 对象的 Object 对象
     * @return List&lt;Object&lt;
     */
    public static List<Object> objToList(Object obj) {
        if (obj instanceof List<?>) {
            return new ArrayList<>((List<?>) obj);
        } else {
            return null;
        }
    }

    /**
     * Object 转 List&lt;classT&gt;
     *
     * @param obj    可能是 List 对象的 Object 对象
     * @param classT 转换的内元素类型
     * @return List&lt;classT&gt;
     */
    public static <T> List<T> objToList(Object obj, Class<T> classT) {
        if (obj instanceof List<?>) {
            ArrayList<T> list = new ArrayList<>();
            for (Object o : (List<?>) obj) {
                list.add(classT.cast(o));
            }
            return list;
        } else {
            return null;
        }
    }
}
