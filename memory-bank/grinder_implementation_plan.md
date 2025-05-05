# Plan: Implement Grinder Game Format

**Goal:** Implement the Grinder game format with 10 starting life, 30-card decks, draw 7 then discard to 5 starting hand, and a maximum hand size of 5.

**Plan Steps:**

1.  **Define Format:**
    *   **`DeckFormat` Enum (`forge-core/src/main/java/forge/deck/DeckFormat.java`):**
        *   Add `GRINDER` constant.
        *   Set `mainRange` to `Range.is(30)`.
        *   Set `sideRange` to `Range.is(0)`.
        *   Set `maxCardCopies` to 4.
    *   **`GameType` Enum (`forge-game/src/main/java/forge/game/GameType.java`):**
        *   Add `GRINDER` constant.
        *   Associate with `DeckFormat.GRINDER`.
        *   Add `getStartingLife()` method override returning 10.
        *   Add `getMaxHandSize()` method override returning 5.
        *   Add `getMulliganRule()` method override returning `"Grinder"`.

2.  **Implement Rules:**
    *   **`Player` Initialization (e.g., in `forge.game.Game`):**
        *   Modify initialization of `Player.life` to use `game.getRules().getGameType().getStartingLife()`.
        *   Modify initialization of `Player.maxHandSize` to use `game.getRules().getGameType().getMaxHandSize()`.
        *   Ensure `Player.startingHandSize` remains initialized to 7.

3.  **Implement Mulligan:**
    *   **Create `GrinderMulligan` Class (`forge-game/src/main/java/forge/game/mulligan/GrinderMulligan.java`):**
        *   Extend `AbstractMulligan`.
        *   Implement `canMulligan()`, `handSizeAfterNextMulligan()` (returns 7), `mulligan()` (draws 7).
        *   Implement `afterMulligan()` to calculate `cardsToDiscard = player.getZone(ZoneType.Hand).size() - 5` and call `player.getController().grinderMulliganDiscardCards(player, cardsToDiscard)`. Move returned cards to library bottom.
    *   **Modify `MulliganService` (`forge-game/src/main/java/forge/game/mulligan/MulliganService.java`):**
        *   In `initializeMulligans`, check `game.getRules().getGameType().getMulliganRule()`. If `"Grinder"`, instantiate `GrinderMulligan`. Otherwise, use global setting.
    *   **Modify `PlayerController` Interface/Base (`forge-game/src/main/java/forge/game/player/PlayerController.java`):**
        *   Add abstract method `CardCollection grinderMulliganDiscardCards(Player player, int cardsToDiscard);`
    *   **Modify `PlayerControllerHuman` (`forge-gui/src/main/java/forge/player/PlayerControllerHuman.java`):**
        *   Implement `grinderMulliganDiscardCards` to use a new `InputGrinderDiscard` dialog.
    *   **Create `InputGrinderDiscard` Class (e.g., `forge-gui/src/main/java/forge/gamemodes/match/input/InputGrinderDiscard.java`):**
        *   Create UI dialog to select `cardsToDiscard` from hand.

4.  **UI Integration (Desktop):**
    *   **Game Setup Screen(s) (`forge-gui-desktop` module):**
        *   Add `GameType.GRINDER` to the list of selectable game types.

5.  **Testing:**
    *   Verify deck size (30 cards).
    *   Verify starting life (10).
    *   Verify initial draw (7 cards).
    *   Verify mulligan discard (keep 7 -> discard 2 -> hand 5).
    *   Verify mulligan redraw (mulligan -> draw 7 -> keep -> discard 2 -> hand 5).
    *   Verify max hand size (5) during cleanup step.

**Mermaid Diagram:**

```mermaid
graph TD
    subgraph "Setup Phase"
        UI("Desktop UI") -- "Selects Grinder" --> GameSetup("GameSetup")
        GameSetup -- "Creates" --> Match("Match Object w/ GameType.GRINDER")
        Match -- "Creates" --> Game("Game Object")
        Game -- "Creates" --> Player("Player Objects")
        GameType("GameType") -- "getStartingLife()=10" --> Player
        GameType -- "getMaxHandSize()=5" --> Player
        GameType -- "getMulliganRule()=\"Grinder\"" --> MulliganService("MulliganService")
    end

    subgraph "Mulligan Phase"
        GameAction("GameAction") -- "Calls" --> MulliganService
        MulliganService -- "Instantiates" --> GrinderMulligan("GrinderMulligan")
        MulliganService -- "Calls" --> PCH("PlayerControllerHuman.mulliganKeepHand")
        PCH -- "Uses" --> InputConfirm("InputConfirmMulligan")
        InputConfirm -- "User Keeps" --> PCH
        PCH -- "Returns Keep" --> MulliganService
        MulliganService -- "Calls" --> GrinderMulligan.afterMulligan
        GrinderMulligan -- "Calls" --> PCH2("PlayerControllerHuman.grinderMulliganDiscardCards")
        PCH2 -- "Uses" --> InputDiscard("InputGrinderDiscard")
        InputDiscard -- "User Selects Cards" --> PCH2
        PCH2 -- "Returns Discards" --> GrinderMulligan
        GrinderMulligan -- "Moves Cards" --> Player("Player's Library")
    end

    subgraph "Gameplay Phase"
        PhaseHandler("PhaseHandler") -- "Cleanup Step" --> Player
        Player -- "getMaxHandSize()=5" --> PhaseHandler
        PhaseHandler -- "Calculates Discards" --> Player
        Player -- "Needs Discard" --> PCH3("PlayerControllerHuman.discardExcessHand")
        PCH3 -- "Uses" --> InputDiscardHand("InputDiscard")
        InputDiscardHand -- "User Selects" --> PCH3
        PCH3 -- "Discards" --> Player("Player's Graveyard")
    end

    DeckFormat("DeckFormat") -- "mainRange=30" --> DeckValidation("DeckValidation")
    GameSetup -- "Validates Deck" --> DeckValidation

    style GrinderMulligan fill:#f9d,stroke:#333,stroke-width:2px
    style InputGrinderDiscard fill:#f9d,stroke:#333,stroke-width:2px