package com.zoltanmagyar.tamagotchi

case class Pet(name: String,
               lifeCycle: LifeCycle,
               isHealthy: Boolean,
               isHungry: Boolean,
               isHappy: Boolean,
               isAsleep: Boolean,
               isDead: Boolean) {
  def age = lifeCycle.next.fold(die)(next => copy(lifeCycle = next))
  def healthy = copy(isHealthy = true)
  def sick = copy(isHealthy = false)
  def fedAndHealthy = copy(isHungry = false, isHealthy = true)
  def hungry = copy(isHungry = true)
  def fedAndHappy = copy(isHungry = true, isHappy = true)
  def happy = copy(isHappy = true)
  def sad = copy(isHappy = false)
  def sleep = copy(isAsleep = true)
  def wakeUp = copy(isAsleep = false)
  def die = copy(isDead = true)
}

object Pet {
  def apply(name: String): Pet = new Pet(
    name = name,
    lifeCycle = Baby,
    isHealthy = true,
    isHungry = true,
    isHappy = true,
    isAsleep = false,
    isDead = false
  )
}
