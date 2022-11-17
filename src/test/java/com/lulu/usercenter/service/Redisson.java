package com.lulu.usercenter.service;

import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author lulu
 * @date 2022/11/15 15:09
 */
@SpringBootTest
public class Redisson {

    @Resource
    private RedissonClient redissonClient;


    @Test
    void test(){
        RList<String> rList = redissonClient.getList("redisson_list");
//        rList.add("lulu");
        System.out.println(rList.get(0));
        rList.remove(0);

    }
}
