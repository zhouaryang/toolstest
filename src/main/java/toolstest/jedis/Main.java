package toolstest.jedis;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.sun.org.apache.xml.internal.resolver.helpers.PublicId;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		final JedisConnect jedisConnect = new JedisConnect(); //匿名内部类只能访问final变量
		/*try {
//			jedisConnect.testKey();
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		/*for(int i = 0 ; i< 10 ; i++){
			jedisConnect.testString();
		}*/
		
		
		//在这里用线程池访问下试试，并发连接下
		/*ExecutorService threadPool = Executors.newFixedThreadPool(10);
		for(int i = 0 ; i< 11 ; i++){
			threadPool.execute(new Thread(){
				@Override
				public void run(){
					jedisConnect.testString(); 
				}
			});
		}*/
		
//		jedisConnect.testHash();
		jedisConnect.testSortedSet();
	}
}
