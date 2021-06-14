package de.blu.reportsystem.util;

import de.blu.reportsystem.ReportPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public final class SchedulerHelper {
  public static void runSync(Runnable runnable) {
    new BukkitRunnable() {
      @Override
      public void run() {
        runnable.run();
      }
    }.runTask(ReportPlugin.getInstance());
  }
}
