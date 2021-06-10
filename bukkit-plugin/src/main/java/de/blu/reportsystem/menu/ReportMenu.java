package de.blu.reportsystem.menu;

import de.blu.reportsystem.config.MainConfig;
import de.blu.reportsystem.data.Report;
import de.blu.reportsystem.data.ReportState;
import de.blu.reportsystem.exception.ServiceUnreachableException;
import de.blu.reportsystem.util.InventoryHelper;
import de.blu.reportsystem.util.ItemStackBuilder;
import de.blu.reportsystem.util.ReportWebExecutor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import javax.inject.Inject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

public final class ReportMenu extends Menu {

  private static final int CONTENT_ROWS = 3;

  @Inject private MainConfig mainConfig;
  @Inject private ReportWebExecutor reportWebExecutor;
  @Inject private ExecutorService executorService;

  private int currentPage = 1;

  private List<Report> reports = new ArrayList<>();
  private Report selectedReport;

  @Inject
  private ReportMenu(JavaPlugin plugin) {
    super(plugin);
  }

  @Override
  public void open(Player player) {
    this.size = 9 * 5;
    this.title = "Reports";

    // Load all Reports
    this.loadReports();

    super.open(player);
  }

  private void loadReports() {
    this.reports.clear();
    this.executorService.submit(
        () -> {
          try {
            List<Report> reports =
                this.reportWebExecutor.getReports(this.mainConfig.getServiceUrl());

            this.reports.addAll(reports);
            this.updateContent();
          } catch (ServiceUnreachableException e) {
            player.closeInventory();
            player.sendMessage(
                "§cThe Report-Service is currently unavailable. Please try again later.");
          }
        });
  }

  private void updateContent() {
    this.getInventory().clear();
    this.getSlotClickEvents().clear();

    if (this.selectedReport != null) {
      this.showReportDetails();
    } else {
      this.showReportList();
    }
  }

  private void showReportDetails() {
    ItemStack placeHolderGlass;

    boolean isInProcess = this.selectedReport.getReportState().equals(ReportState.IN_PROCESS);

    if (isInProcess) {
      placeHolderGlass = this.getPlaceHolderGlass(Material.ORANGE_STAINED_GLASS_PANE);
    } else {
      placeHolderGlass = this.getPlaceHolderGlass(Material.WHITE_STAINED_GLASS_PANE);
    }

    InventoryHelper.fill(this.getInventory(), placeHolderGlass);
    InventoryHelper.line(this.getInventory(), new ItemStack(Material.AIR), 3);

    this.getInventory().setItem(22, this.getReportIcon(this.selectedReport));

    ItemStack changeStateItemStack =
        new ItemStackBuilder()
            .withType(Material.KNOWLEDGE_BOOK)
            .withDisplayName(
                "§aChange the State to " + (isInProcess ? "§f§lNOT IN PROCESS" : "§6§lIN PROCESS"))
            .create();
    ItemStack deleteReportItemStack =
        new ItemStackBuilder()
            .withType(Material.BARRIER)
            .withDisplayName("§c§lDelete Report")
            .create();

    this.addClickableItem(
        19,
        changeStateItemStack,
        e -> {
          this.executorService.submit(
              () -> {
                this.selectedReport.setReportState(
                    this.selectedReport.getReportState().equals(ReportState.NONE)
                        ? ReportState.IN_PROCESS
                        : ReportState.NONE);
                if (this.selectedReport.getReportState().equals(ReportState.IN_PROCESS)) {
                  this.selectedReport.setReportEditingPlayerId(this.player.getUniqueId());
                  this.selectedReport.setReportEditingPlayerName(this.player.getName());
                } else {
                  this.selectedReport.setReportEditingPlayerId(null);
                  this.selectedReport.setReportEditingPlayerName(null);
                }

                try {
                  this.reportWebExecutor.updateReport(
                      this.mainConfig.getServiceUrl(), this.selectedReport);
                  this.updateContent();
                } catch (ServiceUnreachableException serviceUnreachableException) {
                  this.player.sendMessage(
                      "§cThe Report-Service is currently unavailable. Please try again later.");
                }
              });
        });

    this.addClickableItem(
        25,
        deleteReportItemStack,
        e -> {
          this.executorService.submit(
              () -> {
                try {
                  this.reportWebExecutor.deleteReport(
                      this.mainConfig.getServiceUrl(), this.selectedReport.getId());
                  this.selectedReport = null;
                  this.updateContent();
                  this.loadReports();
                } catch (ServiceUnreachableException serviceUnreachableException) {
                  this.player.sendMessage(
                      "§cThe Report-Service is currently unavailable. Please try again later.");
                }
              });
        });
  }

