# Documentation: Game Types and Formats in Forge

This document details how different Magic: The Gathering game types and formats (like Commander, Oathbreaker, etc.) are implemented in the Forge engine, based on code analysis.

## Core Implementation

*   **`forge.game.GameType` Enum:** This enum (`forge-game/src/main/java/forge/game/GameType.java`) serves as the primary definition for base game modes and variants available in Forge.
    *   Examples include `Constructed`, `Commander`, `Oathbreaker`, `TinyLeaders`, `Brawl`, `Planechase`, `Archenemy`, etc.
    *   Each `GameType` entry holds basic configuration flags like `isCardPoolLimited`, `canSideboard`, `addWonCardsMidGame`, and links to localized names/descriptions.
    *   Crucially, each `GameType` is associated with a `DeckFormat`.

*   **`forge.deck.DeckFormat` Enum:** Located in `forge-core/src/main/java/forge/deck/DeckFormat.java`, this enum defines the specific deck construction rules and other format-specific constraints.
    *   It specifies `mainRange` (min/max main deck size), `sideRange` (min/max sideboard size), `maxCardCopies` (e.g., 1 for singleton, 4 for constructed).
    *   It includes methods like `getDeckConformanceProblem` to validate a deck against the format's rules (including color identity for Commander variants).
    *   It defines legality checks for cards within the format (`isLegalCard`, `isLegalCommander`).

*   **`forge.game.GameRules` Class:** This class (`forge-game/src/main/java/forge/game/GameRules.java`) holds the rules for a specific game instance.
    *   It stores the base `GameType` for the match.
    *   It maintains a `Set<GameType> appliedVariants`. This allows combining a base game type (e.g., Constructed) with variants (e.g., Planechase, Commander).
    *   Methods like `hasAppliedVariant(GameType)` and `hasCommander()` are used throughout the codebase to check which rules are active for the current game.

## How Formats are Handled

1.  **Selection:** The UI (likely starting from screens in `forge-gui-desktop` or `forge-gui`) allows the user to select a base `GameType` and potentially add variants.
2.  **Initialization:** When a match starts, a `GameRules` object is created with the selected base `GameType`. The chosen variants are added to the `appliedVariants` set within `GameRules`.
3.  **Rule Application:**
    *   **Deck Validation:** Before the game starts, the player's deck is validated against the rules defined in the `DeckFormat` associated with the base `GameType` (using `DeckFormat.getDeckConformanceProblem`). This checks deck size, sideboard size, card limits, color identity (for Commander/Brawl/Oathbreaker), and specific card legality (like banned lists in TinyLeaders).
    *   **Starting Conditions:** The `forge.game.player.RegisteredPlayer` class uses the `appliedVariants` from `GameRules` to set initial conditions like starting life (e.g., +20 for Commander, +10 for Brawl) and assign commanders/planeswalkers to the command zone.
    *   **Gameplay Rules:** Throughout the game, various classes check `game.getRules().hasAppliedVariant(...)` or `game.getRules().getGameType()` to apply format-specific rules:
        *   `forge.game.player.Player`: Handles commander damage rules, commander replacement effects (moving to command zone), etc.
        *   `forge.game.mulligan.MulliganService`: Adjusts mulligan rules (e.g., free mulligan for Brawl).
        *   `forge.game.GameAction`: Modifies game start procedures (e.g., Archenemy goes first, Planechase setup).
        *   `forge.game.Match`: Controls sideboarding allowance based on `GameType`.
        *   Card scripts (`forge.game.card.Card`) and SpellAbility logic (`forge.game.spellability.*`) frequently check `hasAppliedVariant` to enable/disable effects or modify behavior based on the active format (e.g., checking `isCommander`, applying different replacement effects).

## Key Files and References

*   `forge.game.GameType` (`forge-game/src/main/java/forge/game/GameType.java`): Enum defining base types and variants.
*   `forge.deck.DeckFormat` (`forge-core/src/main/java/forge/deck/DeckFormat.java`): Enum defining deck construction rules.
*   `forge.game.GameRules` (`forge-game/src/main/java/forge/game/GameRules.java`): Holds rules for the current game instance, including applied variants.
*   `forge.game.player.RegisteredPlayer` (`forge-game/src/main/java/forge/game/player/RegisteredPlayer.java`): Sets starting life based on variants.
*   `forge.game.player.Player` (`forge-game/src/main/java/forge/game/player/Player.java`): Implements runtime format-specific rules.
*   `forge.game.GameAction` (`forge-game/src/main/java/forge/game/GameAction.java`): Handles game start modifications.
*   `forge.game.Match` (`forge-game/src/main/java/forge/game/Match.java`): Handles sideboarding rules.