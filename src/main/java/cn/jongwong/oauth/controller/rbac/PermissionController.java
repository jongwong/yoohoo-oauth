package cn.jongwong.oauth.controller;

import cn.jongwong.oauth.entity.Permission;
import cn.jongwong.oauth.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permissions")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    // 创建权限
    @PostMapping
    public String createPermission(@RequestBody Permission permission) {
        permissionService.createPermission(permission);
        return "Permission created successfully";
    }

    // 更新权限
    @PutMapping("/{id}")
    public String updatePermission(@PathVariable String id, @RequestBody Permission permission) {
        permission.setId(id);
        permissionService.updatePermission(permission);
        return "Permission updated successfully";
    }

    // 删除权限
    @DeleteMapping("/{id}")
    public String deletePermission(@PathVariable String id) {
        permissionService.deletePermission(id);
        return "Permission deleted successfully";
    }

    // 获取所有权限
    @GetMapping
    public List<Permission> getAllPermissions() {
        return permissionService.getAllPermissions();
    }

    // 获取单个权限
    @GetMapping("/{id}")
    public Permission getPermissionById(@PathVariable String id) {
        return permissionService.getById(id);
    }
}
