package ru.astridhanson.astridjoin.astridbosses.managers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import ru.astridhanson.astridjoin.astridbosses.config.ConfigManager;

public class MessageManager {
   private final ConfigManager configManager;

   public MessageManager(ConfigManager configManager) {
      this.configManager = configManager;
   }

   public void sendBossSpawnedAnnouncement(String bossName, double x, double y, double z, String world) {
      String announcement = this.getMessage("messages.boss.spawn.announcement");
      this.broadcastMessage(announcement);
      this.broadcastMessage(this.getMessage("messages.boss.spawn.title"));
      this.broadcastMessage("");
      this.broadcastMessage("  " + bossName);
      this.broadcastMessage(this.getMessage("messages.boss.spawn.health").replace("%health%", String.valueOf((int)y)));
      this.broadcastMessage(this.getMessage("messages.boss.spawn.damage").replace("%damage%", "0"));
      this.broadcastMessage(this.getMessage("messages.boss.spawn.coordinates").replace("%x%", String.valueOf((int)x)).replace("%y%", String.valueOf((int)y)).replace("%z%", String.valueOf((int)z)));
      this.broadcastMessage(this.getMessage("messages.boss.spawn.world").replace("%world%", world));
      this.broadcastMessage("");
      this.broadcastMessage(this.getMessage("messages.boss.spawn.hint"));
      this.broadcastMessage(announcement);
   }

   public void sendBossDeathAnnouncement(String bossName) {
      String announcement = this.getMessage("messages.boss.death.announcement");
      this.broadcastMessage(announcement);
      this.broadcastMessage(this.getMessage("messages.boss.death.title"));
      this.broadcastMessage("");
      this.broadcastMessage(this.getMessage("messages.boss.death.killed").replace("%boss%", bossName));
      this.broadcastMessage("");
   }

   public void sendBossDeathEnd() {
      String announcement = this.getMessage("messages.boss.death.announcement");
      this.broadcastMessage(announcement);
   }

   public void sendTopDamagerMessage(String playerName, int place, double damage) {
      String medal = this.getMedalEmoji(place);
      String message = this.getMessage("messages.boss.death.top_damager").replace("%medal%", medal).replace("%place%", String.valueOf(place)).replace("%player%", playerName).replace("%damage%", String.format("%.0f", damage));
      this.broadcastMessage("  " + message);
   }

   public void sendRewardsInfo(int rewardCount) {
      this.broadcastMessage("  " + this.getMessage("messages.boss.death.rewards").replace("%count%", String.valueOf(rewardCount)));
   }

   public void sendPlayerTopRewardMessage(Player player, int place) {
      player.sendMessage(this.getMessage("messages.rewards.top_reward").replace("%place%", String.valueOf(place)));
   }

   public void sendCommandErrorUnknown(Player sender) {
      sender.sendMessage(this.getMessage("messages.commands.errors.unknown_command"));
   }

   public void sendCommandSpawnUsage(Player sender) {
      sender.sendMessage(this.getMessage("messages.commands.spawn.usage"));
   }

   public void sendCommandSpawnSuccess(Player sender, String bossId, double x, double y, double z, String world) {
      sender.sendMessage(this.getMessage("messages.commands.spawn.success").replace("%id%", bossId));
      sender.sendMessage(this.getMessage("messages.commands.spawn.coordinates").replace("%x%", String.valueOf((int)x)).replace("%y%", String.valueOf((int)y)).replace("%z%", String.valueOf((int)z)));
      sender.sendMessage(this.getMessage("messages.commands.spawn.world").replace("%world%", world));
   }

   public void sendCommandSpawnBossNotFound(Player sender, String bossId) {
      sender.sendMessage(this.getMessage("messages.commands.spawn.boss_not_found").replace("%id%", bossId));
   }

   public void sendCommandKillUsage(Player sender) {
      sender.sendMessage(this.getMessage("messages.commands.kill.usage"));
   }

   public void sendCommandKillSuccess(Player sender, String bossId) {
      sender.sendMessage(this.getMessage("messages.commands.kill.success").replace("%id%", bossId));
   }

   public void sendCommandKillNotFound(Player sender, String bossId) {
      sender.sendMessage(this.getMessage("messages.commands.kill.boss_not_found").replace("%id%", bossId));
   }

   public void sendCommandListTitle(Player sender) {
      sender.sendMessage(this.getMessage("messages.commands.list.title"));
   }

   public void sendCommandListEmpty(Player sender) {
      sender.sendMessage(this.getMessage("messages.commands.list.empty"));
   }

   public void sendCommandListBoss(Player sender, String bossName, int health) {
      sender.sendMessage(this.getMessage("messages.commands.list.boss").replace("%boss%", bossName).replace("%health%", String.valueOf(health)));
   }

   public void sendCommandTopUsage(Player sender) {
      sender.sendMessage(this.getMessage("messages.commands.top.usage"));
   }

   public void sendCommandTopInvalidIndex(Player sender) {
      sender.sendMessage(this.getMessage("messages.commands.top.invalid_index"));
   }

   public void sendCommandTopTitle(Player sender) {
      sender.sendMessage(this.getMessage("messages.commands.top.title"));
   }

   public void sendCommandTopDamager(Player sender, int place, String playerName, double damage) {
      sender.sendMessage(this.getMessage("messages.commands.top.damager").replace("%place%", String.valueOf(place)).replace("%player%", playerName).replace("%damage%", String.format("%.2f", damage)));
   }

   public void sendCommandTop3Title(Player sender) {
      sender.sendMessage(this.getMessage("messages.commands.top3.title"));
   }

