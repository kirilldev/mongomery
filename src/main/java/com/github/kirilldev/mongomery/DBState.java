package com.github.kirilldev.mongomery;


import com.github.kirilldev.mongomery.strategy.AssertStrategy;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;

import java.util.*;
import java.util.regex.Pattern;

public class DBState {

    private final Map<String, Set<JSONObject>> collectionToDocuments = new HashMap<String, Set<JSONObject>>();
    private final Map<String, AssertStrategy> collectionToMatchStrategy = new HashMap<String, AssertStrategy>();

    public DBState(JSONObject object) {
        for (Map.Entry<String, Object> collections : object.entrySet()) {
            collectionToMatchStrategy.put(collections.getKey(), AssertStrategy.STRICT_MATCH_STRATEGY);

            for (Pattern pattern : Placeholders.getContainPatterns()) {
                String val = collections.getValue().toString();

                if (pattern.matcher(val).find()) {
                    collectionToMatchStrategy.put(collections.getKey(), AssertStrategy.PATTERN_MATCH_STRATEGY);
                    break;
                }
            }

            final JSONArray documents = (JSONArray) collections.getValue();
            collectionToDocuments.put(collections.getKey(), toJavaSet(documents));
        }
    }

    public boolean containsCollection(String name) {
        return collectionToDocuments.containsKey(name);
    }

    public int getCollectionNumber() {
        return collectionToDocuments.size();
    }

    public SortedSet<String> getCollectionNames() {
        return new TreeSet<String>(collectionToDocuments.keySet());
    }

    public Set<JSONObject> getDocuments(String collectionName) {
        return collectionToDocuments.get(collectionName);
    }

    public AssertStrategy getMatchStrategy(String collectionName) {
        return collectionToMatchStrategy.get(collectionName);
    }

    private Set<JSONObject> toJavaSet(JSONArray documents) {
        final Set<JSONObject> objects = new HashSet<JSONObject>();

        for (Object doc : documents) {
            JSONObject document = (JSONObject) doc;
            objects.add(document);
        }

        return objects;
    }
}
