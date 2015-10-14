package com.github.kirilldev.mongomery.strategy;

import net.minidev.json.JSONObject;

import java.util.Set;

public abstract class AssertStrategy {

    public final static PatternMatchStrategy PATTERN_MATCH_STRATEGY = new PatternMatchStrategy();
    public final static StrictMatchStrategy STRICT_MATCH_STRATEGY = new StrictMatchStrategy();

    public abstract void assertTheSame(Set<JSONObject> expected, Set<JSONObject> actual);
}
