package net.ssehub.jacat.addon.pp1plag;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Result {

    private String from;
    private List<To> to = new ArrayList<>();

    public Result(String from) {
        this.from = from;
    }

    public void add(String to, Double similarity) {
        this.to.add(new To(to, similarity));
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public List<To> getTo() {
        return to;
    }

    public void setTo(List<To> to) {
        this.to = to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Result result = (Result) o;
        return from.equals(result.from);
    }

    @Override
    public int hashCode() {
        return Objects.hash(from);
    }

    public static class To {

        private String submission;
        private Double similarity;

        public To(String submission, Double similarity) {
            this.submission = submission;
            this.similarity = similarity;
        }

        public String getSubmission() {
            return submission;
        }

        public void setSubmission(String submission) {
            this.submission = submission;
        }

        public Double getSimilarity() {
            return similarity;
        }

        public void setSimilarity(Double similarity) {
            this.similarity = similarity;
        }
    }

}
