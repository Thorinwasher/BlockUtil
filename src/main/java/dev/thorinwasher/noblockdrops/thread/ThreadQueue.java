package dev.thorinwasher.noblockdrops.thread;

import org.bukkit.plugin.Plugin;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ThreadQueue {
    private ThreadQueue(){
        throw new IllegalStateException("Utility class");
    }
    private static final BlockingQueue<Runnable> asyncQueue = new LinkedBlockingQueue<>();
    private static boolean enabled = false;

    public static void enableQueue(Plugin plugin){
        addToQueue(() -> enabled = true);
        ThreadHelper.runTaskAsync(ThreadQueue::cycleThroughQueue,plugin);
    }

    public static void disableQueue(){
        addToQueue(() -> enabled = false);
    }

    public static void addToQueue(Runnable runnable){
        try {
            asyncQueue.put(runnable);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void cycleThroughQueue(){
        do {
            try {
                Runnable runnable = asyncQueue.take();
                runnable.run();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while (enabled || !asyncQueue.isEmpty());
    }
}
