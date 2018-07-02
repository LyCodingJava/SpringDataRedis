package com.ly;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
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

    private SetOperations<String, Object> opsForSet;

    @Before
    public void setUp() throws Exception {
        opsForSet=redisTemplate.opsForSet();
    }

  /**
   * @MethodName testSet
   * @Description //TODO 
   * @Param []
   * @return void
   * @Author 刘洋
   * @Date 21:08 2018/7/2
   **/
   @Test
    public  void  testSet(){
        System.out.print("1111");
        opsForSet.add("opsForList:trim",1,2,3);
    }
}
