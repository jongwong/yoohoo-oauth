package cn.jongwong.oauth.service.impl;

import cn.jongwong.oauth.entity.PermissionGroup;
import cn.jongwong.oauth.mapper.PermissionGroupMapper;
import cn.jongwong.oauth.service.PermissionGroupService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PermissionGroupServiceImpl extends ServiceImpl<PermissionGroupMapper, PermissionGroup> implements PermissionGroupService {

    @Override
    public void createPermissionGroup(PermissionGroup permissionGroup) {
        this.save(permissionGroup);
    }

    @Override
    public void updatePermissionGroup(PermissionGroup permissionGroup) {
        this.updateById(permissionGroup);
    }

    @Override
    public void deletePermissionGroup(String permissionGroupId) {
        this.removeById(permissionGroupId);
    }

    @Override
    public List<PermissionGroup> getAllPermissionGroups() {
        return this.list();
    }
}
