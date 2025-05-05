package forge.game.event;

import forge.game.mulligan.AbstractMulligan;
import forge.game.player.Player;

/** 
 * TODO: Write javadoc for this type.
 *
 */
public class GameEventMulligan extends GameEvent {

    public final Player player;
    public final AbstractMulligan mulligan;
    public GameEventMulligan(Player p, AbstractMulligan m) {
        player = p;
        mulligan = m;
    }

    @Override
    public <T> T visit(IGameEventVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
