package cn.jongwong.oauth.service;

public interface PermissionGroupPermissionService {

    // 将权限添加到权限组
    void addPermissionToGroup(String permissionGroupId, String permissionId);

    // 从权限组中移除权限
    void removePermissionFromGroup(String permissionGroupId, String permissionId);
}
