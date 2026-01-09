package ru.astridhanson.astridjoin.astridbosses.listeners;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import ru.astridhanson.astridjoin.astridbosses.gui.ResourceGUI;

public class ResourceGUIListener implements Listener {
   private Map<Player, String> editingResource = new HashMap();

   @EventHandler
   public void onInventoryClick(InventoryClickEvent event) {
      Player player = (Player)event.getWhoClicked();
      String title = event.getView().getTitle();
      if (title.contains("§6⚙ Ресурсы")) {
         event.setCancelled(true);
         if (event.getSlot() == 11) {
            this.editingResource.put(player, "vault");
            ResourceGUI.openResourceEditor(player, "Vault");
         } else if (event.getSlot() == 13) {
            this.editingResource.put(player, "playerpoints");
            ResourceGUI.openResourceEditor(player, "PlayerPoints");
         } else if (event.getSlot() == 15) {
            this.editingResource.put(player, "items");
            ResourceGUI.openResourceEditor(player, "Предметы");
         }
      } else if (title.contains("§6Редактор:")) {
         event.setCancelled(true);
         if (event.getSlot() == 31) {
            player.sendMessage("§aВведите значение в чат:");
            player.closeInventory();
         } else if (event.getSlot() == 32) {
            ResourceGUI.openMainMenu(player);
         }
      }

   }

   @EventHandler
   public void onPlayerChat(AsyncPlayerChatEvent event) {
      Player player = event.getPlayer();
      if (this.editingResource.containsKey(player)) {
         event.setCancelled(true);
         String resource = (String)this.editingResource.get(player);
         String value = event.getMessage();
         player.sendMessage("§a✓ Установлено " + resource + " значение: " + value);
         this.editingResource.remove(player);
      }

   }
}
