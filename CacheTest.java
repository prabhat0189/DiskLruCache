package com.inmobi.cache;

import static org.junit.Assert.*;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.Assert;
import org.junit.Test;

public class CacheTest {
	
	@Test
	public void testSimple() {
		DiskLRUCache<String, String> lruCache = new DiskLRUCache<>(3);
		lruCache.put("A", "aa");
		lruCache.put("B", "bb");
		lruCache.put("C", "cc");
		
		Assert.assertEquals(lruCache.get("A"), "aa");
		
	}
	
	@Test
	public void testCacheMiss() {
		DiskLRUCache<String, String> lruCache = new DiskLRUCache<>(3);
		lruCache.put("A", "aa");
		lruCache.put("B", "bb");
		lruCache.put("C", "cc");
		lruCache.put("D", "dd");
		
		Assert.assertEquals(lruCache.get("A"), "aa");
		
	}
	
	@Test
	public void testCacheUpdate() {
		DiskLRUCache<String, String> lruCache = new DiskLRUCache<>(3);
		lruCache.put("A", "aa");
		lruCache.put("B", "bb");
		lruCache.put("C", "cc");
		lruCache.put("D", "dd");
		lruCache.put("A", "aaaa");
		
		Assert.assertEquals(lruCache.get("A"), "aaaa");
	}
	
	@Test
	public void testMultithreaded() throws InterruptedException, ExecutionException {
		DiskLRUCache<String, String> lruCache = new DiskLRUCache<>(3);
		lruCache.put("A", "aa");
		lruCache.put("B", "bb");
		lruCache.put("C", "cc");
	
		Callable<String> c1 = new Callable<String>() {
			@Override
			public String call() throws Exception {
				lruCache.put("A", "aaaa");
				return "aaaa";
			}
		}; 
		
		Callable<String> c2 = new Callable<String>() {
			@Override
			public String call() throws Exception {
				lruCache.put("D", "ddd");
				return "ddd";
			}
		};
		
		ExecutorService executorService = Executors.newFixedThreadPool(2);
		String r1 = executorService.submit(c1).get();
		String r2 = executorService.submit(c2).get();
		executorService.shutdown();
		
		
		Assert.assertEquals(lruCache.get("A"), r1);
		Assert.assertEquals(lruCache.get("D"), r2);
	}


}
