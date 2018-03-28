package src.worker;

import java.util.concurrent.ThreadLocalRandom;

public abstract class Protocol {
    Dispatcher d;

    public Protocol(Dispatcher d) {
        this.d = d;
    }

    public abstract void run();

    public void sleepRandom(){
        try {
            int sleepFor = ThreadLocalRandom.current().nextInt(10, 401);
            System.out.println(String.format("[Protocol:%9s] - sleep for %3d ms", d.message.action, sleepFor));
            Thread.sleep(sleepFor);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
