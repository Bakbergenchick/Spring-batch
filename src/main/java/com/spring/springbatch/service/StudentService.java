package com.spring.springbatch.service;

import com.spring.springbatch.model.StudentResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class StudentService {
    List<StudentResponse> studentResponseList;

    public List<StudentResponse> studentResponses(){
        RestTemplate restTemplate = new RestTemplate();
        StudentResponse[] studentsArr = restTemplate.getForObject(
                "http://localhost:8081/api/v1/students",
                StudentResponse[].class);

        studentResponseList = new ArrayList<>();

        for (StudentResponse s:
             studentsArr) {
            studentResponseList.add(s);
        }

        return studentResponseList;
    }

    public StudentResponse getStudent(){
        if (studentResponseList == null){
            studentResponses();
        }

        if (studentResponseList != null
                && !studentResponseList.isEmpty()){
            return studentResponseList.remove(0);
        }

        return null;
    }
}
