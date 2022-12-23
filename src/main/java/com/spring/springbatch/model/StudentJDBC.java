package com.spring.springbatch.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class StudentJDBC {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;

}
