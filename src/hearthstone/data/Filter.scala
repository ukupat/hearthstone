package hearthstone.data

import hearthstone.data.enum.MinionType._

abstract class Filter {}

case class AnyCreatureFilter() extends Filter {}

case class AnyHeroFilter() extends Filter {}

case class AnyFriendlyFilter() extends Filter {}

case class TypeFilter(minionType: MinionType) extends Filter {}

case class SelfFilter() extends Filter {}

case class NotFilter(filter: Filter) extends Filter {}

case class AnyFilter(filter: Filter) extends Filter {}
