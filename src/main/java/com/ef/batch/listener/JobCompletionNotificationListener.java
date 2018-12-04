package com.ef.batch.listener;

import com.ef.model.CLIParameters;
import com.ef.model.Duration;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

    private static final Logger log = LoggerFactory.getLogger(JobCompletionNotificationListener.class);

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    private CLIParameters cliParameters;

    @Autowired
    public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<String> doBlockIPs(String comment, Date startDate, Duration duration, int threshold) {
        Date endDate = duration == Duration.HOURLY ? DateUtils.addHours(startDate, 1) : DateUtils.addDays(startDate, 1);
        Long blockingBatchId = System.currentTimeMillis();
        jdbcTemplate.update("INSERT INTO blocked_ips (IP, comments, blocking_batch_id) " +
                "  SELECT DISTINCT IP, ?, ? FROM access_logs " +
                "  WHERE date BETWEEN ? AND ?" +
                "  GROUP BY IP " +
                "  HAVING COUNT(*) > ?", new Object[]{comment, blockingBatchId, startDate, endDate, threshold});

        return jdbcTemplate.queryForList("SELECT IP FROM blocked_ips WHERE blocking_batch_id=?", new Object[]{blockingBatchId}, String.class);

    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("Job finished. Check results summary.");
            List<String> blockedIPs = doBlockIPs(cliParameters.generateBlockingComment(), cliParameters.getStartDate(), cliParameters.getDuration(), cliParameters.getThreshold());
            log.info("List of blocked IPs:" + blockedIPs);
        }
    }
}
