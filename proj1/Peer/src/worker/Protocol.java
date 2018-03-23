package src.worker;

public abstract class Protocol {
    Dispatcher d;

    public Protocol(Dispatcher d) {
        this.d = d;
    }

    public abstract void run();
}
