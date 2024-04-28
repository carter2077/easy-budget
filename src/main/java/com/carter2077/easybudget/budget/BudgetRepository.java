package com.carter2077.easybudget.budget;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    Page<Budget> findByNameContaining(String name, Pageable pageable);
}