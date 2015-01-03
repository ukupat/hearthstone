package hearthstone.data

abstract class EventEffect() {}

abstract class ComplicatedEventEffect(val filters: List[Filter], val creatureEffects: List[CreatureEffect]) extends EventEffect {
  override def toString: String = {
    "\n       - filters " + toStringList(filters) + "       - creature effects " + toStringList(creatureEffects)
  }

  private def toStringList(list: List[Object]): String = {
    if (list == null)
      return "\n         * Nothing\n"

    var ret = "\n"
    for (el <- list)
      ret += "         * " + el + "\n"

    ret
  }
}

case class AllEventEffect(override val filters: List[Filter], override val creatureEffects: List[CreatureEffect]) extends ComplicatedEventEffect(filters, creatureEffects) {

  override def toString: String = {
    "All" + super.toString
  }
}

case class ChooseEventEffect(override val filters: List[Filter], override val creatureEffects: List[CreatureEffect]) extends ComplicatedEventEffect(filters, creatureEffects) {

  override def toString: String = {
    "Choose" + super.toString
  }
}

case class RandomEventEffect(override val filters: List[Filter], override val creatureEffects: List[CreatureEffect]) extends ComplicatedEventEffect(filters, creatureEffects) {

  override def toString: String = {
    "Random" + super.toString
  }
}

case class DrawCardEventEffect() extends EventEffect {

  override def toString: String = {
    "Draw card"
  }
}
