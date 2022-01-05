package com.xxw.java8.bean;

import com.xxw.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "")
public class Book {
    private String name;
    private int releaseYear;
    private String isbn;

}