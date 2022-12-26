package com.spring.springbatch.processor;

import com.spring.springbatch.model.StudentCsv;
import com.spring.springbatch.model.StudentJDBC;
import com.spring.springbatch.model.StudentJson;
import com.spring.springbatch.model.StudentXML;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class FirstItemProcessor implements ItemProcessor<StudentCsv, StudentJson> {
    @Override
    public StudentJson process(StudentCsv item) throws Exception {
        System.out.println("Item processor in action...");

        if (item.getID() == null){
            System.out.println("Processor exception...");
            throw new Exception();
        }

        StudentJson studentJson = new StudentJson();
        studentJson.setId(item.getID());
        studentJson.setFName(item.getFirstName());
        studentJson.setLastName(item.getLastName());
        studentJson.setEmail(item.getEmail());

        return studentJson;
    }
}
