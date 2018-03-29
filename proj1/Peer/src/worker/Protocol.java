package src.worker;

import java.util.concurrent.ThreadLocalRandom;

public abstract class Protocol {
    Dispatcher d;

    public Protocol(Dispatcher d) {
        this.d = d;
    }

    public abstract void run();

    /**
     * sleep time is longer for Peers with lower percentage of disk space occupied
     * for a peer with 0% space occupied, interval is random between [0, 400]
     * for a peer with 100% space occupied, interval is random between [400, 400]
     */
    public void sleepRandomConsiderDiskSpace() {
        int percentOccupied = (int) (100 * (d.peerConfig.internalState.occupiedSpace / d.peerConfig.internalState.allowedSpace));
        percentOccupied = Math.min(percentOccupied, 100); // if memory exceeds 100%
        System.out.println(String.format("[Protocol:%9s] - percentOccupied %d = min %d ms", d.message.action, percentOccupied, 4 * percentOccupied));
        sleepRandom(4 * percentOccupied);
    }

    public void sleepRandom() {sleepRandom(0);}

    public void sleepRandom(int from) {
        try {
            int sleepFor = ThreadLocalRandom.current().nextInt(from, 401);
            System.out.println(String.format("[Protocol:%9s] - sleep for %3d ms", d.message.action, sleepFor));
            Thread.sleep(sleepFor);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
