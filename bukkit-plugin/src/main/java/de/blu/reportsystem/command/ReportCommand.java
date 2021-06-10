package de.blu.reportsystem.command;

import de.blu.reportsystem.config.MainConfig;
import de.blu.reportsystem.exception.ServiceUnreachableException;
import de.blu.reportsystem.util.ReportWebExecutor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;

@Singleton
public final class ReportCommand implements CommandExecutor {

  @Inject private MainConfig mainConfig;
  @Inject private ReportWebExecutor reportWebExecutor;
  @Inject private ExecutorService executorService;

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

    if (args.length < 2) {
      this.sendUsage(player);
      return false;
    }

    Player targetPlayer = Bukkit.getPlayer(args[0]);
    if (targetPlayer == null) {
      player.sendMessage("§cThis Player is not online!");
      return false;
    }

    String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

    this.executorService.submit(
        () -> {
          try {
            this.reportWebExecutor.createReport(
                this.mainConfig.getServiceUrl(),
                targetPlayer.getUniqueId(),
                targetPlayer.getName(),
                player.getUniqueId(),
                player.getName(),
                reason);
            player.sendMessage(
                String.format(
                    "§aYou reported §e%s §afor Reason §e%s§a.", targetPlayer.getName(), reason));
          } catch (ServiceUnreachableException e) {
            player.sendMessage(
                "§cThe §lReport-Service§r§c is currently unavailable. Please try again later.");
          }
        });

    return true;
  }

  private void sendUsage(Player player) {
    player.sendMessage("§eUsage: /report <player> <reason>");
  }
}
