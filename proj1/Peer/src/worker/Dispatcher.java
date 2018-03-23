package src.worker;

import src.util.Message;

public class Dispatcher implements Runnable {
    Message message;

    public Dispatcher(Message message) {
        this.message = message;
    }

    @Override
    public void run() {
        System.out.println("\n[Dispatcher] - HELLO, this is dispatcher with message:\n" + message.toString());
        if (message.isBackup()) {

        }
    }
}
