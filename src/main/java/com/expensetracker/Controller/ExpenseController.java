package com.expensetracker.Controller;

import com.expensetracker.Entities.Expense;
import com.expensetracker.Helpers.ApiResponse;
import com.expensetracker.Services.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @PostMapping("/expenses")
    public ResponseEntity<Expense> addExpense(@Valid @RequestBody Expense expense, @RequestParam int userId, @RequestParam LocalDate date){
        Expense expenseResponse = expenseService.addExpense(expense, userId, date);
        return new ResponseEntity<>(expenseResponse, HttpStatus.CREATED);
    }

    @GetMapping("/expenses/{expId}")
    public ResponseEntity<Expense> getExpenseById(@PathVariable int expId){
        Expense expenseResponse = expenseService.getExpenseById(expId);
        return new ResponseEntity<>(expenseResponse, HttpStatus.FOUND);
    }

    @GetMapping("/expenses")
    public ResponseEntity<List<Expense>> getAllExpenses(){
        List<Expense> expenseLists = expenseService.getAllExpenses();
        return new ResponseEntity<>(expenseLists, HttpStatus.OK);
    }

    @GetMapping("/users/{userId}/expenses")
    public ResponseEntity<List<Expense>> getAllExpensesByUserId(@PathVariable int userId){
        List<Expense> allExpensesByUser = expenseService.getAllExpensesByUserId(userId);
        return new ResponseEntity<>(allExpensesByUser, HttpStatus.OK);
    }

    @PatchMapping("/expenses/{expId}")
    public ResponseEntity<?> updateExpenseById(@RequestBody Map<String, Object> updates, @RequestParam int userId, @RequestParam int budgetId, @PathVariable int expId){
        Expense updatedExpense = expenseService.updateExpenseById(updates, userId, budgetId, expId);
        return new ResponseEntity<>(updatedExpense, HttpStatus.OK);
    }

    @DeleteMapping("/expenses/{expId}")
    public ResponseEntity<ApiResponse> deleteExpenseById(@PathVariable int expId){
        expenseService.deleteExpenseById(expId);
        ApiResponse response = new ApiResponse("Successfully deleted expense by Id : " + expId, true);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
