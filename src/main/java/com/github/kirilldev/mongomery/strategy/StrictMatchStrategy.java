package com.github.kirilldev.mongomery.strategy;

import net.minidev.json.JSONObject;
import org.junit.Assert;

import java.util.Set;

/**
 * Asserts collection state if every document of it DOES NOT contain any of placeholders:
 * $anyObject(), $anyString()
 */
public class StrictMatchStrategy extends AssertStrategy {
    @Override
    public void assertTheSame(Set<JSONObject> expected, Set<JSONObject> actual) {
        Assert.assertEquals("Documents in db collection are different from described in json file!", expected, actual);
    }
}
