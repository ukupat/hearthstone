object Logger {

  def sayHello() = {
    println("\nWelcome!")
    println("\nSheathe your sword, draw your deck, and get ready for Hearthstone - the fast \npaced strategy card game that's easy to learn and massively fun.")
  }

  // TODO mocked
  def askCardsFile(player: String): String = ""

  def sayGameResultsAndEndIt(): Unit = {
    println("\nGame is over!")
  }
}
