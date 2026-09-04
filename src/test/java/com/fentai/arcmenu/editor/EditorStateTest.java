package com.fentai.arcmenu.editor;

import com.fentai.arcmenu.protocol.EditorProtocol;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EditorStateTest {
    @Test
    void ctrlAndShiftSelectionFollowVisibleOrder() {
        EditorState state = state();
        List<String> order = List.of("a", "b", "c", "d");

        state.selectOnly("b");
        state.toggleSelection("d");
        assertEquals(List.of("b", "d"), state.selectedIds().stream().toList());

        state.selectRange(order, "b");
        assertEquals(List.of("b", "c", "d"), state.selectedIds().stream().toList());
        assertEquals("b", state.selectedId());
    }

    @Test
    void authoritativeSnapshotDropsOnlyMissingSelections() {
        EditorState state = state();
        state.selectOnly("a");
        state.toggleSelection("c");
        state.update(snapshot(List.of(node("a"), node("b"))));

        assertTrue(state.selected("a"));
        assertFalse(state.selected("c"));
        assertEquals("a", state.selectedId());
    }

    private static EditorState state() {
        EditorState state = new EditorState();
        state.update(snapshot(List.of(node("a"), node("b"), node("c"), node("d"))));
        return state;
    }

    private static EditorProtocol.SnapshotPacket snapshot(List<EditorProtocol.NodeSnapshot> nodes) {
        return new EditorProtocol.SnapshotPacket(0, "menu", 320, 180, false, true, "26.1.2", nodes, List.of());
    }

    private static EditorProtocol.NodeSnapshot node(String id) {
        return new EditorProtocol.NodeSnapshot(id, "", EditorProtocol.KIND_RECTANGLE,
                0, 0, 10, 10, 0, true, false);
    }
}
