package hearthstone.game

import hearthstone.data._
import hearthstone.data.enum.EffectType.EffectType
import hearthstone.data.enum.{CreatureEffectType, EffectType}
import hearthstone.util.Logger

import scala.collection.mutable.ListBuffer
import scala.util.Random

class Gandalf(attacker: Player, opponent: Player) {

  def playCardEffect(card: Card, effectType: EffectType): Unit = {
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

  private def playCardEventEffect(eventEffect: EventEffect, card: Card) = eventEffect match {
    case effect: DrawCardEventEffect => playDrawCardEventEffect()
    case effect: ChooseEventEffect => playChooseEventEffect(eventEffect.asInstanceOf[ChooseEventEffect], card)
    case effect: RandomEventEffect => playRandomEventEffect(eventEffect.asInstanceOf[RandomEventEffect], card)
    case effect: AllEventEffect => playAllEventEffect(eventEffect.asInstanceOf[AllEventEffect], card)
  }

  private def playDrawCardEventEffect(): Unit = {
    Logger.sayThat("\nI: Drew a card")
    attacker.takeCard()
  }

  private def playChooseEventEffect(eventEffect: ChooseEventEffect, card: Card): Unit = {
    val filteredCards = filterCardsWithFilters(eventEffect.filters, card)
    val target = Logger.askFromFilteredMobs(attacker, filteredCards)

    applyEffectsToTarget(eventEffect.creatureEffects, filteredCards(Integer.valueOf(target)).asInstanceOf[MinionCard])
  }

  private def playRandomEventEffect(eventEffect: RandomEventEffect, card: Card): Unit = {
    val filteredCards = filterCardsWithFilters(eventEffect.filters, card)
    val randomTarget = Random.shuffle(filteredCards).head

    applyEffectsToTarget(eventEffect.creatureEffects, randomTarget.asInstanceOf[MinionCard])
  }

  private def playAllEventEffect(eventEffect: AllEventEffect, card: Card): Unit = {
    for (card <- filterCardsWithFilters(eventEffect.filters, card)) {
      applyEffectsToTarget(eventEffect.creatureEffects, card.asInstanceOf[MinionCard])
    }
  }

  private def applyEffectsToTarget(creatureEffects: List[CreatureEffect], targetCard: MinionCard) : Unit = {
    Logger.sayThat("I: Applying effect to " + targetCard.name)

    for (creatureEffect <- creatureEffects) {
      creatureEffect match {
        case effect: HealthEffect => playHealthEffect(effect.asInstanceOf[HealthEffect], targetCard)
        case effect: AttackEffect => playAttackEffect(effect.asInstanceOf[AttackEffect], targetCard)
        case effect: TauntEffect =>  playTauntEffect(effect.asInstanceOf[TauntEffect], targetCard)
      }
    }
  }

  private def playHealthEffect(effect: HealthEffect, card: MinionCard): Unit = {
    val wasKilled = card.owner.changeMinionHealth(card, effect.health, effect.effectType == CreatureEffectType.Relative)

    this.playCardEffect(card, EffectType.OnDamage)

    if (wasKilled) {
      this.playCardEffect(card, EffectType.OnDeath)
      this.playCardEffect(card, EffectType.UntilDeath)
    }
  }

  private def playAttackEffect(effect: AttackEffect, card: MinionCard): Unit = {
    card.owner.changeMinionAttack(card, effect.attack, effect.effectType == CreatureEffectType.Relative)
  }

  private def playTauntEffect(effect: TauntEffect, card: MinionCard): Unit = {
    card.currentTaunt = effect.taunt
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
}
