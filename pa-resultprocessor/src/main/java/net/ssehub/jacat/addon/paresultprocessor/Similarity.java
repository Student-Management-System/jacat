package net.ssehub.jacat.addon.paresultprocessor;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Similarity {

    public static Similarity fromResult(Object o) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String jsoned = objectMapper.writeValueAsString(o);
            Similarity similarity = objectMapper.readValue(jsoned, Similarity.class);
            return similarity;
        } catch (Exception e) {
            return null;
        }
    }

    private String from;
    private List<To> to = new ArrayList<>();

    public Similarity() {
    }

    public Similarity(String from) {
        this.from = from;
    }

    public Similarity(String from, List<To> to) {
        this.from = from;
        this.to = to;
    }

    public boolean hasOneWithMoreThan(double percentage) {
        return this.getTo().stream().anyMatch(to -> to.getSimilarity() > percentage);
    }

    public Optional<To> findInTo(String submission) {
        return this.getTo().stream()
            .filter(toSim -> toSim.getSubmission().equalsIgnoreCase(submission))
            .findFirst();
    }

    public void add(String to, Double similarity) {
        if (findInTo(to).isPresent()) {
            return;
        }

        this.to.add(new To(to, similarity));
    }

    public void addAll(List<Similarity.To> tos) {
        this.to.addAll(tos);
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
        return "Similarity{\n" +
            "   from='" + from + '\'' + "\n" +
            "   , to=" + to + "\n" +
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

        public To() {
        }

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
            return "To{\n" +
                "        submission='" + submission + '\'' + "\n" +
                "        , similarity=" + similarity + "\n" +
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
