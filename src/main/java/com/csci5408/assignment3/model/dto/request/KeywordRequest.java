package com.csci5408.assignment3.model.dto.request;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/* POJO for consistent API request body for this application.
 * This is a singleton class.
 */
@Component
public class KeywordRequest {

    private List<String> keyword;
    public List<String> getKeyword() {
        return keyword;
    }
    public void setKeyword(List<String> keyword) {
        this.keyword = keyword;
    }

    @Override
    public String toString() {
        return "KeywordRequest{" + "keyword=" + keyword + '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof KeywordRequest)) return false;
        KeywordRequest that = (KeywordRequest) o;
        return Objects.equals(getKeyword(), that.getKeyword());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKeyword());
    }
}
