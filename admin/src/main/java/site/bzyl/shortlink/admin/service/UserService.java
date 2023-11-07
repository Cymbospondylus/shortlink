package site.bzyl.shortlink.admin.service;

import org.springframework.stereotype.Service;
import site.bzyl.shortlink.admin.dto.resp.UserRespDTO;

/**
 * 用户业务接口
 */
@Service
public interface UserService {
    UserRespDTO getUserByUsername(String username);
}
