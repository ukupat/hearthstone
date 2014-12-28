package data

abstract class EventEffect() {}

case class AllEventEffect(filter: Filter, creatureEffect: CreatureEffect) extends EventEffect {}

case class ChooseEventEffect(filter: Filter, creatureEffect: CreatureEffect) extends EventEffect {}

case class RandomEventEffect(filter: Filter, creatureEffect: CreatureEffect) extends EventEffect {}

case class DrawCardEventEffect() extends EventEffect {}
