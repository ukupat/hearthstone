package hearthstone.data

import hearthstone.data.enum.EffectType.EffectType
import hearthstone.data.enum.MinionType._
import hearthstone.game.Player

abstract class Card(val name: String) {}

abstract class PlayCard(override val name: String, val cost: Int, val effects: List[Effect]) extends Card(name) {

  var owner: Player = null

  def getEffectByType(effectType: EffectType): Effect = {
    if (effects == null)
      return null

    for (cardEffect <- effects)
      if (cardEffect.effect == effectType)
        return cardEffect

    null
  }

  protected def toStringEffects: String = {
    if (effects == null)
      return "Nothing"

    var ret = "\n"
    for (effect <- effects)
      ret += "   - " + effect.toString + "\n"

    ret
  }
}

case class HeroCard(override val name: String, var health: Int) extends Card(name) {
  override def toString: String = {
    name + ": health " + health
  }
}

case class SpellCard(override val name: String, override val cost: Int, override val effects: List[Effect]) extends PlayCard(name, cost, effects) {

  override def toString: String = {
    name + ": cost " + cost + "; effects " + toStringEffects
  }
}

case class MinionCard(
                       override val name: String,
                       override val cost: Int,
                       override val effects: List[Effect],
                       health: Int,
                       attack: Int,
                       taunt: Boolean,
                       minionType: MinionType
                       ) extends PlayCard(name, cost, effects) {

  var currentHealth: Int = health
  var currentAttack: Int = attack
  var currentTaunt: Boolean = taunt
  var used: Boolean = false

  override def toString: String = {
    name + ": cost " + cost + "; type " +
      minionType + "; health (current/initial) " + currentHealth + "/" + health + "; attack " + currentAttack + "/" + attack +
      "; taunt " + currentTaunt + "/" + taunt + "; effects " + toStringEffects
  }
}
