package org.taymyr.lagom.elasticsearch;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.taymyr.lagom.elasticsearch.document.dsl.Document;
import org.taymyr.lagom.elasticsearch.search.dsl.SearchResult;

import java.util.Objects;
import java.util.Optional;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_ABSENT;

public class TestDocument {

    private String user;
    private String message;
    private Double balance;
    @JsonInclude(NON_ABSENT)
    private Optional<String> comment;

    public TestDocument() {
    }

    public TestDocument(String user, String message) {
        this(user, message, null, Optional.empty());
    }

    public TestDocument(String user, String message, Double balance) {
        this(user, message, balance, Optional.empty());
    }

    public TestDocument(String user, String message, Double balance, Optional<String> comment) {
        this.user = user;
        this.message = message;
        this.balance = balance;
        this.comment = comment;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Optional<String> getComment() {
        return comment;
    }

    public void setComment(Optional<String> comment) {
        this.comment = comment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TestDocument)) return false;
        TestDocument that = (TestDocument) o;
        return Objects.equals(user, that.user) &&
                Objects.equals(message, that.message) &&
                Objects.equals(balance, that.balance);
    }

    @Override
    public int hashCode() {
        return Objects.hash(user, message, balance);
    }

    public static class TestDocumentResult extends SearchResult<TestDocument> { }

    public static class IndexedTestDocument extends Document<TestDocument> {

        public IndexedTestDocument() { }

        public IndexedTestDocument(TestDocument document) {
            super(document);
        }
    }
}
