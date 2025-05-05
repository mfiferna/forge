# Documentation: Starting Life and Hand Size in Forge

This document details how starting life totals and hand sizes are determined in the Forge engine.

## Core Implementation

*   **`forge.game.player.RegisteredPlayer` Class:** This class (`forge-game/src/main/java/forge/game/player/RegisteredPlayer.java`) is central to defining a player's initial state before a game begins.
    *   **Default Values:**
        *   `private int startingLife = 20;` (Line 25)
        *   `private int startingHand = 7;` (Line 26)
    *   **Getters/Setters:** Provides `getStartingLife()`, `setStartingLife(int)`, `getStartingHand()`, `setStartingHand(int)`.

## How Starting Life is Determined

1.  **Default:** The `startingLife` field defaults to 20.
2.  **Game Type / Variant Modification:**
    *   The static factory method `RegisteredPlayer.forVariants(...)` (Lines 149-187) is called when setting up players for games involving variants. It modifies the `startingLife` based on the `appliedVariants` set passed to it:
        *   `Archenemy` / `ArchenemyRumble`: Sets life directly to 40.
        *   `Commander`: Adds 20 life (`start.setStartingLife(start.getStartingLife() + 20);`). This additive approach allows layering (e.g., a Commander game with Vanguard).
        *   `TinyLeaders`: Adds 5 life.
        *   `Brawl`: Adds 10 life.
    *   The static factory method `RegisteredPlayer.forCommander(...)` (Lines 142-146) sets life directly to 40, likely used when Commander is the base game type.
3.  **Vanguard Avatar Modification:**
    *   The `setVanguardAvatars(List<PaperCard> vanguardAvatars0)` method (Lines 211-218) is called if the `Vanguard` variant is active.
    *   It iterates through the assigned Vanguard avatar cards.
    *   For each avatar, it adds the avatar's life modifier to the current `startingLife`: `setStartingLife(getStartingLife() + avatar.getRules().getLife());` (Line 215).

## How Starting Hand Size is Determined

1.  **Default:** The `startingHand` field defaults to 7.
2.  **Vanguard Avatar Modification:**
    *   The *only* mechanism identified in `RegisteredPlayer.java` for modifying the starting hand size is through Vanguard avatars.
    *   The `setVanguardAvatars(...)` method (Lines 211-218) iterates through assigned avatars.
    *   For each avatar, it adds the avatar's hand size modifier: `setStartingHand(getStartingHand() + avatar.getRules().getHand());` (Line 216).
3.  **Standard Formats:** Based on the analysis of `RegisteredPlayer.java`, standard game types like Commander, Oathbreaker, Brawl, Tiny Leaders, Constructed, Limited, etc., do *not* inherently change the starting hand size from the default of 7.

## Game Initialization

*   When the `Game` object is initialized, it creates `Player` objects based on the `RegisteredPlayer` instances provided by the `Match`.
*   The `Player` object's initial life (`Player.life`) and starting hand size (`Player.startingHandSize`) are set using the values from the corresponding `RegisteredPlayer` object during player initialization (likely within the `Player` constructor or an initialization method).
*   The actual drawing of the starting hand occurs later in the `GameAction.startGame` sequence, using the `startingHandSize` value stored in the `Player` object.

## Key Files and References

*   `forge.game.player.RegisteredPlayer` (`forge-game/src/main/java/forge/game/player/RegisteredPlayer.java`): Defines defaults and modifications based on variants/avatars.
*   `forge.game.GameType` (`forge-game/src/main/java/forge/game/GameType.java`): Enum used to identify variants that modify starting life.
*   `forge.item.PaperCard` / `forge.game.card.CardRules`: Contains the `getLife()` and `getHand()` methods for Vanguard avatars.
*   `forge.game.Player` (`forge-game/src/main/java/forge/game/player/Player.java`): Stores the final starting life/hand size and uses them during game setup.