package com.fentai.arcmenu.editor;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.Test;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class EditorLanguageResourcesTest {
    private static final Pattern PLACEHOLDER = Pattern.compile("%(?:\\d+\\$)?[sdf]");

    @Test
    void bundledClientLanguagesHaveIdenticalNonemptyKeys() {
        JsonObject chinese = resource("/assets/arcmenu_editor/lang/zh_cn.json");
        JsonObject english = resource("/assets/arcmenu_editor/lang/en_us.json");
        assertEquals(chinese.keySet(), english.keySet());
        assertTrue(chinese.size() >= 100);
        for (String key : chinese.keySet()) {
            assertTrue(chinese.get(key).getAsString().length() > 0, key);
            assertTrue(english.get(key).getAsString().length() > 0, key);
            assertEquals(placeholders(chinese.get(key).getAsString()), placeholders(english.get(key).getAsString()), key);
        }
    }

    private JsonObject resource(String path) {
        var stream = getClass().getResourceAsStream(path);
        assertNotNull(stream, path);
        return JsonParser.parseReader(new InputStreamReader(stream, StandardCharsets.UTF_8)).getAsJsonObject();
    }

    private long placeholders(String value) {
        return PLACEHOLDER.matcher(value).results().count();
    }
}
