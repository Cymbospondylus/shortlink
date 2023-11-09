package site.bzyl.shortlink.admin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;
import site.bzyl.shortlink.admin.dao.entity.UserDO;
import site.bzyl.shortlink.admin.dto.req.UserRegisterReqDTO;
import site.bzyl.shortlink.admin.dto.req.UserUpdateReqDTO;
import site.bzyl.shortlink.admin.dto.resp.UserRespDTO;

/**
 * 用户业务接口
 */
@Service
public interface UserService extends IService<UserDO> {
    UserRespDTO getUserByUsername(String username);

    Boolean hasUsername(String username);

    void register(UserRegisterReqDTO requestParam);

    void updateUserByUsername(UserUpdateReqDTO requestParam);
}
