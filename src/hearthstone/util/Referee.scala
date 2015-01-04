package hearthstone.util

import hearthstone.data.{Card, PlayCard, MinionCard}
import hearthstone.game.Player

import scala.collection.mutable.ListBuffer

object Referee {

  def hasAnythingToMove(player: Player): Boolean = {
    for (card <- player.cardHand) {
      if (card.asInstanceOf[PlayCard].cost <= player.mana)
        return true
    }
    false
  }

  def hasMinionsOnBoard(player: Player): Boolean = {
    player.cardBoard.length != 0
  }

  def hasTauntsOnBoard(player: Player): Boolean = {
    for (card <- player.cardBoard)
     if (card.asInstanceOf[MinionCard].currentTaunt)
       return true

    false
  }

  def isCardFromHandValid(cardIndex: String, player: Player): Boolean = {
    try {
      cardIndex.toInt >= 0 && cardIndex.toInt < player.cardHand.length && player.cardHand(cardIndex.toInt).asInstanceOf[PlayCard].cost <= player.mana
    } catch {
      case e: Exception => false
    }
  }

  def isCardFromBoardValid(cardIndex: String, player: Player): Boolean = {
    try {
      cardIndex.toInt >= 0 && cardIndex.toInt < player.cardBoard.length && !player.cardBoard(cardIndex.toInt).asInstanceOf[MinionCard].used
    } catch {
      case e: Exception => false
    }
  }

  def isAttackedCardValid(cardIndex: String, player: Player): Boolean = {
    isCardFromBoardValid(cardIndex, player) &&
      (!hasTauntsOnBoard(player) || player.cardBoard(cardIndex.toInt).asInstanceOf[MinionCard].currentTaunt)
  }

  def fitsIntoCardList(cardIndex: String, cardList: ListBuffer[Card]): Boolean = {
    try {
      cardIndex.toInt >= 0 && cardIndex.toInt < cardList.length
    } catch {
      case e: Exception => false
    }
  }

  def isGameOver(redPlayer: Player, bluePlayer: Player): Boolean = {
    redPlayer.hero.health <= 0 || bluePlayer.hero.health <= 0
  }

  def whoIsWinner(redPlayer: Player, bluePlayer: Player): Player = {
    if (redPlayer.hero.health <= 0)
      bluePlayer
    else
      redPlayer
  }
}
