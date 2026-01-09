package ru.astridhanson.astridjoin.astridbosses.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigManager {
   private final JavaPlugin plugin;
   private File configFile;
   private FileConfiguration config;

   public ConfigManager(JavaPlugin plugin) {
      this.plugin = plugin;
   }

   public void loadConfig() {
      if (!this.plugin.getDataFolder().exists()) {
         this.plugin.getDataFolder().mkdirs();
      }

      this.configFile = new File(this.plugin.getDataFolder(), "config.yml");
      if (!this.configFile.exists()) {
         this.createDefaultConfig();
      }

      this.config = YamlConfiguration.loadConfiguration(this.configFile);
      this.plugin.getLogger().info("✓ Конфиг загружен успешно!");
   }

   private void createDefaultConfig() {
      try {
         InputStream inputStream = this.plugin.getResource("config.yml");
         if (inputStream != null) {
            Files.copy(inputStream, this.configFile.toPath(), new CopyOption[]{StandardCopyOption.REPLACE_EXISTING});
            this.plugin.getLogger().info("✓ Создан config.yml из ресурсов плагина");
            inputStream.close();
         } else {
            this.createMinimalConfig();
         }
      } catch (IOException var2) {
         this.plugin.getLogger().warning("✗ Ошибка при создании config.yml: " + var2.getMessage());
         this.createMinimalConfig();
      }

   }

   private void createMinimalConfig() {
      try {
         this.config = new YamlConfiguration();
         this.config.set("plugin.debug", false);
         this.config.set("plugin.prefix", "&8[&bAstridBosses&8] ");
         this.config.set("spawn-scheduler.enabled", true);
         this.config.set("spawn-scheduler.mode", "SCHEDULED");
         this.config.addDefault("spawn-scheduler.scheduled-times.0", "09:00");
         this.config.addDefault("spawn-scheduler.scheduled-times.1", "18:00");
         this.config.set("messages.boss.spawn.announcement", "&6&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
         this.config.set("messages.boss.spawn.title", "&c&l⚔ БОСС ЗАСПАВНИЛ! ⚔");
         this.config.set("messages.boss.spawn.health", "&7❤ Здоровье: &c%health% HP");
         this.config.set("messages.boss.spawn.damage", "&7⚡ Урон: &c%damage% HP");
         this.config.set("messages.boss.spawn.coordinates", "&7\ud83d\udccd Координаты: &bX: %x% Y: %y% Z: %z%");
         this.config.set("messages.boss.spawn.world", "&7\ud83c\udf0d Мир: &b%world%");
         this.config.set("messages.boss.spawn.hint", "&e&l» Используй &6/boss gps &e&lдля навигации!");
         this.config.set("messages.boss.death.announcement", "&6&l▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬▬");
         this.config.set("messages.boss.death.title", "&a&l✓ БОСС ПОВЕЖДЕН! ✓");
         this.config.set("messages.boss.death.killed", "  %boss% &7был убит!");
         this.config.set("messages.boss.death.top_damager", "%medal% &f#%place% &f%player% &7- %damage% урона");
         this.config.set("messages.boss.death.rewards", "&7\ud83d\udcb0 Основные награды: &a%count% предметов");
         this.config.set("messages.commands.errors.unknown_command", "&cНеизвестная команда!");
         this.config.set("messages.commands.spawn.usage", "&cИспользование: /boss spawn <босс_id>");
         this.config.set("messages.commands.spawn.success", "&a✓ Босс %id% заспавнен!");
         this.config.set("messages.commands.spawn.boss_not_found", "&cБосс с ID '%id%' не найден!");
         this.config.set("messages.commands.kill.usage", "&cИспользование: /boss kill <босс_id>");
         this.config.set("messages.commands.kill.success", "&a✓ Босс %id% убит!");
         this.config.set("messages.commands.kill.boss_not_found", "&cАктивный босс с ID '%id%' не найден!");
         this.config.set("messages.commands.list.title", "&6=== Активные боссы ===");
         this.config.set("messages.commands.list.empty", "&7Нет активных боссов");
         this.config.set("messages.commands.top.usage", "&cИспользование: /boss top <индекс_босса>");
         this.config.set("messages.commands.top.invalid_index", "&cНеверный индекс!");
         this.config.set("messages.commands.top.title", "&6=== Топ урона (топ 10) ===");
         this.config.set("messages.commands.top3.title", "&6=== \ud83c\udfc6 ТОП 3 УРОНА \ud83c\udfc6 ===");
         this.config.set("messages.commands.gps.title", "&6╔════════════════════════════════════════╗");
         this.config.set("messages.commands.reload.success", "&a✓ Конфиг перезагружен!");
         this.config.set("messages.commands.resources.only_player", "&cЭта команда доступна только игрокам!");
         this.config.set("messages.commands.help.title", "&6=== AstridBosses Help ===");
         this.config.set("messages.rewards.vault", "&a+$%amount%");
         this.config.set("messages.rewards.playerpoints", "&b+%points% Points");
         this.config.set("messages.rewards.item", "&6Получено: %amount%x %material%");
         this.config.set("messages.rewards.command_success", "&aКоманда выполнена!");
         this.config.set("messages.rewards.top_reward", "&6&lВы получили награду за место #%place%!");
         this.config.set("messages.errors.vault_not_enabled", "Vault не установлен!");
         this.config.set("messages.errors.playerpoints_not_enabled", "PlayerPoints не установлен!");
         this.config.set("messages.errors.invalid_material", "Неверный материал: %material%");
         this.config.set("messages.errors.invalid_reward_type", "Неверный тип награды: %type%");
         this.config.set("bosses.example_boss.name", "§6\ud83d\udd25 Примерный Босс");
         this.config.set("bosses.example_boss.type", "WITHER");
         this.config.set("bosses.example_boss.health", 300.0D);
         this.config.set("bosses.example_boss.damage", 5.0D);
         this.config.set("bosses.example_boss.protection.ignore_arrow_damage", true);
         this.config.set("bosses.example_boss.protection.ignore_sun_damage", true);
         this.config.set("bosses.example_boss.protection.ignore_fire_damage", true);
         this.config.set("bosses.example_boss.spawn.world", "world");
         this.config.set("bosses.example_boss.spawn.random", true);
         this.config.set("bosses.example_boss.spawn.location", "");
         this.config.set("bosses.example_boss.auto-spawn.enabled", true);
         this.config.set("bosses.example_boss.auto-spawn.mode", "");
         this.config.set("bosses.example_boss.damage_top_rewards.first_place.type", "VAULT");
         this.config.set("bosses.example_boss.damage_top_rewards.first_place.value", "5000");
         this.config.set("bosses.example_boss.damage_top_rewards.first_place.amount", 1);
         this.config.set("bosses.example_boss.damage_top_rewards.second_place.type", "VAULT");
         this.config.set("bosses.example_boss.damage_top_rewards.second_place.value", "3000");
         this.config.set("bosses.example_boss.damage_top_rewards.second_place.amount", 1);
         this.config.set("bosses.example_boss.damage_top_rewards.third_place.type", "VAULT");
         this.config.set("bosses.example_boss.damage_top_rewards.third_place.value", "1000");
         this.config.set("bosses.example_boss.damage_top_rewards.third_place.amount", 1);
         this.config.set("bosses.example_boss.rewards.money.type", "VAULT");
         this.config.set("bosses.example_boss.rewards.money.value", "10000");
         this.config.set("bosses.example_boss.rewards.money.amount", 1);
         this.config.set("bosses.example_boss.effects.strength.type", "INCREASE_DAMAGE");
         this.config.set("bosses.example_boss.effects.strength.amplifier", 2);
         this.config.set("bosses.example_boss.effects.strength.duration", 999999);
         this.config.save(this.configFile);
         this.plugin.getLogger().info("✓ Создан новый config.yml с стандартными значениями");
      } catch (IOException var2) {
         this.plugin.getLogger().warning("✗ Критическая ошибка при создании config.yml: " + var2.getMessage());
         var2.printStackTrace();
      }

   }

   public FileConfiguration getConfig() {
      return this.config;
   }

   public void saveConfig() {
      try {
         this.config.save(this.configFile);
      } catch (IOException var2) {
         var2.printStackTrace();
      }

   }

   public void reloadConfig() {
      this.config = YamlConfiguration.loadConfiguration(this.configFile);
      this.plugin.getLogger().info("✓ Конфиг перезагружен!");
   }

   public String getPrefix() {
      String prefix = this.config.getString("plugin.prefix", "&8[&bAstridBosses&8] ");
      return prefix.replace("&", "§");
   }

   public boolean isDebugEnabled() {
      return this.config.getBoolean("plugin.debug", false);
   }
}
