import data.enum.{MinionType, CreatureEffectType, EffectType}
import data._

object FileReader {

  // TODO currently mocked
  def getCardsFrom(src: String): List[Card] = {
    List(
      new MinionCard("Murloc Raider", 1, null, 2, 1, false, MinionType.Murloc),
      new MinionCard(
        "Raid Leader",
        3,
        Effect(
          EffectType.UntilDeath,
          AllEventEffect(
            AnyFriendlyFilter(), AttackEffect(CreatureEffectType.Absolute, 1)
          )
        ),
        2,
        2,
        false,
        null
      ),
      new SpellCard(
        "Fireball",
        4,
        Effect(
          EffectType.OnPlay,
          ChooseEventEffect(
            AnyCreatureFilter(), AttackEffect(CreatureEffectType.Relative, 6)
          )
        )
      )
    )
  }
}
