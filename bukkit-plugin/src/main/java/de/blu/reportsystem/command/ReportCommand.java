package de.blu.reportsystem.command;

import de.blu.reportsystem.config.MainConfig;
import de.blu.reportsystem.data.Report;
import de.blu.reportsystem.exception.ServiceUnreachableException;
import de.blu.reportsystem.util.ReportWebExecutor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Singleton
public final class ReportCommand implements CommandExecutor, TabCompleter {

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

    String reason = String.join(" ", Arrays.copyOfRange(args, 1, args.length)).toUpperCase();

    if (this.mainConfig.isUseSpecifiedReasons()) {
      // Validate Reason
      if (!this.mainConfig.getReasons().stream()
          .map(String::toUpperCase)
          .collect(Collectors.toList())
          .contains(reason)) {
        player.sendMessage("§cThis Reason is not valid. Try out one of these:");
        player.sendMessage(
            "§e"
                + this.mainConfig.getReasons().stream()
                    .map(String::toUpperCase)
                    .collect(Collectors.joining("§7, §e")));
        return false;
      }
    }

    this.executorService.submit(
        () -> {
          try {
            List<Report> reports =
                this.reportWebExecutor
                    .getReportsByPlayer(this.mainConfig.getServiceUrl(), player.getUniqueId())
                    .stream()
                    .filter(report -> report.getReportSenderPlayerId().equals(player.getUniqueId()))
                    .collect(Collectors.toList());
            if (reports.size() >= this.mainConfig.getPlayerMaxAmountOfReports()) {
              player.sendMessage(
                  "§cYou reached the maximum amount of reports. Your can create new Reports after your previous Reports was marked as done.");
              return;
            }

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

  @Override
  public @Nullable List<String> onTabComplete(
      @NotNull CommandSender sender,
      @NotNull Command command,
      @NotNull String alias,
      @NotNull String[] args) {
    List<String> completions = new ArrayList<>();

    if (args.length == 0) {
      return completions;
    }

    if (args.length == 1) {
      completions.addAll(
          Bukkit.getOnlinePlayers().stream()
              .map(HumanEntity::getName)
              .filter(playerName -> playerName.toLowerCase().startsWith(args[0].toLowerCase()))
              .collect(Collectors.toList()));
      return completions;
    }

    if (args.length == 2) {
      completions.addAll(
          this.mainConfig.getReasons().stream()
              .map(String::toUpperCase)
              .filter(reason -> reason.toLowerCase().startsWith(args[1].toLowerCase()))
              .collect(Collectors.toList()));
      return completions;
    }

    return completions;
  }
}
