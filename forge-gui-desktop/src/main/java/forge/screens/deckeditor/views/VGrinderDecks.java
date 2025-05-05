package forge.screens.deckeditor.views;

import javax.swing.JPanel;

import forge.deck.io.DeckPreferences;
import forge.game.GameType;
import forge.gui.framework.DragCell;
import forge.gui.framework.DragTab;
import forge.gui.framework.EDocID;
import forge.gui.framework.IVDoc;
import forge.itemmanager.DeckManager;
import forge.itemmanager.ItemManagerContainer;
import forge.screens.deckeditor.controllers.CGrinderDecks; 
import forge.screens.match.controllers.CDetailPicture;
import forge.util.Localizer;
import net.miginfocom.swing.MigLayout;

/** 
 * Assembles Swing components of the Grinder deck viewer in the deck editor.
 *
 *
 * <br><br><i>(V at beginning of class name denotes a view class.)</i>
 */
public enum VGrinderDecks implements IVDoc<CGrinderDecks> {
    /** */
    SINGLETON_INSTANCE;
    // Fields used with interface IVDoc
    private DragCell parentCell;
    final Localizer localizer = Localizer.getInstance();
    // Use lblGrinderDecks for localization key, matching the plan
    private final DragTab tab = new DragTab(localizer.getMessage("lblGrinderDecks")); 

    private DeckManager lstDecks;

    //========== Overridden methods

    /* (non-Javadoc)
     * @see forge.gui.framework.IVDoc#getDocumentID()
     */
    @Override
    public EDocID getDocumentID() {
        return EDocID.EDITOR_GRINDER_DECKS; // Use EDITOR_GRINDER_DECKS
    }

    /* (non-Javadoc)
     * @see forge.gui.framework.IVDoc#getTabLabel()
     */
    @Override
    public DragTab getTabLabel() {
        return tab;
    }

    /* (non-Javadoc)
     * @see forge.gui.framework.IVDoc#getLayoutControl()
     */
    @Override
    public CGrinderDecks getLayoutControl() { // Use CGrinderDecks
        return CGrinderDecks.SINGLETON_INSTANCE; // Use CGrinderDecks
    }

    /* (non-Javadoc)
     * @see forge.gui.framework.IVDoc#setParentCell(forge.gui.framework.DragCell)
     */
    @Override
    public void setParentCell(DragCell cell0) {
        this.parentCell = cell0;
    }

    /* (non-Javadoc)
     * @see forge.gui.framework.IVDoc#getParentCell()
     */
    @Override
    public DragCell getParentCell() {
        return this.parentCell;
    }

    /* (non-Javadoc)
     * @see forge.gui.framework.IVDoc#populate()
     */
    @Override
    public void populate() {
        CGrinderDecks.SINGLETON_INSTANCE.refresh(); 
        String preferredDeck = DeckPreferences.getGrinderDeck(); // Method doesn't exist yet
        JPanel parentBody = parentCell.getBody();
        parentBody.setLayout(new MigLayout("insets 5, gap 0, wrap, hidemode 3"));
        parentBody.add(new ItemManagerContainer(lstDecks), "push, grow");
        VAllDecks.editPreferredDeck(lstDecks, preferredDeck); // Omit this line
    }

    //========== Retrieval methods
    /** @return {@link JPanel} */
    public DeckManager getLstDecks() {
        return lstDecks;
    }

    public void setCDetailPicture(final CDetailPicture cDetailPicture) {
        // Use GameType.GRINDER
        this.lstDecks = new DeckManager(GameType.GRINDER, cDetailPicture); 
        // Use lblGrinderDecks for localization key
        this.lstDecks.setCaption(localizer.getMessage("lblGrinderDecks")); 
    }
}