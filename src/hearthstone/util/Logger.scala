package hearthstone.util


import hearthstone.data.Card
import hearthstone.game.Player

import scala.collection.mutable.ListBuffer
import scala.io.StdIn

object Logger {

  def sayHello() = {
    println("\nWelcome!")
    println("\nSheathe your sword, draw your deck, and get ready for Hearthstone - the fast \npaced strategy card game that's easy to learn and massively fun.")
  }

  def askCardsFile(player: String): String = {
    StdIn.readLine("\nPlease enter the card file name for " + player + ": ")
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

  def askWhichCardToUseFromHand(player: Player): String = {
    println("\n### Your card hand\n\n" + player.getCardsListInString(player.cardHand, true))
    println("Q: Which spell card do you want to use or Which minion you want to put on board? (* for no card)")

    var answer: String = StdIn.readLine().trim

    while (answer != "*" && !Referee.isCardFromHandValid(answer, player)) {
      print("-> Invalid input. Write the card index or *, " + player.name + ":\n")
      answer = StdIn.readLine().trim
    }
    answer
  }

  def askToChooseMinionFromBoard(player: Player): String = {
    println("\n### Your card board\n\n" + player.getCardBoardListInString(true, false))
    println("Q: Which card you want to choose from Board to Attack? (* for no card)")

    var answer: String = StdIn.readLine().trim

    while (answer != "*" && !Referee.isCardFromBoardValid(answer, player)) {
      print("-> Invalid input. Write the card index or *, " + player.name + ":\n")
      answer = StdIn.readLine().trim
    }
    answer
  }

  def askFromFilteredMobs(player: Player, availableCards: ListBuffer[Card]): String = {
    println("\nQ: Which card do you want to use effect on?")
    println(player.getCardsListInString(availableCards, false))

    var answer: String = StdIn.readLine().trim

    while (!Referee.fitsIntoCardList(answer, availableCards)) {
      print("-> Invalid input. Write the card index:\n")
      answer = StdIn.readLine().trim
    }
    answer
  }

  def askTarget(player: Player): String = {
    println("\n### Opponent card board\n\n" + player.getCardBoardListInString(false, true))
    println("Q: What do you want to attack? (h for hero)")

    var answer: String = StdIn.readLine().trim

    while (answer != "h" && !Referee.isAttackedCardValid(answer, player)) {
      print("-> Invalid input. Write the card index or h (hero):\n")
      answer = StdIn.readLine().trim
    }
    answer
  }

  def sayThat(message: String): Unit = {
    println(message)
  }

  def sayGameResultsAndEndIt(): Unit = {
    println("\nGame is over!")
  }
}
