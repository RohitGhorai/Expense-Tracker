package com.expensetracker.Services;

import com.expensetracker.Entities.Expense;
import com.expensetracker.Helpers.ApiResponse;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ExpenseService {
    Expense addExpense(Expense expense, int userId, LocalDate date);
    Expense getExpenseById(int expId);
    List<Expense> getAllExpenses();
    List<Expense> getAllExpensesByUserId(int userId);
    Expense updateExpenseById(Map<String, Object> updates, int userId, int budgetId, int expId);
    void deleteExpenseById(int expId);
}
