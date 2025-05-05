package forge.gamemodes.match.input;

// Removed unused import: import forge.game.GameEntityView;
import forge.game.card.Card;
import forge.game.card.CardCollection;
// Removed unused import: import forge.game.card.CardView;
import forge.game.player.Player;
import forge.game.zone.ZoneType;
// Removed incorrect import: import forge.gui.toolbox.FOptionPane;
import forge.player.PlayerControllerHuman;
import forge.util.Localizer;
// Removed unused import: import forge.util.maps.GameEntityViewMap;

import java.util.Collection;

/**
 * Handles input for the Grinder mulligan discard step.
 * Prompts the player to select a specific number of cards from their hand to put on the bottom of their library.
 */
public class InputGrinderDiscard extends InputSelectCardsFromList {
    private static final long serialVersionUID = -8079911111315771741L;
    // Removed unused field: private final int cardsToDiscard;
    // Removed unused field: private final Player player;
    private final Localizer localizer = Localizer.getInstance();
    // Removed unused field: private final PlayerControllerHuman controller;

    public InputGrinderDiscard(PlayerControllerHuman controller, Player player, int cardsToDiscard) {
        // Min and Max are the same, forcing the exact number of discards
        super(controller, cardsToDiscard, cardsToDiscard, player.getZone(ZoneType.Hand).getCards(), null);
        // Removed storing controller, player, cardsToDiscard as they are not needed locally
        this.setCancelAllowed(false); // Player must discard
        // Use the min/max values passed to the super constructor for validation message
        this.setMessage(localizer.getMessage("lblGrinderDiscardPrompt", cardsToDiscard));
    }

    // Removed onOk override - Base class InputSelectManyBase handles enabling/disabling OK
    // based on min/max constraints via hasEnoughTargets() / hasAllTargets().
    // Since min == max here, OK will only be enabled when the exact number is selected.

    // onCancel is final in base class, cannot override. setCancelAllowed(false) handles this.

    public CardCollection getSelectedCards() {
        // getSelected() returns Collection<Card> from the base class
        Collection<Card> selected = getSelected();
        return selected == null ? new CardCollection() : new CardCollection(selected);
    }
}