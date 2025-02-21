package top.boking.user.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.boking.base.vo.SlineResult;
import top.boking.user.domain.entity.User;
import top.boking.user.entity.respose.UserLoginRespose;
import top.boking.user.service.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    //用户登录
    @PostMapping("/login")
    public SlineResult<UserLoginRespose> login(String username, String password) {
        User user = userService.findByTelephone(username);
        if (user == null) {
            return SlineResult.error("500","用户名或密码错误");
        }
        return SlineResult.success(new UserLoginRespose(user, user.getId().toString()));
    }
}
