package com.expensetracker.Exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResourceNotFoundException extends RuntimeException{
    private String entityName;
    private String fieldName;
    private Object fieldValue;

    public ResourceNotFoundException(String entityName, String fieldName, Object fieldValue){
        super(String.format("%s not found with %s : %s", entityName, fieldName, fieldValue));
        this.entityName = entityName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
}
