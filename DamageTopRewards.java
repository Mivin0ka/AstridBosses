package ru.astridhanson.astridjoin.astridbosses.data;

public class DamageTopRewards {
   private Reward firstPlace;
   private Reward secondPlace;
   private Reward thirdPlace;

   public DamageTopRewards(Reward first, Reward second, Reward third) {
      this.firstPlace = first;
      this.secondPlace = second;
      this.thirdPlace = third;
   }

   public Reward getRewardForPlace(int place) {
      switch(place) {
      case 1:
         return this.firstPlace;
      case 2:
         return this.secondPlace;
      case 3:
         return this.thirdPlace;
      default:
         return null;
      }
   }

   public Reward getFirstPlace() {
      return this.firstPlace;
   }

   public Reward getSecondPlace() {
      return this.secondPlace;
   }

   public Reward getThirdPlace() {
      return this.thirdPlace;
   }

   public void setFirstPlace(Reward r) {
      this.firstPlace = r;
   }

   public void setSecondPlace(Reward r) {
      this.secondPlace = r;
   }

   public void setThirdPlace(Reward r) {
      this.thirdPlace = r;
   }
}
