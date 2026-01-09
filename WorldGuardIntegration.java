package ru.astridhanson.astridjoin.astridbosses.integration;

import java.util.Iterator;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public class WorldGuardIntegration {
   private JavaPlugin plugin;
   private boolean worldGuardEnabled = false;
   private Object worldGuardInstance = null;

   public WorldGuardIntegration(JavaPlugin plugin) {
      this.plugin = plugin;
      this.checkWorldGuardInstalled();
   }

   private void checkWorldGuardInstalled() {
      try {
         if (this.plugin.getServer().getPluginManager().getPlugin("WorldGuard") != null) {
            Class.forName("com.sk89q.worldguard.WorldGuard");
            Class.forName("com.sk89q.worldguard.protection.flags.Flags");
            this.worldGuardEnabled = true;
            this.plugin.getLogger().info("✓ WorldGuard обнаружен - интеграция активирована");
         } else {
            this.worldGuardEnabled = false;
            this.plugin.getLogger().info("⚠ WorldGuard не установлен - проверка флагов отключена");
         }
      } catch (ClassNotFoundException var2) {
         this.worldGuardEnabled = false;
         this.plugin.getLogger().info("⚠ WorldGuard не установлен - проверка флагов отключена");
      } catch (Exception var3) {
         this.worldGuardEnabled = false;
         this.plugin.getLogger().warning("⚠ Ошибка при проверке WorldGuard: " + var3.getMessage());
      }

   }

   public boolean canSpawnAtLocation(Location location) {
      if (!this.worldGuardEnabled) {
         return true;
      } else {
         try {
            return this.isSpawnAllowed(location);
         } catch (Exception var3) {
            this.plugin.getLogger().warning("⚠ Ошибка при проверке WorldGuard: " + var3.getMessage());
            return true;
         }
      }
   }

   private boolean isSpawnAllowed(Location location) {
      if (location != null && location.getWorld() != null) {
         try {
            Class<?> worldGuardClass = Class.forName("com.sk89q.worldguard.WorldGuard");
            Class<?> builtInFlagsClass = Class.forName("com.sk89q.worldguard.protection.flags.Flags");
            Object worldGuardInstance = worldGuardClass.getMethod("getInstance").invoke((Object)null);
            Object platform = worldGuardClass.getMethod("getPlatform").invoke(worldGuardInstance);
            Object container = platform.getClass().getMethod("getRegionContainer").invoke(platform);
            Class<?> bukkitAdapterClass = Class.forName("com.sk89q.worldedit.bukkit.BukkitAdapter");
            Object weWorld = bukkitAdapterClass.getMethod("adapt", World.class).invoke((Object)null, location.getWorld());
            Object regionManager = container.getClass().getMethod("get", Class.forName("com.sk89q.worldedit.world.World")).invoke(container, weWorld);
            if (regionManager == null) {
               return true;
            } else {
               Class<?> blockVectorClass = Class.forName("com.sk89q.worldedit.math.BlockVector3");
               Object blockVector = blockVectorClass.getMethod("at", Integer.TYPE, Integer.TYPE, Integer.TYPE).invoke((Object)null, location.getBlockX(), location.getBlockY(), location.getBlockZ());
               Object applicableRegions = regionManager.getClass().getMethod("getApplicableRegions", blockVectorClass).invoke(regionManager, blockVector);
               int size = (Integer)applicableRegions.getClass().getMethod("size").invoke(applicableRegions);
               if (size == 0) {
                  return true;
               } else {
                  Object mobSpawningFlag = builtInFlagsClass.getField("MOB_SPAWNING").get((Object)null);
                  Iterator var15 = ((Iterable)applicableRegions).iterator();

                  while(var15.hasNext()) {
                     Object region = var15.next();
                     Object flagValue = region.getClass().getMethod("getFlag", Class.forName("com.sk89q.worldguard.protection.flags.Flag")).invoke(region, mobSpawningFlag);
                     if (flagValue != null && flagValue instanceof Boolean) {
                        Boolean isAllowed = (Boolean)flagValue;
                        if (!isAllowed) {
                           String regionId = (String)region.getClass().getMethod("getId").invoke(region);
                           this.plugin.getLogger().warning("⚠ Спавн запрещен в регионе '" + regionId + "' - флаг MOB_SPAWNING = false");
                           return false;
                        }
                     }
                  }

                  return true;
               }
            }
         } catch (ClassNotFoundException var20) {
            return true;
         } catch (Exception var21) {
            this.plugin.getLogger().warning("⚠ Ошибка при проверке регионов: " + var21.getMessage());
            return true;
         }
      } else {
         return true;
      }
   }

   public String getRegionName(Location location) {
      if (this.worldGuardEnabled && location != null && location.getWorld() != null) {
         try {
            Class<?> worldGuardClass = Class.forName("com.sk89q.worldguard.WorldGuard");
            Object worldGuardInstance = worldGuardClass.getMethod("getInstance").invoke((Object)null);
            Object platform = worldGuardClass.getMethod("getPlatform").invoke(worldGuardInstance);
            Object container = platform.getClass().getMethod("getRegionContainer").invoke(platform);
            Class<?> bukkitAdapterClass = Class.forName("com.sk89q.worldedit.bukkit.BukkitAdapter");
            Object weWorld = bukkitAdapterClass.getMethod("adapt", World.class).invoke((Object)null, location.getWorld());
            Object regionManager = container.getClass().getMethod("get", Class.forName("com.sk89q.worldedit.world.World")).invoke(container, weWorld);
            if (regionManager == null) {
               return "Unprotected";
            } else {
               Class<?> blockVectorClass = Class.forName("com.sk89q.worldedit.math.BlockVector3");
               Object blockVector = blockVectorClass.getMethod("at", Integer.TYPE, Integer.TYPE, Integer.TYPE).invoke((Object)null, location.getBlockX(), location.getBlockY(), location.getBlockZ());
               Object applicableRegions = regionManager.getClass().getMethod("getApplicableRegions", blockVectorClass).invoke(regionManager, blockVector);
               int size = (Integer)applicableRegions.getClass().getMethod("size").invoke(applicableRegions);
               if (size == 0) {
                  return "Unprotected";
               } else {
                  Iterator var13 = ((Iterable)applicableRegions).iterator();
                  if (var13.hasNext()) {
                     Object region = var13.next();
                     String regionId = (String)region.getClass().getMethod("getId").invoke(region);
                     return regionId;
                  } else {
                     return "Unknown";
                  }
               }
            }
         } catch (Exception var16) {
            return "Error";
         }
      } else {
         return "Unprotected";
      }
   }

   public boolean isInRegion(Location location, String regionName) {
      if (this.worldGuardEnabled && location != null && location.getWorld() != null && regionName != null && !regionName.isEmpty()) {
         try {
            Class<?> worldGuardClass = Class.forName("com.sk89q.worldguard.WorldGuard");
            Object worldGuardInstance = worldGuardClass.getMethod("getInstance").invoke((Object)null);
            Object platform = worldGuardClass.getMethod("getPlatform").invoke(worldGuardInstance);
            Object container = platform.getClass().getMethod("getRegionContainer").invoke(platform);
            Class<?> bukkitAdapterClass = Class.forName("com.sk89q.worldedit.bukkit.BukkitAdapter");
            Object weWorld = bukkitAdapterClass.getMethod("adapt", World.class).invoke((Object)null, location.getWorld());
            Object regionManager = container.getClass().getMethod("get", Class.forName("com.sk89q.worldedit.world.World")).invoke(container, weWorld);
            if (regionManager == null) {
               return false;
            } else {
               Class<?> blockVectorClass = Class.forName("com.sk89q.worldedit.math.BlockVector3");
               Object blockVector = blockVectorClass.getMethod("at", Integer.TYPE, Integer.TYPE, Integer.TYPE).invoke((Object)null, location.getBlockX(), location.getBlockY(), location.getBlockZ());
               Object applicableRegions = regionManager.getClass().getMethod("getApplicableRegions", blockVectorClass).invoke(regionManager, blockVector);
               Iterator var13 = ((Iterable)applicableRegions).iterator();

               String regionId;
               do {
                  if (!var13.hasNext()) {
                     return false;
                  }

                  Object region = var13.next();
                  regionId = (String)region.getClass().getMethod("getId").invoke(region);
               } while(!regionId.equalsIgnoreCase(regionName));

               return true;
            }
         } catch (Exception var16) {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean isEnabled() {
      return this.worldGuardEnabled;
   }
}
