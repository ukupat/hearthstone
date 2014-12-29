
class Game(val bluePlayer: Player, val redPlayer: Player) {

  var round: Int = 0

  def startPlaying(): Unit = {
     while (round != -1) {
       makePlayerMove()
       round = newRound()
     }
  }

  private def makePlayerMove(): Unit = {
    startNewRound()
    carryOutAttackerMoves()

    // REMOVE for testing
    opponent.heroHealth -= 1
  }

  private def startNewRound(): Unit = {
    attacker.takeCardAndMana()

    Logger.startNewRound(round, attacker.name)

    var showHand = false
    Logger.showPlayerInfo(opponent, showHand)

    showHand = true
    Logger.showPlayerInfo(attacker, showHand)
  }

  private def carryOutAttackerMoves(): Unit = {
    Logger.sayThat("# Your moves\n")

    movesFromHand()
  }

  private def movesFromHand(): Unit = {
    Logger.sayThat("## From hand\n")

    if (!Referee.hasAnythingToMove(attacker)) {
      Logger.sayThat("I: No cards to play from hand, skipping hand play!")
      return
    }
    val cardToMove = Logger.askWhichCardToUseFromHand(attacker)
    attacker.moveCardFromHandToBoard(cardToMove)
  }

  private def attacker: Player = {
    if (round % 2 == 0)
      redPlayer
    else
      bluePlayer
  }

  private def opponent: Player = {
    if (round % 2 == 0)
      bluePlayer
    else
      redPlayer
  }

  private def newRound(): Int = {
    if (Referee.isGameOver(redPlayer, bluePlayer)) {
      Logger.sayGameResultsAndEndIt()
      -1
    } else {
      round + 1
    }
  }
}
