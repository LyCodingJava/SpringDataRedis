package com.ly;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * ZSet
 * 121212
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration (locations = { "classpath*:applicationContext.xml" })
public class SetOperationsTest {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    private ListOperations<String, Object> opsForList;
}
