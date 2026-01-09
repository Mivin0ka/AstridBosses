package ru.astridhanson.astridjoin.astridbosses.managers;

import net.milkbowl.vault.economy.Economy;
import org.black_ixx.playerpoints.PlayerPoints;
import org.black_ixx.playerpoints.PlayerPointsAPI;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import ru.astridhanson.astridjoin.astridbosses.data.Reward;

public class RewardManager {
   private final JavaPlugin plugin;
   private final MessageManager messageManager;
   private Economy economy;
   private PlayerPointsAPI playerPointsAPI;
   private boolean vaultEnabled;
   private boolean playerPointsEnabled;

   public RewardManager(JavaPlugin plugin, MessageManager messageManager) {
      this.plugin = plugin;
      this.messageManager = messageManager;
      this.setupEconomy();
      this.setupPlayerPoints();
   }

   private void setupEconomy() {
      if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
         this.vaultEnabled = false;
      } else {
         RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
         if (rsp != null) {
            this.economy = (Economy)rsp.getProvider();
            this.vaultEnabled = true;
         }
      }

   }

   private void setupPlayerPoints() {
      if (Bukkit.getPluginManager().isPluginEnabled("PlayerPoints")) {
         this.playerPointsAPI = PlayerPoints.getInstance().getAPI();
         this.playerPointsEnabled = this.playerPointsAPI != null;
      } else {
         this.playerPointsEnabled = false;
      }

   }

   public void giveReward(Player player, Reward reward) {
      switch(reward.getType()) {
      case VAULT:
         this.giveVaultReward(player, reward);
         break;
      case PLAYERPOINTS:
         this.givePlayerPointsReward(player, reward);
         break;
      case ITEM:
         this.giveItemReward(player, reward);
         break;
      case COMMAND:
         this.executeCommandReward(player, reward);
      }

   }

   private void giveVaultReward(Player player, Reward reward) {
      if (this.vaultEnabled && this.economy != null) {
         try {
            double amount = Double.parseDouble(reward.getValue());
            this.economy.depositPlayer(player, amount);
            this.messageManager.sendVaultReward(player, amount);
         } catch (NumberFormatException var5) {
            this.messageManager.sendInvalidVaultValue(reward.getValue());
         }
      } else {
         this.messageManager.sendVaultNotEnabled();
      }

   }

   private void givePlayerPointsReward(Player player, Reward reward) {
      if (!this.playerPointsEnabled) {
         this.messageManager.sendPlayerPointsNotEnabled();
      } else {
         try {
            int points = Integer.parseInt(reward.getValue());
            this.playerPointsAPI.give(player.getUniqueId(), points);
            this.messageManager.sendPlayerPointsReward(player, points);
         } catch (NumberFormatException var4) {
            this.messageManager.sendInvalidPlayerPointsValue(reward.getValue());
         }
      }

   }

   private void giveItemReward(Player player, Reward reward) {
      try {
         Material material = Material.valueOf(reward.getValue());
         ItemStack item = new ItemStack(material, reward.getAmount());
         player.getInventory().addItem(new ItemStack[]{item});
         this.messageManager.sendItemReward(player, reward.getAmount(), material.name());
      } catch (IllegalArgumentException var5) {
         this.messageManager.sendInvalidMaterial(reward.getValue());
      }

   }

   private void executeCommandReward(Player player, Reward reward) {
      String command = reward.getValue().replace("%player%", player.getName()).replace("%uuid%", player.getUniqueId().toString());
      Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
      this.messageManager.sendCommandRewardSuccess(player);
   }

   public boolean isVaultEnabled() {
      return this.vaultEnabled;
   }

   public boolean isPlayerPointsEnabled() {
      return this.playerPointsEnabled;
   }
}
