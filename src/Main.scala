import hearthstone.game.{Player, Game}
import hearthstone.util.{Logger, FileReader}

object Main {

  def main(args: Array[String]) {
    Logger.sayHello()

    val game = constructGame()
    game.play()
  }

  def constructGame(): Game = {
    val bluePlayerCards = FileReader.getCardsFrom(Logger.askCardsFile("Blue player"))
    val redPlayerCards = FileReader.getCardsFrom(Logger.askCardsFile("Red player"))

    val bluePlayer = new Player("Blue", bluePlayerCards)
    val redPlayer = new Player("Red", redPlayerCards)

    new Game(bluePlayer, redPlayer)
  }
}
