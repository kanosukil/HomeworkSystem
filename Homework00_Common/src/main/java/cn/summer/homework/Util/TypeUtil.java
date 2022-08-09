package cn.summer.homework.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author VHBin
 * @date 2022/8/9-14:28
 */

public class TypeUtil {
    public static List<Object> objToList(Object obj) {
        if (obj instanceof List<?>) {
            return new ArrayList<>((List<?>) obj);
        } else {
            return null;
        }
    }

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
