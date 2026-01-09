package ru.astridhanson.astridjoin.astridbosses.listeners;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityCombustEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import ru.astridhanson.astridjoin.astridbosses.data.BossData;
import ru.astridhanson.astridjoin.astridbosses.managers.BossManager;

public class BossListener implements Listener {
   private final BossManager bossManager;

   public BossListener(BossManager bossManager) {
      this.bossManager = bossManager;
   }

   @EventHandler
   public void onBossDamage(EntityDamageByEntityEvent event) {
      Entity entity = event.getEntity();
      if (entity instanceof LivingEntity) {
         LivingEntity boss = (LivingEntity)entity;
         BossData bossData = this.bossManager.getBossData(boss.getUniqueId());
         if (bossData != null) {
            Entity damager = event.getDamager();
            if (bossData.isIgnoreArrowDamage() && (damager instanceof Arrow || damager instanceof Projectile)) {
               event.setCancelled(true);
               return;
            }

            this.bossManager.onBossDamage(boss, event);
         }
      }

   }

   @EventHandler
   public void onBossDeath(EntityDeathEvent event) {
      LivingEntity boss = event.getEntity();
      if (this.bossManager.getBossData(boss.getUniqueId()) != null) {
         event.getDrops().clear();
         event.setDroppedExp(0);
         this.bossManager.onBossDeath(boss);
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onBossCombust(EntityCombustEvent event) {
      Entity entity = event.getEntity();
      if (entity instanceof LivingEntity) {
         LivingEntity boss = (LivingEntity)entity;
         BossData bossData = this.bossManager.getBossData(boss.getUniqueId());
         if (bossData != null && bossData.isIgnoreSunDamage()) {
            event.setCancelled(true);
         }
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onBossDamageByFire(EntityDamageEvent event) {
      Entity entity = event.getEntity();
      if (entity instanceof LivingEntity) {
         LivingEntity boss = (LivingEntity)entity;
         BossData bossData = this.bossManager.getBossData(boss.getUniqueId());
         if (bossData != null && bossData.isIgnoreFireDamage()) {
            DamageCause cause = event.getCause();
            if (cause == DamageCause.FIRE || cause == DamageCause.FIRE_TICK || cause == DamageCause.LAVA || cause == DamageCause.HOT_FLOOR) {
               event.setCancelled(true);
            }
         }
      }

   }
}
