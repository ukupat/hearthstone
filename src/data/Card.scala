package data

import data.enum.MinionType._

abstract class Card(val name: String, val cost: Int, val effect: Effect) {}

case class SpellCard(override val name: String, override val cost: Int, override val effect: Effect) extends Card(name, cost, effect) {

  override def toString: String = {
    name + "[cost " + cost + "; effect " + effect + "]"
  }
}

case class MinionCard(
                       override val name: String,
                       override val cost: Int,
                       override val effect: Effect,
                       health: Int,
                       attack: Int,
                       taunt: Boolean,
                       minionType: MinionType
                       ) extends Card(name, cost, effect) {

  var currentHealth: Int = health
  var currentAttack: Int = attack
  var currentTaunt: Boolean = taunt

  var used: Boolean = false

  override def toString: String = {
    name + "[cost " + cost + "; effect " + effect + "; type " +
      minionType + "; health (current/initial) " + currentHealth + "/" + health + "; attack " + currentAttack + "/" + attack +
      "; taunt " + currentTaunt + "/" + taunt + "]"
  }
}
