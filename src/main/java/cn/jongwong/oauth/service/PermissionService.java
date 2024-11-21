package cn.jongwong.oauth.service;

import cn.jongwong.oauth.entity.Permission;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface PermissionService extends IService<Permission> {

    // 创建权限
    void createPermission(Permission permission);

    // 修改权限
    void updatePermission(Permission permission);

    // 删除权限
    void deletePermission(String permissionId);

    // 获取所有权限
    List<Permission> getAllPermissions();
}
