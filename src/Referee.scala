object Referee {

  def hasAnythingToMove(player: Player): Boolean = {
    for (card <- player.cardHand) {
      if (card.cost <= player.mana)
        return true
    }
    false
  }

  def isGameOver(redPlayer: Player, bluePlayer: Player): Boolean = {
    redPlayer.heroHealth <= 0 || bluePlayer.heroHealth <= 0
  }

  def isCardFromHandValid(cardIndex: Int, player: Player): Boolean = {
    cardIndex >= player.cardHand.length || player.cardHand(cardIndex).cost > player.mana
  }
}
