package com.zoltanmagyar.tamagotchi

/**
  * Types of food a pet can be fed
  */
sealed trait Food
case object Meal extends Food
case object Snack extends Food
