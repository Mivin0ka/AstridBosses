package ru.astridhanson.astridjoin.astridbosses.managers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Zombie;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.astridhanson.astridjoin.astridbosses.config.ConfigManager;
import ru.astridhanson.astridjoin.astridbosses.data.BossDamageTracker;
import ru.astridhanson.astridjoin.astridbosses.data.BossData;
import ru.astridhanson.astridjoin.astridbosses.data.DamageTopRewards;
import ru.astridhanson.astridjoin.astridbosses.data.Reward;
import ru.astridhanson.astridjoin.astridbosses.integration.WorldGuardIntegration;

public class BossManager {
   private final JavaPlugin plugin;
   private final ConfigManager configManager;
   private final MessageManager messageManager;
   private final WorldGuardIntegration worldGuardIntegration;
   private final Map<UUID, BossData> activeBosses = new HashMap();
   private final Map<UUID, BossBar> bossBars = new HashMap();
   private final Map<UUID, BossDamageTracker> damageTrackers = new HashMap();
   private final Map<String, BossData> bossConfigs = new HashMap();

   public BossManager(JavaPlugin plugin, ConfigManager configManager) {
      this.plugin = plugin;
      this.configManager = configManager;
      this.messageManager = new MessageManager(configManager);
      this.worldGuardIntegration = new WorldGuardIntegration(plugin);
   }

   public void loadBosses() {
      FileConfiguration config = this.configManager.getConfig();
      ConfigurationSection bossList = config.getConfigurationSection("bosses");
      if (bossList != null) {
         Iterator var3 = bossList.getKeys(false).iterator();

         while(var3.hasNext()) {
            String key = (String)var3.next();
            BossData boss = this.loadBossFromConfig(key);
            if (boss != null) {
               this.bossConfigs.put(key, boss);
               this.plugin.getLogger().info("✓ Загружен босс: " + boss.getDisplayName());
            }
         }
      }

   }

   private BossData loadBossFromConfig(String id) {
      FileConfiguration config = this.configManager.getConfig();
      String path = "bosses." + id;
      if (!config.contains(path)) {
         return null;
      } else {
         BossData boss = new BossData();
         boss.setId(id);
         boss.setDisplayName(config.getString(path + ".name", "§6Неизвестный босс"));

         try {
            boss.setEntityType(EntityType.valueOf(config.getString(path + ".type", "WITHER")));
         } catch (IllegalArgumentException var15) {
            boss.setEntityType(EntityType.WITHER);
         }

         boss.setMaxHealth(config.getDouble(path + ".health", 100.0D));
         boss.setDamage(config.getDouble(path + ".damage", 5.0D));
         boss.setSpawnWorld(config.getString(path + ".spawn.world", "world"));
         boss.setRandomSpawn(config.getBoolean(path + ".spawn.random", true));
         boss.setIgnoreArrowDamage(config.getBoolean(path + ".protection.ignore_arrow_damage", true));
         boss.setIgnoreSunDamage(config.getBoolean(path + ".protection.ignore_sun_damage", true));
         boss.setIgnoreFireDamage(config.getBoolean(path + ".protection.ignore_fire_damage", true));
         if (!boss.isRandomSpawn()) {
            String locStr = config.getString(path + ".spawn.location", "");
            boss.setSpawnLocation(this.parseLocation(locStr));
         }

         ConfigurationSection damageTopRewards = config.getConfigurationSection(path + ".damage_top_rewards");
         if (damageTopRewards != null) {
            Reward firstPlace = this.loadRewardFromConfig(config, path + ".damage_top_rewards.first_place");
            Reward secondPlace = this.loadRewardFromConfig(config, path + ".damage_top_rewards.second_place");
            Reward thirdPlace = this.loadRewardFromConfig(config, path + ".damage_top_rewards.third_place");
            if (firstPlace != null && secondPlace != null && thirdPlace != null) {
               boss.setDamageTopRewards(new DamageTopRewards(firstPlace, secondPlace, thirdPlace));
               this.plugin.getLogger().info("  ✓ Загружены награды за топ урона");
            }
         }

         ConfigurationSection effects = config.getConfigurationSection(path + ".effects");
         String typeName;
         if (effects != null) {
            Iterator var6 = effects.getKeys(false).iterator();

            while(var6.hasNext()) {
               String effectKey = (String)var6.next();
               typeName = config.getString(path + ".effects." + effectKey + ".type", "");
               int amplifier = config.getInt(path + ".effects." + effectKey + ".amplifier", 0);
               int duration = config.getInt(path + ".effects." + effectKey + ".duration", 999999);

               try {
                  PotionEffectType effectType = PotionEffectType.getByName(typeName);
                  if (effectType != null) {
                     PotionEffect effect = new PotionEffect(effectType, duration * 20, amplifier, false, false);
                     boss.getEffects().add(effect);
                     this.plugin.getLogger().info("  ✓ Загружен эффект: " + typeName + " (lvl " + (amplifier + 1) + ")");
                  } else {
                     this.plugin.getLogger().warning("  ✗ Неизвестный тип эффекта: " + typeName);
                  }
               } catch (Exception var14) {
                  this.plugin.getLogger().warning("  ✗ Ошибка загрузки эффекта: " + effectKey);
               }
            }
         }

         ConfigurationSection rewards = config.getConfigurationSection(path + ".rewards");
         if (rewards != null) {
            Iterator var18 = rewards.getKeys(false).iterator();

            while(var18.hasNext()) {
               typeName = (String)var18.next();
               Reward reward = this.loadRewardFromConfig(config, path + ".rewards." + typeName);
               if (reward != null) {
                  boss.getRewards().add(reward);
               }
            }
         }

         return boss;
      }
   }

