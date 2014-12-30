
object Main {

  def main(args: Array[String]) {
    Logger.sayHello()

    val game = constructGame()
    game.startPlaying()
  }

  def constructGame(): Game = {
    // TODO mocked
    //val bluePlayerCards = FileReader.getCardsFrom(Logger.askCardsFile("Blue player"))
    //val redPlayerCards = FileReader.getCardsFrom(Logger.askCardsFile("Red player"))

    val bluePlayerCards = FileReader.getCardsFrom("test/deck1")
    val redPlayerCards = FileReader.getCardsFrom("test/deck2")

    val bluePlayer = new Player("Blue", bluePlayerCards)
    val redPlayer = new Player("Red", redPlayerCards)

    new Game(bluePlayer, redPlayer)
  }
}
