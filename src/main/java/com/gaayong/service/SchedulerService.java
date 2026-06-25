package com.gaayong.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Slf4j
@Service
public class SchedulerService {

    private final JdbcTemplate jdbcTemplate;
    private final FixedService fixedService;

    public SchedulerService(JdbcTemplate jdbcTemplate, FixedService fixedService) {
        this.jdbcTemplate = jdbcTemplate;
        this.fixedService = fixedService;
    }

    /**
     * 매일 00:00 — 매월 1일 IS_PAID 리셋, 결제일 자동결제 처리
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void runDailyFixedJobs() {
        try {
            if (LocalDate.now().getDayOfMonth() == 1) {
                fixedService.resetIsPaid();
            }
            fixedService.processAutoPay();
        } catch (Exception e) {
            log.error("[Scheduler] Error processing fixed expense jobs.", e);
        }
    }

    /**
     * 매월 1일 새벽 3시에 실행되어 만료된 remember-me 토큰을 삭제합니다.
     * (토큰 유효기간: 30일)
     */
    @Scheduled(cron = "0 0 3 1 * ?")
    public void cleanupTokens() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        try {
            int delRows = jdbcTemplate.update("DELETE FROM persistent_logins WHERE last_used < ?", thirtyDaysAgo);
            if (delRows > 0) {
                log.info("[Scheduler] Deleted {} expired persistent tokens.", delRows);
            }
        } catch (Exception e) {
            log.error("[Scheduler] Error cleaning up expired persistent tokens.", e);
        }
    }
}
