package com.github.kirilldev.mongomery;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minidev.json.JSONObject;

import static com.github.kirilldev.mongomery.Placeholders.ANY;
import static com.github.kirilldev.mongomery.Placeholders.ANY_OBJECT;
import static com.github.kirilldev.mongomery.Placeholders.ANY_OBJECT_WITH_ARG;
import static com.github.kirilldev.mongomery.Placeholders.ANY_STRING;
import static com.github.kirilldev.mongomery.Placeholders.ANY_STRING_WITH_ARG;

public class PatternMatchUtils {
    private PatternMatchUtils() {
    }

    public static JSONObject applyPropsAndGetResultObj(JSONObject object, Set<String> props) {
        JSONObject clone = (JSONObject) object.clone();

        for (String prop : props) {
            final String[] properties = prop.split("\\.");
            LinkedList<JSONObject> trace = new LinkedList<JSONObject>();
            trace.add(clone);

            for (int i = 0; i < properties.length; i++) {
                if (i == properties.length - 1) {
                    final String[] $s = Utils.splitByLast$(properties[i]);
                    final Object o = clone.get($s[0]);

                    if (o == null) {
                        return null;
                    } else if ((ANY_STRING.eq($s[1]) && o instanceof String) ||
                            (ANY_OBJECT.eq($s[1]) && o instanceof JSONObject)) {
                        clone = createMergedObj(trace, properties, clone.getAsString($s[0]));
                    } else if (ANY_OBJECT_WITH_ARG.eq($s[1]) && o instanceof JSONObject) {
                        int numOfObjs = Integer.valueOf($s[1].substring(11, $s[1].lastIndexOf(')')));
                        if (((JSONObject) o).size() == numOfObjs) {
                            clone = createMergedObj(trace, properties, clone.getAsString($s[0]));
                        }
                    } else if (ANY_STRING_WITH_ARG.eq($s[1]) && o instanceof String) {
                        String regex = $s[1].substring(12, $s[1].lastIndexOf('/'));
                        if (((String) o).matches(regex)) {
                            clone = createMergedObj(trace, properties, clone.getAsString($s[0]));
                        }
                    } else if (ANY.eq($s[1])) {
                        clone = createMergedObj(trace, properties, clone.getAsString($s[0]));
                    } else {
                        return null;
                    }
                } else {
                    trace.add((JSONObject) clone.get(properties[i]));
                    clone = (JSONObject) clone.get(properties[i]);
                }
            }

        }

        return clone;
    }

    private static JSONObject createMergedObj(LinkedList<JSONObject> trace, String[] properties, Object old) {
        final String[] $s = Utils.splitByLast$(properties[properties.length - 1]);
        final JSONObject object = trace.removeLast();
        object.put($s[0], $s[1]);

        JSONObject temp = object;
        for (int i = properties.length - 2; i >= 0; i--) {
            final JSONObject upperNode = trace.removeLast();
            upperNode.put(properties[i], temp);
            temp = upperNode;
        }

        return temp;
    }

    public static Set<String> findPatternPropertiesPaths(JSONObject object) {
        int contains = countStringContainsPattern(object.toString());

        if (contains == 0) {
            return null;
        } else {
            final Set<String> properties = new HashSet<String>();

            for (Map.Entry<String, Object> node : object.entrySet()) {
                final Set<String> props = checkForNode(node, new HashSet<String>());
                if (!props.isEmpty()) {
                    properties.addAll(props);
                    if (contains == properties.size()) {
                        //All was found
                        return properties;
                    }
                }
            }
            return properties;
        }
    }

    private static Set<String> checkForNode(Map.Entry<String, Object> node, Set<String> props) {
        final Object value = node.getValue();

        if (value == null || countStringContainsPattern(value.toString()) == 0) {
            return props;
        } else {
            final Set<String> newProps = addNodeForProps(props, node.getKey());

            if (value instanceof String) {
                if (checkForEqualsPattern((String) value)) {
                    final Set<String> hashSet = new HashSet<String>();
                    for (String p : newProps) {
                        hashSet.add(p + value);
                    }
                    return hashSet;
                } else {
                    if (countStringContainsPattern((String) value) > 0) {
                        throw new IllegalArgumentException("String with placeholder shouldn't contain any other characters (even whitespaces)! Your string was: \""
                                + value + "\"");
                    }
                }
            } else if (value instanceof ArrayList) {
                throw new UnsupportedOperationException("You can't use match patterns in arrays for now!");
            } else {
                final JSONObject childNodes = (JSONObject) value;
                final Set<String> theNewestProps = new HashSet<String>();

                for (Map.Entry<String, Object> childNode : childNodes.entrySet()) {
                    final Set<String> p = checkForNode(childNode, newProps);

                    if (!newProps.equals(p)) {
                        theNewestProps.addAll(p);
                    }
                }

                return theNewestProps;
            }
        }

        throw new IllegalArgumentException();
    }


    private static Set<String> addNodeForProps(Set<String> props, String nodeKey) {
        final Set<String> newProps = new HashSet<String>();

        if (props.isEmpty()) {
            newProps.add(nodeKey);
        } else {
            for (String p : props) {
                newProps.add(p + "." + nodeKey);
            }
        }

        return newProps;
    }

    public static boolean checkForEqualsPattern(String value) {
        final Set<Pattern> patterns = Placeholders.getEqualPatterns();

        for (Pattern pattern : patterns) {
            if (pattern.matcher(value).find()) {
                return true;
            }
        }

        return false;
    }

    public static int countStringContainsPattern(String string) {
        int count = 0;

        for (Pattern pattern : Placeholders.getContainPatterns()) {
            Matcher matcher = pattern.matcher(string);

            while (matcher.find()) {
                count++;
            }
        }

        return count;
    }
}
