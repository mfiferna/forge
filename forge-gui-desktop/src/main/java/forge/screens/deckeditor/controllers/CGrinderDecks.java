package forge.screens.deckeditor.controllers;

import forge.deck.DeckProxy;
import forge.gui.framework.ICDoc;
import forge.screens.deckeditor.views.VGrinderDecks; // Reference the Grinder view

/**
 * Controls the "Grinder Decks" panel in the deck editor UI.
 *
 * <br><br><i>(C at beginning of class name denotes a control class.)</i>
 *
 */
public enum CGrinderDecks implements ICDoc {
    SINGLETON_INSTANCE;

    private final VGrinderDecks view = VGrinderDecks.SINGLETON_INSTANCE; // Reference the Grinder view

    //========== Overridden methods

    @Override
    public void register() {
        // Typically empty for these simple list controllers
    }

    /* (non-Javadoc)
     * @see forge.gui.framework.ICDoc#initialize()
     */
    @Override
    public void initialize() {
        refresh();
    }

    public void refresh() {
        // Assume a method exists to get all Grinder decks, similar to Commander
        // This might require adding DeckProxy.getAllGrinderDecks() later
        CAllDecks.refreshDeckManager(view.getLstDecks(), DeckProxy.getAllGrinderDecks());
    }

    /* (non-Javadoc)
     * @see forge.gui.framework.ICDoc#update()
     */
    @Override
    public void update() {
        // Use the same update logic as CAllDecks/CCommanderDecks
        CAllDecks.updateDeckManager(view.getLstDecks());
    }
}