package com.expensetracker.Services;

import com.expensetracker.Entities.Budget;

import java.time.LocalDate;
import java.util.List;

public interface BudgetService {
    Budget addBudget(Budget budget, int userId, LocalDate date);
    Budget getBudgetById(int budgetId);
    List<Budget> getAllBudgets();
    List<Budget> getAllBudgetsByUserId(int userId);
    Budget updateBudgetLimitById(double limit, int userId, int budgetId);
    void deleteBudgetById(int userId, int budgetId);
}
