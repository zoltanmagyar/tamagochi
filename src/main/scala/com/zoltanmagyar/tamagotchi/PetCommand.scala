package com.zoltanmagyar.tamagotchi

/**
  * Commands a pet understands
  */
sealed trait PetCommand
case object Age extends PetCommand
case object BecomeHungry extends PetCommand
case object BecomeSick extends PetCommand
case class Rise(name: String) extends PetCommand
case class Eat(food: Food) extends PetCommand
case object Sleep extends PetCommand
case object Poop extends PetCommand
case object WakeUp extends PetCommand
