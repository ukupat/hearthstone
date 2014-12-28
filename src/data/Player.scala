package data

class Player(val name: String, var cardDeck: List[Card]) {
  var heroHealth: Int = 30
  var mana: Int = 0
}
