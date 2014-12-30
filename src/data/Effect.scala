package data

import data.enum.EffectType._

case class Effect(effect: EffectType, eventEffects: List[EventEffect]) {}
