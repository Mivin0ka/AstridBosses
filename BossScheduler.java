package ru.astridhanson.astridjoin.astridbosses.managers;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import ru.astridhanson.astridjoin.astridbosses.config.ConfigManager;
import ru.astridhanson.astridjoin.astridbosses.data.BossData;

public class BossScheduler {
   private final JavaPlugin plugin;
   private final BossManager bossManager;
   private final ConfigManager configManager;
   private BukkitTask schedulerTask;
   private Map<String, Long> bossSpawnCounts = new HashMap();
   private Map<String, Long> lastCountResetTime = new HashMap();
   private boolean schedulerEnabled = false;
   private String spawnMode = "SCHEDULED";
   private List<String> scheduledTimes = new ArrayList();
   private static final ZoneId PACIFIC_ZONE = ZoneId.of("America/Los_Angeles");
   private static final long ONE_DAY_MILLIS = 86400000L;

   public BossScheduler(JavaPlugin plugin, BossManager bossManager, ConfigManager configManager) {
      this.plugin = plugin;
      this.bossManager = bossManager;
      this.configManager = configManager;
   }

   public void loadSchedulerConfig() {
      FileConfiguration config = this.configManager.getConfig();
      this.schedulerEnabled = config.getBoolean("spawn-scheduler.enabled", false);
      this.spawnMode = config.getString("spawn-scheduler.mode", "SCHEDULED").toUpperCase();
      this.scheduledTimes = config.getStringList("spawn-scheduler.scheduled-times");
      this.plugin.getLogger().info("✓ Загруженны настройки спавна:");
      this.plugin.getLogger().info("  Включён: " + (this.schedulerEnabled ? "Да" : "Нет"));
      this.plugin.getLogger().info("  Режим: " + this.spawnMode);
      this.plugin.getLogger().info("  Времена спавна: " + String.valueOf(this.scheduledTimes));
      if (this.schedulerEnabled) {
         this.startScheduler();
      } else {
         this.stopScheduler();
      }

   }

   public void startScheduler() {
      if (this.schedulerTask != null) {
         this.schedulerTask.cancel();
      }

      this.schedulerTask = Bukkit.getScheduler().runTaskTimer(this.plugin, this::checkAndSpawnBosses, 0L, 1200L);
      this.plugin.getLogger().info("✓ Планировщик боссов запущен!");
   }

   public void stopScheduler() {
      if (this.schedulerTask != null) {
         this.schedulerTask.cancel();
         this.schedulerTask = null;
         this.plugin.getLogger().info("✓ Планировщик боссов остановлен!");
      }

   }

   private void checkAndSpawnBosses() {
      FileConfiguration config = this.configManager.getConfig();
      if (config.contains("bosses")) {
         Iterator var2 = config.getConfigurationSection("bosses").getKeys(false).iterator();

         while(var2.hasNext()) {
            String bossId = (String)var2.next();
            String bossPath = "bosses." + bossId;
            boolean bossSpawnEnabled = config.getBoolean(bossPath + ".auto-spawn.enabled", false);
            if (bossSpawnEnabled) {
               String bossModeOverride = config.getString(bossPath + ".auto-spawn.mode", "");
               String effectiveMode = bossModeOverride.isEmpty() ? this.spawnMode : bossModeOverride.toUpperCase();
               if (effectiveMode.equals("SCHEDULED")) {
                  this.handleScheduledSpawn(bossId, bossPath, config);
               } else if (effectiveMode.equals("RANDOM")) {
                  this.handleRandomSpawn(bossId, bossPath, config);
               }
            }
         }

      }
   }

   private void handleScheduledSpawn(String bossId, String bossPath, FileConfiguration config) {
      List<String> bossTimes = config.getStringList(bossPath + ".auto-spawn.scheduled-times");
      List<String> timesToCheck = !bossTimes.isEmpty() ? bossTimes : this.scheduledTimes;
      if (!timesToCheck.isEmpty()) {
         ZonedDateTime now = ZonedDateTime.now(PACIFIC_ZONE);
         String currentTime = String.format("%02d:%02d", now.getHour(), now.getMinute());
         Iterator var8 = timesToCheck.iterator();

         while(true) {
            String scheduledTime;
            String spawnKey;
            do {
               do {
                  do {
                     if (!var8.hasNext()) {
                        return;
                     }

                     scheduledTime = (String)var8.next();
                  } while(!currentTime.equals(scheduledTime.trim()));

                  spawnKey = bossId + "_" + scheduledTime;
               } while(this.hasSpawnedToday(spawnKey));
            } while(!this.bossManager.getActiveBosses().isEmpty() && this.isBossAlreadyActive(bossId));

            this.spawnBoss(bossId, "SCHEDULED: " + scheduledTime);
            this.markSpawned(spawnKey);
         }
      }
   }

