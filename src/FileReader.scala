import data.enum.{MinionType, CreatureEffectType, EffectType}
import data._

import scala.collection.mutable.ListBuffer

object FileReader {

  def getCardsFrom(src: String): List[Card] = {
    /*var mockFile = "[(\"Boulderfist Ogre\"\n, 6\n, MinionCard [] 6 7 False Nothing),\n\n(\"Elven Archer\"\n, 4\n, MinionCard [UntilDeath [Choose [] [Health Relative (-1)]]] 4 5 False Nothing),\n\n(\"Gnomish Inventor\"\n, 4\n, MinionCard [OnPlay [DrawCard]] 2 4 False Nothing), (\"Spell\", 4, SpellCard [])]"
    mockFile = mockFile.replaceAll("\\r\\n|\\r|\\n","");
    val eachCardRegex = "\\(\".+?\", \\d+?, ((MinionCard \\[.*?\\] \\d+? \\d+? (True|False) ((Just \\w+?)|(Nothing)))|(SpellCard \\[.*?\\]))\\)".r

    var cards = new ListBuffer[Card]
    for(m <- eachCardRegex.findAllMatchIn(mockFile)) {
      cards += parseCard(m.toString())
    }*/

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
      ),
      new MinionCard("Murloc Raider", 1, null, 2, 1, false, MinionType.Murloc)
    )
  }

  def parseCard(info: String): Card = {
    val tokens = info.replaceAll("\\(|\\)","").split(",")

    val cardName = tokens(0)
    val manaCost = Integer.parseInt(tokens(1).replace(" ", ""))
    val effectString = ("\\[.*\\]".r).findFirstMatchIn(tokens(2)).getOrElse("None").toString
    var cardInfo = tokens(2).replace(effectString, "").split(" ")

    val cardEffect = parseCardEffect(effectString)
    var card = None : Option[Card]
    if (cardInfo(1).equals("MinionCard")) {
      card = Some(new MinionCard(cardName, manaCost, cardEffect, Integer.parseInt(cardInfo(3)), Integer.parseInt(cardInfo(4)), cardInfo(5).toBoolean, MinionType.Beast))
    } else if (cardInfo(1).equals("SpellCard")) {
      card = Some(new SpellCard(cardName, manaCost, cardEffect))
    }
    card.getOrElse(null)
  }

  def parseCardEffect(effectString: String): Effect = {
    if (effectString.equals("[]")) {
      return null // No effect
    }
    var effectStrip = effectString.substring(1,effectString.length-1)
    val eventEffect = parseEventEffect(effectStrip.replace(effectStrip.split(" ")(0),""))

    var effect = new Effect(matchEffectType(effectStrip.split(" ")(0)), eventEffect)
    effect
  }

  def matchEffectType(effectType: String) = effectType match {
    case "OnPlay" => EffectType.OnPlay
    case "UntilDeath" => EffectType.UntilDeath
    case "OnDamage" => EffectType.OnDamage
    case "OnDeath" => EffectType.OnDeath
  }

  def parseEventEffect(effectString: String): EventEffect = {
    val effectStrip = effectString.substring(2,effectString.length-1)
    // TODO
    null
  }
}

