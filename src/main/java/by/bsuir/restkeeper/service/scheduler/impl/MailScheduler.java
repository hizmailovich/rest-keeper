package by.bsuir.restkeeper.service.scheduler.impl;

import by.bsuir.restkeeper.domain.Statistics;
import by.bsuir.restkeeper.domain.User;
import by.bsuir.restkeeper.domain.criteria.UserSearchCriteria;
import by.bsuir.restkeeper.service.MailService;
import by.bsuir.restkeeper.service.StatisticsService;
import by.bsuir.restkeeper.service.UserService;
import by.bsuir.restkeeper.service.builder.impl.CsvBuilder;
import by.bsuir.restkeeper.service.scheduler.Scheduler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class MailScheduler implements Scheduler {

    private final MailService mailService;
    private final StatisticsService statisticsService;
    private final UserService userService;
    private final CsvBuilder builder;

    @Override
    @Scheduled(cron = "59 23 * * *")
    public void schedule() {
        Statistics statistics = this.statisticsService.getStatistics();
        String filename = this.builder.build("report", List.of(statistics));
        UserSearchCriteria criteria = new UserSearchCriteria();
        this.userService.retrieveAllByCriteria(criteria).stream()
                .filter(user -> user.getRole() == User.Role.ROLE_ADMIN)
                .forEach(user -> this.mailService.send(user,
                        "statistics.ftl", "Daily Report", filename, " "));
        log.info("Daily report is sent successfully!");
        new File(filename).delete();
    }

}
