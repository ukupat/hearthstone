package hearthstone.data

import hearthstone.data.enum.EffectType.EffectType
import hearthstone.data.enum.MinionType._

abstract class Card(val name: String, val cost: Int, val effects: List[Effect]) {

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

case class SpellCard(override val name: String, override val cost: Int, override val effects: List[Effect]) extends Card(name, cost, effects) {

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
                       ) extends Card(name, cost, effects) {

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
