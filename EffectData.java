package ru.astridhanson.astridjoin.astridbosses.data;

public class EffectData {
   private String type;
   private int amplifier;
   private int duration;

   public EffectData(String type, int amplifier, int duration) {
      this.type = type;
      this.amplifier = amplifier;
      this.duration = duration;
   }

   public String getType() {
      return this.type;
   }

   public int getAmplifier() {
      return this.amplifier;
   }

   public int getDuration() {
      return this.duration;
   }
}
