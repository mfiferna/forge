package forge.game.mulligan;

import java.util.List;

import com.google.common.collect.Lists;

import forge.MulliganDefs;
import forge.StaticData;
import forge.game.Game;
import forge.game.GameType;
import forge.game.player.Player;

public class MulliganService {
    Player firstPlayer;
    Game game;
    List<AbstractMulligan> mulligans = Lists.newArrayList();

    public MulliganService(Player player) {
        firstPlayer = player;
        game = firstPlayer.getGame();
    }

    public void perform() {
        initializeMulligans();
        runPlayerMulligans();
    }

    private void initializeMulligans() {
        List<Player> whoCanMulligan = Lists.newArrayList(game.getPlayers());
        int offset = whoCanMulligan.indexOf(firstPlayer);

        // Have to cycle-shift the list to get the first player on index 0
        for (int i = 0; i < offset; i++) {
            whoCanMulligan.add(whoCanMulligan.remove(0));
        }

        boolean firstMullFree = game.getPlayers().size() > 2 || game.getRules().hasAppliedVariant(GameType.Brawl);

        for (Player player : whoCanMulligan) {
            if (game.getRules().isGrinder()) {
                mulligans.add(new GrinderMulligan(player, firstMullFree));
            } else {
                MulliganDefs.MulliganRule rule = StaticData.instance().getMulliganRule();
                switch (rule) {
                    case Original:
                        mulligans.add(new OriginalMulligan(player, firstMullFree));
                        break;
                    case Paris:
                        mulligans.add(new ParisMulligan(player, firstMullFree));
                        break;
                    case Vancouver:
                        mulligans.add(new VancouverMulligan(player, firstMullFree));
                        break;
                    case London:
                        mulligans.add(new LondonMulligan(player, firstMullFree));
                        break;
                    default:
                        // Default to Vancouver mulligan for now. Should ideally never get here.
                        mulligans.add(new VancouverMulligan(player, firstMullFree));
                        break;
                }
            }
        }
    }

    private void runPlayerMulligans() {
        boolean allKept;
        do {
            allKept = true;
            for (AbstractMulligan mulligan : mulligans) {
                if (mulligan.hasKept()) {
                    continue;
                }
                Player p = mulligan.getPlayer();
                boolean keep = !mulligan.canMulligan() || p.getController().mulliganKeepHand(firstPlayer, mulligan.tuckCardsAfterKeepHand());

                if (game.isGameOver()) { // conceded on mulligan prompt
                    return;
                }

                if (keep) {
                    mulligan.keep();
                    continue;
                }

                allKept = false;

                mulligan.mulligan();
            }
        } while (!allKept);

        for (AbstractMulligan mulligan : mulligans) {
            mulligan.afterMulligan();
        }
    }

    private class GrinderMulligan extends AbstractMulligan {
        public GrinderMulligan(Player p, boolean firstMullFree) {
            super(p, firstMullFree);
        }

        @Override
        public boolean canMulligan() {
            return !kept && timesMulliganed < player.getMaxHandSize();
        }

        @Override
        public int handSizeAfterNextMulligan() {
            return player.getMaxHandSize();
        }

        @Override
        public void mulligan() {
            CardCollection toMulligan = new CardCollection(player.getCardsIn(ZoneType.Hand));
            if (toMulligan.isEmpty()) return;
            revealPreMulligan(toMulligan);
            for (final Card c : toMulligan) {
                player.getGame().getAction().moveToLibrary(c, null);
            }
            try {
                Thread.sleep(100); //delay for a tiny bit to give UI a chance catch up
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            player.shuffle(null);
            timesMulliganed++;
            mulliganDraw();
            player.onMulliganned();
        }

        @Override
        public void mulliganDraw() {
            player.drawCards(handSizeAfterNextMulligan());
            int tuckingCards = tuckCardsAfterKeepHand();

            for (final Card c : player.getController().londonMulliganReturnCards(player, tuckingCards)) {
                player.getGame().getAction().moveToLibrary(c, -1, null);
            }
        }

        @Override
        public int tuckCardsAfterKeepHand() {
            return 2 + timesMulliganed;
        }
    }
}
