package com.zoltanmagyar.tamagotchi

import akka.actor.{Actor, ActorRef, ActorSystem, PoisonPill, Props, Timers}
import akka.event.Logging
import akka.pattern.gracefulStop
import com.zoltanmagyar.tamagotchi.PetActor._
import language.postfixOps
import scala.concurrent.Await
import scala.concurrent.duration._
import scala.io.StdIn

object Main extends App {
  val system = ActorSystem("tamagotchi")
  val owner = system.actorOf(Props[OwnerActor], "owner")
  val pet = system.actorOf(Props(classOf[PetActor], owner), "pet")
  import system.dispatcher

  Iterator.continually(StdIn.readLine())
    .takeWhile(_ != "exit")
    .map(_.split(" ").toList)
    .flatMap {
      case "activate" :: List(name) =>
        Some(Rise(name))
      case "feed" :: List("meal") =>
        Some(Eat(Meal))
      case "feed" :: List("snack") =>
        Some(Eat(Snack))
      case "sleep" :: _ =>
        Some(Sleep)
      case _ =>
        println("Unknown command")
        None
    }
    .foreach { command =>
      system.scheduler.scheduleOnce(500 millis) {
        pet ! command
      }
    }

  stopActor(pet)
  stopActor(owner)

  def stopActor(actorRef: ActorRef) = Await.result(gracefulStop(actorRef, 5.seconds), 5.seconds)
}

class OwnerActor extends Actor {
  val log = Logging(context.system, this)
  override def receive: Receive = {
    case ImDead =>
      log.info("Your pet has died! Sorry")
    case petResponse: PetResponse =>
      log.info(s"Pet says: ${petResponse.message}")
    case unknown =>
      log.info(s"Unknown message: $unknown")
  }
}

class PetActor(owner: ActorRef) extends Actor with Timers {
  var me: Option[Pet] = None
  override def receive: Receive = {
    case Rise(name) =>
      me = Some(Pet(name))
      timers.startPeriodicTimer("aging", Age, AgingInterval)
      timers.startPeriodicTimer("hunger", BecomeHungry, HungerInterval)
      timers.startPeriodicTimer("tiredness", Sleep, SleepInterval)
      owner ! ImBorn(name)
    case Eat(Meal) =>
      me = me.map(_.fedAndHealthy)
      timers.startPeriodicTimer("hunger", BecomeHungry, HungerInterval)
      timers.startSingleTimer("metabolism", Poop, PoopTimeout)
      owner ! ImFull
    case Eat(Snack) =>
      me = me.map(_.fedAndHappy)
      owner ! ThanksForTheTreats
    case Sleep =>
      me = me.map(_.sleep)
      timers.startSingleTimer("wakeup", WakeUp, SleepTimeout)
      owner ! ImSleeping
    case Age =>
      val olderMe = me.get.age
      me = Some(olderMe)
      if (olderMe.isDead) {
        timers.cancelAll()
        owner ! ImDead
        self ! PoisonPill
      } else {
        owner ! IveAged(olderMe.lifeCycle)
      }
    case BecomeHungry =>
      me = me.map(_.hungry)
      timers.startSingleTimer("sickness", BecomeSick, SicknessTimeout)
      owner ! ImHungry
    case BecomeSick =>
      me = me.map(_.sick)
      owner ! ImSick
    case WakeUp =>
      me = me.map(_.wakeUp)
      owner ! ImAwake
    case Poop =>
      owner ! IPooped
  }
}

object PetActor {
  val AgingInterval: FiniteDuration = 10 minutes
  val HungerInterval: FiniteDuration = 1 minute
  val SleepInterval: FiniteDuration = 2 minutes
  val PoopTimeout: FiniteDuration = 30 seconds
  val SleepTimeout: FiniteDuration = 1 minute
  val SicknessTimeout: FiniteDuration = 30 seconds
}
