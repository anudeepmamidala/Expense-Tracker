package com.anudeep.budgetmanager.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.anudeep.budgetmanager.entity.ExpenseEntity;
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

    @Value("${expense.manager.frontend.url}")
    private String frontendurl;

    @Scheduled(cron = "0 0 22 * * *",zone = "IST")
    public void sendDailyIncomeExpenseRemainder(){

        log.info("Job started:SendDailyIncomeExpenseRemainder");
        List<ProfileEntity> profiles=profileRepository.findAll();
        for(ProfileEntity profile: profiles){
            String body = "Hi"+profile.getFullname()+",<br><br>"
                        +"This is a friendly reminder to log your daily income or expenses.<br>"
                            + "Keeping your finances up-to-date helps you stay on budget!<br><br>"
                            + "Click here to add a new transaction:<br>"
                            + "<a href=\"" + frontendurl + "\">" + frontendurl + "</a><br><br>"
                            + "Best,<br>"
                            + "The Budget Manager Team";

            emailService.sendEmail(profile.getEmail(), "Daily Remainder:Add your income and expenses", body);


        }
    }
    @Scheduled(cron = "0 0 23 * * *", zone = "Asia/Kolkata")
public void sendDailyExpenseSummary() {
    log.info("Job started: sendDailyExpenseSummary");
    List<ProfileEntity> profiles = profileRepository.findAll();

    for (ProfileEntity profile : profiles) {
        List<ExpenseEntity> todayExpenses = expenseService.getTodayExpenses(profile.getId());
        double totalExpenses = todayExpenses.stream()
                .mapToDouble(e -> e.getAmount().doubleValue())
                .sum();

        StringBuilder table = new StringBuilder();
        table.append("<table border='1' cellspacing='0' cellpadding='6' style='border-collapse:collapse;width:100%;'>")
             .append("<tr><th>Category</th><th>Name</th><th>Amount (₹)</th></tr>");

        if (todayExpenses.isEmpty()) {
            table.append("<tr><td colspan='3' style='text-align:center;'>No expenses today</td></tr>");
        } else {
            todayExpenses.forEach(e -> table.append(
                String.format("<tr><td>%s</td><td>%s</td><td>%.2f</td></tr>",
                        e.getCategory(), e.getName(), e.getAmount().doubleValue())
            ));
        }
        table.append("</table>");

        String body = """
            <html>
                <body>
                    <p>Hi <b>%s</b>,</p>
                    <p>Here’s your daily expense summary:</p>
                    %s
                    <p><b>Total:</b> ₹%.2f</p>
                    <p><a href="%s" target="_blank">View Dashboard</a></p>
                    <br>
                    <p>– Budget Manager</p>
                </body>
            </html>
        """.formatted(profile.getFullname(), table, totalExpenses, frontendUrl);

        emailService.sendEmail(profile.getEmail(), "Daily Expense Summary", body);
    }

    log.info("Job finished: sendDailyExpenseSummary");
}

}
