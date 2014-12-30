package data

import data.enum.MinionType._

abstract class Card(val name: String, val cost: Int, val effects: List[Effect]) {}

case class SpellCard(override val name: String, override val cost: Int, override val effects: List[Effect]) extends Card(name, cost, effects) {

  override def toString: String = {
    name + "[cost " + cost + "; effects " + effects + "]"
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
    name + "[cost " + cost + "; effects " + effects + "; type " +
      minionType + "; health (current/initial) " + currentHealth + "/" + health + "; attack " + currentAttack + "/" + attack +
      "; taunt " + currentTaunt + "/" + taunt + "]"
  }
}
