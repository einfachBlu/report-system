package de.blu.reportsystem.util;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Arrays;
import java.util.List;

public class ItemStackBuilder {
  private ItemStack itemStack;
  private ItemMeta itemMeta;

  public ItemStackBuilder() {
    this(new ItemStack(Material.STONE));
  }

  public ItemStackBuilder(ItemStack itemStack) {
    this.itemStack = itemStack;
    this.itemMeta = itemStack.getItemMeta();
  }

  public ItemStackBuilder withType(Material material) {
    this.itemStack.setType(material);
    this.itemMeta = this.itemStack.getItemMeta();
    return this;
  }

  public ItemStackBuilder withAmount(int amount) {
    this.itemStack.setAmount(amount);
    return this;
  }

  public ItemStackBuilder withDurability(int durability) {
    this.itemStack.setDurability((short) durability);
    this.itemMeta = this.itemStack.getItemMeta();
    return this;
  }

  public ItemStackBuilder withDisplayName(String displayName) {
    this.itemMeta.setDisplayName(displayName);
    return this;
  }

  public ItemStackBuilder withLore(String... lore) {
    return this.withLore(Arrays.asList(lore));
  }

  public ItemStackBuilder withLore(List<String> lore) {
    this.itemMeta.setLore(lore);
    return this;
  }

  public ItemStackBuilder addLore(String lore) {
    List<String> list = this.itemMeta.getLore();
    list.add(lore);
    this.itemMeta.setLore(list);
    return this;
  }

  public ItemStackBuilder addEnchant(Enchantment enchantment, int level) {
    this.itemMeta.addEnchant(enchantment, level, true);
    return this;
  }

  public ItemStackBuilder withUnbreakability() {
    return this.withUnbreakability(true);
  }

  public ItemStackBuilder withUnbreakability(boolean unbreakability) {
    this.itemMeta.setUnbreakable(unbreakability);
    return this;
  }

  public ItemStackBuilder withAuthor(String author) {
    if (this.itemMeta instanceof BookMeta) {
      ((BookMeta) this.itemMeta).setAuthor(author);
    }

    return this;
  }

  public ItemStackBuilder withPages(List<String> pages) {
    if (this.itemMeta instanceof BookMeta) {
      ((BookMeta) this.itemMeta).setPages(pages);
    }

    return this;
  }

  public ItemStackBuilder withColor(Color color) {
    if (this.itemMeta instanceof LeatherArmorMeta) {
      ((LeatherArmorMeta) this.itemMeta).setColor(color);
    }

    return this;
  }

  public ItemStackBuilder withOwner(String owner) {
    if (this.itemMeta instanceof SkullMeta) {
      ((SkullMeta) this.itemMeta).setOwner(owner);
    }

    return this;
  }

  public ItemStackBuilder clone() {
    ItemStack clonedItem = this.itemStack.clone();
    clonedItem.setItemMeta(this.itemMeta);
    return new ItemStackBuilder(clonedItem);
  }

  public ItemStack create() {
    this.itemStack.setItemMeta(this.itemMeta);

    return this.itemStack;
  }
}
