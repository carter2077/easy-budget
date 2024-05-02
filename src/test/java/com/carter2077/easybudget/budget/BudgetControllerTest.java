package com.carter2077.easybudget.budget;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(BudgetController.class)
public class BudgetControllerTest {
    @MockBean
    private BudgetRepository budgetRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateBudget() throws Exception {
        Budget budget = new Budget("Test Budget");

        mockMvc.perform(post("/api/budget/").contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(budget)))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    void shouldReturnBudget() throws Exception {
        Long id = 0L;
        Budget budget = new Budget("Test Budget");

        when(budgetRepository.findById(id)).thenReturn(Optional.of(budget));
        mockMvc.perform(get("/api/budget/{id}", id)).andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.name").value(budget.getName()))
                .andDo(print());
    }

    @Test
    void shouldReturnNotFoundBudget() throws Exception {
        long id = 0L;

        when(budgetRepository.findById(id)).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/budget/{id}", id))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void shouldReturnListOfBudgets() throws Exception {
        List<Budget> budgets = new ArrayList<>(
                Arrays.asList(new Budget("Test Budget 1"),
                        new Budget("Test Budget 2"),
                        new Budget("Test Budget 3")));
        Pageable pageable = PageRequest.of(0, 10);
        Page<Budget> page = new PageImpl<>(budgets);

        when(budgetRepository.findAll(pageable)).thenReturn(page);
        mockMvc.perform(get("/api/budget/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(budgets.size()))
                .andDo(print());
    }

    @Test
    void shouldReturnListOfBudgetsWithFilter() throws Exception {
        List<Budget> budgets = new ArrayList<>(
                Arrays.asList(new Budget("Test Budget 1"),
                        new Budget("Test Budget 2")));
        Page<Budget> page = new PageImpl<>(budgets);

        String name = "Budget";
        MultiValueMap<String, String> paramsMap = new LinkedMultiValueMap<>();
        paramsMap.add("name", name);

        Pageable paging = PageRequest.of(0, 10);
        when(budgetRepository.findByNameContaining(name, paging)).thenReturn(page);
        mockMvc.perform(get("/api/budget/list").params(paramsMap))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(budgets.size()))
                .andDo(print());

        budgets = Collections.emptyList();
        page = new PageImpl<>(budgets);

        when(budgetRepository.findByNameContaining(name, paging)).thenReturn(page);
        mockMvc.perform(get("/api/budget/list").params(paramsMap))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(0))
                .andDo(print());
    }

    @Test
    void shouldReturnNoContentWhenFilter() throws Exception {
        String name = "Carter";
        MultiValueMap<String, String> paramsMap = new LinkedMultiValueMap<>();
        paramsMap.add("name", name);

        List<Budget> budgets = Collections.emptyList();
        Pageable paging = PageRequest.of(0, 10);
        Page<Budget> page = new PageImpl<>(budgets);

        when(budgetRepository.findByNameContaining(name, paging)).thenReturn(page);
        mockMvc.perform(get("/api/budget/list").params(paramsMap))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(0))
                .andDo(print());
    }

    @Test
    void shouldUpdateBudget() throws Exception {
        long id = 0L;

        Budget budget = new Budget("Test Budget");
        Budget updatedBudget = new Budget("Updated");

        when(budgetRepository.findById(id)).thenReturn(Optional.of(budget));
        when(budgetRepository.save(any(Budget.class))).thenReturn(updatedBudget);

        mockMvc.perform(put("/api/budget/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedBudget)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(updatedBudget.getName()))
                .andDo(print());
    }

    @Test
    void shouldReturnNotFoundUpdateBudget() throws Exception {
        long id = 0L;

        Budget budget = new Budget("Updated");

        when(budgetRepository.findById(id)).thenReturn(Optional.empty());
        when(budgetRepository.save(any(Budget.class))).thenReturn(budget);

        mockMvc.perform(put("/api/budget/{id}", id).contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(budget)))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    void shouldDeleteBudget() throws Exception {
        long id = 0L;

        doNothing().when(budgetRepository).deleteById(id);
        mockMvc.perform(delete("/api/budget/{id}", id))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
