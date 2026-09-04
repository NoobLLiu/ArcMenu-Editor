package com.fentai.arcmenu.editor.net;

import com.fentai.arcmenu.protocol.EditorProtocol;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public final class FrameAssembler {
    private static final long MAX_AGE_NANOS = 10_000_000_000L;
    private final Map<Integer, Pending> pending = new HashMap<>();

    public byte[] accept(byte[] bytes) {
        EditorProtocol.WireFrame frame = EditorProtocol.decodeFrame(bytes);
        long now = System.nanoTime();
        pending.values().removeIf(value -> now - value.startedAt > MAX_AGE_NANOS);
        Pending value = pending.computeIfAbsent(frame.messageId(), ignored -> new Pending(frame.count(), now));
        if (value.parts.length != frame.count()) {
            pending.remove(frame.messageId());
            throw new IllegalArgumentException("ArcMenu editor frame count changed mid-message");
        }
        if (value.parts[frame.index()] == null) {
            value.parts[frame.index()] = frame.data();
            value.received++;
            value.size += frame.data().length;
        }
        if (value.size > EditorProtocol.MAX_PACKET_BYTES) {
            pending.remove(frame.messageId());
            throw new IllegalArgumentException("ArcMenu editor message is too large");
        }
        if (value.received != value.parts.length) return null;
        pending.remove(frame.messageId());
        ByteArrayOutputStream output = new ByteArrayOutputStream(value.size);
        for (byte[] part : value.parts) output.writeBytes(part);
        return output.toByteArray();
    }

    public void clear() {
        pending.clear();
    }

    private static final class Pending {
        private final byte[][] parts;
        private final long startedAt;
        private int received;
        private int size;

        private Pending(int count, long startedAt) {
            this.parts = new byte[count][];
            this.startedAt = startedAt;
        }
    }
}
