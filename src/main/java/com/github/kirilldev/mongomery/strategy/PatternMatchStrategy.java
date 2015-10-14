package com.github.kirilldev.mongomery.strategy;

import com.github.kirilldev.mongomery.PatternMatchUtils;

import net.minidev.json.JSONObject;
import org.junit.Assert;

import java.util.*;

public class PatternMatchStrategy extends AssertStrategy {

    @Override
    public void assertTheSame(Set<JSONObject> expectedObjects, Set<JSONObject> actualObjects) {
        Assert.assertEquals("Size of collection in db is different from described in json file",
                expectedObjects.size(), actualObjects.size());

        final Map<JSONObject, Set<String>> patternMatchExpectedObjects = new HashMap<JSONObject, Set<String>>();
        final Set<JSONObject> strictMatchExpectedObjects = new HashSet<JSONObject>();

        for (JSONObject object : expectedObjects) {
            final Set<String> patternPropertiesPaths = PatternMatchUtils.findPatternPropertiesPaths(object);

            if (patternPropertiesPaths == null) {
                strictMatchExpectedObjects.add(object);
            } else {
                patternMatchExpectedObjects.put(object, patternPropertiesPaths);
            }
        }

        final Set<JSONObject> patternMatchCandidates = tryToMatchStrictly(strictMatchExpectedObjects,
                patternMatchExpectedObjects.size(), actualObjects);
        tryToMatchOverPattern(patternMatchExpectedObjects, patternMatchCandidates);
    }

    private Set<JSONObject> tryToMatchStrictly(Set<JSONObject> strictMatchExpectedObjects,
                                               int patternMatchExpectedObjectsSize, Set<JSONObject> actualObjects) {
        final Set<JSONObject> actualObjectsCopy = new HashSet<JSONObject>(actualObjects);
        actualObjectsCopy.removeAll(strictMatchExpectedObjects);

        if (actualObjectsCopy.size() != patternMatchExpectedObjectsSize) {
            strictMatchExpectedObjects.removeAll(actualObjects);
            throw new AssertionError("Can't find strict match for " + strictMatchExpectedObjects.size()
                    + " EXPECTED object(s): " + strictMatchExpectedObjects);
        }

        return actualObjectsCopy;
    }

    private void tryToMatchOverPattern(final Map<JSONObject, Set<String>> expectedObjects,
                                       final Set<JSONObject> actualObjects) {
        final Map<JSONObject, Set<String>> patternMatchExpectedObjects
                = new HashMap<JSONObject, Set<String>>(expectedObjects);
        final Set<JSONObject> unmatchedActualObjects = new HashSet<JSONObject>();

        for (JSONObject actualObject : actualObjects) {
            boolean isMatched = false;
            final Iterator<Map.Entry<JSONObject, Set<String>>> iterator
                    = patternMatchExpectedObjects.entrySet().iterator();

            while (iterator.hasNext() && !isMatched) {
                final Map.Entry<JSONObject, Set<String>> patternMatchObjAndPropsPaths = iterator.next();
                final JSONObject object = PatternMatchUtils.applyPropsAndGetResultObj(actualObject,
                        patternMatchObjAndPropsPaths.getValue());

                if (object != null && object.equals(patternMatchObjAndPropsPaths.getKey())) {
                    iterator.remove();
                    isMatched = true;
                }
            }

            if (!isMatched) {
                unmatchedActualObjects.add(actualObject);
            }
        }

        if (!unmatchedActualObjects.isEmpty()) {
            throw new AssertionError("Can't find pattern match for "
                    + unmatchedActualObjects.size() + " ACTUAL object(s): " + unmatchedActualObjects);
        }
    }
}
