package cn.jongwong.oauth.mapper;

import cn.jongwong.oauth.entity.PermissionGroup;
import cn.jongwong.oauth.entity.Permission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface PermissionGroupMapper extends BaseMapper<PermissionGroup> {

    // 获取权限组的所有权限
    @Select("SELECT p.id, p.name, p.description " +
            "FROM tb_permission p " +
            "JOIN tb_permission_group_permission pgp ON p.id = pgp.permission_id " +
            "WHERE pgp.permission_group_id = #{permissionGroupId}")
    List<Permission> findPermissionsByGroupId(String permissionGroupId);
}
