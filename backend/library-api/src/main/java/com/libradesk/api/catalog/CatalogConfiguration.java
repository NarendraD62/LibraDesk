package com.libradesk.api.catalog;

import com.libradesk.core.CatalogIndex;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CatalogConfiguration {
    @Bean CatalogIndex catalogIndex(){
        CatalogIndex index=new CatalogIndex();
        index.add(new CatalogIndex.Book(1,"The Algorithm Atlas","Mira Sen","9780000000001","Computer Science","English",2023,91,2,"CS-A1"));
        index.add(new CatalogIndex.Book(2,"Gardens of the Monsoon","Asha Raman","9780000000002","Literature","English",2021,76,1,"LIT-B2"));
        index.add(new CatalogIndex.Book(3,"A Brief History of Stars","Dev Kapoor","9780000000003","Science","English",2024,68,3,"SCI-C1"));
        index.add(new CatalogIndex.Book(4,"Designing Small Systems","Nina Das","9780000000004","Computer Science","English",2022,57,0,"CS-A2"));
        index.add(new CatalogIndex.Book(5,"The Quiet Archive","Kabir Iyer","9780000000005","History","English",2020,42,2,"HIS-D1"));
        index.add(new CatalogIndex.Book(6,"Maps of Salt and Wind","Tara Menon","9780000000006","Literature","English",2023,38,1,"LIT-C1"));
        index.add(new CatalogIndex.Book(7,"Practical Machine Learning","Arjun Mehta","9780000000007","Computer Science","English",2024,82,2,"CS-B3"));
        index.add(new CatalogIndex.Book(8,"The Garden of Small Worlds","Tara Bose","9780000000008","Science","English",2019,35,1,"SCI-A2"));
        index.add(new CatalogIndex.Book(9,"Cities Before Dawn","Leela Mukherjee","9780000000009","History","English",2018,31,2,"HIS-A4"));
        index.add(new CatalogIndex.Book(10,"Learning TypeScript","Omar Khan","9780000000010","Computer Science","English",2025,49,3,"CS-B1"));
        index.add(new CatalogIndex.Book(11,"The River Between Us","Ria Dutta","9780000000011","Literature","English",2022,46,0,"LIT-A3"));
        index.add(new CatalogIndex.Book(12,"Patterns in Nature","Ishaan Roy","9780000000012","Science","English",2023,52,1,"SCI-C2"));
        return index;
    }
}
