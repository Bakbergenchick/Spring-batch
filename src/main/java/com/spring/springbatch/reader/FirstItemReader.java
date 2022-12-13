package com.spring.springbatch.reader;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class FirstItemReader implements ItemReader<Integer> {

    List<Integer> integerList = Arrays.asList(2, 3, 4, 5, 9);
    int i = 0;

    @Override
    public Integer read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        System.out.println("Item reader in action...");
        Integer item;

        if (i < integerList.size()){
            item = integerList.get(i);
            i++;
            return item;
        }
        i = 0;
        return null;
    }
}
