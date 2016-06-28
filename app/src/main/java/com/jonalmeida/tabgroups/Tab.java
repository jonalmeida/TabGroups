package com.jonalmeida.tabgroups;

import android.support.annotation.Nullable;
import android.support.annotation.PluralsRes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class Tab {
    private String title;
    private String url;
    private String topKeyword;
    private HashMap<String, Integer> keywords;
    private int[] faviconColours;
    private long colourWeight;
    private String faviconUrl;

    private List<String> sortedKeywords;

    public Tab(String title, String url) {
        this.title = title;
        this.url = url;
    }

    private Tab(Builder builder) {
        this.title = builder.title;
        this.url = builder.url;
//        this.topKeyword = builder.topKeyword;
        this.keywords = builder.keywords;
        this.faviconColours = builder.colours;
        this.colourWeight = builder.colourWeight;
        this.faviconUrl = builder.faviconUrl;
        this.topKeyword = highestRankedKeyword();
    }

    public String title() {
        return title;
    }

    public String url() {
        return url;
    }

    public String topKeyword() {
        return topKeyword;
    }

    public HashMap<String, Integer> keywords() {
        return keywords;
    }

    public @Nullable String highestRankedKeyword() {
        return rankedKeyword(0);
    }

    public String rankedKeyword(int i) {
        List<String> sortedKeywords = sortRankedKeywords();
        if (sortedKeywords.size() == 0) {
            return null;
        } else if (sortedKeywords.size() >= i) {
            return sortedKeywords.get(i);
        } else if (sortedKeywords.size() > 0) {
            return sortedKeywords.get(0);
        }
        return null;
    }

    private List<String> sortRankedKeywords() {
        if (sortedKeywords != null) {
            return sortedKeywords;
        }
        sortedKeywords = new ArrayList<>(keywords.keySet());
        Collections.sort(sortedKeywords, new Comparator<String>() {
            @Override
            public int compare(String s, String t) {
                Integer sScore = keywords.get(s);
                Integer tScore = keywords.get(t);
                return tScore.compareTo(sScore);
            }
        });
        System.out.println(sortedKeywords);
        return sortedKeywords;
    }

    public int[] colours() {
        return faviconColours;
    }

    public long colourWeight() {
        return colourWeight;
    }

    public String faviconUrl() {
        return faviconUrl;
    }

    public static class Builder {
        private String title;
        private String url;
        private String topKeyword;
        private HashMap<String, Integer> keywords;
        private int[] colours;
        private long colourWeight;
        private String faviconUrl;

        public Builder() {
            this.title = "Default";
            this.topKeyword = "Unsorted";
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder topKeyword(String keyword) {
            this.topKeyword = keyword != null ? keyword : "Misc.";
            return this;
        }

        public Builder keywords(HashMap<String, Integer> keywords) {
            this.keywords = keywords;
            return this;
        }

        public Builder rgbColours(int[] colours) {
           this.colours = new int[] {colours[0], colours[1], colours[2]};
            return this;
        }

        public Builder rgbColours(int r, int g, int b) {
            this.colours = new int[] {r, g, b};
            return this;
        }

        public Builder colourWeight(long weight) {
            this.colourWeight = weight;
            return this;
        }

        public Builder faviconUrl(String url) {
            this.faviconUrl = url;
            return this;
        }

        public Tab build() {
            return new Tab(this);
        }
    }
}
