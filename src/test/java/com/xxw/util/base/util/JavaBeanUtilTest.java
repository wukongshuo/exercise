package com.xxw.util.base.util;

import com.xxw.base.util.JavaBeanUtil;
import com.xxw.java8.bean.Book;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class JavaBeanUtilTest {

    @Test
    public void convertMapToBeanTest () {
        Class<Book> bookClass = Book.class;
        Map<String,Object> map = new HashMap<>();
        map.put("name","dfs");
        map.put("isbn","嚯嚯");
        JavaBeanUtil.convertMapToBean(bookClass,map);
    }
}
