package com.lulu.usercenter.once;
import java.util.Date;

import com.lulu.usercenter.Mapper.UserMapper;
import com.lulu.usercenter.Model.domain.User;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;

/**
 * @author lulu
 * @date 2022/11/11 12:56
 */
@Component
public class InsertUsers {

    @Resource
    private UserMapper userMapper;

    /**
     *  批量插入用户
     */

    public void doInsertUsers(){

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 10000000;
        for(int i = 0; i < INSERT_NUM; i++) {
            User user = new User();
            user.setUserName("假人");
            user.setUserAccount("jialulu");
            user.setAvatarUrl("https://tse2-mm.cn.bing.net/th/id/OIP-C._u--TL_D0EQIhFrxzltDFgHaHa?w=215&h=215&c=7&r=0&o=5&dpr=1.3&pid=1.7");
            user.setGender(0);
            user.setUserPassword("12345678");
            user.setEmail("123456");
            user.setTags("12255");
            user.setUserStatus(0);
            user.setPhone("1234578");
            user.setUserRole(0);
            userMapper.insert(user);
        }
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

}
