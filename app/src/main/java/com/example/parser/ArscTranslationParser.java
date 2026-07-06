package com.example.parser;

import com.example.translation.TranslationItem;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class ArscTranslationParser implements TranslationFileParser {

    @Override
    public List<TranslationItem> parse(InputStream inputStream) throws Exception {
        List<TranslationItem> items = new ArrayList<>();
        
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        byte[] data = bos.toByteArray();
        if (data.length < 8) {
            return items;
        }

        ByteBuffer buf = ByteBuffer.wrap(data);
        buf.order(ByteOrder.LITTLE_ENDIAN);

        int rootType = buf.getShort() & 0xFFFF;
        int rootHeaderSize = buf.getShort() & 0xFFFF;
        int rootSize = buf.getInt();

        if (rootType != 0x0002) {
            // Return empty list or fallback to stream-based string mining
            return extractStringsFallback(data);
        }

        int offset = rootHeaderSize;
        while (offset + 8 <= data.length) {
            buf.position(offset);
            int chunkType = buf.getShort() & 0xFFFF;
            int chunkHeaderSize = buf.getShort() & 0xFFFF;
            int chunkSize = buf.getInt();

            if (chunkType == 0x0001) {
                parseStringPool(buf, offset, items);
                break;
            }

            if (chunkSize <= 0) {
                break;
            }
            offset += chunkSize;
        }

        return items;
    }

    private void parseStringPool(ByteBuffer buf, int startOffset, List<TranslationItem> items) {
        buf.position(startOffset + 8);
        int stringCount = buf.getInt();
        int styleCount = buf.getInt();
        int flags = buf.getInt();
        int stringStart = buf.getInt();
        int stylesStart = buf.getInt();

        boolean isUtf8 = (flags & (1 << 8)) != 0;

        int[] offsets = new int[stringCount];
        for (int i = 0; i < stringCount; i++) {
            offsets[i] = buf.getInt();
        }

        int stringDataStart = startOffset + stringStart;

        for (int i = 0; i < stringCount; i++) {
            int strPos = stringDataStart + offsets[i];
            buf.position(strPos);

            String str = null;
            try {
                if (isUtf8) {
                    int charLen = readUtf8Len(buf);
                    int byteLen = readUtf8Len(buf);
                    byte[] strBytes = new byte[byteLen];
                    buf.get(strBytes);
                    str = new String(strBytes, StandardCharsets.UTF_8);
                } else {
                    int charLen = readUtf16Len(buf);
                    byte[] strBytes = new byte[charLen * 2];
                    buf.get(strBytes);
                    str = new String(strBytes, StandardCharsets.UTF_16LE);
                }
            } catch (Exception e) {
                // Ignore malformed string pool segments
            }

            if (str != null) {
                String trimmed = str.trim();
                if (XmlTranslationParser.isHumanReadable(trimmed) && !isInternalKey(trimmed)) {
                    items.add(new TranslationItem("arsc_str_" + i, trimmed));
                }
            }
        }
    }

    private int readUtf8Len(ByteBuffer buf) {
        int val = buf.get() & 0xFF;
        if ((val & 0x80) != 0) {
            val = ((val & 0x7F) << 8) | (buf.get() & 0xFF);
        }
        return val;
    }

    private int readUtf16Len(ByteBuffer buf) {
        int val = buf.getShort() & 0xFFFF;
        if ((val & 0x8000) != 0) {
            val = ((val & 0x7FFF) << 16) | (buf.getShort() & 0xFFFF);
        }
        return val;
    }

    private boolean isInternalKey(String str) {
        if (str.matches("[a-zA-Z0-9_\\.]+") && str.length() < 30) {
            if (str.equals("string") || str.equals("layout") || str.equals("dimen") || str.equals("color") || str.equals("drawable") || str.equals("attr") || str.equals("style") || str.equals("id")) {
                return true;
            }
            if (str.matches("^res/.*") || str.matches("^AndroidManifest\\.xml.*") || str.matches("^assets/.*")) {
                return true;
            }
        }
        return false;
    }

    private List<TranslationItem> extractStringsFallback(byte[] data) {
        List<TranslationItem> items = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        int count = 0;
        for (byte b : data) {
            if (b >= 32 && b <= 126) {
                current.append((char) b);
            } else {
                if (current.length() > 3) {
                    String s = current.toString().trim();
                    if (XmlTranslationParser.isHumanReadable(s) && !isInternalKey(s)) {
                        items.add(new TranslationItem("arsc_fallback_" + count++, s));
                    }
                }
                current.setLength(0);
            }
        }
        return items;
    }
}
