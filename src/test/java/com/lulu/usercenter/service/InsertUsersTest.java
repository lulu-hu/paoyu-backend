package com.lulu.usercenter.service;

import com.lulu.usercenter.Model.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author lulu
 * @date 2022/11/11 15:09
 */
@SpringBootTest
public class InsertUsersTest {

    @Resource
    private UserService userService ;

    private ExecutorService executorService = new ThreadPoolExecutor(40, 1000, 10000, TimeUnit.MINUTES, new ArrayBlockingQueue<>(10000));

    private static final String SALT = "YUPI";

    String passWord = "12345678";

    /**
     *  批量插入用户
     */

    @Test
    public void doInsertUsers(){
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        final int INSERT_NUM = 1000;
        List<User> userList = new ArrayList<>();
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
            userList.add(user);
        }
        // 20 秒 10 万条
        userService.saveBatch(userList, 100);
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }


    /**
     * 并发批量插入用户
     */
    @Test
    public void doConcurrencyInsertUsers() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // 分十组
        int batchSize = 50;
        int j = 0;
        List<CompletableFuture<Void>> futureList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            List<User> userList = new ArrayList<>();
            while(true) {
                j++;
                User user = new User();
                user.setUserName("超人");
                user.setUserAccount("jialulu");
                user.setAvatarUrl("https://tse2-mm.cn.bing.net/th/id/OIP-C._u--TL_D0EQIhFrxzltDFgHaHa?w=215&h=215&c=7&r=0&o=5&dpr=1.3&pid=1.7");
                user.setGender(0);
                user.setUserPassword(DigestUtils.md5DigestAsHex((SALT + passWord).getBytes()));
                user.setEmail("123456@qq.com");
                user.setTags("[]");
                user.setUserStatus(0);
                user.setPhone("1234578");
                user.setUserRole(0);
                userList.add(user);
                if (j % batchSize == 0) {
                    break;
                }
            }
            // 异步执行
            CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                System.out.println("threadName: " +Thread.currentThread().getName());
                userService.saveBatch(userList, batchSize);
            }, executorService);
            futureList.add(future);
        }
        CompletableFuture.allOf(futureList.toArray(new CompletableFuture[]{})).join();
        // 20 秒 10 万条
        stopWatch.stop();
        System.out.println(stopWatch.getTotalTimeMillis());
    }

    @Test
    public void test(){

        String res = DigestUtils.md5DigestAsHex((SALT+passWord).getBytes());
        System.out.println(res);
    }


}
