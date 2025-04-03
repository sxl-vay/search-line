package top.boking.user.utils;

import top.boking.user.domain.entity.User;

public class UserContext {

    public static User getCurrentUser() {
        User user = new User();
        user.setId(1L);
        return user;
    }
}
