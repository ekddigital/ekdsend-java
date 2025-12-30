package com.ekddigital.ekdsend.api;

import com.ekddigital.ekdsend.EKDSend;
import com.ekddigital.ekdsend.exception.EKDSendException;
import com.ekddigital.ekdsend.model.Email;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Emails API
 */
public class EmailsApi {

    private final EKDSend client;

    public EmailsApi(EKDSend client) {
        this.client = client;
    }

    /**
     * Send an email
     */
    public Email send(SendEmailRequest request) throws EKDSendException {
        return client.request("POST", "/emails", request.toMap(), Email.class);
    }

    /**
     * Get an email by ID
     */
    public Email get(String emailId) throws EKDSendException {
        return client.request("GET", "/emails/" + emailId, null, Email.class);
    }

    /**
     * List emails with optional filters
     */
    public EmailsListResponse list(ListEmailsRequest request) throws EKDSendException {
        StringBuilder path = new StringBuilder("/emails?");
        List<String> params = new ArrayList<>();

        if (request.limit != null)
            params.add("limit=" + request.limit);
        if (request.offset != null)
            params.add("offset=" + request.offset);
        if (request.status != null)
            params.add("status=" + request.status);

        path.append(String.join("&", params));
        return client.request("GET", path.toString(), null, EmailsListResponse.class);
    }

    /**
     * Cancel a scheduled email
     */
    public Email cancel(String emailId) throws EKDSendException {
        return client.request("DELETE", "/emails/" + emailId, null, Email.class);
    }

    /**
     * Request builder for sending an email
     */
    public static class SendEmailRequest {
        private String from;
        private List<String> to;
        private String subject;
        private String html;
        private String text;
        private List<String> cc;
        private List<String> bcc;
        private String replyTo;
        private List<String> tags;
        private Map<String, String> metadata;
        private String scheduledFor;

        private SendEmailRequest() {
        }

        public static SendEmailRequest builder() {
            return new SendEmailRequest();
        }

        public SendEmailRequest from(String from) {
            this.from = from;
            return this;
        }

        public SendEmailRequest to(List<String> to) {
            this.to = to;
            return this;
        }

        public SendEmailRequest to(String... to) {
            this.to = List.of(to);
            return this;
        }

        public SendEmailRequest subject(String subject) {
            this.subject = subject;
            return this;
        }

        public SendEmailRequest html(String html) {
            this.html = html;
            return this;
        }

        public SendEmailRequest text(String text) {
            this.text = text;
            return this;
        }

        public SendEmailRequest cc(List<String> cc) {
            this.cc = cc;
            return this;
        }

        public SendEmailRequest bcc(List<String> bcc) {
            this.bcc = bcc;
            return this;
        }

        public SendEmailRequest replyTo(String replyTo) {
            this.replyTo = replyTo;
            return this;
        }

        public SendEmailRequest tags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        public SendEmailRequest metadata(Map<String, String> metadata) {
            this.metadata = metadata;
            return this;
        }

        public SendEmailRequest scheduledFor(String scheduledFor) {
            this.scheduledFor = scheduledFor;
            return this;
        }

        Map<String, Object> toMap() {
            Map<String, Object> map = new HashMap<>();
            if (from != null)
                map.put("from", from);
            if (to != null)
                map.put("to", to);
            if (subject != null)
                map.put("subject", subject);
            if (html != null)
                map.put("html", html);
            if (text != null)
                map.put("text", text);
            if (cc != null)
                map.put("cc", cc);
            if (bcc != null)
                map.put("bcc", bcc);
            if (replyTo != null)
                map.put("reply_to", replyTo);
            if (tags != null)
                map.put("tags", tags);
            if (metadata != null)
                map.put("metadata", metadata);
            if (scheduledFor != null)
                map.put("scheduled_for", scheduledFor);
            return map;
        }
    }

    /**
     * Request for listing emails
     */
    public static class ListEmailsRequest {
        private Integer limit;
        private Integer offset;
        private String status;

        private ListEmailsRequest() {
        }

        public static ListEmailsRequest builder() {
            return new ListEmailsRequest();
        }

        public ListEmailsRequest limit(int limit) {
            this.limit = limit;
            return this;
        }

        public ListEmailsRequest offset(int offset) {
            this.offset = offset;
            return this;
        }

        public ListEmailsRequest status(String status) {
            this.status = status;
            return this;
        }
    }

    /**
     * Response for listing emails
     */
    public static class EmailsListResponse {
        private List<Email> data;
        private int total;
        private int limit;
        private int offset;

        public List<Email> getData() {
            return data;
        }

        public void setData(List<Email> data) {
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
