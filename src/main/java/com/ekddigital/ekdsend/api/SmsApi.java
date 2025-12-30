package com.ekddigital.ekdsend.api;

import com.ekddigital.ekdsend.EKDSend;
import com.ekddigital.ekdsend.exception.EKDSendException;
import com.ekddigital.ekdsend.model.Sms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SMS API
 */
public class SmsApi {

    private final EKDSend client;

    public SmsApi(EKDSend client) {
        this.client = client;
    }

    /**
     * Send an SMS
     */
    public Sms send(SendSmsRequest request) throws EKDSendException {
        return client.request("POST", "/sms", request.toMap(), Sms.class);
    }

    /**
     * Get an SMS by ID
     */
    public Sms get(String smsId) throws EKDSendException {
        return client.request("GET", "/sms/" + smsId, null, Sms.class);
    }

    /**
     * List SMS messages with optional filters
     */
    public SmsListResponse list(ListSmsRequest request) throws EKDSendException {
        StringBuilder path = new StringBuilder("/sms?");
        List<String> params = new ArrayList<>();

        if (request.limit != null)
            params.add("limit=" + request.limit);
        if (request.offset != null)
            params.add("offset=" + request.offset);
        if (request.status != null)
            params.add("status=" + request.status);

        path.append(String.join("&", params));
        return client.request("GET", path.toString(), null, SmsListResponse.class);
    }

    /**
     * Cancel a scheduled SMS
     */
    public Sms cancel(String smsId) throws EKDSendException {
        return client.request("DELETE", "/sms/" + smsId, null, Sms.class);
    }

    /**
     * Request builder for sending SMS
     */
    public static class SendSmsRequest {
        private String to;
        private String from;
        private String message;
        private Map<String, String> metadata;
        private String scheduledFor;

        private SendSmsRequest() {
        }

        public static SendSmsRequest builder() {
            return new SendSmsRequest();
        }

        public SendSmsRequest to(String to) {
            this.to = to;
            return this;
        }

        public SendSmsRequest from(String from) {
            this.from = from;
            return this;
        }

        public SendSmsRequest message(String message) {
            this.message = message;
            return this;
        }

        public SendSmsRequest metadata(Map<String, String> metadata) {
            this.metadata = metadata;
            return this;
        }

        public SendSmsRequest scheduledFor(String scheduledFor) {
            this.scheduledFor = scheduledFor;
            return this;
        }

        Map<String, Object> toMap() {
            Map<String, Object> map = new HashMap<>();
            if (to != null)
                map.put("to", to);
            if (from != null)
                map.put("from", from);
            if (message != null)
                map.put("message", message);
            if (metadata != null)
                map.put("metadata", metadata);
            if (scheduledFor != null)
                map.put("scheduled_for", scheduledFor);
            return map;
        }
    }

    /**
     * Request for listing SMS messages
     */
    public static class ListSmsRequest {
        private Integer limit;
        private Integer offset;
        private String status;

        private ListSmsRequest() {
        }

        public static ListSmsRequest builder() {
            return new ListSmsRequest();
        }

        public ListSmsRequest limit(int limit) {
            this.limit = limit;
            return this;
        }

        public ListSmsRequest offset(int offset) {
            this.offset = offset;
            return this;
        }

        public ListSmsRequest status(String status) {
            this.status = status;
            return this;
        }
    }

    /**
     * Response for listing SMS messages
     */
    public static class SmsListResponse {
        private List<Sms> data;
        private int total;
        private int limit;
        private int offset;

        public List<Sms> getData() {
            return data;
        }

        public void setData(List<Sms> data) {
            this.data = data;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getLimit() {
            return limit;
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }

        public int getOffset() {
            return offset;
        }

        public void setOffset(int offset) {
            this.offset = offset;
        }

        public boolean hasMore() {
            return (offset + limit) < total;
        }
    }
}
