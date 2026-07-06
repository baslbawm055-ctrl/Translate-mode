package com.example.translation;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PlaceholderProtector {

    private static final Pattern[] PATTERNS = {
            Pattern.compile("<!\\[CDATA\\[.*?\\]\\]>", Pattern.DOTALL), // CDATA
            Pattern.compile("<[^>]+>"), // HTML or XML tags like <xliff:g>
            Pattern.compile("%\\d+\\$[a-zA-Z]"), // Format specifiers with position %1$s, %2$d
            Pattern.compile("%[a-zA-Z%]"), // Simple format specifiers %s, %d, %%
            Pattern.compile("\\$\\{[a-zA-Z0-9_\\-]+\\}"), // ${value}
            Pattern.compile("\\{[a-zA-Z0-9_\\-]+\\}"), // {name}
            Pattern.compile("\\\\u[0-9a-fA-F]{4}") // Unicode escapes like \u0020
    };

    public static class ProtectedText {
        private final String original;
        private final String maskedText;
        private final List<String> placeholders;

        public ProtectedText(String original, String maskedText, List<String> placeholders) {
            this.original = original;
            this.maskedText = maskedText;
            this.placeholders = placeholders;
        }

        public String getMaskedText() {
            return maskedText;
        }

        public String restore(String translatedMaskedText) {
            if (translatedMaskedText == null) {
                return null;
            }
            String restored = translatedMaskedText;
            for (int i = 0; i < placeholders.size(); i++) {
                // Handle possible spaces introduced by translation APIs (e.g., ___ PH_0 ___)
                String fuzzyPattern = "___\\s*PH_" + i + "\\s*___";
                restored = restored.replaceAll(fuzzyPattern, Matcher.quoteReplacement(placeholders.get(i)));
            }
            return restored;
        }
    }

    public static ProtectedText protect(String text) {
        if (text == null || text.isEmpty()) {
            return new ProtectedText(text, text, new ArrayList<>());
        }

        List<String> placeholders = new ArrayList<>();
        String tempText = text;

        for (Pattern pattern : PATTERNS) {
            Matcher matcher = pattern.matcher(tempText);
            StringBuffer sb = new StringBuffer();
            while (matcher.find()) {
                String match = matcher.group();
                int index = placeholders.size();
                placeholders.add(match);
                // Safe unique token structure: ___PH_Index___
                matcher.appendReplacement(sb, "___PH_" + index + "___");
            }
            matcher.appendTail(sb);
            tempText = sb.toString();
        }

        return new ProtectedText(text, tempText, placeholders);
    }
}
