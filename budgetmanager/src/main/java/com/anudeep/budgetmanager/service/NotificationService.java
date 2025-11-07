package com.anudeep.budgetmanager.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.anudeep.budgetmanager.dto.ExpenseDTO;
import com.anudeep.budgetmanager.entity.ProfileEntity;
import com.anudeep.budgetmanager.repository.ProfileRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    
    private final ProfileRepository profileRepository;
    private final EmailService emailService;
    private final ExpenseService expenseService;

    @Value("${expense.manager.frontend.url}")  // ✅ FIXED: Use config
    private String frontendUrl;

    // ✅ FIXED: Changed from "IST" to "Asia/Kolkata" (standard timezone format)
    @Scheduled(cron = "0 0 22 * * *", zone = "Asia/Kolkata")
    public void sendDailyIncomeExpenseReminder() {
        log.info("Job started: SendDailyIncomeExpenseReminder");
        List<ProfileEntity> profiles = profileRepository.findAll();
        for (ProfileEntity profile : profiles) {
            String body = "Hi " + profile.getFullname() + ",<br><br>"
                + "This is a friendly reminder to log your daily income or expenses.<br>"
                + "Keeping your finances up-to-date helps you stay on budget!<br><br>"
                + "Click here to add a new transaction:<br>"
                + "<a href=\"" + frontendUrl + "\">" + frontendUrl + "</a><br><br>"
                + "Best,<br>"
                + "The Budget Manager Team";

            emailService.sendEmail(profile.getEmail(), "Daily Reminder: Add your income and expenses", body);
        }
        log.info("Job finished: SendDailyIncomeExpenseReminder");
    }

    // ✅ FIXED: Timezone format consistent
    @Scheduled(cron = "0 0 22 * * *", zone = "Asia/Kolkata")
    public void sendDailyExpenseSummary() {
        log.info("Job started: sendDailyExpenseSummary");
        List<ProfileEntity> profiles = profileRepository.findAll();
        LocalDate today = LocalDate.now();

        for (ProfileEntity profile : profiles) {
            try {
                List<ExpenseDTO> todaysExpenses = expenseService.getExpensesForUserOnDate(profile.getId(), today);
                
                StringBuilder body = new StringBuilder();
                body.append("Hi ").append(profile.getFullname()).append(",<br><br>");
                body.append("Here is your daily expense summary for <b>").append(today).append("</b>:<br><br>");

                if (todaysExpenses.isEmpty()) {
                    body.append("You have not logged any expenses today.<br><br>");
                    body.append("<b>Total Expenses Today: 0.00</b><br><br>");
                } else {
                    BigDecimal total = BigDecimal.ZERO;
                    
                    String tableStyle = "style='width: 100%; max-width: 400px; border-collapse: collapse; font-family: Arial, sans-serif;'";
                    String thStyle = "style='background-color: #4CAF50; color: white; padding: 12px; text-align: left; border-bottom: 1px solid #ddd;'";
                    String tdStyle = "style='padding: 10px; border-bottom: 1px solid #ddd;'";
                    String tdAmountStyle = "style='padding: 10px; border-bottom: 1px solid #ddd; text-align: right;'";
                    String totalRowStyle = "style='padding: 12px; text-align: right; font-weight: bold; font-size: 1.1em;'";

                    body.append("<table ").append(tableStyle).append(">");
                    body.append("<thead>");
                    body.append("<tr>");
                    body.append("<th ").append(thStyle).append(">Name</th>");
                    body.append("<th ").append(thStyle).append(" align='right'>Amount</th>");
                    body.append("</tr>");
                    body.append("</thead><tbody>");

                    for (ExpenseDTO expense : todaysExpenses) {
                        body.append("<tr>");
                        body.append("<td ").append(tdStyle).append(">").append(expense.getName()).append("</td>");
                        body.append("<td ").append(tdAmountStyle).append(">").append(expense.getAmount().toString()).append("</td>");
                        body.append("</tr>");
                        total = total.add(expense.getAmount());
                    }

                    body.append("</tbody><tfoot>");
                    body.append("<tr>");
                    body.append("<td ").append(totalRowStyle).append(">Total:</td>");
                    body.append("<td ").append(totalRowStyle).append(" align='right'>").append(total.toString()).append("</td>");
                    body.append("</tr>");
                    body.append("</tfoot></table><br>");
                }

                body.append("You can view more details or add new transactions here:<br>");
                body.append("<a href=\"").append(frontendUrl).append("\">").append(frontendUrl).append("</a><br><br>");
                body.append("Best,<br>");
                body.append("The Budget Manager Team");

                emailService.sendEmail(profile.getEmail(), "Your Daily Expense Summary for " + today, body.toString());
                log.info("Successfully sent daily summary to {}", profile.getEmail());
                
            } catch (Exception e) {
                log.error("Failed to send daily summary to {}: {}", profile.getEmail(), e.getMessage());
            }
        }
        log.info("Job finished: sendDailyExpenseSummary");
    }
}