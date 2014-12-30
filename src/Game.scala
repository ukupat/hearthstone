import data.{MinionCard, SpellCard}

import scala.collection.mutable.ListBuffer

class Game(val bluePlayer: Player, val redPlayer: Player) {

  var round: Int = 0

  def startPlaying(): Unit = {
    bluePlayer.takeStartingCards()
    redPlayer.takeStartingCards()

    while (round != -1) {
      startNewRound()
      carryOutAttackerMoves()
      endRound()

      round = nextRound()
    }
  }

  private def startNewRound(): Unit = {
    attacker.takeCardAndMana()

    Logger.startNewRound(round, attacker.name)

    var showHand = false
    Logger.showPlayerInfo(opponent, showHand)

    showHand = true
    Logger.showPlayerInfo(attacker, showHand)
  }

  private def carryOutAttackerMoves(): Unit = {
    Logger.sayThat("# Your moves\n")

    playFromHand()
    playFromBoard()
  }

  private def playFromHand(): Unit = {
    Logger.sayThat("## From hand\n")

    while (true) {
      if (!Referee.hasAnythingToMove(attacker)) {
        Logger.sayThat("\nI: No cards to play from hand, skipping")
        return
      }
      val answer = Logger.askWhichCardToUseFromHand(attacker)

      if (answer == "*") {
        return
      }
      val cardToMove = attacker.cardHand(answer.toInt)

      if (cardToMove.isInstanceOf[SpellCard]) {
        // TODO spell cards
      } else {
        attacker.moveCardFromHandToBoard(cardToMove)

        // TODO effects
      }
    }
  }

  private def playFromBoard(): Unit = {
    Logger.sayThat("\n## From board\n")

    while (true) {
      if (!Referee.hasMinionsOnBoard(attacker)) {
        Logger.sayThat("\nI: No cards to play from board, skipping")
        return
      }
      var answer = Logger.askToChooseMinionFromBoard(attacker)

      if (answer == "*")
        return

      val chosenMinion: MinionCard = attacker.cardBoard(answer.toInt).asInstanceOf[MinionCard]
      chosenMinion.used = true

      answer = Logger.askTarget(opponent)

      if (answer != "h")
        attackMinion(chosenMinion, opponent.cardBoard(answer.toInt).asInstanceOf[MinionCard])
      else
        attackHero(chosenMinion)
    }
  }

  private def attackHero(minion: MinionCard): Unit = {
    opponent.heroHealth -= minion.currentAttack
  }

  private def attackMinion(minion: MinionCard, target: MinionCard): Unit = {
    target.currentHealth -= minion.currentAttack
    minion.currentHealth -= target.currentAttack

    if (minion.currentHealth <= 0)
      attacker.cardBoard -= minion
    if (target.currentHealth <= 0)
      opponent.cardBoard -= target

    // TODO effects
  }

  private def endRound(): Unit = {
    for (card <- attacker.cardBoard)
      card.asInstanceOf[MinionCard].used = false
  }

  private def attacker: Player = {
    if (round % 2 == 0)
      redPlayer
    else
      bluePlayer
  }

  private def opponent: Player = {
    if (round % 2 == 0)
      bluePlayer
    else
      redPlayer
  }

  private def nextRound(): Int = {
    if (Referee.isGameOver(redPlayer, bluePlayer)) {
      Logger.sayGameResultsAndEndIt()
      -1
    } else {
      round + 1
    }
  }
}
