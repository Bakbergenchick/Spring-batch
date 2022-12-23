package com.spring.springbatch.writer;


import com.spring.springbatch.model.*;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FirstItemWriter implements ItemWriter<StudentJDBC> {
    @Override
    public void write(List<? extends StudentJDBC> list) throws Exception {
        System.out.println("Item writer in action...");
        list.forEach(System.out::println);
    }
}
