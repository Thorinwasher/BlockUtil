package dev.thorinwasher.blockutil.thread;

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
}