  private void showReportList() {
    ItemStack placeHolderGlass = this.getPlaceHolderGlass();
    InventoryHelper.line(this.getInventory(), placeHolderGlass, (this.getSize() / 9) - 1);

    this.addClickableItem(
        this.getSize() - 9,
        this.getPreviousPageIcon(),
        e -> {
          if (this.currentPage - 1 < 1) {
            return;
          }

          this.currentPage--;
          this.updateContent();
        });
    this.addClickableItem(
        this.getSize() - 1,
        this.getNextPageIcon(),
        e -> {
          if (this.currentPage + 1 > this.getLastPage()) {
            return;
          }

          this.currentPage++;
          this.updateContent();
        });

    // Load Reports of current Page
    List<Report> reports =
        this.reports.stream()
            .skip((this.currentPage - 1) * (9 * CONTENT_ROWS))
            .limit(9 * CONTENT_ROWS)
            .collect(Collectors.toList());

    int i = 0;
    for (Report report : reports) {
      this.addClickableItem(
          i,
          this.getReportIcon(report),
          e -> {
            // Show only details to the player who claimed this report
            if (report.getReportState().equals(ReportState.IN_PROCESS)) {
              if (!this.player.getUniqueId()
                  .equals(report
                          .getReportEditingPlayerId())) {
                return;
              }
            }

            this.selectedReport = report;
            this.updateContent();
          });

      i++;
    }
  }

  @Override
  protected void onMenuClick(InventoryClickEvent e) {
    e.setCancelled(true);

    if (e.getClickedInventory() == null && this.selectedReport != null) {
      this.selectedReport = null;
      this.updateContent();
    }
  }

  @Override
  protected void onMenuClose(InventoryCloseEvent e) {
    Player player = (Player) e.getPlayer();
    if (!player.isOnline()) {
      HandlerList.unregisterAll(this);
      return;
    }

    if (this.selectedReport == null) {
      HandlerList.unregisterAll(this);
      return;
    }

    this.selectedReport = null;
    this.updateContent();
    new BukkitRunnable() {
      @Override
      public void run() {
        player.openInventory(ReportMenu.this.getInventory());
      }
    }.runTask(this.getPlugin());
  }

  public int getLastPage() {
    return (int) Math.round((this.reports.size() / (9 * CONTENT_ROWS)) + 0.5);
  }

  public ItemStack getPlaceHolderGlass() {
    return new ItemStackBuilder()
        .withType(Material.BLACK_STAINED_GLASS_PANE)
        .withDisplayName(" ")
        .create();
  }

  public ItemStack getPlaceHolderGlass(Material material) {
    return new ItemStackBuilder().withType(material).withDisplayName(" ").create();
  }

  public ItemStack getNextPageIcon() {
    return new ItemStackBuilder()
        .withType(Material.PAPER)
        .withDisplayName("§bNext Page =>")
        .create();
  }

  public ItemStack getPreviousPageIcon() {
    return new ItemStackBuilder()
        .withType(Material.PAPER)
        .withDisplayName("§b<= Previous Page")
        .create();
  }

  private ItemStack getReportIcon(Report report) {
    Date date = new Date(report.getTime());
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy - HH:mm");

    List<String> lore = new ArrayList<>();
    lore.add("§7Reported by: §e" + report.getReportSenderPlayerName());
    lore.add("§7Reported Time: §e" + dateFormat.format(date));

    if (report.getReportState().equals(ReportState.IN_PROCESS)) {
      lore.add("");
      lore.add("§6§lIN EDIT! §r§6Currently edited by §e" + report.getReportEditingPlayerName());
    }

    return new ItemStackBuilder()
        .withType(
            report.getReportState().equals(ReportState.IN_PROCESS)
                ? Material.ORANGE_WOOL
                : Material.WHITE_WOOL)
        .withDisplayName(
            "§6Report of "
                + report.getReportReceiverPlayerName()
                + " §7[§e"
                + report.getReason()
                + "§7]")
        .withLore(lore)
        .create();
  }
}
