package toolstest.jedis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import redis.clients.jedis.Jedis;
import sun.reflect.generics.tree.VoidDescriptor;

public class JedisConnect {
	
	public JedisConnect(){ //构造函数中给池子中添加10个链接
		for(int i = 0; i < REDIS_CONN_MAX; i++){
			jedisPool.add(setup());
		}
	}
	public Jedis setup(){
		Jedis jedis= new Jedis("9.134.45.54",6380);
		jedis.auth("KqZQC*3586DF");
		return jedis;
	}
	//在这里实现池化管理
	List<Jedis> jedisPool = new ArrayList<Jedis>(); //池子
	private static final int  REDIS_CONN_MAX = 10;
	
	//创建一个连接池 ,线程安全型
	public synchronized Jedis getPoolConn(){
		System.out.println("jedisPoolSize:" + jedisPool.size());
		if(!jedisPool.isEmpty()){
			if(jedisPool.size() > REDIS_CONN_MAX){
				return null; 
			}
			return jedisPool.remove(0); //从池子中取出，用完后还需要还回来
		}
		return null;
	}
	//用完之后从池中移除
	public synchronized void removePoolConn(Jedis jedis){
		//还回来，方便下次使用
		jedisPool.add(jedis);
	}
	
	public void testKey()  throws InterruptedException{
		Jedis jedis= new Jedis("9.134.45.54",6380);
		jedis.auth("KqZQC*3586DF");
		System.out.println(jedis.exists("ping"));
	    System.out.println("清空数据："+jedis.flushDB());
	    System.out.println("判断某个键是否存在："+jedis.exists("username"));
	    System.out.println("新增<'username','xmr'>的键值对："+jedis.set("username", "xmr"));
	    System.out.println(jedis.exists("username"));
	    System.out.println("新增<'password','password'>的键值对："+jedis.set("password", "123"));
	    System.out.print("系统中所有的键如下：");
	    Set<String> keys = jedis.keys("*");
	    System.out.println(keys);
	    System.out.println("删除键password:"+jedis.del("password"));
	    System.out.println("判断键password是否存在："+jedis.exists("password"));
	    System.out.println("设置键username的过期时间为5s:"+jedis.expire("username", 8));
	    TimeUnit.SECONDS.sleep(2);
	    System.out.println("查看键username的剩余生存时间："+jedis.ttl("username"));
	    System.out.println("移除键username的生存时间："+jedis.persist("username"));
	    System.out.println("查看键username的剩余生存时间："+jedis.ttl("username"));
	    System.out.println("查看键username所存储的值的类型："+jedis.type("username"));

	}
	public void testString(){
		Jedis jedis = getPoolConn();
		if(!jedis.exists("username")){
			jedis.set("username", "yz");
		}
		System.out.println(jedis.get("username") + " " + Thread.currentThread().getName() + "running");
		System.out.println(jedis.expire("username", 3));
		System.out.println("查看键username的剩余生存时间："+jedis.ttl("username"));
		
		System.out.println(jedis.setnx("key1", "value1"));
		System.out.println(jedis.setex("key2", 3,"value2" ));
		System.out.println("get key" + jedis.get("key1"));
		System.out.println("delete key" + jedis.del("key1"));
		System.out.println("modify key1" + jedis.set("key2","yz" ));
		
		//返回pool
		removePoolConn(jedis);
	}
	
	public void testHash(){
		Jedis jedis = getPoolConn();
		jedis.flushDB();//delete all keys
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("key1", "value1");
		map.put("key2", "value2");
		map.put("key3", "value3");
		map.put("key4", "value4");
		jedis.hmset("hash", map);
		jedis.hset("hash", "key5", "value5");
		System.out.println("hash的所有键值对："+jedis.hgetAll("hash"));//return Map<k,v>
		System.out.println("hash的所有键为："+ jedis.hkeys("hash"));//return Set<String>
		System.out.println("hash的所有值为："+ jedis.hvals("hash"));//return List<String>
		
	}
	
