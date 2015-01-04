package hearthstone.game

import hearthstone.data._
import hearthstone.data.enum.EffectType.EffectType
import hearthstone.data.enum.{CreatureEffectType, EffectType}
import hearthstone.util.Logger

import scala.collection.mutable.ListBuffer
import scala.util.Random

class Gandalf(attacker: Player, opponent: Player) {

  def playCardEffect(card: PlayCard, effectType: EffectType): Unit = {
    val effect = card.getEffectByType(effectType)

    if (effect == null)
      return

    for (eventEffect <- effect.eventEffects) {
      // REMOVE when effects are done
      effectType match {
        case EffectType.OnPlay => playCardEventEffect(eventEffect, card)
        case EffectType.OnDamage => playCardEventEffect(eventEffect, card)
        case EffectType.OnDeath => playCardEventEffect(eventEffect, card)
      }
    }
  }

  private def playCardEventEffect(eventEffect: EventEffect, card: PlayCard) = eventEffect match {
    case effect: DrawCardEventEffect => playDrawCardEventEffect()
    case effect: ChooseEventEffect => playChooseEventEffect(eventEffect.asInstanceOf[ChooseEventEffect], card)
    case effect: RandomEventEffect => playRandomEventEffect(eventEffect.asInstanceOf[RandomEventEffect], card)
    case effect: AllEventEffect => playAllEventEffect(eventEffect.asInstanceOf[AllEventEffect], card)
  }

  private def playDrawCardEventEffect(): Unit = {
    Logger.sayThat("\nI: Drew a card")
    attacker.takeCard()
  }

  private def playChooseEventEffect(eventEffect: ChooseEventEffect, card: PlayCard): Unit = {
    val filteredCards = filterCardsWithFilters(eventEffect.filters, card)
    val target = Logger.askFromFilteredMobs(attacker, filteredCards)

    applyEffectsToTarget(eventEffect.creatureEffects, filteredCards(Integer.valueOf(target)))
  }

  private def playRandomEventEffect(eventEffect: RandomEventEffect, card: PlayCard): Unit = {
    val filteredCards = filterCardsWithFilters(eventEffect.filters, card)
    val randomTarget = Random.shuffle(filteredCards).head

    applyEffectsToTarget(eventEffect.creatureEffects, randomTarget.asInstanceOf[MinionCard])
  }

  private def playAllEventEffect(eventEffect: AllEventEffect, card: PlayCard): Unit = {
    for (card <- filterCardsWithFilters(eventEffect.filters, card)) {
      applyEffectsToTarget(eventEffect.creatureEffects, card.asInstanceOf[MinionCard])
    }
  }

  private def applyEffectsToTarget(creatureEffects: List[CreatureEffect], targetCard: Card) : Unit = {
    Logger.sayThat("I: Applying effect to " + targetCard.name)

    for (creatureEffect <- creatureEffects) {
      creatureEffect match {
        case effect: HealthEffect => playHealthEffect(effect, targetCard)
        case effect: AttackEffect => playAttackEffect(effect, targetCard)
        case effect: TauntEffect =>  playTauntEffect(effect, targetCard)
      }
    }
  }

  private def playHealthEffect(effect: HealthEffect, card: Card): Unit = {
    if (card.isInstanceOf[PlayCard]) {
      val playCard = card.asInstanceOf[MinionCard]
      val wasKilled = playCard.owner.changeMinionHealth(playCard, effect.health, effect.effectType == CreatureEffectType.Relative)

      this.playCardEffect(playCard, EffectType.OnDamage)

      if (wasKilled) {
        this.playCardEffect(playCard, EffectType.OnDeath)
        this.playCardEffect(playCard, EffectType.UntilDeath)
      }
    } else {
      val heroCard = card.asInstanceOf[HeroCard]
      heroCard.health -= effect.health
    }
  }

  private def playAttackEffect(effect: AttackEffect, card: Card): Unit = {
    if (card.isInstanceOf[PlayCard]) {
      card.asInstanceOf[MinionCard].owner.changeMinionAttack(card.asInstanceOf[MinionCard], effect.attack, effect.effectType == CreatureEffectType.Relative)
    } else {
      // Do nothing.
    }
  }

  private def playTauntEffect(effect: TauntEffect, card: Card): Unit = {
    if (card.isInstanceOf[PlayCard]) {
      card.asInstanceOf[MinionCard].currentTaunt = effect.taunt
    } else {
      // Do nothing.
    }
  }

  private def filterCardsWithFilters(filters: List[Filter], self: PlayCard): ListBuffer[Card] = {
    var filteredCards = new ListBuffer[Card]
    val friendlyCards = attacker.cardBoard.clone()
    friendlyCards.append(attacker.hero)
    for (friendlyCard <- friendlyCards) {
      var passesFilter = true
      println("siin")
      if (filters != null) {
        for (filter <- filters) {
          filter match {
            case _: AnyCreatureFilter =>
              if (!friendlyCard.isInstanceOf[MinionCard])
                passesFilter = false
            case _: AnyHeroFilter =>
              if (!friendlyCard.isInstanceOf[HeroCard])
                passesFilter = false
            case _: AnyFriendlyFilter =>
            case a: TypeFilter =>
              if (a.minionType != friendlyCard.asInstanceOf[MinionCard].minionType)
                passesFilter = false
            case _: SelfFilter =>
              if (self != friendlyCard)
                passesFilter = false
          }
        }
      }
      if (passesFilter)
        filteredCards += friendlyCard
    }
    val opponentCards = opponent.cardBoard.clone()
    opponentCards.append(opponent.hero)
    for (enemyCard <- opponentCards) {
      var passesFilter = true
      if (filters != null) {
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
      }
      if (passesFilter)
        filteredCards += enemyCard
    }
    filteredCards
  }
}
