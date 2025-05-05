# Documentation: Start of Game Sequence in Forge

This document outlines the sequence of events that occur at the beginning of a game in Forge, from determining the first player to starting the first turn.

## Core Implementation

*   **`forge.game.GameAction.startGame(GameOutcome lastGameOutcome, Runnable startGameHook)`:** This method (`forge-game/src/main/java/forge/game/GameAction.java`, lines 2270-2330) orchestrates the entire game start process.

## Sequence of Events

1.  **Determine First Player:**
    *   Calls `determineFirstTurnPlayer(lastGameOutcome)` (Lines 2332-2390).
    *   **Logic:**
        *   Handles `Puzzle` game type (player 0 always starts).
        *   Handles `Archenemy` variant (Archenemy player starts).
        *   Checks for `Power Play` conspiracy cards in the command zone; if present, randomly chooses among players with Power Play.
        *   If it's the first game (`lastGameOutcome == null`), randomly selects a player (`Aggregates.random(game.getPlayers())`).
        *   If not the first game, the player who lost the previous game chooses.
        *   The chosen player is then prompted via their controller (`goesFirst.getController().chooseStartingPlayer(isFirstGame)`) to decide whether they want to play first or draw first (for 2-player games) or choose the starting player (for multiplayer).
    *   **Result:** Returns the `Player` object who will take the first turn.

2.  **Fire `GameEventGameStarted`:** (Line 2279)
    *   Notifies listeners (including UI) that the game is starting, passing the `GameType`, the determined `first` player, and the list of all players.

3.  **Run Pre-Opening Hand Actions:** (Line 2281)
    *   Calls `runPreOpeningHandActions(first)` (Lines 2392-2427).
    *   Handles effects that occur *before* hands are drawn (e.g., choosing colors for Cryptic Spires, setting numbers for Emissary's Ploy). Iterates through players in turn order starting from `first`.

4.  **Set Game Stage to Mulligan:** (Line 2283)
    *   `game.setAge(GameStage.Mulligan);`

5.  **Draw Initial Hands:** (Lines 2284-2294)
    *   Iterates through all players.
    *   Calls `p1.drawCards(p1.getStartingHandSize())` (Line 2288). The hand size is retrieved from `Player.getStartingHandSize()`, which was initialized from `RegisteredPlayer`.
    *   *(Note: Includes logic for optional "filtered hands" based on land ratio, calling `drawStartingHand` instead if enabled).*
    *   Handles extra hands from Backup Plan conspiracy.

6.  **Mulligan Phase:** (Lines 2297-2300)
    *   Checks `game.getRules().getGameType() != GameType.Puzzle`.
    *   Creates `new MulliganService(first)` and calls `perform()`.
    *   The `MulliganService` handles the entire mulligan process according to the configured rules (see `memory-bank/mulligan.md`).
    *   The game can end here if a player concedes during the mulligan prompt.

7.  **Set Game Stage to Play:** (Line 2303)
    *   `game.setAge(GameStage.Play);`

8.  **Planechase Setup:** (Lines 2306-2311)
    *   If `game.getRules().hasAppliedVariant(GameType.Planechase)`:
        *   Initializes the first plane (`first.initPlane()`).
        *   Creates Planechase effects for all players.

9.  **Run Opening Hand Actions:** (Line 2313)
    *   Calls `runOpeningHandActions(first)` (Lines 2430-2474).
    *   Allows players (in turn order starting from `first`) to activate abilities from cards in their opening hand (e.g., Gemstone Caverns).
    *   This can potentially change who the starting player is (`newFirst = takesAction;`).
    *   Returns the potentially updated starting player.

10. **Check State-Based Actions:** (Line 2314)
    *   `checkStateEffects(true);`

11. **Run Beginning of Game Triggers:** (Line 2317)
    *   `game.getTriggerHandler().runTrigger(TriggerType.NewGame, AbilityKey.newMap(), true);`

12. **Start First Turn:** (Line 2321)
    *   Sets the final starting player: `game.setStartingPlayer(first);` (using the potentially updated `first` player from step 9).
    *   Calls `game.getPhaseHandler().startFirstTurn(first, startGameHook);` which transitions the game to the first player's untap step and begins the normal turn progression.

13. **Post-Start Cleanup:** (Lines 2323-2326)
    *   Records initial hand size for turn tracking.
    *   Cancels any lingering auto-pass settings for all players.

## Key Files and References

*   `forge.game.GameAction` (`forge-game/src/main/java/forge/game/GameAction.java`): Contains the `startGame` method orchestrating the sequence.
*   `forge.game.player.PlayerController` (`forge-game/src/main/java/forge/game/player/PlayerController.java`): Interface/base class defining `chooseStartingPlayer`.
*   `forge.gui.player.PlayerControllerHuman` (`forge-gui/src/main/java/forge/player/PlayerControllerHuman.java`): Implements `chooseStartingPlayer` for humans.
*   `forge.game.player.Player` (`forge-game/src/main/java/forge/game/player/Player.java`): Contains `drawCards`, `getStartingHandSize`.
*   `forge.game.mulligan.MulliganService` (`forge-game/src/main/java/forge/game/mulligan/MulliganService.java`): Handles the mulligan phase.
*   `forge.game.PhaseHandler` (`forge-game/src/main/java/forge/game/phase/PhaseHandler.java`): Manages turn and phase progression, including `startFirstTurn`.
*   `forge.game.Game` (`forge-game/src/main/java/forge/game/Game.java`): Holds game state, rules, players, phase handler, etc.
*   `forge.game.GameRules` (`forge-game/src/main/java/forge/game/GameRules.java`): Provides game type and variant information.