package com.spring.springbatch.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "student")
@Getter
@Setter
@ToString
public class StudentXML {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;

    @XmlElement(name = "fName")
    public String getFirstName() {
        return firstName;
    }
}
