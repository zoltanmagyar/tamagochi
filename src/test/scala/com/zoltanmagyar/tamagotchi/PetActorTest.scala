package com.zoltanmagyar.tamagotchi

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import scala.concurrent.duration._
import scala.language.postfixOps

class PetActorTest() extends TestKit(ActorSystem("PetActorTest")) with ImplicitSender with WordSpecLike with Matchers
  with BeforeAndAfterAll {

  override def afterAll: Unit = TestKit.shutdownActorSystem(system, verifySystemShutdown = true)

  "A pet" must {
    "be able to be fed" in {
      val pet = system.actorOf(Props(classOf[PetActor], testActor, PetActorConfig()))

      pet ! Rise("hungry pet")
      expectMsg(ImBorn("hungry pet"))

      pet ! Eat(Meal)
      expectMsg(ImFull)

      system.stop(pet)
    }
  }

  "A pet" must {
    "be able to be put to bed" in {
      val pet = system.actorOf(Props(classOf[PetActor], testActor, PetActorConfig()))

      pet ! Rise("sleepy pet")
      expectMsg(ImBorn("sleepy pet"))

      pet ! Sleep
      expectMsg(ImSleeping)

      system.stop(pet)
    }
  }

  "A pet" must {
    "be able to age from birth to death" in {
      val pet = system.actorOf(Props(classOf[PetActor], testActor, PetActorConfig().copy(agingInterval = 50 millis)))

      pet ! Rise("mayfly")
      val last = receiveN(6, 500 millis).last
      assert(last == ImDead)

      system.stop(pet)
    }
  }

  "A pet" must {
    "be able to able to go to sleep on its own" in {
      val pet = system.actorOf(Props(classOf[PetActor], testActor, PetActorConfig().copy(sleepInterval = 50 millis)))

      pet ! Rise("snoozy")
      expectMsg(ImBorn("snoozy"))

      expectMsg(ImSleeping)

      system.stop(pet)
    }
  }

  "A pet" must {
    "be able to able to go to poop on its own" in {
      val pet = system.actorOf(Props(classOf[PetActor], testActor, PetActorConfig().copy(poopTimeout = 50 millis)))

      pet ! Rise("number two")
      expectMsg(ImBorn("number two"))

      pet ! Eat(Meal)
      expectMsg(ImFull)

      expectMsg(IPooped)

      system.stop(pet)
    }
  }

  "A pet" must {
    "become sick if not fed for a while" in {
      val pet = system.actorOf(Props(classOf[PetActor], testActor, PetActorConfig().copy(
        hungerInterval = 50 millis,
        sicknessTimeout = 10 millis
      )))

      pet ! Rise("sick dude")
      expectMsg(ImBorn("sick dude"))

      expectMsg(ImHungry)
      expectMsg(ImSick)

      system.stop(pet)
    }
  }
}
