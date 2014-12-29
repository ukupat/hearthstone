
import data.Card
import scala.io.StdIn

object Logger {

  def sayHello() = {
    println("\nWelcome!")
    println("\nSheathe your sword, draw your deck, and get ready for Hearthstone - the fast \npaced strategy card game that's easy to learn and massively fun.")
  }

  def askCardsFile(player: String): String = {
      ""
//    TODO mocked
//    StdIn.readLine("Please enter the card file name for " + player + + ": ")
  }

  def startNewRound(round: Int, playerName: String): Unit = {
    println("\n\n\n\n\n\n###############################\n#          New Round          #\n###############################\n")
    println("\n" + round + ". round, " + playerName + " player turn\n")
  }

  def showPlayerInfo(player: Player, showHand: Boolean): Unit = {
    println(player + "\n")

    val isManaFilterOn = showHand

    println("## Cards on Board\n")
    println(player.getCardsListInString(player.cardBoard, false))

    if (showHand) {
      println("## Cards in Hand\n")
      println(player.getCardsListInString(player.cardHand, isManaFilterOn))
    }
  }

  // TODO make sure that the input is int
  def askWhichCardToUseFromHand(player: Player): Card = {
    println("Q: Which card do you want to use?")

    var cardIndex: Int = StdIn.readInt()

    while (Referee.isCardFromHandValid(cardIndex, player)) {
      print("-> Invalid input. Write the card index, " + player.name + ":\n")
      cardIndex = StdIn.readInt()
    }
    player.cardHand(cardIndex)
  }

  def sayThat(message: String): Unit = {
    println(message)
  }

  def sayGameResultsAndEndIt(): Unit = {
    println("\nGame is over!")
  }
}
