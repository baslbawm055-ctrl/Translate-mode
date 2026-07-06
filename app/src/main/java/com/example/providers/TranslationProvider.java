package com.example.providers;

import com.example.translation.TranslationResult;
import java.util.List;

public interface TranslationProvider {
    String getName();
    
    TranslationResult translate(String text, String sourceLang, String targetLang) throws Exception;
    
    List<TranslationResult> translateBatch(List<String> texts, String sourceLang, String targetLang) throws Exception;
}
