package com.expensetracker.Services.Impl;

import com.expensetracker.Entities.Budget;
import com.expensetracker.Entities.Expense;
import com.expensetracker.Entities.User;
import com.expensetracker.Exceptions.ApiException;
import com.expensetracker.Exceptions.ResourceNotFoundException;
import com.expensetracker.Repositories.BudgetRepository;
import com.expensetracker.Repositories.ExpenseRepository;
import com.expensetracker.Services.ExpenseService;
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
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    @Autowired
    private ExpenseRepository expenseRepo;
    @Autowired
    private BudgetRepository budgetRepo;
    @Autowired
    private UserService userService;

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "expenses", key = "'allExpenses'"),
            @CacheEvict(value = "budgets", key = "'allBudgets'")
    })
    public Expense addExpense(Expense expense, int userId, LocalDate date) {
        User user = userService.getUserById(userId);
        Budget budget = budgetRepo.getBudgetByUserIdAndCategoryAndMonthAndYear(user.getId(), expense.getCategory(), date.getMonthValue(), date.getYear());
        if (budget == null) throw new ApiException("There is no budget under this category for this date", HttpStatus.BAD_REQUEST);
        expense.setDate(date);
        double newRemainLimit = budget.getRemainLimit() - expense.getAmount();
        if (newRemainLimit >= 0) {
            budget.setRemainLimit(newRemainLimit);
            budgetRepo.save(budget);
            expense.setUser(user);
            return expenseRepo.save(expense);
        } else throw new ApiException("Budget limit exceeded", HttpStatus.BAD_REQUEST);
    }

    @Override
    @Cacheable(value = "expenses", key = "#expId")
    public Expense getExpenseById(int expId) {
        return expenseRepo.findById(expId).orElseThrow(() -> new ResourceNotFoundException("Expense", "Id", expId));
    }

    @Override
    @Cacheable(value = "expenses", key = "'allExpenses'")
    public List<Expense> getAllExpenses() {
        return expenseRepo.findAll();
    }

    @Override
    public List<Expense> getAllExpensesByUserId(int userId) {
        List<Expense> expensesByUser = getAllExpenses().stream().filter(expense -> expense.getUser().getId() == userId).collect(Collectors.toList());
        return expensesByUser;
    }

    @Override
    @Transactional
    @Caching(put = @CachePut(value = "expenses", key = "#expId"),
            evict = {
                    @CacheEvict(value = "expenses", key = "'allExpenses'"),
                    @CacheEvict(value = "budgets", key = "'allBudgets'")
            }
    )
    public Expense updateExpenseById(Map<String, Object> updates, int userId, int budgetId, int expId) {
        if (updates == null) throw new ApiException("NO_CONTENT", HttpStatus.NO_CONTENT);
        Expense oldExpense = getExpenseById(expId);
        Budget budget = budgetRepo.findById(budgetId).orElseThrow(() -> new ResourceNotFoundException("Budget", "Id", budgetId));
        User user = userService.getUserById(userId);
        if (oldExpense.getUser().getId() != user.getId()) throw new ApiException("UserId not matched with Expense", HttpStatus.BAD_REQUEST);
        if (budget.getUser().getId() != user.getId()) throw new ApiException("UserId not matched with Budget", HttpStatus.BAD_REQUEST);
        LocalDate expenseDate = oldExpense.getDate();
        String expenseCategory = oldExpense.getCategory();
        boolean isCheckMonthAndYear = expenseDate.getMonthValue() == budget.getMonth() && expenseDate.getYear() == budget.getYear();
        if (!isCheckMonthAndYear) throw new ApiException("Month and year are mismatched with budget", HttpStatus.BAD_REQUEST);
        boolean isCheckCategory = budget.getCategory().equalsIgnoreCase(expenseCategory);
        if (!isCheckCategory) throw new ApiException("Category mismatch with budget", HttpStatus.BAD_REQUEST);
        updates.forEach((key, value) ->
                {
                    if (key.equals("amount")) {
                        double newLimit = 0;
                        if (oldExpense.getAmount() > (double) updates.get("amount"))
                            newLimit = oldExpense.getAmount() + budget.getRemainLimit() - (double) updates.get("amount");
                        else newLimit = budget.getRemainLimit() - ((double) updates.get("amount") - oldExpense.getAmount());
                        if (newLimit >= 0) {
                            budget.setRemainLimit(newLimit);
                            oldExpense.setAmount((double) updates.get("amount"));
                            budgetRepo.save(budget);
                        } else throw new ApiException("Budget limit exceeded", HttpStatus.BAD_REQUEST);
                    }
                    else if (key.equals("description")) oldExpense.setDescription((String) updates.get("description"));
                    else throw new ApiException(key + " : Invalid field or unable to update", HttpStatus.BAD_REQUEST);
                }
                );
        return expenseRepo.save(oldExpense);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "expenses", key = "#expId"),
            @CacheEvict(value = "expenses", key = "'allExpenses'"),
            @CacheEvict(value = "budgets", key = "'allBudgets'")
        })
    public void deleteExpenseById(int expId) {
        Expense oldExpense = getExpenseById(expId);
        User user = userService.getUserById(oldExpense.getUser().getId());
        LocalDate expenseDate = oldExpense.getDate();
        Budget updatedBudget = budgetRepo.getBudgetByUserIdAndCategoryAndMonthAndYear(user.getId(), oldExpense.getCategory(), expenseDate.getMonthValue(), expenseDate.getYear());
        if (updatedBudget == null) throw new ApiException("Unable to delete expense by Id : " + expId, HttpStatus.CONFLICT);
        double newRemainLimit = updatedBudget.getRemainLimit() + oldExpense.getAmount();
        updatedBudget.setRemainLimit(newRemainLimit);
        budgetRepo.save(updatedBudget);
        expenseRepo.delete(oldExpense);
    }
}
