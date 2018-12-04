package com.ef.batch.processor;

import com.ef.model.AccessLogObject;
import com.ef.model.AccessLogString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class AccessLogItemProcessor implements ItemProcessor<AccessLogString, AccessLogObject> {

    private static final Logger log = LoggerFactory.getLogger(AccessLogItemProcessor.class);

    static public final DateFormat LOG_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public AccessLogObject process(final AccessLogString accessLogString) throws Exception {
        AccessLogObject accessLogObject = new AccessLogObject();
        try {
            accessLogObject.setDate(LOG_DATE_FORMAT.parse(accessLogString.getDate()));
            accessLogObject.setHTTPStatus(Integer.parseInt(accessLogString.getHTTPStatus()));
        } catch (ParseException e) {
            log.error("Wrong format for date string of HTTP status in access loc record: " + accessLogString);
            throw e;
        }
        accessLogObject.setIP(accessLogString.getIP());
        accessLogObject.setClient(accessLogString.getClient());
        accessLogObject.setHTTPStatus(Integer.parseInt(accessLogString.getHTTPStatus()));
        accessLogObject.setURL(accessLogString.getURL());
        return accessLogObject;
    }

}
