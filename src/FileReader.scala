import data.enum.{MinionType, CreatureEffectType, EffectType}
import data._

import scala.collection.mutable.ListBuffer
import scala.io.Source
import scala.util.Random

object FileReader {

  val cardsRegex = "\\(\".+?\", \\d+?, ((MinionCard \\[.*?\\] \\d+? \\d+? (True|False) ((Murloc)|(Beast)|(Nothing)))|(SpellCard \\[.*?\\]))\\)".r
  val effectsRegex = "\\w+\\s+\\[(DrawCard(\\,\\s*)*|\\w+\\s+\\[(\\w+(\\,\\s*)*)*\\]\\s*\\[(\\w+\\s+\\w+\\s+(\\d+|\\-\\d+)(\\,\\s*)*)+](\\,\\s*)*)+\\]".r
  val eventEffectsRegex = "DrawCard(\\,\\s*)*|\\w+\\s+\\[(\\w+(\\,\\s*)*)*\\]\\s*\\[(\\w+\\s+\\w+\\s+(\\d+|\\-\\d+)(\\,\\s*)*)+](\\,\\s*)*".r

  def getCardsFrom(src: String): List[Card] = {
    val lines = getLinesFromFile(src)
    var cards = new ListBuffer[Card]

    for (m <- cardsRegex.findAllMatchIn(lines))
      cards += parseCard(m.toString())

    Random.shuffle(cards.toList)
  }

  private def getLinesFromFile(src: String): String = {
    val source = Source.fromFile(src)
    val lines = source.mkString

    source.close()
    lines.replaceAll("\\r\\n|\\r|\\n","")
  }

  private def parseCard(info: String): Card = {
    val cardString = info.replaceAll("\\(|\\)", "")
    println(cardString)
    val tokens = cardString.split(",")
    val effectString = "\\[.*\\]".r.findFirstMatchIn(cardString).getOrElse("None").toString

    val cardName = tokens(0)
    val manaCost = Integer.parseInt(tokens(1).replace(" ", ""))
    val cardEffects = parseCardEffects(effectString)
    val cardInfo = cardString.replace(effectString, "").split(",")(2).split(" ")

    var card = None : Option[Card]

    if (cardInfo(1).equals("MinionCard"))
      card = Some(new MinionCard(cardName, manaCost, cardEffects, Integer.parseInt(cardInfo(3)), Integer.parseInt(cardInfo(4)), cardInfo(5).toBoolean, matchMinionType(cardInfo(6))))
    else if (cardInfo(1).equals("SpellCard"))
      card = Some(new SpellCard(cardName, manaCost, cardEffects))

    card.orNull(null)
  }

  private def parseCardEffects(effectsString: String): List[Effect] = {
    if (effectsString.equals("[]"))
      return null // No effect

    val effects = effectsString.substring(1, effectsString.length - 1)
    val ret: ListBuffer[Effect] = ListBuffer()

    for (m <- effectsRegex.findAllMatchIn(effects)) {
      val effectString = m.toString().trim()
      val effectType = matchEffectType(effectString.split(" ")(0))
      val eventEffects = parseEventEffects(effectString.replace(effectString.split(" ")(0), ""))

      ret += Effect(effectType, eventEffects)
    }
    ret.toList
  }

  private def matchEffectType(effectType: String) = effectType match {
    case "OnPlay" => EffectType.OnPlay
    case "UntilDeath" => EffectType.UntilDeath
    case "OnDamage" => EffectType.OnDamage
    case "OnDeath" => EffectType.OnDeath
  }

  private def parseEventEffects(effectString: String): List[EventEffect] = {
    val eventEffects = effectString.substring(2, effectString.length - 1)
    val ret: ListBuffer[EventEffect] = ListBuffer()

    for (m <- eventEffectsRegex.findAllMatchIn(eventEffects)) {
        ret += parseEventEffect(m.toString().trim())
    }
    ret.toList
  }

  private def parseEventEffect(eventEffectString: String): EventEffect = {
    if (eventEffectString.startsWith("DrawCard")) {
      return DrawCardEventEffect()
    }
    val eventEffectType = eventEffectString.split(" ")(0)
    val eventEffectFiltersString = matchParentheses(eventEffectString.replace(eventEffectType, ""))

    var creatureEffectsString = eventEffectString.replace(eventEffectType, "").replace(eventEffectFiltersString, "")
    creatureEffectsString = creatureEffectsString.substring(5, creatureEffectsString.length - 1).replace("]", "")

    val filters = parseEventEffectFilters(eventEffectFiltersString)
    val creatureEffects = parseCreatureEffects(creatureEffectsString)

    if (eventEffectType.equals("All")) {
      AllEventEffect(filters, creatureEffects)
    } else if (eventEffectType.equals("Choose")) {
      ChooseEventEffect(filters, creatureEffects)
    } else if (eventEffectType.equals("Random")) {
      RandomEventEffect(filters, creatureEffects)
    } else {
      null
    }
  }

  private def matchMinionType(minionTypeString: String) = minionTypeString match {
    case "Beast" => MinionType.Beast
    case "Murloc" => MinionType.Murloc
    case _ => null
  }

  private def parseEventEffectFilters(filtersString: String): List[Filter] = {
    if (filtersString.equals(""))
      return null

    val filters = filtersString.split(",")
    val ret: ListBuffer[Filter] = ListBuffer()

    for (filter <- filters) {
      ret += parseEventEffectFilter(filter)
    }
    ret.toList
  }

  private def parseEventEffectFilter(filterString: String): Filter = {
    val filterTypeString = filterString.trim().replace(",", "")

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

  private def parseCreatureEffects(creatureEffectsString: String): List[CreatureEffect] = {
    if (creatureEffectsString.equals(""))
      return null

    val effects = creatureEffectsString.split(",")
    val ret: ListBuffer[CreatureEffect] = ListBuffer()

    for (effect <- effects) {
      ret += parseCreatureEffect(effect)
    }
    ret.toList
  }

  private def parseCreatureEffect(creatureEffectString: String): CreatureEffect = {
    val tokens = creatureEffectString.trim().split(" ")

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

  private def matchCreatureEffectType(typeStr: String) = typeStr match {
    case "Relative" => CreatureEffectType.Relative
    case "Absolute" => CreatureEffectType.Absolute
  }

  private def matchParentheses(str: String): String = {
    var closePos = 1
    var counter = 1

    while (counter > 0) {
      closePos += 1
      val c = str.charAt(closePos)

      if (c == '[')
        counter += 1
      else if (c == ']')
        counter -= 1
    }
    str.substring(2, closePos)
  }
}