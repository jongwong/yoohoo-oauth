package cn.jongwong.server.service;

import cn.jongwong.server.entity.GroupAdminVO;
import cn.jongwong.server.repository.GroupAdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class GroupAdminService {

    private final GroupAdminRepository groupAdminRepository;

    // 获取所有管理员
    public Flux<GroupAdminVO> getAllAdmins() {
        return groupAdminRepository.findAll();
    }

    // 根据管理员ID获取管理员信息
    public Mono<GroupAdminVO> getAdminById(String id) {
        return groupAdminRepository.findById(id);
    }

    // 根据 userId 获取管理员信息
    public Flux<GroupAdminVO> getAdminsByUserId(String userId) {
        return groupAdminRepository.findByUserId(userId);
    }

    // 添加管理员
    public Mono<GroupAdminVO> addAdmin(GroupAdminVO admin) {
        return groupAdminRepository.save(admin);
    }

    // 更新管理员信息
    public Mono<GroupAdminVO> updateAdmin(GroupAdminVO admin) {
        return groupAdminRepository.save(admin);
    }

    // 删除管理员
    public Mono<Void> deleteAdmin(String id) {
        return groupAdminRepository.deleteById(id);
    }
}
