package com.example.parser;

import com.example.translation.TranslationItem;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JsonTranslationParser implements TranslationFileParser {

    @Override
    public List<TranslationItem> parse(InputStream inputStream) throws Exception {
        List<TranslationItem> items = new ArrayList<>();
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        
        String jsonStr = sb.toString().trim();
        if (jsonStr.isEmpty()) {
            return items;
        }

        Object json = new JSONTokener(jsonStr).nextValue();
        if (json instanceof JSONObject) {
            parseObject("", (JSONObject) json, items);
        } else if (json instanceof JSONArray) {
            parseArray("", (JSONArray) json, items);
        }
        
        return items;
    }

    private void parseObject(String prefix, JSONObject obj, List<TranslationItem> items) throws Exception {
        Iterator<String> keys = obj.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            Object value = obj.get(key);
            String fullKey = prefix.isEmpty() ? key : prefix + "." + key;
            
            if (value instanceof JSONObject) {
                parseObject(fullKey, (JSONObject) value, items);
            } else if (value instanceof JSONArray) {
                parseArray(fullKey, (JSONArray) value, items);
            } else if (value != null) {
                String strValue = value.toString();
                if (XmlTranslationParser.isHumanReadable(strValue)) {
                    items.add(new TranslationItem(fullKey, strValue));
                }
            }
        }
    }

    private void parseArray(String prefix, JSONArray arr, List<TranslationItem> items) throws Exception {
        for (int i = 0; i < arr.length(); i++) {
            Object value = arr.get(i);
            String fullKey = prefix + "[" + i + "]";
            if (value instanceof JSONObject) {
                parseObject(fullKey, (JSONObject) value, items);
            } else if (value instanceof JSONArray) {
                parseArray(fullKey, (JSONArray) value, items);
            } else if (value != null) {
                String strValue = value.toString();
                if (XmlTranslationParser.isHumanReadable(strValue)) {
                    items.add(new TranslationItem(fullKey, strValue));
                }
            }
        }
    }
}
