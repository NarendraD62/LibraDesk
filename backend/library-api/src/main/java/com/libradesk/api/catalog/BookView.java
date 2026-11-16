package com.libradesk.api.catalog;
public record BookView(long id,String title,String author,String isbn,String category,String language,int year,int available,int popularity,String description,String shelfLocation) {}