   private Reward loadRewardFromConfig(FileConfiguration config, String path) {
      String type = config.getString(path + ".type", "");
      String value = config.getString(path + ".value", "");
      int amount = config.getInt(path + ".amount", 1);

      try {
         return new Reward(Reward.RewardType.valueOf(type.toUpperCase()), value, amount);
      } catch (IllegalArgumentException var7) {
         this.plugin.getLogger().warning("Неверный тип награды: " + type);
         return null;
      }
   }

   public LivingEntity spawnBoss(String bossId) {
      BossData bossData = (BossData)this.bossConfigs.get(bossId);
      if (bossData == null) {
         return null;
      } else {
         World world = Bukkit.getWorld(bossData.getSpawnWorld());
         if (world == null) {
            world = (World)Bukkit.getWorlds().get(0);
         }

         Location spawnLocation;
         if (bossData.isRandomSpawn()) {
            spawnLocation = this.getRandomLocation(world);
         } else {
            spawnLocation = bossData.getSpawnLocation();
         }

         if (spawnLocation == null) {
            spawnLocation = this.getRandomLocation(world);
         }

         if (this.worldGuardIntegration.isEnabled()) {
            for(int attempts = 0; !this.worldGuardIntegration.canSpawnAtLocation(spawnLocation) && attempts < 10; ++attempts) {
               spawnLocation = this.getRandomLocation(world);
            }

            if (!this.worldGuardIntegration.canSpawnAtLocation(spawnLocation)) {
               this.plugin.getLogger().warning("✗ Не удалось найти место для спавна босса '" + bossId + "' - все локации защищены WorldGuard!");
               Bukkit.broadcastMessage("§c✗ Не удалось заспавнить босса - нет подходящих локаций!");
               return null;
            }
         }

         LivingEntity entity = (LivingEntity)world.spawnEntity(spawnLocation, bossData.getEntityType());
         entity.setCustomName(bossData.getDisplayName());
         entity.setCustomNameVisible(true);
         entity.setMaxHealth(bossData.getMaxHealth());
         entity.setHealth(bossData.getMaxHealth());
         entity.setRemoveWhenFarAway(false);
         if (entity instanceof Zombie || entity instanceof Skeleton || entity instanceof Phantom) {
            try {
               entity.getClass().getMethod("setShouldBurnInDay", Boolean.TYPE).invoke(entity, false);
            } catch (Exception var11) {
               entity.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, false, false));
            }
         }

         Iterator var6 = bossData.getEffects().iterator();

         while(var6.hasNext()) {
            PotionEffect effect = (PotionEffect)var6.next();
            entity.addPotionEffect(effect);
            Logger var10000 = this.plugin.getLogger();
            String var10001 = effect.getType().getName();
            var10000.info("✓ Применен эффект: " + var10001 + " к " + bossData.getDisplayName());
         }

         BossBar bossBar = Bukkit.createBossBar(bossData.getDisplayName(), BarColor.RED, BarStyle.SOLID, new BarFlag[0]);
         bossBar.setProgress(1.0D);
         this.activeBosses.put(entity.getUniqueId(), bossData);
         this.bossBars.put(entity.getUniqueId(), bossBar);
         this.damageTrackers.put(entity.getUniqueId(), new BossDamageTracker());
         this.messageManager.sendBossSpawnedAnnouncement(bossData.getDisplayName(), spawnLocation.getX(), bossData.getMaxHealth(), spawnLocation.getZ(), world.getName());
         String regionName = this.worldGuardIntegration.getRegionName(spawnLocation);
         this.plugin.getLogger().info("✓ Босс '" + bossId + "' заспавнен в регионе: " + regionName);
         Iterator var8 = world.getPlayers().iterator();

         while(var8.hasNext()) {
            Player player = (Player)var8.next();
            if (player.getLocation().distance(spawnLocation) < 50.0D) {
               bossBar.addPlayer(player);
            }
         }

