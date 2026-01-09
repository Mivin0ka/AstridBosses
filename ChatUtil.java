package ru.astridhanson.astridjoin.astridbosses.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ChatUtil {
   public static String colorize(String message) {
      return ChatColor.translateAlternateColorCodes('&', message);
   }

   public static void sendMessage(Player player, String message) {
      player.sendMessage(colorize(message));
   }

   public static void sendMessages(Player player, String... messages) {
      String[] var2 = messages;
      int var3 = messages.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String message = var2[var4];
         sendMessage(player, message);
      }

   }

   public static void sendCenteredMessage(Player player, String message) {
      int messagePxSize = 0;
      boolean previouslyBold = false;
      char[] var4 = message.toCharArray();
      int toCompensate = var4.length;

      for(int var6 = 0; var6 < toCompensate; ++var6) {
         char c = var4[var6];
         if (c == 167) {
            previouslyBold = true;
         } else if (previouslyBold && c == 'l') {
            messagePxSize += 4;
         } else if (previouslyBold) {
            messagePxSize += 0;
            previouslyBold = false;
         } else {
            messagePxSize += 4;
         }
      }

      int CENTER_PX = 154;
      toCompensate = (CENTER_PX - messagePxSize) / 2;
      StringBuilder sb = new StringBuilder();

      for(int i = 0; i < toCompensate; i += 4) {
         sb.append(" ");
      }

      String var10001 = sb.toString();
      player.sendMessage(colorize(var10001 + message));
   }

   public static String formatNumber(double number) {
      if (number >= 1000000.0D) {
         return String.format("%.2fM", number / 1000000.0D);
      } else {
         return number >= 1000.0D ? String.format("%.2fK", number / 1000.0D) : String.format("%.0f", number);
      }
   }

   public static String createProgressBar(double current, double max, int length) {
      double progress = current / max;
      int filledLength = (int)((double)length * progress);
      StringBuilder bar = new StringBuilder();
      bar.append("§c");

      int i;
      for(i = 0; i < filledLength; ++i) {
         bar.append("█");
      }

      bar.append("§7");

      for(i = filledLength; i < length; ++i) {
         bar.append("█");
      }

      return bar.toString();
   }

   public static String getDifficultyColor(double health) {
      if (health > 250.0D) {
         return "§4";
      } else if (health > 150.0D) {
         return "§6";
      } else {
         return health > 75.0D ? "§e" : "§2";
      }
   }
}
