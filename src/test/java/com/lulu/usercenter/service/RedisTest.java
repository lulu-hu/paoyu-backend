package com.lulu.usercenter.service;

import com.lulu.usercenter.Model.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;

/**
 * @author lulu
 * @date 2022/11/12 16:29
 */
@SpringBootTest
public class RedisTest {

    @Resource
    private RedisTemplate redisTemplate;

    @Test
    void test(){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //增
        valueOperations.set("luluString","dog");
        valueOperations.set("luluInt",1);
        valueOperations.set("luluDouble",2.5);
        User user = new User();
        user.setId(1L);
        user.setUserName("lulu");
        valueOperations.set("luluUser",user);
        //查
        Object lulu = valueOperations.get("luluString");
        Assertions.assertTrue("dog".equals((String)lulu));
        lulu =  valueOperations.get("luluInt");
        if (lulu == null) {
            return;
        }
        Assertions.assertTrue(1 == (Integer) lulu);
        lulu = valueOperations.get("luluDouble");
        if (lulu == null) {
            return;
        }
        Assertions.assertTrue(2.5 == (Double) lulu);
        System.out.println(valueOperations.get("luluUser"));

    }
}
