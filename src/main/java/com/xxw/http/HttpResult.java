package com.xxw.http;

public class HttpResult {

    private Integer status;
    private String reasonPhrase;
    private String data;

    public Integer getStatus() {
        return status;
    }

    public HttpResult(Integer status, String reasonPhrase, String data) {
        this.status = status;
        this.reasonPhrase = reasonPhrase;
        this.data = data;
    }

    public HttpResult() {

    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public void setReasonPhrase(String reasonPhrase) {
        this.reasonPhrase = reasonPhrase;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "HttpResult [status=" + status + ", reasonPhrase=" + reasonPhrase + ", data=" + data + "]";
    }


}