   private void handleRandomSpawn(String bossId, String bossPath, FileConfiguration config) {
      int maxSpawnsPerDay = config.getInt(bossPath + ".auto-spawn.max-spawns-per-day", 3);
      long minIntervalMinutes = config.getLong(bossPath + ".auto-spawn.min-interval-minutes", 120L);
      int spawnChancePercent = config.getInt(bossPath + ".auto-spawn.spawn-chance-percent", 50);
      String countKey = bossId + "_count";
      long currentCount = (Long)this.bossSpawnCounts.getOrDefault(countKey, 0L);
      long lastReset = (Long)this.lastCountResetTime.getOrDefault(countKey, System.currentTimeMillis());
      if (System.currentTimeMillis() - lastReset > 86400000L) {
         currentCount = 0L;
         this.lastCountResetTime.put(countKey, System.currentTimeMillis());
      }

      if (currentCount < (long)maxSpawnsPerDay) {
         String lastSpawnKey = bossId + "_last_spawn";
         long lastSpawnTime = (Long)this.lastCountResetTime.getOrDefault(lastSpawnKey, 0L);
         if (System.currentTimeMillis() - lastSpawnTime >= minIntervalMinutes * 60000L) {
            Random random = new Random();
            if (random.nextInt(100) < spawnChancePercent) {
               if (this.bossManager.getActiveBosses().isEmpty() || !this.isBossAlreadyActive(bossId)) {
                  this.spawnBoss(bossId, "RANDOM SPAWN");
                  this.bossSpawnCounts.put(countKey, currentCount + 1L);
                  this.lastCountResetTime.put(lastSpawnKey, System.currentTimeMillis());
               }

            }
         }
      }
   }

   private void spawnBoss(String bossId, String spawnReason) {
      BossData bossData = (BossData)this.bossManager.getAllBossConfigs().stream().filter((b) -> {
         return b.getId().equals(bossId);
      }).findFirst().orElse((Object)null);
      if (bossData == null) {
         this.plugin.getLogger().warning("✗ Босс '" + bossId + "' не найден в конфиге!");
      } else {
         this.bossManager.spawnBoss(bossId);
         this.plugin.getLogger().info("✓ Босс '" + bossId + "' заспавнен автоматически (" + spawnReason + ")");
         Bukkit.broadcastMessage("§a§l[AUTO-SPAWN] §f" + bossData.getDisplayName() + " §7появился на сервере!");
      }
   }

   private boolean isBossAlreadyActive(String bossId) {
      return this.bossManager.getActiveBosses().values().stream().anyMatch((bossData) -> {
         return bossData.getId().equals(bossId);
      });
   }

   private boolean hasSpawnedToday(String key) {
      long lastSpawnTime = (Long)this.lastCountResetTime.getOrDefault(key, 0L);
      long timeSinceSpawn = System.currentTimeMillis() - lastSpawnTime;
      return timeSinceSpawn < 120000L;
   }

   private void markSpawned(String key) {
      this.lastCountResetTime.put(key, System.currentTimeMillis());
   }

   public boolean isSchedulerEnabled() {
      return this.schedulerEnabled;
   }

   public String getSpawnMode() {
      return this.spawnMode;
   }

   public List<String> getScheduledTimes() {
      return this.scheduledTimes;
   }

   public Map<String, Long> getBossSpawnCounts() {
      return this.bossSpawnCounts;
   }

   public void printSchedulerStatus() {
      this.plugin.getLogger().info("§6=== Статус планировщика боссов ===");
      this.plugin.getLogger().info("§bВключён: §f" + (this.schedulerEnabled ? "Да" : "Нет"));
      this.plugin.getLogger().info("§bРежим: §f" + this.spawnMode);
      this.plugin.getLogger().info("§bВремена спавна: §f" + String.valueOf(this.scheduledTimes));
      this.plugin.getLogger().info("§bТекущее время (Pacific): §f" + String.valueOf(ZonedDateTime.now(PACIFIC_ZONE)));
      if (!this.bossSpawnCounts.isEmpty()) {
         this.plugin.getLogger().info("§bСпавны за день:");
         Iterator var1 = this.bossSpawnCounts.entrySet().iterator();

         while(var1.hasNext()) {
            Entry<String, Long> entry = (Entry)var1.next();
            Logger var10000 = this.plugin.getLogger();
            String var10001 = (String)entry.getKey();
            var10000.info("  §7" + var10001 + ": §f" + String.valueOf(entry.getValue()));
         }
      }

   }
}
