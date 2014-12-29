object Referee {

  def hasAnythingToMove(player: Player): Boolean = {
    for (card <- player.cardHand) {
      if (card.cost <= player.mana)
        return true
    }
    false
  }

  def hasMinionsOnBoard(player: Player): Boolean = {
    player.cardBoard.length != 0
  }

  def isCardFromHandValid(cardIndex: String, player: Player): Boolean = {
    try {
      cardIndex.toInt >= 0 && cardIndex.toInt < player.cardHand.length && player.cardHand(cardIndex.toInt).cost <= player.mana
    } catch {
      case e: Exception => false
    }
  }

  def isCardFromBoardValid(cardIndex: String, player: Player): Boolean = {
    try {
      cardIndex.toInt >= 0 && cardIndex.toInt < player.cardBoard.length
    } catch {
      case e: Exception => false
    }
  }

  def isAttackedCardValid(cardIndex: String, player: Player): Boolean = {
    isCardFromBoardValid(cardIndex, player)
  }

  def isGameOver(redPlayer: Player, bluePlayer: Player): Boolean = {
    redPlayer.heroHealth <= 0 || bluePlayer.heroHealth <= 0
  }
}
