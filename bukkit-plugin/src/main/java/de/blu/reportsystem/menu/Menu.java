package de.blu.reportsystem.menu;

import com.google.inject.Inject;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Getter
public abstract class Menu implements Listener {

  protected Inventory inventory;
  private JavaPlugin plugin;

  protected Player player;
  protected String title = "";
  protected int size = -1;

  private Map<Integer, Consumer<InventoryClickEvent>> slotClickEvents = new HashMap<>();

  @Inject
  protected Menu(JavaPlugin plugin) {
    this.plugin = plugin;

    this.getPlugin().getServer().getPluginManager().registerEvents(this, this.getPlugin());
  }

  public Inventory getInventory() {
    if (this.getSize() == -1 || this.getTitle().equalsIgnoreCase("")) {
      return null;
    }

    if (this.inventory == null) {
      this.inventory =
              Bukkit.createInventory(
                      null,
                      this.getSize(),
                      this.getTitle().length() > 32 ? this.getTitle().substring(0, 32) : this.getTitle());
    }

    return this.inventory;
  }

  public void addClickableItem(int slot, ItemStack itemStack, Consumer<InventoryClickEvent> clickCallback) {
    this.getSlotClickEvents().put(slot, clickCallback);
    this.getInventory().setItem(slot, itemStack);
  }

  public void addClickableSlot(int slot, Consumer<InventoryClickEvent> clickCallback) {
    this.getSlotClickEvents().put(slot, clickCallback);
  }

  public void open(Player player) {
    this.player = player;
    new BukkitRunnable() {
      @Override
      public void run() {
        if (player == null) {
          throw new IllegalArgumentException("player is null");
        }

        if (Menu.this.getInventory() == null) {
          throw new IllegalArgumentException("inventory is null");
        }

        player.openInventory(Menu.this.getInventory());
      }
    }.runTaskLater(this.getPlugin(), 1);
  }

  protected void onMenuOpen(InventoryOpenEvent e) {}

  protected void onMenuClose(InventoryCloseEvent e) {}

  protected void onMenuClick(InventoryClickEvent e) {}

  @EventHandler
  public void onInventoryOpen(InventoryOpenEvent e) {
    if (!e.getView().getTopInventory().equals(this.getInventory())
            || e.getView().getTopInventory().getSize() != this.getSize()) {
      return;
    }

    this.onMenuOpen(e);
  }

  @EventHandler
  public void onInventoryClose(InventoryCloseEvent e) {
    if (!e.getView().getTopInventory().equals(this.getInventory())
            || e.getView().getTopInventory().getSize() != this.getSize()) {
      return;
    }

    this.onMenuClose(e);
  }

  @EventHandler
  public void onClickEvent(InventoryClickEvent e) {
    Player player = (Player) e.getWhoClicked();
    ItemStack clickedItem = e.getCurrentItem();

    if (!player.getOpenInventory().getTopInventory().equals(this.getInventory())
            || player.getOpenInventory().getTopInventory().getSize() != this.getSize()) {
      return;
    }

    this.onMenuClick(e);

    if (e.getClickedInventory() == null) {
      return;
    }

    InventoryView inventoryView = player.getOpenInventory();
    if (!inventoryView.getTopInventory().equals(this.getInventory())) {
      return;
    }

    List<ClickType> possibleClickTypes =
            Arrays.asList(ClickType.LEFT, ClickType.RIGHT, ClickType.SHIFT_LEFT, ClickType.SHIFT_RIGHT);
    if (!possibleClickTypes.contains(e.getClick())) {
      return;
    }

    if (!e.getClickedInventory().equals(this.getInventory())
            || !this.getSlotClickEvents().containsKey(e.getSlot())) {
      return;
    }

    this.getSlotClickEvents().get(e.getSlot()).accept(e);
  }
}
