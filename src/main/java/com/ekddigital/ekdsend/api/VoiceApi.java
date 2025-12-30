package com.ekddigital.ekdsend.api;

import com.ekddigital.ekdsend.EKDSend;
import com.ekddigital.ekdsend.exception.EKDSendException;
import com.ekddigital.ekdsend.model.Recording;
import com.ekddigital.ekdsend.model.VoiceCall;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Voice API
 */
public class VoiceApi {

    private final EKDSend client;

    public VoiceApi(EKDSend client) {
        this.client = client;
    }

    /**
     * Create a voice call
     */
    public VoiceCall create(CreateCallRequest request) throws EKDSendException {
        return client.request("POST", "/calls", request.toMap(), VoiceCall.class);
    }

    /**
     * Get a call by ID
     */
    public VoiceCall get(String callId) throws EKDSendException {
        return client.request("GET", "/calls/" + callId, null, VoiceCall.class);
    }

    /**
     * List calls with optional filters
     */
    public CallListResponse list(ListCallsRequest request) throws EKDSendException {
        StringBuilder path = new StringBuilder("/calls?");
        List<String> params = new ArrayList<>();

        if (request.limit != null)
            params.add("limit=" + request.limit);
        if (request.offset != null)
            params.add("offset=" + request.offset);
        if (request.status != null)
            params.add("status=" + request.status);

        path.append(String.join("&", params));
        return client.request("GET", path.toString(), null, CallListResponse.class);
    }

    /**
     * Hangup an active call
     */
    public VoiceCall hangup(String callId) throws EKDSendException {
        return client.request("POST", "/calls/" + callId + "/hangup", null, VoiceCall.class);
    }

    /**
     * Get call recording
     */
    public Recording getRecording(String callId) throws EKDSendException {
        return client.request("GET", "/calls/" + callId + "/recording", null, Recording.class);
    }

    /**
     * Request builder for creating a call
     */
    public static class CreateCallRequest {
        private String to;
        private String from;
        private String ttsMessage;
        private String audioUrl;
        private String voice;
        private String language;
        private Boolean record;
        private Boolean machineDetection;
        private String webhookUrl;
        private Map<String, String> metadata;

        private CreateCallRequest() {
        }

        public static CreateCallRequest builder() {
            return new CreateCallRequest();
        }

        public CreateCallRequest to(String to) {
            this.to = to;
            return this;
        }

        public CreateCallRequest from(String from) {
            this.from = from;
            return this;
        }

        public CreateCallRequest ttsMessage(String ttsMessage) {
            this.ttsMessage = ttsMessage;
            return this;
        }

        public CreateCallRequest audioUrl(String audioUrl) {
            this.audioUrl = audioUrl;
            return this;
        }

        public CreateCallRequest voice(String voice) {
            this.voice = voice;
            return this;
        }

        public CreateCallRequest language(String language) {
            this.language = language;
            return this;
        }

        public CreateCallRequest record(boolean record) {
            this.record = record;
            return this;
        }

        public CreateCallRequest machineDetection(boolean machineDetection) {
            this.machineDetection = machineDetection;
            return this;
        }

        public CreateCallRequest webhookUrl(String webhookUrl) {
            this.webhookUrl = webhookUrl;
            return this;
        }

        public CreateCallRequest metadata(Map<String, String> metadata) {
            this.metadata = metadata;
            return this;
        }

        Map<String, Object> toMap() {
            Map<String, Object> map = new HashMap<>();
            if (to != null)
                map.put("to", to);
            if (from != null)
                map.put("from", from);
            if (ttsMessage != null)
                map.put("tts_message", ttsMessage);
            if (audioUrl != null)
                map.put("audio_url", audioUrl);
            if (voice != null)
                map.put("voice", voice);
            if (language != null)
                map.put("language", language);
            if (record != null)
                map.put("record", record);
            if (machineDetection != null)
                map.put("machine_detection", machineDetection);
            if (webhookUrl != null)
                map.put("webhook_url", webhookUrl);
            if (metadata != null)
                map.put("metadata", metadata);
            return map;
        }
    }

    /**
     * Request for listing calls
     */
    public static class ListCallsRequest {
        private Integer limit;
        private Integer offset;
        private String status;

        private ListCallsRequest() {
        }

        public static ListCallsRequest builder() {
            return new ListCallsRequest();
        }

        public ListCallsRequest limit(int limit) {
            this.limit = limit;
            return this;
        }

        public ListCallsRequest offset(int offset) {
            this.offset = offset;
            return this;
        }

        public ListCallsRequest status(String status) {
            this.status = status;
            return this;
        }
    }

    /**
     * Response for listing calls
     */
    public static class CallListResponse {
        private List<VoiceCall> data;
        private int total;
        private int limit;
        private int offset;

        public List<VoiceCall> getData() {
            return data;
        }

        public void setData(List<VoiceCall> data) {
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