	public void testList(){
		Jedis jedis = getPoolConn();
		jedis.flushDB();
		//添加list
		jedis.lpush("lists","ArrayList","Vector","HashMap","WeakHashMap","LinkedHashMap");
		jedis.lpush("lists","HashSet");
		System.out.println("Lists的全部内容："+ jedis.lrange("lists",0 ,-1 ));//-1代表倒数第一，-2代表倒数第二个
		System.out.println("lists区间0-3：" + jedis.lrange("lists",0,3));
		// 删除列表指定的值 ，第二个参数为删除的个数（有重复时），后add进去的值先被删，类似于出栈
		System.out.println("删除指定元素个数：" + jedis.lrem("lists", 0, "HashMap"));
		System.out.println("Lists的全部内容："+ jedis.lrange("lists",0 ,-1 ));
		System.out.println("lists列表出栈（左端）：" + jedis.lpop("lists")); //会出去
		//list可以排序
		jedis.lpush("sortedList", "3", "6", "2", "0", "7", "4");
	    System.out.println("sortedList排序前：" + jedis.lrange("sortedList", 0, -1));
	    System.out.println(jedis.sort("sortedList"));
	    System.out.println("sortedList排序后：" + jedis.lrange("sortedList", 0, -1));
	    
	}
	public void testSet(){
		Jedis jedis = getPoolConn();
		jedis.flushDB();
		
		System.out.println(jedis.sadd("eleSet", "e1", "e2", "e4", "e3", "e0", "e8", "e7", "e5"));
	    System.out.println(jedis.sadd("eleSet", "e6"));
	    System.out.println("eleSet的所有元素为：" + jedis.smembers("eleSet"));
	    System.out.println("删除一个元素e0：" + jedis.srem("eleSet", "e0"));
	    System.out.println("eleSet的所有元素为：" + jedis.smembers("eleSet"));
	    System.out.println("删除两个元素e7和e6：" + jedis.srem("eleSet", "e7", "e6"));
	    System.out.println("eleSet的所有元素为：" + jedis.smembers("eleSet"));
	    System.out.println("随机的移除集合中的一个元素：" + jedis.spop("eleSet"));
	    System.out.println("eleSet的所有元素为：" + jedis.smembers("eleSet"));
	    System.out.println("eleSet中包含元素的个数：" + jedis.scard("eleSet"));
	    System.out.println("e1是否在eleSet中：" + jedis.sismember("eleSet", "e1"));
	}
	public void testSortedSet(){
		Jedis jedis = getPoolConn();
		jedis.flushDB();
		Map<String, Double> map = new HashMap<String,Double>();
		map.put("key2",1.5);
		map.put("key3",1.6);
		map.put("key4",1.9);
		System.out.println(jedis.zadd("zset",3,"key1"));
		System.out.println(jedis.zadd("zset",map));
		System.out.println("zset中的所有元素："+jedis.zrangeByScore("zset", 0,100));
//		System.out.println("zset所有元素：倒排："+ jedis.rangebyscore);
	    System.out.println("zset中key2的分值："+jedis.zscore("zset", "key2"));
	    System.out.println("zset中key2的排名："+jedis.zrank("zset", "key2"));
	    System.out.println("删除zset中的元素key3："+jedis.zrem("zset", "key3"));
	    System.out.println("zset中的所有元素："+jedis.zrange("zset", 0, -1));
	    System.out.println("zset中元素的个数："+jedis.zcard("zset"));
	    System.out.println("zset中分值在1-4之间的元素的个数："+jedis.zcount("zset", 1, 4));
	    System.out.println("key2的分值加上5："+jedis.zincrby("zset", 5, "key2"));
	    System.out.println("key3的分值加上4："+jedis.zincrby("zset", 4, "key3"));
	    System.out.println("zset中的所有元素："+jedis.zrange("zset", 0, -1));
	}
}
