package net.ssehub.jacat.addon.pp1plag;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Similarity {

    private String from;
    private List<To> to = new ArrayList<>();

    public Similarity(String from) {
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
    public String toString() {
        return "Similarity{" +
            "from='" + from + '\'' +
            ", to=" + to +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Similarity result = (Similarity) o;
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

        @Override
        public String toString() {
            return "To{" +
                "submission='" + submission + '\'' +
                ", similarity=" + similarity +
                '}';
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            To to = (To) o;
            return submission.equalsIgnoreCase(to.submission);
        }

        @Override
        public int hashCode() {
            return Objects.hash(submission);
        }
    }

}
