package com.zoltanmagyar.tamagotchi

/**
  * Life cycle of a pet
  */
sealed trait LifeCycle extends Product {
  def next: Option[LifeCycle]
}
case object Baby extends LifeCycle {
  val next = Some(Child)
}
case object Child extends LifeCycle {
  val next = Some(Teen)
}
case object Teen extends LifeCycle {
  val next = Some(Adult)
}
case object Adult extends LifeCycle {
  val next = Some(Senior)
}
case object Senior extends LifeCycle {
  val next = None
}
