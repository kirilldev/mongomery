package com.github.kirilldev.mongomery;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public enum Placeholders {
    ANY_OBJECT("\\$anyObject\\(\\)"),
    ANY_OBJECT_WITH_ARG("\\$anyObject\\(\\d{1,10}\\)"),
    ANY_STRING("\\$anyString\\(\\)"),
    ANY_STRING_WITH_ARG("\\$anyString\\(\\\\?/.+\\\\?/\\)");

    public Pattern containPattern;
    public Pattern equalPattern;

    private final static Set<Pattern> containPatterns = new HashSet<Pattern>();
    private final static Set<Pattern> equalPatterns = new HashSet<Pattern>();

    Placeholders(String pattern) {
        this.containPattern = Pattern.compile(pattern);
        this.equalPattern = Pattern.compile("^" + pattern + "$");
    }

    public boolean eq(String s) {
        return this.equalPattern.matcher(s).matches();
    }

    public static Set<Pattern> getContainPatterns() {
        if (containPatterns.isEmpty()) {
            for (Placeholders words : Placeholders.values()) {
                containPatterns.add(words.containPattern);
            }
        }

        return containPatterns;
    }

    public static Set<Pattern> getEqualPatterns() {
        if (equalPatterns.isEmpty()) {
            for (Placeholders words : Placeholders.values()) {
                equalPatterns.add(words.equalPattern);
            }
        }

        return equalPatterns;
    }
}
