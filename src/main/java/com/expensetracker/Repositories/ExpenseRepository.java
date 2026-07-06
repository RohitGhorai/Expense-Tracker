package com.expensetracker.Repositories;

import com.expensetracker.Entities.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Integer> {
    List<Expense> findAllByUserId(int userId);

    @Query(value = "select * from expenses e where e.user_id = :userId and e.category = :category and date_format(e.date, '%m-%y') = date_format(:date, '%m-%y')", nativeQuery = true)
    List<Expense> getAllExpensesByUserIdAndCategoryAndDate(
            @Param("userId") int userId,
            @Param("category") String category,
            @Param("date") LocalDate date
    );
}
