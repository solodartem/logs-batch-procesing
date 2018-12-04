package com.ef.model;

public class AccessLogString {

    private String date;
    private String IP;
    private String URL;
    private String HTTPStatus;
    private String client;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
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

    public String getHTTPStatus() {
        return HTTPStatus;
    }

    public void setHTTPStatus(String HTTPStatus) {
        this.HTTPStatus = HTTPStatus;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    @Override
    public String toString() {
        return "AccessLogString{" +
                "date='" + date + '\'' +
                ", IP='" + IP + '\'' +
                ", URL='" + URL + '\'' +
                ", HTTPStatus='" + HTTPStatus + '\'' +
                ", client='" + client + '\'' +
                '}';
    }
}
