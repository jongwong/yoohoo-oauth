package cn.jongwong.oauth.service.impl;

import cn.jongwong.oauth.mapper.PermissionGroupPermissionMapper;
import cn.jongwong.oauth.service.PermissionGroupPermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PermissionGroupPermissionServiceImpl implements PermissionGroupPermissionService {

    @Autowired
    private PermissionGroupPermissionMapper permissionGroupPermissionMapper;

    @Override
    public void addPermissionToGroup(String permissionGroupId, String permissionId) {
        permissionGroupPermissionMapper.insert(permissionGroupId, permissionId);
    }

    @Override
    public void removePermissionFromGroup(String permissionGroupId, String permissionId) {
        permissionGroupPermissionMapper.deleteByPermissionGroupIdAndPermissionId(permissionGroupId, permissionId);
    }
}
