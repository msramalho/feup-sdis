package src.util;

import java.util.concurrent.ThreadLocalRandom;

public class Utils {
    public static String capitalize(String original) {
        return original.substring(0, 1).toUpperCase() + original.substring(1).toLowerCase();
    }

    static boolean isInt(String source) {
        try {
            Integer.parseInt(source);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public static void sleep(Integer miliseconds) { try { Thread.sleep(miliseconds); } catch (InterruptedException ignored) { } }

    public static void sleepRandom(Integer from, Integer to) {
        sleep(ThreadLocalRandom.current().nextInt(from, to)); // milliseconds
    }

    public static class ClusterInfo {
        public int level;
        public int clusterId;

        ClusterInfo(int level, int clusterId) {
            this.level = level;
            this.clusterId = clusterId;
        }
    }

    public static ClusterInfo splitCluster(String original) {
        String[] parts = original.split(":"); // level:clusterId
        return new Utils.ClusterInfo(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]));
    }
}
