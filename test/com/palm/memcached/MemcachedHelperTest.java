package com.palm.memcached;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MemcachedHelperTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() {
		MemcachedHelper helper = MemcachedHelper.getInstance();
		helper.add("key", "value");
		String value = (String) helper.get("key");
		System.out.println("value:" + value);
	}

	@After
	public void tearDown() throws Exception {

	}

}
