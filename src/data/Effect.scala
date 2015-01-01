package data

import data.enum.EffectType._

case class Effect(effect: EffectType, eventEffects: List[EventEffect]) {

  override def toString: String = {
    effect + ": " + toStringEventEffects
  }

  private def toStringEventEffects: String = {
    var ret = "\n"
    for (effect <- eventEffects)
      ret += "     * " + effect + "\n"

    ret
  }
}
