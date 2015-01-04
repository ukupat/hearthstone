package hearthstone.game

import hearthstone.data._
import hearthstone.data.enum.EffectType
import hearthstone.util.{Logger, Referee}

class Game(val bluePlayer: Player, val redPlayer: Player) {

  var round: Int = 0

  def play(): Unit = {
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
    Logger.sayThat("# " + attacker.name + " moves\n")

    playFromHand()
    playFromBoard()
  }

  private def playFromHand(): Unit = {
    Logger.sayThat("## From " + attacker.name + " hand\n")

    while (true) {
      if (!Referee.hasAnythingToMove(attacker)) {
        Logger.sayThat("\nI: No cards to play from hand, skipping")
        return
      }
      val answer = Logger.askWhichCardToUseFromHand(attacker)

      if (answer == "*") {
        return
      }
      makeMoveFromHand(attacker.cardHand(answer.toInt).asInstanceOf[PlayCard])
    }
  }

  private def makeMoveFromHand(attackerCard: PlayCard) = attackerCard match {
    case attackerCard: SpellCard =>
      new Gandalf(attacker, opponent).playCardEffect(attackerCard, EffectType.OnPlay)
      attacker.cardHand -= attackerCard
    case attackerCard: MinionCard =>
      val gandalf = new Gandalf(attacker, opponent)

      gandalf.playCardEffect(attackerCard, EffectType.OnPlay)
      gandalf.playCardEffect(attackerCard, EffectType.UntilDeath)

      attacker.moveCardFromHandToBoard(attackerCard)
  }

  private def playFromBoard(): Unit = {
    Logger.sayThat("\n## From " + attacker.name + " board\n")

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
        fightBetweenMinions(chosenMinion, opponent.cardBoard(answer.toInt).asInstanceOf[MinionCard])
      else
        attackHero(chosenMinion)
    }
  }

  private def attackHero(minion: MinionCard): Unit = {
    opponent.hero.health -= minion.currentAttack
  }

  private def fightBetweenMinions(minion: MinionCard, target: MinionCard): Unit = {
    var gandalf = new Gandalf(opponent, attacker)
    var wasKilled = opponent.changeMinionHealth(target, -minion.currentAttack, true)
    gandalf.playCardEffect(target, EffectType.OnDamage)

    if (wasKilled) {
      gandalf.playCardEffect(target, EffectType.OnDeath)
      gandalf.playCardEffect(target, EffectType.UntilDeath)
    }
    gandalf = new Gandalf(attacker, opponent)
    wasKilled = attacker.changeMinionHealth(minion, -target.currentAttack, true)
    gandalf.playCardEffect(minion, EffectType.OnDamage)

    if (wasKilled) {
      gandalf.playCardEffect(minion, EffectType.OnDeath)
      gandalf.playCardEffect(minion, EffectType.UntilDeath)
    }
  }

  private def endRound(): Unit = {
    for (card <- attacker.cardBoard)
      card.asInstanceOf[MinionCard].used = false
  }

  private def nextRound(): Int = {
    if (Referee.isGameOver(redPlayer, bluePlayer)) {
      Logger.sayGameResultsAndEndIt(Referee.whoIsWinner(redPlayer, bluePlayer).name)
      -1
    } else {
      round + 1
    }
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
}
