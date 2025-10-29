package com.anudeep.budgetmanager.service;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import com.anudeep.budgetmanager.dto.ExpenseDTO;
import com.anudeep.budgetmanager.dto.IncomeDTO;
import com.anudeep.budgetmanager.dto.RecentTransactionDTO;
import com.anudeep.budgetmanager.entity.ProfileEntity;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DashBoardService {

    private final IncomeService incomeService;
    private final ExpenseService expenseService;
    private final ProfileService profileService;

    public Map<String, Object> getDashBoard() {
        ProfileEntity profile = profileService.getCurrentProfile();
        Map<String, Object> returnValue = new LinkedHashMap<>();

        // Fetch latest 5 records
        List<IncomeDTO> latestIncomes = incomeService.getLatest5IncomesForCurrentUser();
        List<ExpenseDTO> latestExpenses = expenseService.getLatest5ExpensesForCurrentUser();

        // Combine into unified recent transaction list
        List<RecentTransactionDTO> recentTransactions = Stream.concat(
                latestIncomes.stream().map(income ->
                        RecentTransactionDTO.builder()
                                .id(income.getId())
                                .profileId(profile.getId())
                                .icon(income.getIcon())
                                .name(income.getName())
                                .amount(income.getAmount())
                                .createdAt(income.getCreatedAt())
                                .updatedAt(income.getUpdatedAt())
                                .type("INCOME")
                                .build()
                ),
                latestExpenses.stream().map(expense ->
                        RecentTransactionDTO.builder()
                                .id(expense.getId())
                                .profileId(profile.getId())
                                .icon(expense.getIcon())
                                .name(expense.getName())
                                .amount(expense.getAmount())
                                .createdAt(expense.getCreatedAt())
                                .updatedAt(expense.getUpdatedAt())
                                .type("EXPENSE")
                                .build()
                )
        )
        .sorted(Comparator.comparing(RecentTransactionDTO::getCreatedAt).reversed()) // newest first
        .limit(10)
        .collect(Collectors.toList());

        // Optional: summary cards
        // (Only if your IncomeService and ExpenseService have total calculation methods)
        /*
        BigDecimal totalIncome = incomeService.getTotalIncomeForCurrentUser();
        BigDecimal totalExpense = expenseService.getTotalExpenseForCurrentUser();
        BigDecimal balance = totalIncome.subtract(totalExpense);

        Map<String, Object> summary = Map.of(
            "totalIncome", totalIncome,
            "totalExpense", totalExpense,
            "balance", balance
        );
        returnValue.put("summary", summary);
        */

        // Final dashboard structure
        returnValue.put("profile", profile);
        returnValue.put("latestIncomes", latestIncomes);
        returnValue.put("latestExpenses", latestExpenses);
        returnValue.put("recentTransactions", recentTransactions);

        return returnValue;
    }
}
