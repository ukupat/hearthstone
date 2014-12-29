object Logger {

  def sayHello() = {
    println("\nWelcome!")
    println("\nSheathe your sword, draw your deck, and get ready for Hearthstone - the fast \npaced strategy card game that's easy to learn and massively fun.")
  }

  def askCardsFile(player: String): String = {
    println("\nPlease enter the card file name for " + player + ".")
    scala.io.StdIn.readLine("Filename: ")
  }

  def sayGameResultsAndEndIt(): Unit = {
    println("\nGame is over!")
  }
}
