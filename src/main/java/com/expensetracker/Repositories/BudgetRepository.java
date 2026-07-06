package com.expensetracker.Repositories;

import com.expensetracker.Entities.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Integer> {
    List<Budget> findAllByUserId(int userId);

    @Query(value = "select * from budgets b where b.user_id=:userId and b.category=:category and b.month=:month and b.year=:year", nativeQuery = true)
    Budget getBudgetByUserIdAndCategoryAndMonthAndYear(
            @Param("userId") int userId,
            @Param("category") String category,
            @Param("month") int month,
            @Param("year") int year
    );

}
