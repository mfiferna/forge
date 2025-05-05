package forge.game.mulligan;

import forge.game.card.Card;
import forge.game.card.CardCollection;
import forge.game.player.Player;
import forge.game.zone.ZoneType;

public class GrinderMulligan extends AbstractMulligan {

    public GrinderMulligan(Player p, boolean firstMullFree) {
        // Grinder doesn't have a free mulligan concept, but keep constructor signature consistent
        super(p, false); // Pass false for firstMullFree
    }

    @Override
    public boolean canMulligan() {
        // Can always mulligan if hand hasn't been kept
        return !kept;
    }

    @Override
    public int handSizeAfterNextMulligan() {
        // Always draw 7 cards
        return 7;
    }

    @Override
    public void mulliganDraw() {
        // Standard mulligan draw: shuffle library, draw 7
        player.getZone(ZoneType.Library).shuffle(); // Shuffle the library directly
        player.drawCards(handSizeAfterNextMulligan());
    }

    @Override
    public void afterMulligan() {
        // This is called after the player decides to keep the hand.
        // Implement the discard logic here.
        if (!kept) {
            return; // Only proceed if the hand was kept
        }

        int currentHandSize = player.getZone(ZoneType.Hand).size();
        int cardsToDiscard = currentHandSize - 5; // Target hand size is 5

        if (cardsToDiscard > 0) {
            // Call the controller to prompt the user to select cards to discard
            CardCollection discardedCards = player.getController().grinderMulliganDiscardCards(player, cardsToDiscard);

            // Move the selected cards to the bottom of the library
            for (final Card c : discardedCards) {
                // Use -1 for index to indicate bottom of library
                player.getGame().getAction().moveToLibrary(c, -1, null);
            }
        }
        // No Scry or other post-mulligan actions needed for Grinder
    }

    @Override
    public int tuckCardsAfterKeepHand() {
        // This method is used by London/Vancouver to determine cards to tuck *before* drawing.
        // Grinder discards *after* keeping, so this isn't directly applicable in the same way.
        // Return 0 as no cards are tucked back before drawing based on mulligan count.
        return 0;
    }
}