   public void sendCommandTop3Damager(Player sender, int place, String playerName, double damage) {
      String medal = this.getMedalEmoji(place);
      String var10001 = this.getMessage("messages.commands.top3.damager").replace("%medal%", medal).replace("%place%", String.valueOf(place)).replace("%player%", playerName);
      sender.sendMessage("  " + var10001.replace("%damage%", String.format("%.2f", damage)));
   }

   public void sendCommandInfoTitle(Player sender, String bossName) {
      sender.sendMessage(this.getMessage("messages.commands.info.title").replace("%boss%", bossName));
   }

   public void sendCommandInfoLine(Player sender, String label, String value) {
      sender.sendMessage(this.getMessage("messages.commands.info.line").replace("%label%", label).replace("%value%", value));
   }

   public void sendCommandInfoNotFound(Player sender) {
      sender.sendMessage(this.getMessage("messages.commands.info.not_found"));
   }

   public void sendCommandGPSTitle(Player sender) {
      sender.sendMessage(this.getMessage("messages.commands.gps.title"));
   }

   public void sendCommandGPSEmpty(Player sender) {
      sender.sendMessage(this.getMessage("messages.commands.gps.empty"));
   }

   public void sendCommandGPSBoss(Player sender, int number, String bossName) {
      sender.sendMessage("§b" + number + ". " + bossName);
   }

   public void sendCommandGPSCoordinates(Player sender, int x, int y, int z) {
      sender.sendMessage(this.getMessage("messages.commands.gps.coordinates").replace("%x%", String.valueOf(x)).replace("%y%", String.valueOf(y)).replace("%z%", String.valueOf(z)));
   }

   public void sendCommandGPSWorld(Player sender, String world) {
      sender.sendMessage(this.getMessage("messages.commands.gps.world").replace("%world%", world));
   }

   public void sendCommandGPSHealth(Player sender, int percent, int current, int max) {
      String color = this.getHealthColor(percent);
      sender.sendMessage(this.getMessage("messages.commands.gps.health").replace("%color%", color).replace("%percent%", String.valueOf(percent)).replace("%current%", String.valueOf(current)).replace("%max%", String.valueOf(max)));
   }

   public void sendCommandGPSDistance(Player sender, int distance) {
      sender.sendMessage(this.getMessage("messages.commands.gps.distance").replace("%distance%", String.valueOf(distance)));
   }

   public void sendCommandGPSDirection(Player sender, String direction) {
      sender.sendMessage(this.getMessage("messages.commands.gps.direction").replace("%direction%", direction));
   }

   public void sendCommandGPSAnotherWorld(Player sender) {
      sender.sendMessage(this.getMessage("messages.commands.gps.another_world"));
   }

   public void sendCommandReloadSuccess(Player sender) {
      sender.sendMessage(this.getMessage("messages.commands.reload.success"));
   }

   public void sendCommandResourcesOnlyPlayer(Player sender) {
      sender.sendMessage(this.getMessage("messages.commands.resources.only_player"));
   }

   public void sendCommandHelpTitle(Player sender) {
      sender.sendMessage(this.getMessage("messages.commands.help.title"));
   }

   public void sendCommandHelpLine(Player sender, String command, String description) {
      sender.sendMessage(this.getMessage("messages.commands.help.line").replace("%command%", command).replace("%description%", description));
   }

   public void sendVaultReward(Player player, double amount) {
      player.sendMessage(this.getMessage("messages.rewards.vault").replace("%amount%", String.valueOf(amount)));
   }

   public void sendPlayerPointsReward(Player player, int points) {
      player.sendMessage(this.getMessage("messages.rewards.playerpoints").replace("%points%", String.valueOf(points)));
   }

   public void sendItemReward(Player player, int amount, String material) {
      player.sendMessage(this.getMessage("messages.rewards.item").replace("%amount%", String.valueOf(amount)).replace("%material%", material));
   }

   public void sendCommandRewardSuccess(Player player) {
      player.sendMessage(this.getMessage("messages.rewards.command_success"));
   }

   public void sendVaultNotEnabled() {
      this.logWarning(this.getMessage("messages.errors.vault_not_enabled"));
   }

   public void sendPlayerPointsNotEnabled() {
      this.logWarning(this.getMessage("messages.errors.playerpoints_not_enabled"));
   }

   public void sendInvalidVaultValue(String value) {
      this.logWarning(this.getMessage("messages.errors.invalid_vault_value").replace("%value%", value));
   }

   public void sendInvalidPlayerPointsValue(String value) {
      this.logWarning(this.getMessage("messages.errors.invalid_playerpoints_value").replace("%value%", value));
   }

   public void sendInvalidMaterial(String material) {
      this.logWarning(this.getMessage("messages.errors.invalid_material").replace("%material%", material));
   }

   public void sendInvalidRewardType(String type) {
      this.logWarning(this.getMessage("messages.errors.invalid_reward_type").replace("%type%", type));
   }

   private String getMessage(String path) {
      FileConfiguration config = this.configManager.getConfig();
      String message = config.getString(path, "§cОшибка: сообщение не найдено");
      return this.colorize(message);
   }

   private void broadcastMessage(String message) {
      Bukkit.broadcastMessage(message);
   }

   private void logWarning(String message) {
      Bukkit.getLogger().warning(message);
   }

   private String colorize(String message) {
      return ChatColor.translateAlternateColorCodes('&', message);
   }

   private String getMedalEmoji(int place) {
      switch(place) {
      case 1:
         return "§6§l\ud83e\udd47";
      case 2:
         return "§7§l\ud83e\udd48";
      case 3:
         return "§d§l\ud83e\udd49";
      default:
         return "§f§l•";
      }
   }

   private String getHealthColor(int percent) {
      if (percent > 75) {
         return "§a";
      } else if (percent > 50) {
         return "§e";
      } else {
         return percent > 25 ? "§6" : "§c";
      }
   }
}
