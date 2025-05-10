package forge.game.mulligan;

import forge.game.card.Card;
import forge.game.player.Player;

public class GrinderMulligan extends AbstractMulligan {
    public GrinderMulligan(Player p, boolean firstMullFree) {
        super(p, false);
        super.timesMulliganed = 2;
    }

    @Override
    public void keep() {
        super.kept = true;
        // player.onMulliganned();
    }

    @Override
    public boolean canMulligan() {
        return !kept && timesMulliganed < player.getMaxHandSize();
    }

    @Override
    public int handSizeAfterNextMulligan() {
        return player.getStartingHandSize();
    }

    @Override
    public void mulliganDraw() {
        player.drawCards(player.getStartingHandSize());
    }

    @Override
    public int tuckCardsAfterKeepHand() {
        if (timesMulliganed == 0) {
            return 0;
        }

        int extraCard = firstMulliganFree ? 1 : 0;
        return timesMulliganed - extraCard;
    }

    @Override
    public void afterMulligan() {

        int tuckingCards = tuckCardsAfterKeepHand();
        for (final Card c : player.getController().londonMulliganReturnCards(player, tuckingCards)) {
            player.getGame().getAction().moveToLibrary(c, -1, null);
        }

        super.afterMulligan();
    }
    
}
