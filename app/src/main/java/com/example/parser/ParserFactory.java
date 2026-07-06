package com.example.parser;

import java.util.Locale;

public class ParserFactory {
    
    public static TranslationFileParser getParser(String fileName) {
        if (fileName == null) {
            return new XmlTranslationParser();
        }
        
        String lower = fileName.toLowerCase(Locale.ROOT);
        if (lower.endsWith(".xml")) {
            return new XmlTranslationParser();
        } else if (lower.endsWith(".json")) {
            return new JsonTranslationParser();
        } else if (lower.endsWith(".arsc")) {
            return new ArscTranslationParser();
        }
        
        return new XmlTranslationParser();
    }
}
