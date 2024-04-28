package com.carter2077.easybudget.budget;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/budget")
public class BudgetController {

    BudgetRepository budgetRepository;

    @Autowired
    BudgetController(BudgetRepository budgetRepository) {
        this.budgetRepository = budgetRepository;
    }

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getBudgets(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            List<Budget> budgets;
            Pageable paging = PageRequest.of(page, size);

            Page<Budget> pagedBudgets;
            if (name == null)
                pagedBudgets = budgetRepository.findAll(paging);
            else
                pagedBudgets = budgetRepository.findByNameContaining(name, paging);

            budgets = pagedBudgets.getContent();

            Map<String, Object> response = new HashMap<>();
            response.put("budgets", budgets);
            response.put("currentPage", pagedBudgets.getNumber());
            response.put("totalItems", pagedBudgets.getTotalElements());
            response.put("totalPages", pagedBudgets.getTotalPages());

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Budget> getBudgetById(@PathVariable("id") long id) {
        Optional<Budget> budgetData = budgetRepository.findById(id);

        if (budgetData.isPresent()) {
            return new ResponseEntity<>(budgetData.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/")
    public ResponseEntity<Budget> createBudget(@RequestBody Budget budget) {
        try {
            Budget _budget = budgetRepository
                    .save(new Budget(budget.getName()));
            return new ResponseEntity<>(_budget, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Budget> updateBudget(@PathVariable("id") long id, @RequestBody Budget budget) {
        Optional<Budget> budgetData = budgetRepository.findById(id);

        if (budgetData.isPresent()) {
            Budget _budget = budgetData.get();
            _budget.setName(budget.getName());
            return new ResponseEntity<>(budgetRepository.save(_budget), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteBudget(@PathVariable("id") long id) {
        try {
            budgetRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}