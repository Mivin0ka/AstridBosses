package ru.astridhanson.astridjoin.astridbosses;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import ru.astridhanson.astridjoin.astridbosses.commands.BossCommand;
import ru.astridhanson.astridjoin.astridbosses.config.ConfigManager;
import ru.astridhanson.astridjoin.astridbosses.listeners.BossListener;
import ru.astridhanson.astridjoin.astridbosses.listeners.PlayerDamageListener;
import ru.astridhanson.astridjoin.astridbosses.listeners.ResourceGUIListener;
import ru.astridhanson.astridjoin.astridbosses.managers.BossManager;
import ru.astridhanson.astridjoin.astridbosses.managers.BossScheduler;

public class Astridbosses extends JavaPlugin {
   private static Astridbosses instance;
   private ConfigManager configManager;
   private BossManager bossManager;
   private BossScheduler bossScheduler;

   public void onEnable() {
      instance = this;
      this.getLogger().info("╔════════════════════════════════════════╗");
      this.getLogger().info("║        AstridBosses v1.1 enabled       ║");
      this.getLogger().info("╚════════════════════════════════════════╝");
      this.configManager = new ConfigManager(this);
      this.configManager.loadConfig();
      this.bossManager = new BossManager(this, this.configManager);
      this.bossManager.loadBosses();
      this.bossScheduler = new BossScheduler(this, this.bossManager, this.configManager);
      this.bossScheduler.loadSchedulerConfig();
      PluginManager pm = Bukkit.getPluginManager();
      pm.registerEvents(new BossListener(this.bossManager), this);
      pm.registerEvents(new PlayerDamageListener(this.bossManager), this);
      pm.registerEvents(new ResourceGUIListener(), this);
      this.getCommand("boss").setExecutor(new BossCommand(this, this.bossManager, this.configManager));
      this.getCommand("boss").setTabCompleter(new BossCommand(this, this.bossManager, this.configManager));
      this.getLogger().info("✓ Плагин загружен успешно!");
   }

   public void onDisable() {
      if (this.bossManager != null) {
         this.bossManager.removeBosses();
      }

      if (this.bossScheduler != null) {
         this.bossScheduler.stopScheduler();
      }

      this.getLogger().info("✓ AstridBosses отключен!");
   }

   public static Astridbosses getInstance() {
      return instance;
   }

   public ConfigManager getConfigManager() {
      return this.configManager;
   }

   public BossManager getBossManager() {
      return this.bossManager;
   }

   public BossScheduler getBossScheduler() {
      return this.bossScheduler;
   }
}
