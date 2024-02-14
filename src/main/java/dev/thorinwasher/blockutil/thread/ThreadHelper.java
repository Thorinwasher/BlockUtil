package dev.thorinwasher.blockutil.thread;

import dev.thorinwasher.blockutil.BlockUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class ThreadHelper {
    private ThreadHelper(){
        throw new IllegalStateException("Utility class");
    }

    static final boolean FOLIA = foliaSupport();

    public static void runTaskAsync(Runnable runnable, Plugin plugin){
        if(FOLIA){
            Bukkit.getServer().getAsyncScheduler().runNow(plugin, ignored -> runnable.run());
        } else {
            new BukkitRunnable() {
                @Override
                public void run() {
                    runnable.run();
                }
            }.runTaskAsynchronously(plugin);
        }
    }

    private static boolean foliaSupport(){
        try{
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e){
            return false;
        }
    }

    public static void runGlobalTask(Runnable runnable, BlockUtil blockUtil) {
        if(FOLIA){
            Bukkit.getServer().getAsyncScheduler().runNow(blockUtil, (ignored) -> runnable.run());
        }
        new BukkitRunnable(){
            @Override
            public void run(){
                runnable.run();
            }
        }.runTask(blockUtil);
    }
}
