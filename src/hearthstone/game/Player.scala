package hearthstone.game

import hearthstone.data.{Card, HeroCard, PlayCard, MinionCard}
import hearthstone.util.Referee

import scala.collection.mutable.ListBuffer

class Player(val name: String, var hero: HeroCard, var cards: List[Card]) {
  var mana: Int = 0
  var cardDeck: ListBuffer[Card] = constructCardsDeck(cards)
  var cardHand: ListBuffer[Card] = ListBuffer()
  var cardBoard: ListBuffer[Card] = ListBuffer()

  override def toString: String = {
    "# Player " + name + "\n\n" +
    "- Hero health: " + hero.health +
    "\n- Mana amount: " + mana +
    "\n- Cards in deck: " + cardDeck.length +
    "\n- Cards in hand: " + cardHand.length +
    "\n- Cards on board: " + cardBoard.length
  }

  def getCardsListInString(cards: ListBuffer[Card], manaFilter: Boolean): String = {
    if (cards.length == 0)
      return "Not a single card\n"

    var ret = ""
    var i = 0

    for (card <- cards) {
      if (manaFilter && mana < card.asInstanceOf[PlayCard].cost)
        ret += "*. " + card + "\n\n"
      else
        ret += i + ". " + card + "\n\n"

      i += 1
    }
    ret
  }

  def getCardBoardListInString(usedCardsFilter: Boolean, tauntFilter: Boolean): String = {
    if (cardBoard.length == 0)
      return "Not a single card\n"

    var ret = ""
    var i = 0
    var showOnlyTaunts = false

    if (tauntFilter)
      showOnlyTaunts = Referee.hasTauntsOnBoard(this)

    for (card <- cardBoard) {
      if (usedCardsFilter && card.asInstanceOf[MinionCard].used || showOnlyTaunts && !card.asInstanceOf[MinionCard].currentTaunt)
        ret += "*. " + card + "\n\n"
      else
        ret += i + ". " + card + "\n\n"

      i += 1
    }
    ret
  }

  def takeStartingCards(): Unit = {
    takeCard()
    takeCard()
    takeCard()
  }

  def takeCardAndMana(): Unit = {
    mana += 1
    takeCard()
  }

  def moveCardFromHandToBoard(card: PlayCard): Unit = {
    cardHand -= card
    cardBoard += card
    mana -= card.cost
  }

  def takeCard(): Unit = {
    if (cardDeck.length == 0) {
      hero.health -= 10
    } else {
      cardHand += cardDeck.last
      cardDeck = cardDeck.init
    }
  }

  def changeMinionHealth(minion: MinionCard, amount: Int, isRelative: Boolean): Boolean = {
    if (isRelative)
      minion.currentHealth += amount
    else
      minion.currentHealth = amount

    if (minion.currentHealth <= 0) {
      cardBoard -= minion
      true
    } else {
      false
    }
  }

  def changeMinionAttack(minion: MinionCard, amount: Int, isRelative: Boolean): Unit = {
    if (isRelative)
      minion.currentAttack += amount
    else
      minion.currentAttack = amount
  }

  private def constructCardsDeck(cards: List[Card]): ListBuffer[Card] = {
    for (card <- cards)
      card.asInstanceOf[PlayCard].owner = this

    cards.to[ListBuffer]
  }
}
