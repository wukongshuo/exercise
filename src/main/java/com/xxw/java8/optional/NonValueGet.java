package com.xxw.java8.optional;

import com.xxw.java8.bean.Book;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class NonValueGet {
    public static void main(String[] args) {
        List<Book> books = Arrays.asList(new Book("The Fellowship of the Ring", 1954, "0395489318"),
                new Book("The Two Towers", 1954, "0345339711"),
                new Book("The Return of the King", 1955, "0618129111"));
        List<Book> booksWithNull = new ArrayList<>();

        Optional<Book> theX = books.stream().filter(x -> x.getName().startsWith("The R")).findFirst();
        System.out.println(theX.orElse(new Book("theX",2020,"1215483")));

    }
}
