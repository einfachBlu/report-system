package de.blu.reportsystem.command;

import com.google.inject.Injector;
import de.blu.reportsystem.config.MainConfig;
import de.blu.reportsystem.menu.ReportMenu;
import de.blu.reportsystem.util.ReportWebExecutor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.concurrent.ExecutorService;

@Singleton
public final class ReportsCommand implements CommandExecutor {

  @Inject private MainConfig mainConfig;
  @Inject private ReportWebExecutor reportWebExecutor;
  @Inject private ExecutorService executorService;
  @Inject private Injector injector;

  @Override
  public boolean onCommand(
      @NotNull CommandSender sender,
      @NotNull Command command,
      @NotNull String label,
      @NotNull String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage("§cOnly executeable via ingame");
      return false;
    }

    Player player = (Player) sender;
    this.injector.getInstance(ReportMenu.class).open(player);
    return true;
  }
}
