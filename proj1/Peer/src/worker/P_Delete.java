package src.worker;

import java.util.concurrent.atomic.AtomicReference;

public class P_Delete extends Protocol {
    public P_Delete(Dispatcher d) { super(d); }

    @Override
    public void run() {
        AtomicReference<Integer> count = new AtomicReference<>(0);
        d.peerConfig.internalState.storedChunks.forEach(1, (k, v) -> {
            // System.out.println(String.format("[P_Delete] - checking %s against %s", v.fileId.substring(0, 10), d.message.fileId.substring(0, 10)));
            if (v.fileId.equals(d.message.fileId) && v.isSavedLocally()) {
                d.peerConfig.internalState.deleteStoredChunk(v, true);
                count.getAndSet(count.get() + 1);
            }
        });
        System.out.println(String.format("[Protocol:Delete] - deleted %d chunk(s)", count.get()));
    }
}
