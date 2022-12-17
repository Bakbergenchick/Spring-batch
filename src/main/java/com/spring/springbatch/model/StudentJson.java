package com.spring.springbatch.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
@Setter
@ToString
public class StudentJson {
    private Long id;
    @JsonProperty(value = "firstName")
    private String fName;
//    private String lastName;
    private String email;

}
