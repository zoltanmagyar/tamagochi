package com.zoltanmagyar.tamagotchi

import akka.actor.Actor
import akka.event.Logging

/**
  * Owner actor
  *
  * Pretty pointless apart from logging what the pet does
  */
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