         return entity;
      }
   }

   public void onBossDamage(LivingEntity boss, EntityDamageByEntityEvent event) {
      if (this.activeBosses.containsKey(boss.getUniqueId())) {
         Entity damager = event.getDamager();
         if (damager instanceof Player) {
            Player player = (Player)damager;
            double damage = event.getFinalDamage();
            ((BossDamageTracker)this.damageTrackers.get(boss.getUniqueId())).addDamage(player.getUniqueId(), damage);
         }

         this.updateBossBar(boss);
      }

   }

   private void updateBossBar(LivingEntity boss) {
      BossBar bossBar = (BossBar)this.bossBars.get(boss.getUniqueId());
      if (bossBar != null) {
         double health = boss.getHealth();
         double maxHealth = boss.getMaxHealth();
         bossBar.setProgress(Math.max(0.0D, health / maxHealth));
      }

   }

   public void onBossDeath(LivingEntity boss) {
      if (this.activeBosses.containsKey(boss.getUniqueId())) {
         BossData bossData = (BossData)this.activeBosses.get(boss.getUniqueId());
         BossDamageTracker tracker = (BossDamageTracker)this.damageTrackers.get(boss.getUniqueId());
         this.messageManager.sendBossDeathAnnouncement(bossData.getDisplayName());
         Map<UUID, Double> top3 = tracker.getTop3Damage();
         int place = 1;
         RewardManager rewardManager = new RewardManager(this.plugin, this.messageManager);
         UUID topDamager = null;

         for(Iterator var8 = top3.entrySet().iterator(); var8.hasNext(); ++place) {
            Entry<UUID, Double> entry = (Entry)var8.next();
            Player topPlayer = Bukkit.getPlayer((UUID)entry.getKey());
            if (topPlayer != null) {
               if (place == 1) {
                  topDamager = (UUID)entry.getKey();
               }

               this.messageManager.sendTopDamagerMessage(topPlayer.getName(), place, (Double)entry.getValue());
               if (bossData.getDamageTopRewards() != null) {
                  Reward reward = bossData.getDamageTopRewards().getRewardForPlace(place);
                  if (reward != null) {
                     rewardManager.giveReward(topPlayer, reward);
                     this.messageManager.sendPlayerTopRewardMessage(topPlayer, place);
                  }
               }
            }
         }

         this.messageManager.sendRewardsInfo(bossData.getRewards().size());
         this.messageManager.sendBossDeathEnd();
         if (topDamager != null) {
            Player topPlayer = Bukkit.getPlayer(topDamager);
            if (topPlayer != null) {
               this.giveRewards(topPlayer, bossData);
            }
         }

         this.removeBoss(boss.getUniqueId());
      }

   }

   private void giveRewards(Player player, BossData boss) {
      RewardManager rewardManager = new RewardManager(this.plugin, this.messageManager);
      Iterator var4 = boss.getRewards().iterator();

      while(var4.hasNext()) {
         Reward reward = (Reward)var4.next();
         rewardManager.giveReward(player, reward);
      }

   }

   public void removeBoss(UUID bossUUID) {
      this.activeBosses.remove(bossUUID);
      BossBar bossBar = (BossBar)this.bossBars.remove(bossUUID);
      if (bossBar != null) {
         bossBar.removeAll();
      }

      this.damageTrackers.remove(bossUUID);
   }

   public void removeBosses() {
      List<UUID> bossList = new ArrayList(this.activeBosses.keySet());
      Iterator var2 = bossList.iterator();

      while(var2.hasNext()) {
         UUID uuid = (UUID)var2.next();
         this.removeBoss(uuid);
      }

   }

   private Location getRandomLocation(World world) {
      Random random = new Random();
      int x = random.nextInt(1000) - 500;
      int z = random.nextInt(1000) - 500;
      int y = world.getHighestBlockYAt(x, z) + 1;
      return new Location(world, (double)x, (double)y, (double)z);
   }

   private Location parseLocation(String str) {
      if (str != null && !str.isEmpty()) {
         String[] parts = str.split(",");
         if (parts.length < 4) {
            return null;
         } else {
            try {
               World world = Bukkit.getWorld(parts[0]);
               double x = Double.parseDouble(parts[1]);
               double y = Double.parseDouble(parts[2]);
               double z = Double.parseDouble(parts[3]);
               return new Location(world, x, y, z);
            } catch (Exception var10) {
               return null;
            }
         }
      } else {
         return null;
      }
   }

   public Map<UUID, Double> getBossDamageTop(UUID bossUUID) {
      BossDamageTracker tracker = (BossDamageTracker)this.damageTrackers.get(bossUUID);
      return (Map)(tracker != null ? tracker.getSortedDamage() : new HashMap());
   }

   public Map<UUID, Double> getBossDamageTop3(UUID bossUUID) {
      BossDamageTracker tracker = (BossDamageTracker)this.damageTrackers.get(bossUUID);
      return (Map)(tracker != null ? tracker.getTop3Damage() : new HashMap());
   }

   public BossData getBossData(UUID bossUUID) {
      return (BossData)this.activeBosses.get(bossUUID);
   }

   public Collection<BossData> getAllBossConfigs() {
      return this.bossConfigs.values();
   }

   public Map<UUID, BossData> getActiveBosses() {
      return this.activeBosses;
   }

   public MessageManager getMessageManager() {
      return this.messageManager;
   }

   public WorldGuardIntegration getWorldGuardIntegration() {
      return this.worldGuardIntegration;
   }
}
