# Documentation: Deck Size Rules in Forge

This document details how deck size constraints are defined and enforced in the Forge engine.

## Core Implementation

*   **`forge.deck.DeckFormat` Enum:** This enum, located in `forge-core/src/main/java/forge/deck/DeckFormat.java`, is the primary source for deck construction rules, including size limits.
    *   **`mainRange` Field:** Each `DeckFormat` constant defines a `Range<Integer> mainRange`. This specifies the minimum and maximum allowed number of cards in the main deck.
        *   Examples:
            *   `Constructed`: `Range.between(60, Integer.MAX_VALUE)` (60 or more)
            *   `Limited`: `Range.between(40, Integer.MAX_VALUE)` (40 or more)
            *   `Commander`: `Range.is(99)` (Exactly 99, plus commander)
            *   `Oathbreaker`: `Range.is(58)` (Exactly 58, plus Oathbreaker/Sig Spell)
            *   `TinyLeaders`: `Range.is(49)` (Exactly 49, plus commander)
            *   `Brawl`: `Range.is(59)` (Exactly 59, plus commander)
    *   **`sideRange` Field:** Similarly defines the allowed range for the sideboard size (e.g., `Range.between(0, 15)` for Constructed, `null` for Limited meaning no specific limit other than total pool).
    *   **`getDeckConformanceProblem(Deck deck)` Method:** This method within `DeckFormat` performs the actual validation. It checks:
        *   Main deck size against `mainRange`.
        *   Sideboard size against `sideRange`.
        *   Maximum copies of individual cards (`maxCardCopies`).
        *   Commander legality and color identity.
        *   Format-specific card legality (e.g., CMC limits and banned list for TinyLeaders).
        *   Adjustments for specific cards (like `Advantageous Proclamation` conspiracy modifying minimum size).

*   **`forge.game.GameType` Enum:** (`forge-game/src/main/java/forge/game/GameType.java`)
    *   Each `GameType` is associated with a specific `DeckFormat` via its constructor (e.g., `GameType.Commander` uses `DeckFormat.Commander`).
    *   This links the chosen game mode to its corresponding deck construction rules.

*   **`forge.game.GameRules` Class:** (`forge-game/src/main/java/forge/game/GameRules.java`)
    *   Holds the `GameType` for the current match.
    *   Provides access to the associated `DeckFormat` via `getGameType().getDeckFormat()`.

## Enforcement

1.  **Deck Editor:** The deck editor UI likely uses the `DeckFormat` rules associated with the selected format to provide feedback to the user during deck construction and saving, preventing the saving of invalid decks. (This part is inferred, as the deck editor UI code wasn't explicitly examined in this analysis phase).
2.  **Game Start:** Before a game begins (likely during match setup or just before `GameAction.startGame`), the system validates the selected decks for all players using `DeckFormat.getDeckConformanceProblem(deck)` based on the match's `GameType`. If a deck is invalid, the game typically cannot start.
3.  **Runtime Checks (Less Common for Size):** While the primary check is pre-game, some runtime checks might exist, for example, the code snippet found in `Player.java` (lines 3148-3150) retrieves the minimum deck size, potentially for effects that care about library size relative to the format minimum.

## Key Files and References

*   `forge.deck.DeckFormat` (`forge-core/src/main/java/forge/deck/DeckFormat.java`): Defines `mainRange`, `sideRange`, and the `getDeckConformanceProblem` validation logic.
*   `forge.game.GameType` (`forge-game/src/main/java/forge/game/GameType.java`): Links game modes to their `DeckFormat`.
*   `forge.game.GameRules` (`forge-game/src/main/java/forge/game/GameRules.java`): Provides access to the current game's `GameType` and thus its `DeckFormat`.
*   `forge.game.player.Player` (`forge-game/src/main/java/forge/game/player/Player.java`): Example of runtime code accessing `DeckFormat` rules (lines 3148-3150).