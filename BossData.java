package ru.astridhanson.astridjoin.astridbosses.data;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.potion.PotionEffect;

public class BossData {
   private String id;
   private String displayName;
   private EntityType entityType;
   private double maxHealth;
   private double damage;
   private String spawnWorld;
   private Location spawnLocation;
   private boolean randomSpawn;
   private List<Reward> rewards = new ArrayList();
   private DamageTopRewards damageTopRewards;
   private List<PotionEffect> effects = new ArrayList();
   private boolean ignoreArrowDamage = true;
   private boolean ignoreSunDamage = true;
   private boolean ignoreFireDamage = true;

   public String getId() {
      return this.id;
   }

   public void setId(String id) {
      this.id = id;
   }

   public String getDisplayName() {
      return this.displayName;
   }

   public void setDisplayName(String displayName) {
      this.displayName = displayName;
   }

   public EntityType getEntityType() {
      return this.entityType;
   }

   public void setEntityType(EntityType entityType) {
      this.entityType = entityType;
   }

   public double getMaxHealth() {
      return this.maxHealth;
   }

   public void setMaxHealth(double maxHealth) {
      this.maxHealth = maxHealth;
   }

   public double getDamage() {
      return this.damage;
   }

   public void setDamage(double damage) {
      this.damage = damage;
   }

   public String getSpawnWorld() {
      return this.spawnWorld;
   }

   public void setSpawnWorld(String spawnWorld) {
      this.spawnWorld = spawnWorld;
   }

   public Location getSpawnLocation() {
      return this.spawnLocation;
   }

   public void setSpawnLocation(Location spawnLocation) {
      this.spawnLocation = spawnLocation;
   }

   public boolean isRandomSpawn() {
      return this.randomSpawn;
   }

   public void setRandomSpawn(boolean randomSpawn) {
      this.randomSpawn = randomSpawn;
   }

   public List<Reward> getRewards() {
      return this.rewards;
   }

   public List<PotionEffect> getEffects() {
      return this.effects;
   }

   public void setEffects(List<PotionEffect> effects) {
      this.effects = effects;
   }

   public boolean isIgnoreArrowDamage() {
      return this.ignoreArrowDamage;
   }

   public void setIgnoreArrowDamage(boolean ignoreArrowDamage) {
      this.ignoreArrowDamage = ignoreArrowDamage;
   }

   public boolean isIgnoreSunDamage() {
      return this.ignoreSunDamage;
   }

   public void setIgnoreSunDamage(boolean ignoreSunDamage) {
      this.ignoreSunDamage = ignoreSunDamage;
   }

   public boolean isIgnoreFireDamage() {
      return this.ignoreFireDamage;
   }

   public void setIgnoreFireDamage(boolean ignoreFireDamage) {
      this.ignoreFireDamage = ignoreFireDamage;
   }

   public DamageTopRewards getDamageTopRewards() {
      return this.damageTopRewards;
   }

   public void setDamageTopRewards(DamageTopRewards damageTopRewards) {
      this.damageTopRewards = damageTopRewards;
   }
}
