import data.enum.{MinionType, CreatureEffectType, EffectType}
import data._

import scala.collection.mutable.ListBuffer
import scala.io.Source

object FileReader {

  def getCardsFrom(src: String): List[Card] = {
    val lines = getLinesFromFile(src)
    val eachCardRegex = "\\(\".+?\", \\d+?, ((MinionCard \\[.*?\\] \\d+? \\d+? (True|False) ((Murloc)|(Beast)|(Nothing)))|(SpellCard \\[.*?\\]))\\)".r
    var cards = new ListBuffer[Card]

    for (m <- eachCardRegex.findAllMatchIn(lines))
      cards += parseCard(m.toString())

    cards.toList
  }

  def parseCard(info: String): Card = {
    val tokens = info.replaceAll("\\(|\\)","").split(",")

    val cardName = tokens(0)
    val manaCost = Integer.parseInt(tokens(1).replace(" ", ""))
    val effectString = "\\[.*\\]".r.findFirstMatchIn(tokens(2)).getOrElse("None").toString
    val cardInfo = tokens(2).replace(effectString, "").split(" ")

    val cardEffect = parseCardEffect(effectString)
    var card = None : Option[Card]

    if (cardInfo(1).equals("MinionCard"))
      card = Some(new MinionCard(cardName, manaCost, cardEffect, Integer.parseInt(cardInfo(3)), Integer.parseInt(cardInfo(4)), cardInfo(5).toBoolean, matchMinionType(cardInfo(6))))
    else if (cardInfo(1).equals("SpellCard"))
      card = Some(new SpellCard(cardName, manaCost, cardEffect))

    card.orNull(null)
  }

  def parseCardEffect(effectString: String): Effect = {
    if (effectString.equals("[]"))
      return null // No effect

    val effectStrip = effectString.substring(1,effectString.length-1)
    val eventEffect = parseEventEffect(effectStrip.replace(effectStrip.split(" ")(0),""))

    new Effect(matchEffectType(effectStrip.split(" ")(0)), eventEffect)
  }

  def matchEffectType(effectType: String) = effectType match {
    case "OnPlay" => EffectType.OnPlay
    case "UntilDeath" => EffectType.UntilDeath
    case "OnDamage" => EffectType.OnDamage
    case "OnDeath" => EffectType.OnDeath
  }

  def parseEventEffect(effectString: String): EventEffect = {
    val effectStrip = effectString.substring(2,effectString.length-1)

    if (effectStrip.startsWith("DrawCard")) {
      return new DrawCardEventEffect()
    }
    val eventEffectType = effectStrip.split(" ")(0)
    val eventEffectFilterString = matchParentheses(effectStrip.replace(eventEffectType,""))
    var creatureEffectString = effectStrip.replace(eventEffectType,"").replace(eventEffectFilterString, "")
    creatureEffectString = creatureEffectString.substring(5,creatureEffectString.length - 1)

    if (eventEffectType.equals("All")) {
      return new AllEventEffect(parseEventEffectFilter(eventEffectFilterString), parseCreatureEffect(creatureEffectString))
    } else if (eventEffectType.equals("Choose")) {
      return new ChooseEventEffect(parseEventEffectFilter(eventEffectFilterString), parseCreatureEffect(creatureEffectString))
    } else if (eventEffectType.equals("Random")) {
      return new RandomEventEffect(parseEventEffectFilter(eventEffectFilterString), parseCreatureEffect(creatureEffectString))
    }
    null
  }

  def matchMinionType(minionTypeString: String) = minionTypeString match {
    case "Beast" => MinionType.Beast
    case "Murloc" => MinionType.Murloc
    case _ => null
  }

  def parseEventEffectFilter(filterString: String): Filter = {
    if (filterString.equals("")) {
      return null
    }
    val filterTypeString = filterString.split(" ")(0)

    if (filterTypeString.equals("AnyCreature")) {
      return AnyCreatureFilter()
    } else if (filterTypeString.equals("AnyHero")) {
      return AnyHeroFilter()
    } else if (filterTypeString.equals("AnyFriendly")) {
      return AnyFriendlyFilter()
    } else if (filterTypeString.equals("Self")) {
      return SelfFilter()
    } else if (filterTypeString.equals("Type")) {
      return TypeFilter(matchMinionType(filterString.split(" ")(1)))
    } else if (filterTypeString.equals("Not")) {
      val nextFilterString = filterString.replace(filterTypeString,"")
      return new NotFilter(parseEventEffectFilter(nextFilterString.substring(2,nextFilterString.length-1)))
    } else if (filterTypeString.equals("Any")) {
      val nextFilterString = filterString.replace(filterTypeString,"")
      return new AnyFilter(parseEventEffectFilter(nextFilterString.substring(2,nextFilterString.length-1)))
    }
    null
  }

  def parseCreatureEffect(creatureEffectString: String): CreatureEffect = {
    val tokens = creatureEffectString.split(" ")

    if (tokens(0).equals("Taunt")) {
      return new TauntEffect(tokens(1).toBoolean)
    }
    val effectType = matchCreatureEffectType(tokens(1))

    if (tokens(0).equals("Health"))
      return new HealthEffect(effectType, Integer.valueOf(tokens(2)))
    else if (tokens(0).equals("Attack"))
      return new AttackEffect(effectType, Integer.valueOf(tokens(2)))

    null
  }

  def matchCreatureEffectType(typeStr: String) = typeStr match {
    case "Relative" => CreatureEffectType.Relative
    case "Absolute" => CreatureEffectType.Absolute
  }

  def matchParentheses(str: String): String = {
    var closePos = 2
    var counter = 1

    while (counter > 0) {
      val c = str.charAt(closePos)

      if (c == '[')
        counter += 1
      else if (c == ']')
        counter -= 1

      closePos += 1
    }
    str.substring(2, closePos)
  }

  private def getLinesFromFile(src: String): String = {
    val source = Source.fromFile(src)
    val lines = source.mkString

    source.close()
    lines.replaceAll("\\r\\n|\\r|\\n","")
  }
}