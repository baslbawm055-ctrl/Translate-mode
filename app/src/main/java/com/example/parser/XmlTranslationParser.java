package com.example.parser;

import android.util.Xml;
import com.example.translation.TranslationItem;
import org.xmlpull.v1.XmlPullParser;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class XmlTranslationParser implements TranslationFileParser {

    @Override
    public List<TranslationItem> parse(InputStream inputStream) throws Exception {
        List<TranslationItem> items = new ArrayList<>();
        XmlPullParser parser = Xml.newPullParser();
        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
        parser.setInput(inputStream, "UTF-8");

        int eventType = parser.getEventType();
        String currentArrayName = null;
        String currentPluralName = null;
        int arrayIndex = 0;
        String currentPluralQuantity = null;

        while (eventType != XmlPullParser.END_DOCUMENT) {
            String tagName = parser.getName();

            switch (eventType) {
                case XmlPullParser.START_TAG:
                    if ("string".equals(tagName)) {
                        String name = parser.getAttributeValue(null, "name");
                        String text = parser.nextText();
                        if (isHumanReadable(text)) {
                            items.add(new TranslationItem(name, text));
                        }
                    } else if ("string-array".equals(tagName)) {
                        currentArrayName = parser.getAttributeValue(null, "name");
                        arrayIndex = 0;
                    } else if ("plurals".equals(tagName)) {
                        currentPluralName = parser.getAttributeValue(null, "name");
                    } else if ("item".equals(tagName)) {
                        if (currentArrayName != null) {
                            String text = parser.nextText();
                            if (isHumanReadable(text)) {
                                items.add(new TranslationItem(currentArrayName + "[" + arrayIndex + "]", text));
                            }
                            arrayIndex++;
                        } else if (currentPluralName != null) {
                            String quantity = parser.getAttributeValue(null, "quantity");
                            String text = parser.nextText();
                            if (isHumanReadable(text)) {
                                items.add(new TranslationItem(currentPluralName + ":" + (quantity != null ? quantity : "other"), text));
                            }
                        }
                    }
                    break;

                case XmlPullParser.END_TAG:
                    if ("string-array".equals(tagName)) {
                        currentArrayName = null;
                    } else if ("plurals".equals(tagName)) {
                        currentPluralName = null;
                    }
                    break;
            }
            eventType = parser.next();
        }

        return items;
    }

    public static boolean isHumanReadable(String text) {
        if (text == null) {
            return false;
        }
        text = text.trim();
        if (text.isEmpty()) {
            return false;
        }
        // Exclude hex colors (e.g., #FFF, #FFFFFF, #FF001122)
        if (text.startsWith("#") && (text.length() == 4 || text.length() == 7 || text.length() == 9)) {
            return false;
        }
        // Exclude booleans
        if ("true".equalsIgnoreCase(text) || "false".equalsIgnoreCase(text)) {
            return false;
        }
        // Exclude pure numbers
        try {
            Double.parseDouble(text);
            return false;
        } catch (NumberFormatException e) {
            // Not a pure number
        }
        // Exclude Android dimension styles (e.g., 16dp, 12sp, 10px, 4dip)
        if (text.matches("\\d+(\\.\\d+)?(dp|sp|px|dip|in|mm|pt)")) {
            return false;
        }
        return true;
    }
}
