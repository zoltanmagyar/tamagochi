package com.zoltanmagyar.tamagotchi

import akka.actor.{Actor, ActorRef, PoisonPill, Timers}
import akka.event.Logging
import language.postfixOps
import scala.concurrent.duration._

/**
  * Config for the Pet actor
  *
  * @param agingInterval how quick the pet should age
  * @param hungerInterval how frequently the pet should become hungry
  * @param sleepInterval how frequently the pet should go to sleep
  * @param poopTimeout how soon after a meal the pet should poop
  * @param sleepTimeout how long the pet should sleep
  * @param sicknessTimeout how long before the pet should become sick if not fed
  */
case class PetActorConfig(agingInterval: FiniteDuration = 10 minutes,
                          hungerInterval: FiniteDuration = 1 minute,
                          sleepInterval: FiniteDuration = 2 minutes,
                          poopTimeout: FiniteDuration = 30 seconds,
                          sleepTimeout: FiniteDuration = 1 minute,
                          sicknessTimeout: FiniteDuration = 30 seconds)
/**
  * Pet actor
  *
  * Responds to PetCommands
  *
  * Implements the basic requirements as per [[https://gist.github.com/davidvuong/90f8ac0916dd3e14fad014bc814614ff]]
  *
  * @param owner ref to the Owner actor
  * @param petActorConfig configuration
  */
class PetActor(owner: ActorRef, petActorConfig: PetActorConfig) extends Actor with Timers {
  val log = Logging(context.system, this)
  // (☉_☉) it's OK to use a var and no synchronisation here because the actor only processes a single message at a time
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
