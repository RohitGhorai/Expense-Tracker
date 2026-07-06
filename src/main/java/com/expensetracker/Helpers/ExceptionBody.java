package com.expensetracker.Helpers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ExceptionBody {
    private int errorCode;
    private String errorMessage;
}
