package de.blu.reportsystem.util;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryHelper {

  /**
   * fill all slots in an inventory with an item
   *
   * @param inventory inventory
   * @param item item
   */
  public static void fill(Inventory inventory, ItemStack item) {
    for (int i = 0; i < inventory.getSize(); i++) {
      inventory.setItem(i, item);
    }
  }

  /**
   * fill all slots in a line with an item
   *
   * @param inventory inventory
   * @param item item
   * @param row row, starting from 1
   */
  public static void line(Inventory inventory, ItemStack item, int row) {
    for (int i = 0; i < 9; i++) {
      inventory.setItem((row - 1) * 9 + i, item);
    }
  }

  /**
   * fill the border of an inventory with an item
   *
   * @param inventory inventory
   * @param item item
   */
  public static void border(Inventory inventory, ItemStack item) {
    for (int row = 0; row < inventory.getSize() / 9; row++) {
      if (row == 0 || row == inventory.getSize() / 9 - 1) {
        line(inventory, item, row + 1);
        continue;
      }
      inventory.setItem(row * 9, item);
      inventory.setItem(row * 9 + 8, item);
    }
  }

  /**
   * Check if an ItemStack can be added to the inventory
   *
   * @param inventory the inventory to add the item
   * @param itemStack the itemStack with amount to add
   * @return the amount which can be added
   */
  public static int canAdd(Inventory inventory, ItemStack itemStack) {
    int amount = itemStack.getAmount();

    for (int i = 0; i < inventory.getSize(); i++) {
      ItemStack slotItem = inventory.getItem(i);
      if (slotItem == null) {
        amount -= itemStack.getType().getMaxStackSize();

        if (amount <= 0) {
          return itemStack.getAmount();
        }
        continue;
      }

      if (!slotItem.getType().equals(itemStack.getType())) {
        continue;
      }

      amount -= slotItem.getType().getMaxStackSize() - slotItem.getAmount();

      if (amount <= 0) {
        return itemStack.getAmount();
      }
    }

    return itemStack.getAmount() - amount;
  }
}
