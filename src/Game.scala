import data.Player

class Game(val bluePlayer: Player, val redPlayer: Player) {

  def startPlaying(): Unit = {
    var roundCount: Int = 0

     while (roundCount != -1) {
       val opponent = getOpponent(roundCount)

       // REMOVE for testing
       opponent.heroHealth -= 1

       roundCount = newRound(opponent, roundCount)
     }
  }

  private def getOpponent(round: Int): Player = {
    if (round % 2 == 0)
      bluePlayer
    else
      redPlayer
  }

  private def newRound(opponent: Player, round: Int): Int = {
    if (opponent.heroHealth <= 0) {
      Logger.sayGameResultsAndEndIt()
      -1
    } else {
      round + 1
    }
  }
}
