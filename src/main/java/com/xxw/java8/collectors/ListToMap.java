package com.xxw.java8.collectors;



import com.xxw.java8.bean.Book;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ListToMap {
    public static void main(String[] args) {
        List<Book> books = Arrays.asList(new Book("The Fellowship of the Ring", 1954, "0395489318"),
                new Book("The Two Towers", 1954, "0345339711"),
                new Book("The Return of the King", 1955, "0618129111"));
        /**
         * 指定key和value
         */
        Map<String, String> mapWithKeyAndValue = books.stream().collect(Collectors.toMap(Book::getIsbn, Book::getName));
        System.out.println(mapWithKeyAndValue);

        /**
         * key值重复的情况
         * Exception: Duplicate key Book 1954
         */
//        Map<Integer, Book> mapWithDuplicateKey = books.stream().collect(Collectors.toMap(Book::getReleaseYear, Function.identity()));
//        System.out.println(mapWithDuplicateKey);

        /**
         * key值重复，给定重复的时候取值方式
         */
        Map<Integer, Book> mapWithDuplicateKeyAndMergeFunction = books.stream().collect(Collectors.toMap(Book::getReleaseYear, Function.identity(), (key1, key2) -> key1));
        System.out.println(mapWithDuplicateKeyAndMergeFunction);
    }
}


