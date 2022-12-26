package com.spring.springbatch.processor;

import com.spring.springbatch.entity.postgre.Student;
import com.spring.springbatch.model.StudentCsv;
import com.spring.springbatch.model.StudentJDBC;
import com.spring.springbatch.model.StudentJson;
import com.spring.springbatch.model.StudentXML;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class FirstItemProcessor implements ItemProcessor<Student, com.spring.springbatch.entity.mysql.Student> {
    @Override
    public com.spring.springbatch.entity.mysql.Student process(Student item) throws Exception {
        com.spring.springbatch.entity.mysql.Student student =
                new com.spring.springbatch.entity.mysql.Student();
        System.out.println(item.getId());

        student.setId(item.getId());
        student.setFirstName(item.getFirstName());
        student.setLastName(item.getLastName());
        student.setEmail(item.getEmail());
        student.setDepId(item.getDepId());
        student.setIsActive(item.getIsActive() != null ?
                Boolean.valueOf(item.getIsActive()) : false);

        return student;
    }
}
