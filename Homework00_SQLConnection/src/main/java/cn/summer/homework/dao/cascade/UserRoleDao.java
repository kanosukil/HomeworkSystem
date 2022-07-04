package cn.summer.homework.dao.cascade;

import cn.summer.homework.PO.UserRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author VHBin
 * @date 2022/7/3-16:25
 */

@Mapper
public interface UserRoleDao {
    List<Integer> selectByUser(Integer uid);

    List<Integer> selectByRole(Integer rid);

    int accurateSelect(UserRole userRole);

    int addNewUser(UserRole userRole);

    int deleteUser(Integer uid);

    int accurateDelete(UserRole userRole);
}
