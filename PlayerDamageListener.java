package ru.astridhanson.astridjoin.astridbosses.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import ru.astridhanson.astridjoin.astridbosses.data.BossData;
import ru.astridhanson.astridjoin.astridbosses.managers.BossManager;

public class PlayerDamageListener implements Listener {
   private final BossManager bossManager;

   public PlayerDamageListener(BossManager bossManager) {
      this.bossManager = bossManager;
   }

   @EventHandler(
      priority = EventPriority.HIGH
   )
   public void onPlayerDamagedByBoss(EntityDamageByEntityEvent event) {
      Entity victim = event.getEntity();
      Entity damager = event.getDamager();
      if (victim instanceof Player && this.bossManager.getBossData(damager.getUniqueId()) != null) {
         Player player = (Player)victim;
         BossData bossData = this.bossManager.getBossData(damager.getUniqueId());
         event.setCancelled(true);
         double customDamage = bossData.getDamage();
         player.damage(customDamage);
         player.setVelocity(player.getVelocity().add(damager.getLocation().getDirection().normalize().multiply(0.5D).setY(0.3D)));
      }

   }
}
