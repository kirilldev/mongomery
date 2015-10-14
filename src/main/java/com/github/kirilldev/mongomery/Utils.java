package com.github.kirilldev.mongomery;

import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class Utils {
    private Utils() {
    }

    public static String[] splitByLast$(String s) {
        final int $ = s.lastIndexOf("$");
        return new String[]{s.substring(0, $), s.substring($)};
    }

    public static JSONObject readJsonFile(String filePath) {
        final InputStream fileStream = Utils.class.getClass().getResourceAsStream(filePath);

        if (fileStream == null) {
            throw new RuntimeException(new FileNotFoundException(filePath));
        }

        try {
            return JSONValue.parse(new InputStreamReader(fileStream, "UTF-8"), JSONObject.class);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
