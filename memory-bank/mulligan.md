# Documentation: Mulligan Process in Forge

This document details how the mulligan process is implemented in the Forge engine, covering both the core game logic and the desktop UI interaction.

## Core Implementation (`forge-game` module)

*   **`forge.game.mulligan.MulliganService`:** This class orchestrates the mulligan phase.
    *   **Initialization (`initializeMulligans`):**
        *   Determines the player order, starting with the `firstPlayer`.
        *   Reads the globally configured mulligan rule (e.g., Paris, Vancouver, London) from `forge.StaticData.instance().getMulliganRule()`.
        *   Checks if the first mulligan is free (`game.getPlayers().size() > 2 || game.getRules().hasAppliedVariant(GameType.Brawl)`).
        *   Instantiates the appropriate `AbstractMulligan` subclass (e.g., `ParisMulligan`, `VancouverMulligan`, `LondonMulligan`) for each player based on the selected rule.
    *   **Execution (`runPlayerMulligans`):**
        *   Loops through each player's `AbstractMulligan` instance.
        *   If the player hasn't kept their hand yet and can still mulligan (`mulligan.canMulligan()`), it calls the player's controller: `p.getController().mulliganKeepHand(firstPlayer, mulligan.tuckCardsAfterKeepHand())`. This is the call that triggers the UI prompt for human players.
        *   Based on the controller's return value (true for keep, false for mulligan):
            *   If keep: Calls `mulligan.keep()`.
            *   If mulligan: Calls `mulligan.mulligan()`, which executes the specific logic for that rule (drawing a new hand, potentially reducing hand size).
        *   The loop continues until all players have kept their hands (`allKept` is true).
    *   **Post-Mulligan Actions (`afterMulligan`):**
        *   After all players have kept, it calls `mulligan.afterMulligan()` for each player. This handles rule-specific actions like the Scry for Vancouver/London mulligans.

*   **`forge.game.mulligan.AbstractMulligan` and Subclasses:** (e.g., `ParisMulligan`, `VancouverMulligan`, `LondonMulligan`)
    *   These classes contain the specific logic for each mulligan rule, such as how many cards to draw on subsequent mulligans and whether/how cards are put back (e.g., bottom of library for London).
    *   The `mulligan()` method implements the core redraw logic.
    *   The `afterMulligan()` method implements post-keep actions (like Scry).

## UI Implementation (`forge-gui` module)

*   **`forge.gui.player.PlayerControllerHuman`:** This class acts as the controller for human players.
    *   **`mulliganKeepHand(Player mulliganingPlayer, int cardsToReturn)` method:**
        *   This method overrides the abstract method from the base `PlayerController`.
        *   It's called by `MulliganService` when it's the human player's turn to decide.
        *   It creates an `InputConfirmMulligan` object.
        *   `InputConfirmMulligan.showAndWait()` displays the mulligan dialog (showing the current hand, number of cards, and Keep/Mulligan buttons) and waits for user input.
        *   Returns `true` if the user chose "Keep", `false` if they chose "Mulligan".
    *   **`londonMulliganReturnCards(Player mulliganingPlayer, int cardsToReturn)` method:**
        *   Called specifically for the London mulligan *after* the player keeps.
        *   Creates an `InputLondonMulligan` object.
        *   `InputLondonMulligan.showAndWait()` displays a dialog allowing the user to select the specified number of cards from their hand to put on the bottom of the library.
        *   Returns the `CardCollection` of cards selected by the user.

*   **Input Classes (`InputConfirmMulligan`, `InputLondonMulligan`):** These classes (likely within `forge.gamemodes.match.input` or similar) are responsible for the actual GUI dialogs presented to the user during the mulligan process.

## Summary Flow

1.  `GameAction.startGame` initiates the mulligan phase.
2.  `MulliganService` determines the rule and player order.
3.  `MulliganService` loops, calling `PlayerControllerHuman.mulliganKeepHand` for the current human player.
4.  `PlayerControllerHuman` uses `InputConfirmMulligan` to show the dialog and get the Keep/Mulligan decision.
5.  `MulliganService` receives the decision. If "Mulligan", the corresponding `AbstractMulligan` subclass handles the redraw. If "Keep", the player is marked as having kept.
6.  Once all players keep, `MulliganService` calls `afterMulligan` on each instance.
7.  For London mulligan, `afterMulligan` likely triggers a call back to `PlayerControllerHuman.londonMulliganReturnCards`, which uses `InputLondonMulligan` to get the cards to put on the bottom.

## Key Files and References

*   `forge.game.mulligan.MulliganService` (`forge-game/src/main/java/forge/game/mulligan/MulliganService.java`)
*   `forge.game.mulligan.AbstractMulligan` and subclasses (e.g., `LondonMulligan`) (`forge-game/src/main/java/forge/game/mulligan/`)
*   `forge.gui.player.PlayerControllerHuman` (`forge-gui/src/main/java/forge/player/PlayerControllerHuman.java`)
*   `forge.gamemodes.match.input.InputConfirmMulligan` (Likely location)
*   `forge.gamemodes.match.input.InputLondonMulligan` (Likely location)
*   `forge.StaticData` (`forge-core/src/main/java/forge/StaticData.java`): Provides the configured mulligan rule.