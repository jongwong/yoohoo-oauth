package cn.jongwong.server.service;

import cn.jongwong.server.entity.ThirdPartyLoginVO;
import cn.jongwong.server.entity.UserVO;
import cn.jongwong.server.repository.ThirdPartyLoginRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ThirdPartyLoginService {

    @Autowired
    ThirdPartyLoginRepository thirdPartyLoginRepository;
    @Autowired
    UserService userService;


    public Mono<ThirdPartyLoginVO> save(ThirdPartyLoginVO data) {
        return thirdPartyLoginRepository.save(data);
    }

    public Mono<ThirdPartyLoginVO> insert(ThirdPartyLoginVO data) {
        return thirdPartyLoginRepository.insert(data);
    }

    public Mono<UserVO> findWithPasswordUserByThirdPartyUserId(String thirdPartyUserId) {
        return thirdPartyLoginRepository.findByThirdPartyUserId(thirdPartyUserId).flatMap(e -> {

            return userService.getUserWithPasswordByIdentifier(e.getUserId());

        });
    }


}
