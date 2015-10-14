package com.github.kirilldev.mongomery;

import com.github.fakemongo.Fongo;

import com.mongodb.DB;
import net.minidev.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;

public class TestMongoDBTester {

    private MongoDBTester mongoDBTester;

    @Before
    public void init() {
        if (mongoDBTester == null) {
            final DB db = new Fongo("FongoServer").getDB("test");
            mongoDBTester = new MongoDBTester(db, "/expected/", "/predefined/");
        } else {
            mongoDBTester.dropDataBase();
        }
    }

    @Test
    public void strictMatchSimplestTestShouldPass() {
        mongoDBTester.setDBState("strictMatch/simplestTestDataSet.json");
        mongoDBTester.assertDBStateEquals("strictMatch/simplestTestShouldPassExpectedDataSet.json");
    }

    @Test(expected = AssertionError.class)
    public void strictMatchSimplestTestShouldFail() {
        mongoDBTester.setDBState("strictMatch/simplestTestDataSet.json");
        mongoDBTester.assertDBStateEquals("strictMatch/simplestTestShouldFailExpectedDataSet.json");
    }

    @Test
    public void patternMatchOnlySimplestTestFor$AnyObjectShouldPass() {
        mongoDBTester.setDBState("patternMatch/simplestTest$anyObjectDataSet.json");
        mongoDBTester.assertDBStateEquals("patternMatch/simplestTest$anyObjectDataSet.json");
    }

    @Test
    public void simplestTest$anyObjectMixedWith$anyString() {
        mongoDBTester.setDBState("patternMatch/simplestTest$anyObjectMixedWith$anyStringDataSet.json");
        mongoDBTester.assertDBStateEquals("patternMatch/simplestTest$anyObjectMixedWith$anyStringDataSet.json");
    }

    @Test(expected = AssertionError.class)
    public void patternMatch$anyObjectShouldFailOnNull() {
        mongoDBTester.setDBState("patternMatch/simplestTest$PlaceholderCantBeNullDataSet.json");
        mongoDBTester.assertDBStateEquals("patternMatch/simplestTest$anyObjectCantBeNullDataSet.json");
    }

    @Test(expected = AssertionError.class)
    public void patternMatch$anyStringShouldFailOnNull() {
        mongoDBTester.setDBState("patternMatch/simplestTest$PlaceholderCantBeNullDataSet.json");
        mongoDBTester.assertDBStateEquals("patternMatch/simplestTest$anyStringCantBeNullDataSet.json");
    }

    @Test
    public void complexTestShouldPass() {
        mongoDBTester.setDBState("patternMatch/complexTestDataSet.json");
        mongoDBTester.assertDBStateEquals("patternMatch/complexTestDataSet.json");
    }

    @Test(expected = AssertionError.class)
    public void complexTestShouldFailIfCantFindAllStrictMatches() {
        mongoDBTester.setDBState("patternMatch/complexTestDataSet.json");
        mongoDBTester.assertDBStateEquals("patternMatch/complexTestShouldFailIfCantFindAllStrictMatches.json");
    }

    @Test(expected = AssertionError.class)
    public void complexTestShouldFailIfCantFindAllPatternMatches() {
        mongoDBTester.setDBState("patternMatch/complexTestDataSet.json");
        mongoDBTester.assertDBStateEquals("patternMatch/complexTestShouldFailIfCantFindAllPatternMatches.json");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailIfUserUsesPlaceholderMixedWithCharInString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("_id", "$anyObject()");
        jsonObject.put("email", " $anyString()");
        jsonObject.put("firstName", "firstName)");
        mongoDBTester.setDBState("TestCollection", jsonObject);
        mongoDBTester.assertDBStateEquals("patternMatch/placeholderCantBeMixedWithCharacter.json");
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldFailIfPlaceholderIsInsideArray() {
        final InputStream fileStream = this.getClass()
                .getResourceAsStream("/predefined/patternMatch/placeholderCantBeInsideArray.json");
        mongoDBTester.setDBState(fileStream);
        mongoDBTester.assertDBStateEquals("patternMatch/placeholderCantBeInsideArray.json");
    }

    @Test(expected = AssertionError.class)
    public void simplestTestShouldFail$anyObjectDataSet() {
        mongoDBTester.setDBState("patternMatch/simplestTestShouldFail$anyObjectDataSet.json");
        mongoDBTester.assertDBStateEquals("patternMatch/simplestTestShouldFail$anyObjectDataSet.json");
    }

    @Test
    public void advancedPatternMatchShouldPass() {
        mongoDBTester.setDBState("advancedPatternMatch/testShouldPass$anyStringDataSet.json");
        mongoDBTester.assertDBStateEquals("advancedPatternMatch/testShouldPass$anyStringDataSet.json");
    }

    @Test
    public void testShouldPass$anyObjectDataSet() {
        mongoDBTester.setDBState("advancedPatternMatch/testShouldPass$anyObjectDataSet.json");
        mongoDBTester.assertDBStateEquals("advancedPatternMatch/testShouldPass$anyObjectDataSet.json");
    }

    @Test(expected = AssertionError.class)
    public void testShouldFail$anyObjectDataSet() {
        mongoDBTester.setDBState("advancedPatternMatch/testShouldPass$anyObjectDataSet.json");
        mongoDBTester.assertDBStateEquals("advancedPatternMatch/testShouldFail$anyObjectDataSet.json");
    }
}