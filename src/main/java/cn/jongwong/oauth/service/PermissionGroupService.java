package cn.jongwong.oauth.service;

import cn.jongwong.oauth.entity.PermissionGroup;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface PermissionGroupService extends IService<PermissionGroup> {

    // 创建权限组
    void createPermissionGroup(PermissionGroup permissionGroup);

    // 修改权限组
    void updatePermissionGroup(PermissionGroup permissionGroup);

    // 删除权限组
    void deletePermissionGroup(String permissionGroupId);

    // 获取所有权限组
    List<PermissionGroup> getAllPermissionGroups();
}
