package data

abstract class EventEffect() {}

case class AllEventEffect(filters: List[Filter], creatureEffects: List[CreatureEffect]) extends EventEffect {}

case class ChooseEventEffect(filters: List[Filter], creatureEffects: List[CreatureEffect]) extends EventEffect {}

case class RandomEventEffect(filters: List[Filter], creatureEffects: List[CreatureEffect]) extends EventEffect {}

case class DrawCardEventEffect() extends EventEffect {}
