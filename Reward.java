package ru.astridhanson.astridjoin.astridbosses.data;

public class Reward {
   private Reward.RewardType type;
   private String value;
   private int amount;

   public Reward(Reward.RewardType type, String value, int amount) {
      this.type = type;
      this.value = value;
      this.amount = amount;
   }

   public Reward.RewardType getType() {
      return this.type;
   }

   public void setType(Reward.RewardType type) {
      this.type = type;
   }

   public String getValue() {
      return this.value;
   }

   public void setValue(String value) {
      this.value = value;
   }

   public int getAmount() {
      return this.amount;
   }

   public void setAmount(int amount) {
      this.amount = amount;
   }

   public static enum RewardType {
      VAULT,
      PLAYERPOINTS,
      ITEM,
      COMMAND;

      // $FF: synthetic method
      private static Reward.RewardType[] $values() {
         return new Reward.RewardType[]{VAULT, PLAYERPOINTS, ITEM, COMMAND};
      }
   }
}
