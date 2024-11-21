package cn.jongwong.oauth.service.impl;

import cn.jongwong.oauth.entity.Permission;
import cn.jongwong.oauth.mapper.PermissionMapper;
import cn.jongwong.oauth.service.PermissionService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    @Override
    public void createPermission(Permission permission) {
        this.save(permission);
    }

    @Override
    public void updatePermission(Permission permission) {
        this.updateById(permission);
    }

    @Override
    public void deletePermission(String permissionId) {
        this.removeById(permissionId);
    }

    @Override
    public List<Permission> getAllPermissions() {
        return this.list();
    }
}
