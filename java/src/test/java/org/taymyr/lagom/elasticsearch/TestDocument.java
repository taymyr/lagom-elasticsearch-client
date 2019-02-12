package org.taymyr.lagom.elasticsearch;

import org.taymyr.lagom.elasticsearch.document.dsl.Document;
import org.taymyr.lagom.elasticsearch.search.dsl.SearchResult;

import java.util.Objects;

public class TestDocument {

    private String user;
    private String message;
    private Double balance;

    public TestDocument() {
    }

    public TestDocument(String user, String message) {
        this.user = user;
        this.message = message;
    }

    public TestDocument(String user, String message, Double balance) {
        this.user = user;
        this.message = message;
        this.balance = balance;
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
