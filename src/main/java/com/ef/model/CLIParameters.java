package com.ef.model;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class CLIParameters {

    static public final DateFormat CLI_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd.HH:mm:ss");

    @Value("${startDate:#{null}}")
    private String strStartDate;

    @Value("${duration:#{null}}")
    private String strDuration;

    @Value("${threshold:#{null}}")
    private String strThreshold;

    private Date startDate;

    private Duration duration;

    private int threshold;

    @PostConstruct
    public void validateParameters() {
        Assert.notNull(strStartDate, "CLI parameter startDate is required");
        Assert.notNull(strDuration, "CLI parameter duration is required");
        Assert.notNull(strThreshold, "CLI parameter threshold is required");
        try {
            this.startDate = CLI_DATE_FORMAT.parse(strStartDate);
            this.duration = Duration.valueOf(strDuration.toUpperCase());
            this.threshold = Integer.parseInt(strThreshold);
        } catch (ParseException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    public Date getStartDate() {
        return startDate;
    }

    public Duration getDuration() {
        return duration;
    }

    public int getThreshold() {
        return threshold;
    }

    public String generateBlockingComment() {
        return "Blocked IPs according to parameters: " +
                "startDate=" + startDate +
                ", duration=" + duration +
                ", threshold=" + threshold +
                '}';
    }
}
