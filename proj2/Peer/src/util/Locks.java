package src.util;

import java.util.HashSet;

/**
 * Provide a simple interface for classes to use that allow to communicate between threads
 */
public class Locks {
    HashSet<String> locks = new HashSet<>();

    public boolean lock(String lock) throws LockException {
        if (locked(lock)) { throw new LockException("Already locked for " + lock); }
        return locks.add(lock);
    }

    public void unlock(String lock) { locks.remove(lock); }

    public boolean locked(String lock) { return locks.contains(lock); }
}
