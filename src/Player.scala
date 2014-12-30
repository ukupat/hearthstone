import data.{MinionCard, Card}

import scala.collection.mutable.ListBuffer

class Player(val name: String, var cardDeck: List[Card]) {
  var heroHealth: Int = 30
  var mana: Int = 0
  var cardHand: ListBuffer[Card] = ListBuffer()
  var cardBoard: ListBuffer[Card] = ListBuffer()
  var usedCardsFromBoard: ListBuffer[Card] = ListBuffer()

  override def toString: String = {
    "# Player " + name + "\n\n" +
    "- Hero health: " + heroHealth +
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
      if (manaFilter && mana < card.cost)
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
      if (usedCardsFilter && usedCardsFromBoard.contains(card) || showOnlyTaunts && !card.asInstanceOf[MinionCard].currentTaunt)
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

  def moveCardFromHandToBoard(card: Card): Unit = {
    cardHand -= card
    cardBoard += card
    mana -= card.cost
  }

  // TODO randomize the card pick
  def takeCard(): Unit = {
    if (cardDeck.length == 0) {
      heroHealth -= 10
    } else {
      cardHand += cardDeck.last
      cardDeck = cardDeck.init
    }
  }
}
