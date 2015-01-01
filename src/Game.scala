import data.enum.{CreatureEffectType, EffectType}
import data._

import scala.collection.mutable.ListBuffer
import scala.util.Random

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
    Logger.sayThat("# " + attacker.name + " moves\n")

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
        useSpellCardEffect(cardToMove.asInstanceOf[SpellCard])
        attacker.cardHand -= cardToMove
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
        fightBetweenMinions(chosenMinion, opponent.cardBoard(answer.toInt).asInstanceOf[MinionCard])
      else
        attackHero(chosenMinion)
    }
  }

  private def attackHero(minion: MinionCard): Unit = {
    opponent.heroHealth -= minion.currentAttack
  }

  private def fightBetweenMinions(minion: MinionCard, target: MinionCard): Unit = {
    target.currentHealth -= minion.currentAttack
    minion.currentHealth -= target.currentAttack

    if (minion.currentHealth <= 0)
      attacker.cardBoard -= minion
    if (target.currentHealth <= 0)
      opponent.cardBoard -= target

    // TODO effects
  }

  private def damageMinion(target: MinionCard, amount: Int): Unit = {
    target.currentHealth -= amount

    if (target.currentHealth <= 0)
      opponent.cardBoard -= target

    // TODO effects
  }

  private def changeMinionHealth(target: MinionCard, amount: Int): Unit = {
    target.currentHealth = amount

    if (target.currentHealth <= 0)
      opponent.cardBoard -= target

    // TODO effects
  }

  private def changeMinionAttack(target: MinionCard, amount: Int, isRelative: Boolean): Unit = {
    if (isRelative)
      target.currentAttack += amount
    else
      target.currentAttack = amount
  }

  private def useSpellCardEffect(card: SpellCard): Unit = {
    for (cardEffect <- card.effects) {
      if (cardEffect.effect == EffectType.OnPlay) {
        for (eventEffect <- cardEffect.eventEffects) {
          eventEffect match {
            case _: DrawCardEventEffect =>
              Logger.sayThat("Drew card!")
              attacker.takeCard()
            case effect: ChooseEventEffect =>
              val filteredCards = filterCardsWithFilters(effect.filters, card)
              val target = Logger.askFromFilteredMobs(attacker, filteredCards)
              Logger.sayThat("Applying effect to " + filteredCards(Integer.valueOf(target)).name)
              applyEffectsToTarget(effect.creatureEffects, filteredCards(Integer.valueOf(target)))
            case effect: RandomEventEffect =>
              val filteredCards = filterCardsWithFilters(eventEffect.asInstanceOf[ChooseEventEffect].filters, card)
              val randomTarget = Random.shuffle(filteredCards).head
              Logger.sayThat("Applying effect to " + randomTarget.name)
              applyEffectsToTarget(effect.creatureEffects, randomTarget)
            case effect: AllEventEffect =>
              for (card <- filterCardsWithFilters(effect.filters, card)) {
                Logger.sayThat("Applying effect to " + card.name)
                applyEffectsToTarget(effect.creatureEffects, card)
              }
            case _ =>
          }
        }
      }
    }
  }

  private def applyEffectsToTarget(creatureEffects: List[CreatureEffect], targetCard: Card) : Unit = {
    for (creatureEffect <- creatureEffects) {
      creatureEffect match {
        case effect: HealthEffect =>
          if (effect.effectType == CreatureEffectType.Absolute) {
            changeMinionHealth(targetCard.asInstanceOf[MinionCard], effect.health)
          } else if (effect.effectType == CreatureEffectType.Relative) {
            damageMinion(targetCard.asInstanceOf[MinionCard], effect.health)
          }
        case effect: AttackEffect =>
          if (effect.effectType == CreatureEffectType.Absolute) {
            changeMinionAttack(targetCard.asInstanceOf[MinionCard], effect.attack, false)
          } else if (effect.effectType == CreatureEffectType.Relative) {
            changeMinionAttack(targetCard.asInstanceOf[MinionCard], effect.attack, true)
          }
        case effect: TauntEffect =>
          targetCard.asInstanceOf[MinionCard].currentTaunt = effect.taunt
        case _ =>
      }
    }
  }

  private def filterCardsWithFilters(filters: List[Filter], self: Card): ListBuffer[Card] = {
    var filteredCards = new ListBuffer[Card]
    for (friendlyCard <- attacker.cardBoard) {
      var passesFilter = true
      for (filter <- filters) {
        filter match {
          case _: AnyCreatureFilter =>
          case _: AnyHeroFilter =>
          case _: AnyFriendlyFilter =>
          case a: TypeFilter =>
            if (a.minionType != friendlyCard.asInstanceOf[MinionCard].minionType)
              passesFilter = false
          case _: SelfFilter =>
            if (self != friendlyCard)
              passesFilter = false
        }
      }
      if (passesFilter)
        filteredCards += friendlyCard
    }
    for (enemyCard <- opponent.cardBoard) {
      var passesFilter = true
      for (filter <- filters) {
        filter match {
          case _: AnyCreatureFilter =>
          case _: AnyHeroFilter =>
          case _: AnyFriendlyFilter =>
            passesFilter = false
          case a: TypeFilter =>
            if (a.minionType != enemyCard.asInstanceOf[MinionCard].minionType)
              passesFilter = false
          case _: SelfFilter =>
        }
      }
      if (passesFilter)
        filteredCards += enemyCard
    }
    filteredCards
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
