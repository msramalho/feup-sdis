package src.worker;

import src.main.PeerConfig;
import src.util.Logger;
import src.util.Message;
import src.worker.service.*;

import java.lang.reflect.InvocationTargetException;

public class Dispatcher implements Runnable {
    public Message message;
    public PeerConfig peerConfig;
    private static Logger logger = new Logger("Dispatcher");

    public Dispatcher(Message message, PeerConfig peerConfig) {
        this.message = message;
        this.peerConfig = peerConfig;
    }

    @Override
    public void run() {
        // dispatch the message to the proper protocol handler
        Protocol p = getProtocol();
        if (p != null) p.run();
        else logger.err("Unable to find and instantiate protocol class '" + getProtocolName("*") + "' with constructor (Dispatcher d)'");
    }


    /**
     * Try to find the protocol in the service package and then in the clustering package
     * @return
     */
    private Protocol getProtocol() {
        Protocol p = getProtocol("service");
        if (p != null) return p;
        return getProtocol("clustering");
    }

    /**
     * Uses reflection to get the protocol from the message action, according to the format given by getProtocolName
     *
     * @return the protocol to execute or null if none exists
     */
    private Protocol getProtocol(String packageName) {
        try {
            return (Protocol) Class.forName(getProtocolName(packageName)).getConstructor(Dispatcher.class).newInstance(this);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | ClassNotFoundException | NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * get the protcol name formatted form the message action
     *
     * @return a string with the expected classname for the protocol that handles this message
     */
    private String getProtocolName(String packageName) {
        return "src.worker." + packageName + ".P_" + message.action.substring(0, 1).toUpperCase() + message.action.substring(1).toLowerCase();
    }
}
