package ru.astridhanson.astridjoin.astridbosses.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Map.Entry;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import ru.astridhanson.astridjoin.astridbosses.Astridbosses;
import ru.astridhanson.astridjoin.astridbosses.config.ConfigManager;
import ru.astridhanson.astridjoin.astridbosses.data.BossData;
import ru.astridhanson.astridjoin.astridbosses.gui.ResourceGUI;
import ru.astridhanson.astridjoin.astridbosses.managers.BossManager;

public class BossCommand implements CommandExecutor, TabCompleter {
   private final JavaPlugin plugin;
   private final BossManager bossManager;
   private final ConfigManager configManager;

   public BossCommand(JavaPlugin plugin, BossManager bossManager, ConfigManager configManager) {
      this.plugin = plugin;
      this.bossManager = bossManager;
      this.configManager = configManager;
   }

   public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
      if (args.length == 0) {
         this.sendHelp(sender);
         return true;
      } else {
         String action = args[0].toLowerCase();
         byte var7 = -1;
         switch(action.hashCode()) {
         case -934641255:
            if (action.equals("reload")) {
               var7 = 4;
            }
            break;
         case 102570:
            if (action.equals("gps")) {
               var7 = 6;
            }
            break;
         case 115029:
            if (action.equals("top")) {
               var7 = 3;
            }
            break;
         case 3237038:
            if (action.equals("info")) {
               var7 = 5;
            }
            break;
         case 3268115:
            if (action.equals("top3")) {
               var7 = 7;
            }
            break;
         case 3291998:
            if (action.equals("kill")) {
               var7 = 1;
            }
            break;
         case 3322014:
            if (action.equals("list")) {
               var7 = 2;
            }
            break;
         case 109638523:
            if (action.equals("spawn")) {
               var7 = 0;
            }
            break;
         case 1234567890:
            if (action.equals("scheduler")) {
               var7 = 9;
            }
            break;
         case 1341287949:
            if (action.equals("resources")) {
               var7 = 8;
            }
         }

         switch(var7) {
         case 0:
            return this.handleSpawn(sender, args);
         case 1:
            return this.handleKill(sender, args);
         case 2:
            return this.handleList(sender);
         case 3:
            return this.handleTop(sender, args);
         case 4:
            return this.handleReload(sender);
         case 5:
            return this.handleInfo(sender, args);
         case 6:
            return this.handleGPS(sender);
         case 7:
            return this.handleTop3(sender, args);
         case 8:
            return this.handleResources(sender);
         case 9:
            return this.handleSchedulerStatus(sender);
         default:
            sender.sendMessage(this.colorize("&cНеизвестная команда!"));
            return false;
         }
      }
   }

   private boolean handleSpawn(CommandSender sender, String[] args) {
      if (args.length < 2) {
         sender.sendMessage(this.colorize("&cИспользование: /boss spawn <босс_id>"));
         return false;
      } else {
         String bossId = args[1];
         LivingEntity boss = this.bossManager.spawnBoss(bossId);
         if (boss == null) {
            sender.sendMessage(this.colorize("&cБосс с ID '" + bossId + "' не найден!"));
            return false;
         } else {
            Location loc = boss.getLocation();
            sender.sendMessage(this.colorize("&a✓ Босс " + bossId + " заспавнен!"));
            int var10002 = (int)loc.getX();
            sender.sendMessage(this.colorize("&7Координаты: &bX: " + var10002 + " Y: " + (int)loc.getY() + " Z: " + (int)loc.getZ()));
            sender.sendMessage(this.colorize("&7Мир: &b" + loc.getWorld().getName()));
            return true;
         }
      }
   }

   private boolean handleKill(CommandSender sender, String[] args) {
      if (args.length < 2) {
         sender.sendMessage(this.colorize("&cИспользование: /boss kill <босс_id>"));
         return false;
      } else {
         String bossId = args[1];
         boolean killed = false;
         Iterator var4 = (new ArrayList(this.bossManager.getActiveBosses().keySet())).iterator();

         while(var4.hasNext()) {
            UUID uuid = (UUID)var4.next();
            BossData data = this.bossManager.getBossData(uuid);
            if (data != null && data.getId().equals(bossId)) {
               this.bossManager.removeBoss(uuid);
               killed = true;
            }
         }

         if (killed) {
            sender.sendMessage(this.colorize("&a✓ Босс " + bossId + " убит!"));
         } else {
            sender.sendMessage(this.colorize("&cАктивный босс с ID '" + bossId + "' не найден!"));
         }

         return true;
      }
   }

   private boolean handleList(CommandSender sender) {
      sender.sendMessage(this.colorize("&6=== Активные боссы ==="));
      if (this.bossManager.getActiveBosses().isEmpty()) {
         sender.sendMessage(this.colorize("&7Нет активных боссов"));
      } else {
         Iterator var2 = this.bossManager.getActiveBosses().entrySet().iterator();

         while(var2.hasNext()) {
            Entry<UUID, BossData> entry = (Entry)var2.next();
            BossData data = (BossData)entry.getValue();
            String var10002 = data.getDisplayName();
            sender.sendMessage(this.colorize("&b↳ " + var10002 + " &7(HP: " + (int)data.getMaxHealth() + ")"));
         }
      }

      return true;
   }

   private boolean handleTop(CommandSender sender, String[] args) {
      if (args.length < 2) {
         sender.sendMessage(this.colorize("&cИспользование: /boss top <индекс_босса>"));
         return false;
      } else {
         try {
            int index = Integer.parseInt(args[1]);
            ArrayList<UUID> bosses = new ArrayList(this.bossManager.getActiveBosses().keySet());
            if (index < 0 || index >= bosses.size()) {
               sender.sendMessage(this.colorize("&cНеверный индекс!"));
               return false;
            }

            Map<UUID, Double> topDamage = this.bossManager.getBossDamageTop((UUID)bosses.get(index));
            sender.sendMessage(this.colorize("&6=== Топ урона (топ 10) ==="));
            int place = 1;

            for(Iterator var7 = topDamage.entrySet().iterator(); var7.hasNext(); ++place) {
               Entry<UUID, Double> entry = (Entry)var7.next();
               Player player = Bukkit.getPlayer((UUID)entry.getKey());
               String name = player != null ? player.getName() : "Неизвестно";
               sender.sendMessage(this.colorize("&b#" + place + " &f" + name + " &7- " + String.format("%.2f", entry.getValue()) + " урона"));
            }
         } catch (NumberFormatException var11) {
            sender.sendMessage(this.colorize("&cОшибка при парсинге индекса!"));
         }

         return true;
      }
   }

   private boolean handleTop3(CommandSender sender, String[] args) {
      if (args.length < 2) {
         sender.sendMessage(this.colorize("&cИспользование: /boss top3 <индекс_босса>"));
         return false;
      } else {
         try {
            int index = Integer.parseInt(args[1]);
            ArrayList<UUID> bosses = new ArrayList(this.bossManager.getActiveBosses().keySet());
            if (index >= 0 && index < bosses.size()) {
               Map<UUID, Double> top3 = this.bossManager.getBossDamageTop3((UUID)bosses.get(index));
               sender.sendMessage(this.colorize("&6=== \ud83c\udfc6 ТОП 3 УРОНА \ud83c\udfc6 ==="));
               int place = 1;

               for(Iterator var7 = top3.entrySet().iterator(); var7.hasNext(); ++place) {
                  Entry<UUID, Double> entry = (Entry)var7.next();
                  Player player = Bukkit.getPlayer((UUID)entry.getKey());
                  String name = player != null ? player.getName() : "Неизвестно";
                  String medal = this.getMedal(place);
                  sender.sendMessage(this.colorize("  " + medal + " &f#" + place + " &f" + name + " &7- " + String.format("%.2f", entry.getValue()) + " урона"));
               }

               return true;
            } else {
               sender.sendMessage(this.colorize("&cНеверный индекс!"));
               return false;
            }
         } catch (NumberFormatException var12) {
            sender.sendMessage(this.colorize("&cОшибка при парсинге индекса!"));
            return true;
         }
      }
   }

   private boolean handleReload(CommandSender sender) {
      this.configManager.reloadConfig();
      this.bossManager.loadBosses();
      Astridbosses.getInstance().getBossScheduler().loadSchedulerConfig();
      sender.sendMessage(this.colorize("&a✓ Конфиг перезагружен!"));
      return true;
   }

   private boolean handleInfo(CommandSender sender, String[] args) {
      if (args.length < 2) {
         sender.sendMessage(this.colorize("&cИспользование: /boss info <босс_id>"));
         return false;
      } else {
         String bossId = args[1];
         Optional<BossData> bossData = this.bossManager.getAllBossConfigs().stream().filter((b) -> {
            return b.getId().equals(bossId);
         }).findFirst();
         if (bossData.isEmpty()) {
            sender.sendMessage(this.colorize("&cБосс не найден!"));
            return false;
         } else {
            BossData boss = (BossData)bossData.get();
            String var10002 = boss.getDisplayName();
            sender.sendMessage(this.colorize("&6=== Информация о босса: " + var10002 + " ==="));
            var10002 = boss.getId();
            sender.sendMessage(this.colorize("&bID: &f" + var10002));
            var10002 = boss.getEntityType().name();
            sender.sendMessage(this.colorize("&bТип: &f" + var10002));
            int var6 = (int)boss.getMaxHealth();
            sender.sendMessage(this.colorize("&bHP: &f" + var6));
            var6 = (int)boss.getDamage();
            sender.sendMessage(this.colorize("&bУрон: &f" + var6));
            var10002 = boss.getSpawnWorld();
            sender.sendMessage(this.colorize("&bМир: &f" + var10002));
            sender.sendMessage(this.colorize("&bРандомный спавн: &f" + (boss.isRandomSpawn() ? "Да" : "Нет")));
            sender.sendMessage(this.colorize("&bНаград: &f" + boss.getRewards().size()));
            sender.sendMessage(this.colorize("&bЭффектов: &f" + boss.getEffects().size()));
            return true;
         }
      }
   }

   private boolean handleGPS(CommandSender sender) {
      sender.sendMessage(this.colorize("&6╔════════════════════════════════════════╗"));
      sender.sendMessage(this.colorize("&6║      &b\ud83d\uddfa GPS Трекер Боссов &6       ║"));
      sender.sendMessage(this.colorize("&6╚════════════════════════════════════════╝"));
      if (this.bossManager.getActiveBosses().isEmpty()) {
         sender.sendMessage(this.colorize("&7  Нет активных боссов для отслеживания"));
         return true;
      } else {
         int bossNumber = 1;
         Iterator var3 = this.bossManager.getActiveBosses().entrySet().iterator();

         while(var3.hasNext()) {
            Entry<UUID, BossData> entry = (Entry)var3.next();
            UUID bossUUID = (UUID)entry.getKey();
            BossData bossData = (BossData)entry.getValue();
            Entity bossEntity = Bukkit.getEntity(bossUUID);
            if (bossEntity != null && bossEntity instanceof LivingEntity) {
               Location loc = bossEntity.getLocation();
               double health = ((LivingEntity)bossEntity).getHealth();
               double maxHealth = bossData.getMaxHealth();
               int healthPercent = (int)(health / maxHealth * 100.0D);
               sender.sendMessage("");
               sender.sendMessage(this.colorize("&b" + bossNumber + ". " + bossData.getDisplayName()));
               sender.sendMessage(this.colorize("&7   \ud83d\udccd Координаты:"));
               int var10002 = (int)loc.getX();
               sender.sendMessage(this.colorize("&7      X: &f" + var10002 + " &7Y: &f" + (int)loc.getY() + " &7Z: &f" + (int)loc.getZ()));
               sender.sendMessage(this.colorize("&7   \ud83c\udf0d Мир: &f" + loc.getWorld().getName()));
               String healthColor = this.getHealthColor(healthPercent);
               sender.sendMessage(this.colorize("&7   ❤ Здоровье: " + healthColor + healthPercent + "% &7(" + (int)health + "/" + (int)maxHealth + ")"));
               if (sender instanceof Player) {
                  Player player = (Player)sender;
                  if (player.getWorld().equals(loc.getWorld())) {
                     double distance = player.getLocation().distance(loc);
                     String direction = this.getDirection(player.getLocation(), loc);
                     sender.sendMessage(this.colorize("&7   \ud83d\udccf Расстояние: &f" + (int)distance + " блоков"));
                     sender.sendMessage(this.colorize("&7   \ud83e\udded Направление: &f" + direction));
                  } else {
                     sender.sendMessage(this.colorize("&7   \ud83d\udccf Расстояние: &cВ другом мире"));
                  }
               }

               ++bossNumber;
            }
         }

         sender.sendMessage("");
         sender.sendMessage(this.colorize("&6╚════════════════════════════════════════╝"));
         return true;
      }
   }

   private boolean handleResources(CommandSender sender) {
      if (!(sender instanceof Player)) {
         sender.sendMessage(this.colorize("&cЭта команда доступна только игрокам!"));
         return false;
      } else {
         Player player = (Player)sender;
         ResourceGUI.openMainMenu(player);
         return true;
      }
   }

   private boolean handleSchedulerStatus(CommandSender sender) {
      sender.sendMessage(this.colorize("&a✓ Информация о планировщике выведена в консоль сервера!"));
      Astridbosses.getInstance().getBossScheduler().printSchedulerStatus();
      return true;
   }

   private String getMedal(int place) {
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

   private String getDirection(Location from, Location to) {
      double dx = to.getX() - from.getX();
      double dz = to.getZ() - from.getZ();
      double angle = Math.toDegrees(Math.atan2(dz, dx));
      angle = (angle + 450.0D) % 360.0D;
      if (!(angle >= 337.5D) && !(angle < 22.5D)) {
         if (angle >= 22.5D && angle < 67.5D) {
            return "§bСевероЗапад ↖";
         } else if (angle >= 67.5D && angle < 112.5D) {
            return "§bСевер ↑";
         } else if (angle >= 112.5D && angle < 157.5D) {
            return "§bСевероВосток ↗";
         } else if (angle >= 157.5D && angle < 202.5D) {
            return "§bВосток →";
         } else if (angle >= 202.5D && angle < 247.5D) {
            return "§bЮгоВосток ↘";
         } else {
            return angle >= 247.5D && angle < 292.5D ? "§bЮг ↓" : "§bЮгоЗапад ↙";
         }
      } else {
         return "§bЗапад ←";
      }
   }

   private void sendHelp(CommandSender sender) {
      sender.sendMessage(this.colorize("&6=== AstridBosses Help ==="));
      sender.sendMessage(this.colorize("&b/boss spawn <id> &7- Заспавнить босса"));
      sender.sendMessage(this.colorize("&b/boss kill <id> &7- Убить босса"));
      sender.sendMessage(this.colorize("&b/boss list &7- Список активных боссов"));
      sender.sendMessage(this.colorize("&b/boss gps &7- Координаты всех боссов"));
      sender.sendMessage(this.colorize("&b/boss top <индекс> &7- Топ урона (топ 10)"));
      sender.sendMessage(this.colorize("&b/boss top3 <индекс> &7- Топ 3 по урону"));
      sender.sendMessage(this.colorize("&b/boss info <id> &7- Информация о босса"));
      sender.sendMessage(this.colorize("&b/boss resources &7- GUI управления ресурсами"));
      sender.sendMessage(this.colorize("&b/boss scheduler &7- Статус планировщика"));
      sender.sendMessage(this.colorize("&b/boss reload &7- Перезагрузить конфиг"));
   }

   private String colorize(String message) {
      return ChatColor.translateAlternateColorCodes('&', message);
   }

   public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
      List<String> completions = new ArrayList();
      if (args.length == 1) {
         completions.addAll(Arrays.asList("spawn", "kill", "list", "gps", "top", "top3", "info", "resources", "scheduler", "reload"));
      } else if (args.length == 2 && (args[0].equalsIgnoreCase("spawn") || args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("kill"))) {
         this.bossManager.getAllBossConfigs().forEach((b) -> {
            completions.add(b.getId());
         });
      }

      return completions.stream().filter((c) -> {
         return c.toLowerCase().startsWith(args[args.length - 1].toLowerCase());
      }).toList();
   }
}
