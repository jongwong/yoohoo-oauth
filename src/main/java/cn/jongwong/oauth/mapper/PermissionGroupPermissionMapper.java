package cn.jongwong.oauth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import cn.jongwong.oauth.entity.PermissionGroupPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PermissionGroupPermissionMapper extends BaseMapper<PermissionGroupPermission> {

    // 插入权限与权限组的关系
    void insert(@Param("permissionGroupId") String permissionGroupId, @Param("permissionId") String permissionId);

    // 删除权限与权限组的关系
    void deleteByPermissionGroupIdAndPermissionId(@Param("permissionGroupId") String permissionGroupId, @Param("permissionId") String permissionId);

    // 获取权限组中的所有权限
    List<String> getPermissionsByGroup(@Param("permissionGroupId") String permissionGroupId);

    // 判断权限组中是否包含指定权限
    boolean existsPermissionInGroup(@Param("permissionGroupId") String permissionGroupId, @Param("permissionId") String permissionId);
}
