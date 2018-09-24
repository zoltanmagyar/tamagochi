package com.zoltanmagyar.tamagotchi

/**
  * Responses a pet can give
  */
sealed trait PetResponse {
  def message: String
}
case class ImBorn(name: String) extends PetResponse {
  val message: String = s"Hello world! My name is $name."
}
case class IveAged(lifeCycle: LifeCycle) extends PetResponse {
  val message: String = s"I am ${lifeCycle.productPrefix} now!"
}
case object ImDead extends PetResponse {
  val message: String = "I'm dead! Bury me."
}
case object ImFull extends PetResponse {
  val message: String = "Thanks for feeding me. I'm full now."
}
case object ThanksForTheTreats extends PetResponse {
  val message: String = "Thanks for the treats!"
}
case object ImHungry extends PetResponse {
  val message: String = "I'm hungry! Feed me!"
}
case object ImSleeping extends PetResponse {
  val message: String = "Zzz"
}
case object ImAwake extends PetResponse {
  val message: String = "I love a snooze :) Buzzing now!"
}
case object IPooped extends PetResponse {
  val message: String = "Oops! I did it again"
}
case object ImSick extends PetResponse {
  val message: String = "I'm sick."
}
