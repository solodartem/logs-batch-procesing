package com.ef.model;

import java.util.Date;

public class AccessLogObject {

    private Date date;
    private String IP;
    private String URL;
    private Integer HTTPStatus;
    private String client;

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public Integer getHTTPStatus() {
        return HTTPStatus;
    }

    public void setHTTPStatus(Integer HTTPStatus) {
        this.HTTPStatus = HTTPStatus;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }
}
