package chapter4.good;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class GoodNewspaper {
    private final String headline;
    private final String writer;
    private final List<String> articles;

    public GoodNewspaper(String headline, String writer, List<String> articles) {
        this.headline = Objects.requireNonNull(headline);
        this.writer = Objects.requireNonNull(writer);
        this.articles = new ArrayList<>(articles);
    }

    public String headline() {
        return headline;
    }

    public String writer() {
        return writer;
    }

    public List<String> articles() {
        return new ArrayList<>(articles);
    }

    @Override
    public String toString() {
        return "GoodNewspaper[headline=" + headline + ", writer=" + writer + ", articles=" + articles + "]";
    }
}