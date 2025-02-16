package top.boking.user.domain.entity;


import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;
import top.boking.base.domain.entity.BaseEntity;
import top.boking.user.utils.constant.UserRole;
import top.boking.user.utils.constant.UserStateEnum;

import java.util.Date;

/**
 * 用户
 *
 * @author shxl
 */
@Setter
@Getter
@TableName("users")
public class User extends BaseEntity {
    /**
     * 昵称
     */
    private String nickName;

    /**
     * 密码
     */
    private String passwordHash;

    /**
     * 状态
     */
    private UserStateEnum state;


    /**
     * 手机号
     */
//    @SensitiveStrategyPhone
    private String telephone;

    /**
     * 最后登录时间
     */
    private Date lastLoginTime;

    /**
     * 头像地址
     */
    private String profilePhotoUrl;




    /**
     * 实名认证
     */
    private Boolean certification;

    /**
     * 真实姓名
     */
//    @TableField(typeHandler = AesEncryptTypeHandler.class)
    private String realName;

    /**
     * 身份证hash
     */
//    @TableField(typeHandler = AesEncryptTypeHandler.class)
    private String idCardNo;

    /**
     * 用户角色
     */
    private UserRole userRole;

    public User register(String telephone, String nickName, String password) {
        this.setTelephone(telephone);
        this.setNickName(nickName);
        this.setPasswordHash(DigestUtil.md5Hex(password));
        this.setState(UserStateEnum.INIT);
        this.setUserRole(UserRole.CUSTOMER);
        return this;
    }

    public User registerAdmin(String telephone, String nickName, String password) {
        this.setTelephone(telephone);
        this.setNickName(nickName);
        this.setPasswordHash(DigestUtil.md5Hex(password));
        this.setState(UserStateEnum.ACTIVE);
        this.setUserRole(UserRole.ADMIN);
        return this;
    }

    public User auth(String realName, String idCard) {
        this.setRealName(realName);
        this.setIdCardNo(idCard);
        this.setCertification(true);
        this.setState(UserStateEnum.AUTH);
        return this;
    }

    public boolean canModifyInfo() {
        return state == UserStateEnum.INIT || state == UserStateEnum.AUTH || state == UserStateEnum.ACTIVE;
    }
}
