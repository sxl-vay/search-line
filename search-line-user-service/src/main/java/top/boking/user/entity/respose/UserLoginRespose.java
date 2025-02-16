package top.boking.user.entity.respose;

import lombok.Data;
import top.boking.base.response.BaseResponse;
import top.boking.user.domain.entity.User;
import top.boking.user.utils.constant.UserRole;

@Data
public class UserLoginRespose extends BaseResponse {
    private String token;
    /**
     * 昵称
     */
    private String nickName;
    /**
     * 头像地址
     */
    private String profilePhotoUrl;
    /**
     * 用户角色
     */
    private UserRole userRole;

    //通过 top.boking.user.domain.entity.User 构建一个UserLoginRespose
    public UserLoginRespose(User user, String token) {
        this.token = token;
        this.nickName = user.getNickName();
        this.profilePhotoUrl = user.getProfilePhotoUrl();
        this.userRole = user.getUserRole();
    }
}
