package com.ly;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.ly.entity.Book;
import com.ly.entity.User;
/**
 * 
 * @author LY
 *11
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:applicationContext.xml" })
public class ListOperationsTest {
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	
	private ListOperations<String, Object> opsForList;
	
	@Before
	public void setUp() throws Exception {
		opsForList=redisTemplate.opsForList();
		
	}
	
	public User userConfig() {
		Random rand = new Random();
		 UUID uuid = UUID.randomUUID(); 
		User user=new User();
		user.setId(rand.nextLong()+1);
		user.setName(uuid.toString());
		user.setTime(new Date());
		List<Book>books=new ArrayList<Book>();
		Book book=new Book();
		book.setBookName(uuid.toString());
		book.setId(rand.nextLong()+1);
		books.add(book);
		user.setBooks(books);
		return user;
	}
	
	/**
	 * List<V> range(K key, long start, long end);
	 *  返回列表中指定区间内的元素，区间以偏移量 START 和 END 指定。 其中 0 表示列表的第一个元素， 1 表示列表的第二个元素，以此类推。
	 *   你也可以使用负数下标，以 -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推。 
	 */
	@Test
	public void testRange() {
		opsForList.range("opsForList:leftPush", 0,-1);
	}
	
	
	/**
	 * void trim(K key, long start, long end);
	 * 让列表只保留指定区间内的元素，不在指定区间之内的元素都将被删除
	 * 下标 0 表示列表的第一个元素，以 1 表示列表的第二个元素，以此类推。 
	 * 你也可以使用负数下标，以 -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推。
	 */
	@Test
	public void testTrim() {
		//opsForList.leftPushAll("opsForList:trim","1","2",new Object[] {"3","4"});
		opsForList.trim("opsForList:trim", 0, -3);
	}
	
	/**
	 * Long size(K key); 
	 * 获得制定Key的长度。
	 */
	@Test
	public void testSize() {
		System.out.println(opsForList.size("opsForList:leftPushAllForList"));
	}
	
	/**
	 * Long leftPush(K key, V value); //left 是左面进入 也就是靠前 先进后出
	 * List<V> range(K key, long start, long end); //取得list值。
	 */
	@Test
	public void leftPush() {
		System.out.println(opsForList.leftPush("opsForList:leftPush",userConfig()));//8
		System.out.println(opsForList.leftPush("opsForList:leftPush",userConfig()));//9
		System.out.println(opsForList.leftPush("opsForList:leftPush",userConfig()));//10
		System.out.println(opsForList.range("opsForList:leftPush", 0, -1).size());//10 
		//如果便利list 得到的一定是 10 9 8 的顺序
	}
	/**
	 * Long leftPushAll(K key, V... values); //多个值同 leftPush
	 */
	@Test
	public void leftPushAll() {
		System.out.println(opsForList.leftPushAll("opsForList:leftPushAll",userConfig(),userConfig()));
	}
	/**
	 * Long leftPushAll(K key, Collection<V> values); 存放list
	 */
	@Test
	public void leftPushAllForList() {
		/*List<User> users=new ArrayList<User>();
		for (int i = 0; i < 10; i++) {
			users.add(userConfig());
		}*/
		
		List<Object> objects=new ArrayList<Object>();
		for (int i = 0; i < 10; i++) {
			objects.add(i);
		}
		//opsForList.leftPushAll("opsForList:leftPushAllForList",users);
		opsForList.leftPushAll("opsForList:leftPushAllForList",objects);
		
	}
	
	/**
	 * Long leftPushIfPresent(K key, V value);
	 * 把值加入列表的头部 必须有一个值。没有值无效
	 */
	@Test
	public void testLeftPushIfPresent() {
		System.out.println(opsForList.leftPush("opsForList:leftPushIfPresent", 0));//1
		System.out.println(opsForList.leftPushIfPresent("opsForList:leftPushIfPresent",1));//2
		System.out.println(opsForList.leftPushIfPresent("opsForList:leftPushIfPresent",2));
	}
	/**
	 * Long leftPush(K key, V pivot, V value);
	 * 把值插入到制定元素的前面 
	 * 输入 0 1 50 然后把 2插入到0前面 就是 50 1 2 0
	 */
	@Test
	public void testLeftPush1() {
		/*System.out.println(opsForList.leftPush("opsForList:leftPush1", 0));
		System.out.println(opsForList.leftPush("opsForList:leftPush1", 1));
		System.out.println(opsForList.leftPush("opsForList:leftPush1", 50));
		System.out.println(opsForList.leftPush("opsForList:leftPush1", 0,2));*/
		for (Object object:opsForList.range("opsForList:leftPush1", 0, -1)) {
			System.out.println(object);
		}
		//输出结果 50 1 2 0
	}
	
	/*********************************************************************************************************/
	/**
	 * Long rightPush(K key, V value);
	 * 顺序执行，先进先出
	 */
	@Test
	public void testRightPush() {
		opsForList.rightPush("opsForList:rightPush", 1);
		opsForList.rightPush("opsForList:rightPush", 2);
	}
	/**
	 * Long rightPushAll(K key, V... values);
	 */
	@Test
	public void testRightPushAll() {
		System.out.println(opsForList.rightPushAll("opsForList:testRightPushAll", 1,2,3));//3
		for(Object object:opsForList.range("opsForList:testRightPushAll", 0, -1)) {
			System.out.println(object);
		}
		//输出 123
	}
	/**
	 * ong rightPushAll(K key, Collection<V> values);
	 */
	@Test
	public void testRightPushAllForConlection() {
		List<Object> objects=new ArrayList<Object>();
		for (int i = 0; i < 10; i++) {
			objects.add(i);
		}
		System.out.println(opsForList.rightPushAll("opsForList:rightPushAllForConlection", objects));//10
		for(Object object:opsForList.range("opsForList:rightPushAllForConlection", 0, -1)) {
			System.out.println(object);
		}
		//输出结果 0 1 2 3 4 5 6 7 8 9
	}
	/**
	 * Long rightPushIfPresent(K key, V value);
	 * 必须有一个key 否则无效 然后存在之后继续向后插值
	 */
	@Test
	public void testRightPushIfPresent() {
		System.out.println(opsForList.rightPushIfPresent("opsForList:rightPushIfPresent", 0));//0
		System.out.println(opsForList.rightPush("opsForList:rightPushIfPresent", 0));//1
		System.out.println(opsForList.rightPushIfPresent("opsForList:rightPushIfPresent", 1));//2
		for(Object object:opsForList.range("opsForList:rightPushIfPresent", 0, -1)) {
			System.out.println(object);
		}
		//输出结果 0 1
	}
	/**
	 * Long rightPush(K key, V pivot, V value);
	 * 指定Value后面插入值
	 */
	@Test
	public void testRightPush1() {
		System.out.println(opsForList.rightPush("opsForList:testRightPush1", 0));//1
		System.out.println(opsForList.rightPush("opsForList:testRightPush1", 1));//2
		System.out.println(opsForList.rightPush("opsForList:testRightPush1", 2));//3
		System.out.println(opsForList.rightPush("opsForList:testRightPush1", 0,3));//4
		for(Object object:opsForList.range("opsForList:testRightPush1", 0, -1)) {
			System.out.println(object);
		}
		//输出结果 0 3 1 2
	}
	
	/******************************************************************************************/
	/**
	 * void set(K key, long index, V value);
	 * 当索引参数超出范围，或对一个空列表进行 LSET 时，返回一个错误。
	 */
	@Test
	public void testSetErrorForNoSuchKey () {
		try {
			opsForList.set("opsForList:set", 2, 1);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		//输出结果 ERR no such key
	}
	
	@Test
	public void testSetErrorForIndexOutTofrange () {
		try {
			opsForList.leftPush("opsForList:set", 1);
			opsForList.set("opsForList:set", 2, 1);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		//输出结果 ERR index out of range
	}
	
	/**
	 * 正确的set实例
	 * 通过索引来设置元素的值
	 * void set(K key, long index, V value);
	 */
	@Test
	public void testSerForTrue() {
		redisTemplate.delete("opsForList:set");
		try {
			opsForList.leftPush("opsForList:set", 1);
			opsForList.leftPush("opsForList:set", 2);
			opsForList.leftPush("opsForList:set", 3);
			opsForList.set("opsForList:set", 1, 333);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		for(Object object:opsForList.range("opsForList:set", 0, -1)) {
			System.out.println(object);
		}
		//输出结果3 333 1
	}
	/**
	 * Long remove(K key, long count, Object value);
	 *count > 0 : 从表头开始向表尾搜索，移除与 VALUE 相等的元素，数量为 COUNT 。
	 *count < 0 : 从表尾开始向表头搜索，移除与 VALUE 相等的元素，数量为 COUNT 的绝对值。
	 *count = 0 : 移除表中所有与 VALUE 相等的值。
	 */
	@Test
	public void testRemove() {
		opsForList.rightPush("opsForList:remove", 1);
		opsForList.rightPush("opsForList:remove", 1);
		opsForList.rightPush("opsForList:remove", 2);
		opsForList.rightPush("opsForList:remove", 3);
		opsForList.rightPush("opsForList:remove", 4);
		opsForList.rightPush("opsForList:remove", 4);
		opsForList.rightPush("opsForList:remove", 4);
		opsForList.rightPush("opsForList:remove", 5);
		opsForList.rightPush("opsForList:remove", 5);
		opsForList.rightPush("opsForList:remove", 5);
		opsForList.rightPush("opsForList:remove", 5);
		System.out.println(opsForList.remove("opsForList:remove", 2, 1));//234445555
		System.out.println(opsForList.remove("opsForList:remove", -3, 5));//234445
		System.out.println(opsForList.remove("opsForList:remove", 0, 4));//235
	}
	/**
	 * Redis Lindex 命令用于通过索引获取列表中的元素。你也可以使用负数下标，
	 * 以 -1 表示列表的最后一个元素， -2 表示列表的倒数第二个元素，以此类推。
	 * 从0起。。 
	 */
	@Test
	public void testIndex() {
		opsForList.rightPush("opsForList:index", 1);
		opsForList.rightPush("opsForList:index", 1);
		opsForList.rightPush("opsForList:index", 2);
		opsForList.rightPush("opsForList:index", 3);
		opsForList.rightPush("opsForList:index", 14);
		opsForList.rightPush("opsForList:index", 12);
		opsForList.rightPush("opsForList:index", 4);
		System.out.println(opsForList.index("opsForList:index", -1)); //4
		System.out.println(opsForList.index("opsForList:index", 3)); //3
		System.out.println(opsForList.index("opsForList:index", 5)); //12
	}
	/**
	 * V leftPop(K key);
	 * 命令用于移除并返回列表的第一个元素
	 */
	@Test
	public void testLeftPop() {
		System.out.println(opsForList.leftPop("opsForList:leftPop"));//没找到就会出现Null
		System.out.println(opsForList.rightPush("opsForList:leftPop", "123"));//1
		System.out.println(opsForList.rightPush("opsForList:leftPop","1234"));//2
		System.out.println(opsForList.leftPop("opsForList:leftPop"));//123
		System.out.println(opsForList.range("opsForList:leftPop", 0, -1).get(0));//1234
	}
	/**
	 * V rightPop(K key, long timeout, TimeUnit unit);
	 * @throws InterruptedException 
	 * Redis Blpop 命令移出并获取列表的第一个元素，
	 *  如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。 
	 */
	@Test
	public void testLeftPopForTimeOut() throws InterruptedException {
		System.out.println(opsForList.rightPush("opsForList:leftPopForTimeOut", "123"));//1
		System.out.println(opsForList.rightPush("opsForList:leftPopForTimeOut","1234"));//2
		System.out.println(opsForList.leftPop("opsForList:leftPopForTimeOut",1,TimeUnit.SECONDS));
		Thread.sleep(5000);
		System.out.println(opsForList.size("opsForList:leftPopForTimeOut"));
		Thread.sleep(5000);
		System.out.println(opsForList.size("opsForList:leftPopForTimeOut"));
	}
	/**
	 * V rightPop(K key);
	 * 移除最后一个 并返回结果
	 */
	@Test
	public void testRightPop() {
		System.out.println(opsForList.rightPop("opsForList:rightPop"));//没找到就会出现Null
		System.out.println(opsForList.rightPush("opsForList:rightPop", "123"));//1
		System.out.println(opsForList.rightPush("opsForList:rightPop","1234"));//2
		System.out.println(opsForList.rightPop("opsForList:rightPop"));//1234
		System.out.println(opsForList.range("opsForList:rightPop", 0, -1).get(0));//123
	}
	/**
	 * V rightPop(K key, long timeout, TimeUnit unit);
	 * Redis Blpop 命令移出并获取列表的第一个元素，
	 *  如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止。 
	 */
	@Test
	public void testRightPopForTimeOut() {
		System.out.println(opsForList.rightPop("opsForList:rightPopForTimeOut"));//没找到就会出现Null
		System.out.println(opsForList.rightPush("opsForList:rightPopForTimeOut", "123"));//1
		System.out.println(opsForList.rightPush("opsForList:rightPopForTimeOut","1234"));//2
		System.out.println(opsForList.rightPop("opsForList:rightPopForTimeOut",1,TimeUnit.SECONDS));//1234
		System.out.println(opsForList.range("opsForList:rightPopForTimeOut", 0, -1).get(0));//123
	}
	/**
	 * V rightPopAndLeftPush(K sourceKey, K destinationKey);
	 * 列表中弹出一个值，将弹出的元素插入到另外一个列表中并返回它； 如果列表没有元素会阻塞列表直到等待超时或发现可弹出元素为止
	 */
	@Test
	public void testRightPopAndLeftPush() {
		System.out.println(opsForList.rightPush("opsForList:rightPopAndLeftPush:right", "right1"));//1
		System.out.println(opsForList.rightPush("opsForList:rightPopAndLeftPush:right", "right2"));//2
		System.out.println(opsForList.rightPush("opsForList:rightPopAndLeftPush:right", "right3"));//3
		System.out.println(opsForList.rightPush("opsForList:rightPopAndLeftPush:left","left1"));//1
		System.out.println(opsForList.rightPush("opsForList:rightPopAndLeftPush:left","left2"));//2
		System.out.println(opsForList.rightPush("opsForList:rightPopAndLeftPush:left","left3"));//3
		System.out.println(opsForList.rightPopAndLeftPush("opsForList:rightPopAndLeftPush:left", "opsForList:rightPopAndLeftPush:right"));
		//俩个key中的值 分别为 left :left1 left2 right:right1 right2 right3 left 3
	}
	/**
	 * V rightPopAndLeftPush(K sourceKey, K destinationKey, long timeout, TimeUnit unit);
	 * @throws InterruptedException  
	 * 设置一个过期时间。在这时间中将被阻塞，如果超时未找到。就返回Null.
	 */
	@Test
	public void testRightPopAndLeftPushForTimeOut() throws InterruptedException {
		System.out.println(opsForList.rightPopAndLeftPush("opsForList:rightPopAndLeftPushTime:left", "opsForList:rightPopAndLeftPushTime:right",5,TimeUnit.SECONDS));
		System.out.println("5秒后 opsForList:rightPopAndLeftPushTime:right "+opsForList.size("opsForList:rightPopAndLeftPushTime:right"));
		
		Runnable runnable1 = new Runnable() {
		  public void run() {
              for(int i = 0;i<10;i++){
                  try {
                      Thread.sleep(1000);
                      if(i==7) {
                    	  opsForList.rightPush("opsForList:rightPopAndLeftPushTime:left", "left1");
                    	  return;
                      }else {
                    	  System.out.println("Runnable"+i);
                      }
                  } catch (InterruptedException e) {
                      e.printStackTrace();
                  }

              }
          }
      };
      Thread thread1 = new Thread(runnable1);
      thread1.start();
      System.out.println(opsForList.rightPopAndLeftPush("opsForList:rightPopAndLeftPushTime:left", "opsForList:rightPopAndLeftPushTime:right",10,TimeUnit.SECONDS));
	}
}
