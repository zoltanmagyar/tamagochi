package com.zoltanmagyar.tamagotchi

import akka.actor.{Actor, ActorRef, PoisonPill, Timers}
import akka.event.Logging
import language.postfixOps
import scala.concurrent.duration._

case class PetActorConfig(agingInterval: FiniteDuration = 10 minutes,
                          hungerInterval: FiniteDuration = 1 minute,
                          sleepInterval: FiniteDuration = 2 minutes,
                          poopTimeout: FiniteDuration = 30 seconds,
                          sleepTimeout: FiniteDuration = 1 minute,
                          sicknessTimeout: FiniteDuration = 30 seconds)

class PetActor(owner: ActorRef, petActorConfig: PetActorConfig) extends Actor with Timers {
  val log = Logging(context.system, this)
  var me: Option[Pet] = None
  override def receive: Receive = {
    case Rise(name) =>
      me = Some(Pet(name))
      timers.startPeriodicTimer("aging", Age, petActorConfig.agingInterval)
      timers.startPeriodicTimer("hunger", BecomeHungry, petActorConfig.hungerInterval)
      timers.startPeriodicTimer("tiredness", Sleep, petActorConfig.sleepInterval)
      owner ! ImBorn(name)
    case Eat(Meal) =>
      log.info("Eating a meal")
      me = me.map(_.fedAndHealthy)
      timers.startPeriodicTimer("hunger", BecomeHungry, petActorConfig.hungerInterval)
      timers.startSingleTimer("metabolism", Poop, petActorConfig.poopTimeout)
      owner ! ImFull
    case Eat(Snack) =>
      log.info("Eating a snack")
      me = me.map(_.fedAndHappy)
      owner ! ThanksForTheTreats
    case Sleep =>
      log.info("Going to sleep")
      me = me.map(_.sleep)
      timers.startSingleTimer("wakeup", WakeUp, petActorConfig.sleepTimeout)
      owner ! ImSleeping
    case Age =>
      val olderMe = me.get.age
      me = Some(olderMe)
      if (olderMe.isDead) {
        log.info("I'm dead")
        timers.cancelAll()
        owner ! ImDead
        self ! PoisonPill
      } else {
        owner ! IveAged(olderMe.lifeCycle)
      }
    case BecomeHungry =>
      log.info("Becoming hungry")
      me = me.map(_.hungry)
      timers.startSingleTimer("sickness", BecomeSick, petActorConfig.sicknessTimeout)
      owner ! ImHungry
    case BecomeSick =>
      log.info("Becoming sick")
      me = me.map(_.sick)
      owner ! ImSick
    case WakeUp =>
      me = me.map(_.wakeUp)
      owner ! ImAwake
    case Poop =>
      log.info("Pooping")
      owner ! IPooped
  }
}
