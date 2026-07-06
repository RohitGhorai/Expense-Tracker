package com.expensetracker.Controller;

import com.expensetracker.Entities.Budget;
import com.expensetracker.Helpers.ApiResponse;
import com.expensetracker.Services.BudgetService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/")
public class BudgetController {

    @Autowired
    private BudgetService budgetService;

    @PostMapping("/budgets")
    public ResponseEntity<Budget> addBudget(@Valid @RequestBody Budget budget, @RequestParam int userId, @RequestParam LocalDate date){
        Budget saved = budgetService.addBudget(budget, userId, date);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping("/budgets/{budgetId}")
    public ResponseEntity<Budget> getBudgetById(@PathVariable int budgetId){
        Budget budget = budgetService.getBudgetById(budgetId);
        return new ResponseEntity<>(budget, HttpStatus.FOUND);
    }

    @GetMapping("/budgets")
    public ResponseEntity<List<Budget>> getAllBudgets(){
        List<Budget> budgets = budgetService.getAllBudgets();
        return new ResponseEntity<>(budgets, HttpStatus.OK);
    }

    @GetMapping("/users/{userId}/budgets")
    public ResponseEntity<List<Budget>> getAllBudgetsByUserId(@PathVariable int userId){
        List<Budget> budgets = budgetService.getAllBudgetsByUserId(userId);
        return new ResponseEntity<>(budgets, HttpStatus.OK);
    }

    @PatchMapping("/budgets/{budgetId}")
    public ResponseEntity<Budget> updateBudgetLimitById(@RequestParam double limit, @RequestParam int userId, @PathVariable int budgetId){
        Budget updated = budgetService.updateBudgetLimitById(limit, userId, budgetId);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("/budgets/{budgetId}")
    public ResponseEntity<ApiResponse> deleteBudgetById(@RequestParam int userId, @PathVariable int budgetId){
        budgetService.deleteBudgetById(userId, budgetId);
        ApiResponse response = new ApiResponse("Successfully deleted budget by Id : " + budgetId, true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}