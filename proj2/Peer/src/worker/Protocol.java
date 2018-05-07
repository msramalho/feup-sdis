package src.worker;

import src.util.LockException;
import src.util.Logger;
import src.util.Utils;

import java.net.UnknownHostException;
import java.util.concurrent.ThreadLocalRandom;

public abstract class Protocol {
    protected Dispatcher d;
    protected Logger logger = new Logger(this);

    public Protocol(Dispatcher d) { this.d = d; }

    public abstract void run() throws LockException, UnknownHostException;

    /**
     * sleep time is longer for Peers with lower percentage of disk space occupied
     * for a peer with 0% space occupied, interval is random between [0, 400]
     * for a peer with 100% space occupied, interval is random between [400, 400]
     * ENHANCEMENT_1
     */
    protected void sleepRandom() {
        int percentOccupied = 0;
        // only use heuristic if this peer is enhanced and the message is a PUTCHUNK
        if (d.peerConfig.isEnhanced() && d.message.isPutchunk()) {
            percentOccupied = (int) (100 * (d.peerConfig.internalState.occupiedSpace / d.peerConfig.internalState.allowedSpace));
            percentOccupied = Math.min(percentOccupied, 100); // if memory exceeds 100%
        }
        sleepRandom(4 * percentOccupied);
    }

    private void sleepRandom(int from) {
        int miliseconds = ThreadLocalRandom.current().nextInt(from, 401);
        logger.print(String.format("sleep for %3d ms", miliseconds));
        sleep(miliseconds);
    }

    protected void sleep(int miliseconds) { Utils.sleep(miliseconds); }


}
