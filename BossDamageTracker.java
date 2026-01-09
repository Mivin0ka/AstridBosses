package ru.astridhanson.astridjoin.astridbosses.data;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class BossDamageTracker {
   private Map<UUID, Double> damageMap = new HashMap();
   private double totalDamage = 0.0D;

   public void addDamage(UUID playerUUID, double damage) {
      this.damageMap.put(playerUUID, (Double)this.damageMap.getOrDefault(playerUUID, 0.0D) + damage);
      this.totalDamage += damage;
   }

   public UUID getTopDamager() {
      return (UUID)this.damageMap.entrySet().stream().max(Entry.comparingByValue()).map(Entry::getKey).orElse((Object)null);
   }

   public Map<UUID, Double> getSortedDamage() {
      return (Map)this.damageMap.entrySet().stream().sorted((e1, e2) -> {
         return Double.compare((Double)e2.getValue(), (Double)e1.getValue());
      }).limit(10L).collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> {
         return e1;
      }, LinkedHashMap::new));
   }

   public Map<UUID, Double> getTop3Damage() {
      return (Map)this.damageMap.entrySet().stream().sorted((e1, e2) -> {
         return Double.compare((Double)e2.getValue(), (Double)e1.getValue());
      }).limit(3L).collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> {
         return e1;
      }, LinkedHashMap::new));
   }

   public Map<UUID, Double> getDamageMap() {
      return this.damageMap;
   }

   public double getTotalDamage() {
      return this.totalDamage;
   }

   public void clear() {
      this.damageMap.clear();
      this.totalDamage = 0.0D;
   }
}
