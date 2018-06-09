package com.ly;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ly.entity.Book;
import com.ly.entity.User;
/**
 * ValueOpertions 说明：只能一个键对应一个值.可以存放Map List等json.
 * @author ly
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:applicationContext.xml" })
public class ValueOperationsTest {
	@Autowired
	private StringRedisTemplate stringRedisTemplate;
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	
	private ValueOperations<String, String> opsForValue=null; 
	//操作单表的Object
	private ValueOperations<String, Object> opsForValueToObject=null; 
	private BoundValueOperations<String, String> boundValueOps=null;
	
	
	@Before
	public void setUp() throws Exception {
		opsForValue=stringRedisTemplate.opsForValue();
		opsForValueToObject=redisTemplate.opsForValue();
		boundValueOps=stringRedisTemplate.boundValueOps("boundValueOps:test");
	}
	/**
	 * 指定Key 只对Key进行操作
	 */
	@Test
	public void testBoundValueOps() {
		boundValueOps.set("boundValueOps");
	}
	
	
	/**
	 *V get(Object key);
	 *void set(K key, V value);
	 *
	 */
	@Test
	public void testGetSet() {
		System.out.println(111);
		opsForValue.set("TestValueOperations:test", "test");
		System.err.println(opsForValue.get("TestValueOperations:test"));
		opsForValue.set("TestValueOperations:mkt", "test");
		opsForValue.set("boundValueOps:test", "test");
	}
	/**
	 * 设置过期时间
	 * void set(K key, V value, long timeout, TimeUnit unit);
	 * @throws InterruptedException
	 */
	@Test
	public void testSetTimeOut() throws InterruptedException {
		opsForValue.set("TestValueOperations:test", "test1", 10, TimeUnit.SECONDS);
		Thread.sleep(5000);
        System.out.println(opsForValue.get("TestValueOperations:test"));//liu2
        Thread.sleep(5000);
        System.out.println(opsForValue.get("TestValueOperations:test"));//null
	}
	
	/**
	 * void set(K key, V value, long offset);
	 */
	@Test
	public void testSetOffset() {
		opsForValue.set("TestValueOperations:ly", "liuyang");
		System.err.println(opsForValue.get("TestValueOperations:ly"));
		opsForValue.set("TestValueOperations:ly", "666", 3);
		System.err.println(opsForValue.get("TestValueOperations:ly"));//liu666g
	}
	/**
	 * Boolean setIfAbsent(K key, V value);
	 * key唯一 如果key存在 就返回false 并且无法插入
	 */
	@Test
	public void testSetIfAbsent() {
		System.out.println(opsForValue.setIfAbsent("TestValueOperations:ly1", "tss"));//true
		System.out.println(opsForValue.setIfAbsent("TestValueOperations:ly", "tss"));//false
	}
	/**
	 * 多个键值 封装成map 一起进去
	 * void multiSet(Map<? extends K, ? extends V> map);
	 */
	@Test
	public void testMultiSet() {
		Map<String, String> map=new HashMap<String, String>();
		map.put("MultiSet:test1", "1");
		map.put("MultiSet:test2", "2");
		map.put("MultiSet:test3", "3");
		opsForValue.multiSet(map);
	}
	/**
	 * List<V> multiGet(Collection<K> keys);
	 * out 1,2,3
	 */
	@Test
	public void testMultiGet() {
		List<String> list=new ArrayList<String>();
		list.add("MultiSet:test1");
		list.add("MultiSet:test2");
		list.add("MultiSet:test3");
		for (String string : opsForValue.multiGet(list)) {
			System.out.println(string);
		}
	}
	/**
	 * Boolean multiSetIfAbsent(Map<? extends K, ? extends V> map);
	 * 只要map中的key存在就返回false 也不会插入
	 */
	@Test
	public void testMultiSetIfAbsent() {
		Map<String,String> map=new HashMap<String, String>();
		map.put("MultiSet:test2", "MultiSetIfAbsent2");
		map.put("MultiSet:test3", "MultiSetIfAbsent3");
		map.put("MultiSet:test4", "MultiSetIfAbsent4");
		System.out.println(opsForValue.multiSetIfAbsent(map));//false
		System.out.println(opsForValue.get("MultiSet:test2"));//2
		System.out.println(opsForValue.get("MultiSet:test4"));//null
		
	}
	/**
	 * V getAndSet(K key, V value);
	 */
	@Test
	public void testGetAndSet() {
		//key  TestValueOperations:ly  Value  liu666g
		opsForValue.getAndSet("TestValueOperations:ly", "GetAndSet");
		System.out.println(opsForValue.get("TestValueOperations:ly"));
	}
	/**
	 * Long increment(K key, long delta); Long 加减 增长
	 */
	@Test
	public void testIncrement() {
		opsForValue.set("increment:test1", "1");
		System.out.println(opsForValue.get("increment:test1"));//1
		System.out.println(opsForValue.increment("increment:test1", 2L));//3
	}
	/**
	 * Double increment(K key, double delta);
	 */
	@Test
	public void testIncrementForDouble() {
		opsForValue.set("increment:test2", "1");
		System.out.println(opsForValue.get("increment:test2"));//1
		System.out.println(opsForValue.increment("increment:test2", 3.33));//3
	}
	/**
	 * Integer append(K key, String value);
	 * 如果key已经存在并且是一个字符串，则该命令将该值追加到字符串的末尾。如果键不存在，
	 * 则它被创建并设置为空字符串，因此APPEND在这种特殊情况下将类似于SET。
	 */
	@Test
	public void testAppend() {
		opsForValue.append("append:test1", "test1");
		System.out.println(opsForValue.get("append:test1"));//test1
		opsForValue.set("append:test2", "liu12");
		opsForValue.append("append:test2", "test2");
		System.out.println(opsForValue.get("append:test2"));//liu12test2
	}
	
	
	/**
	 * Long size(K key);
	 */
	@Test
	public void testSize() {
		System.out.println(opsForValue.size("TestValueOperations:ly"));//Value For liu666g  输出7
	}
	/**
	 * Boolean setBit(K key, long offset, boolean value);
	 * 这个偏移有啥用
	 *  //true为1，false为0
 		//对 key 所储存的字符串值，设置或清除指定偏移量上的位(bit)
        //key键对应的值value对应的ASCII码,在offset的位置(从左向右数)变为value

	 */
	@Test
	public void testSetBit() {
		opsForValue.set("TestSetBit:ly", "TEST");
		System.out.println(opsForValue.setBit("TestSetBit:ly", 4, true));//true
		System.out.println(opsForValue.get("TestSetBit:ly"));//lmu15
	        for(int i = 0 ; i<"TEST".length()*8;i++){
	            if(opsForValue.getBit("TestSetBit:ly", i)){
	                System.out.print(1);
	            }else{
	                System.out.print(0);
	            }
	        }
	}
	
	
	
	/**
	 *  Boolean delete(K key)
	 *  Long delete(Collection<K> keys)
	 *  
	 */
	@Test
	public void testDelete() {
		//单条记录
		System.out.println(stringRedisTemplate.delete("TestValueOperations?test"));
		List<String> list=new ArrayList<String>();
		list.add("TestValueOperations:test");
		System.out.println(stringRedisTemplate.delete(list));
	}
	
	/************************************************************操作List Map等Json*********************************************************************/
	public User userConfig() {
		Random rand = new Random();
		 UUID uuid = UUID.randomUUID(); 
		User user=new User();
		user.setId(rand.nextLong()+1);
		user.setName(uuid.toString());
		List<Book>books=new ArrayList<Book>();
		Book book=new Book();
		book.setBookName(uuid.toString());
		book.setId(rand.nextLong()+1);
		books.add(book);
		user.setBooks(books);
		return user;
	}
	
	@Test
	public void setObject() {
		opsForValueToObject.set("opsForValue:setObject", userConfig());
	}
	
	
	
	
	
}
