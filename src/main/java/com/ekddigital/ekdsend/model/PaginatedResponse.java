package com.ekddigital.ekdsend.model;

import java.util.List;

/**
 * Paginated response wrapper
 */
public class PaginatedResponse<T> {

    private List<T> data;
    private int total;
    private int limit;
    private int offset;

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
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

    /**
     * Check if there are more pages
     */
    public boolean hasMore() {
        return (offset + limit) < total;
    }

    /**
     * Get the offset for the next page
     */
    public int nextOffset() {
        return offset + limit;
    }
}
