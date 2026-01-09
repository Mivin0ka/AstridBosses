package ru.astridhanson.astridjoin.astridbosses.gui;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ResourceGUI {
   public static void openMainMenu(Player player) {
      Inventory inv = Bukkit.createInventory((InventoryHolder)null, 27, "§6⚙ Ресурсы");
      ItemStack vault = createItem(Material.GOLD_BLOCK, "§6Vault (Деньги)", "§7Нажмите для управления");
      inv.setItem(11, vault);
      ItemStack points = createItem(Material.DIAMOND, "§bPlayerPoints", "§7Нажмите для управления");
      inv.setItem(13, points);
      ItemStack items = createItem(Material.CHEST, "§aПредметы", "§7Нажмите для управления");
      inv.setItem(15, items);
      player.openInventory(inv);
   }

   public static void openResourceEditor(Player player, String resourceType) {
      Inventory inv = Bukkit.createInventory((InventoryHolder)null, 36, "§6Редактор: " + resourceType);
      ItemStack input = createItem(Material.OAK_SIGN, "§eВвести значение", "§7Напишите в чат");
      inv.setItem(10, input);
      ItemStack confirm = createItem(Material.LIME_CONCRETE, "§a✓ Применить", "§7Подтвердить изменения");
      inv.setItem(31, confirm);
      ItemStack back = createItem(Material.RED_CONCRETE, "§c✕ Назад", "§7Вернуться в меню");
      inv.setItem(32, back);
      player.openInventory(inv);
   }

   private static ItemStack createItem(Material material, String name, String... lore) {
      ItemStack item = new ItemStack(material);
      ItemMeta meta = item.getItemMeta();
      meta.setDisplayName(name);
      List<String> loreList = new ArrayList();
      String[] var6 = lore;
      int var7 = lore.length;

      for(int var8 = 0; var8 < var7; ++var8) {
         String line = var6[var8];
         loreList.add(line);
      }

      meta.setLore(loreList);
      item.setItemMeta(meta);
      return item;
   }
}
