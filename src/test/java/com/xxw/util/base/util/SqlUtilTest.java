package com.xxw.util.base.util;

import com.xxw.base.util.SqlUtil;
import com.xxw.java8.bean.Book;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

//@ActiveProfiles("test")
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class SqlUtilTest {



    @Test
    public void getTableName () {
        String tableNameByClass = SqlUtil.getTableNameByClass(Book.class);
        System.out.println(tableNameByClass);
    }

    @Test
    public void transFormTest () {
        Integer[] integers = Stream.iterate(0, n -> n).limit(6).toArray(Integer[]::new);
        Book book1 = Book.builder().name("aa").isbn("te").build();
        Book book2 = Book.builder().name("bb").isbn("df").build();
        Book book3 = Book.builder().name("cc").isbn("fe").build();
        Book book4 = Book.builder().name("dd").isbn("gf").build();
        Book book5 = Book.builder().name("ee").isbn("fgr").build();
        Book book6 = Book.builder().name("ff").isbn("ght").build();
        Book[] bookArr1 = new Book[] {book1,book2,book3};
        Book[] bookArr2 = new Book[] {book4,book5,book6};
        List<Object[]> bookList = new ArrayList<>();
        bookList.add(bookArr1);
        bookList.add(bookArr2);
        SqlUtil.transForm(bookList);
    }
}
