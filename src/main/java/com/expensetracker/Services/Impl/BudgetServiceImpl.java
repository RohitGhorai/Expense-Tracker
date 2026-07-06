package com.expensetracker.Services.Impl;

import com.expensetracker.Entities.Budget;
import com.expensetracker.Entities.Expense;
import com.expensetracker.Entities.User;
import com.expensetracker.Exceptions.ApiException;
import com.expensetracker.Exceptions.ResourceNotFoundException;
import com.expensetracker.Repositories.BudgetRepository;
import com.expensetracker.Repositories.ExpenseRepository;
import com.expensetracker.Services.BudgetService;
import com.expensetracker.Services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class BudgetServiceImpl implements BudgetService {

    @Autowired
    private BudgetRepository budgetRepo;
    @Autowired
    private ExpenseRepository expenseRepo;
    @Autowired
    private UserService userService;

    @Override
    @CacheEvict(value = "budgets", key = "'allBudgets'")
    public Budget addBudget(Budget budget, int userId, LocalDate date) {
        User user = userService.getUserById(userId);
        Budget existingBudget = budgetRepo.getBudgetByUserIdAndCategoryAndMonthAndYear(
                user.getId(),
                budget.getCategory(),
                date.getMonthValue(),
                date.getYear());
        if (existingBudget != null) throw new ApiException("Budget already exists for this category and month.", HttpStatus.CONFLICT);
        budget.setUser(user);
        budget.setRemainLimit(budget.getLimit());
        budget.setMonth(date.getMonthValue());
        budget.setYear(date.getYear());
        return budgetRepo.save(budget);
    }

    @Override
    @Cacheable(value = "budgets", key = "#budgetId")
    public Budget getBudgetById(int budgetId) {
        return budgetRepo.findById(budgetId).orElseThrow(() -> new ResourceNotFoundException("Budget", "Id", budgetId));
    }

    @Override
    @Cacheable(value = "budgets", key = "'allBudgets'")
    public List<Budget> getAllBudgets() {
        return budgetRepo.findAll();
    }

    @Override
    public List<Budget> getAllBudgetsByUserId(int userId) {
        return budgetRepo.findAllByUserId(userId);
    }

    @Override
    @Caching(put = @CachePut(value = "budgets", key = "#budgetId"),
            evict = @CacheEvict(value = "budgets", key = "'allBudgets'"))
    public Budget updateBudgetLimitById(double limit, int userId, int budgetId) {
        Budget budget = getBudgetById(budgetId);
        User user = userService.getUserById(userId);
        if (user.getId() != budget.getUser().getId()) throw new ApiException("UserId mismatch", HttpStatus.BAD_REQUEST);
        double usedLimit = budget.getLimit() - budget.getRemainLimit();
        if (usedLimit <= limit) {
            budget.setLimit(limit);
            budget.setRemainLimit(limit - usedLimit);
        } else throw new ApiException("Limit is less then used-limit", HttpStatus.NOT_ACCEPTABLE);
        return budgetRepo.save(budget);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "budgets", key = "#budgetId"),
            @CacheEvict(value = "budgets", key = "'allBudgets'")
    })
    public void deleteBudgetById(int userId, int budgetId) {
        Budget budget = getBudgetById(budgetId);
        User user = userService.getUserById(userId);
        if (user.getId() != budget.getUser().getId()) throw new ApiException("User Id mismatch", HttpStatus.BAD_REQUEST);
        List<Expense> expenses = expenseRepo.getAllExpensesByUserIdAndCategoryAndDate(budget.getUser().getId(), budget.getCategory(), LocalDate.of(budget.getYear(), budget.getMonth(), 1));
        if (!expenses.isEmpty()) expenseRepo.deleteAll(expenses);
        budgetRepo.delete(budget);
    }
}
