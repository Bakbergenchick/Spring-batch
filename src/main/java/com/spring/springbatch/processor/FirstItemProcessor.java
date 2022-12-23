package com.spring.springbatch.processor;

import com.spring.springbatch.model.StudentCsv;
import com.spring.springbatch.model.StudentJDBC;
import com.spring.springbatch.model.StudentJson;
import com.spring.springbatch.model.StudentXML;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class FirstItemProcessor implements ItemProcessor<StudentCsv, StudentJDBC> {
    @Override
    public StudentJDBC process(StudentCsv item) throws Exception {
        System.out.println("Item processor in action...");
        StudentJDBC studentJDBC = new StudentJDBC();
        studentJDBC.setId(item.getID());
        studentJDBC.setFirstName(item.getFirstName());
        studentJDBC.setLastName(item.getLastName());
        studentJDBC.setEmail(item.getEmail());

        return studentJDBC;
    }
}
