package cn.jongwong.oauth.controller.rbac;

import cn.jongwong.oauth.service.PermissionGroupPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/permission-groups/{groupId}/permissions")
public class PermissionGroupPermissionController {

    @Autowired
    private PermissionGroupPermissionService permissionGroupPermissionService;

    // 将权限添加到权限组
    @PostMapping("/{permissionId}")
    public String addPermissionToGroup(@PathVariable String groupId, @PathVariable String permissionId) {
        permissionGroupPermissionService.addPermissionToGroup(groupId, permissionId);
        return "Permission added to group successfully";
    }

    // 从权限组中移除权限
    @DeleteMapping("/{permissionId}")
    public String removePermissionFromGroup(@PathVariable String groupId, @PathVariable String permissionId) {
        permissionGroupPermissionService.removePermissionFromGroup(groupId, permissionId);
        return "Permission removed from group successfully";
    }
}
