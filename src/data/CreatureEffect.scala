package data

import data.enum.CreatureEffectType._

abstract class CreatureEffect {}

case class HealthEffect(effectType: CreatureEffectType, health: Int) extends CreatureEffect {}

case class AttackEffect(effectType: CreatureEffectType, attack: Int) extends CreatureEffect {}

case class TauntEffect(taunt: Boolean) extends CreatureEffect {}
