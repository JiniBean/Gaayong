package com.gaayong.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
@Service
public class SchedulerService {

    private final JdbcTemplate jdbcTemplate;

    public SchedulerService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 매일 밤 10시에 실행되어 만료된 remember-me 토큰을 삭제합니다.
     * (토큰 유효기간: 30일)
     */
    @Scheduled(cron = "0 0 22 * * ?")
    public void cleanupExpiredPersistentTokens() {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        try {
            int deletedRows = jdbcTemplate.update("DELETE FROM persistent_logins WHERE last_used < ?", thirtyDaysAgo);
            if (deletedRows > 0) {
                log.info("[Scheduler] Deleted {} expired persistent tokens.", deletedRows);
            }
        } catch (Exception e) {
            log.error("[Scheduler] Error cleaning up expired persistent tokens.", e);
        }
    }
}
