package com.example.parser;

import com.example.translation.TranslationItem;
import java.io.InputStream;
import java.util.List;

public interface TranslationFileParser {
    List<TranslationItem> parse(InputStream inputStream) throws Exception;
}
