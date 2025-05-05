# Forge Game Mechanics Analysis Plan

This document outlines the plan for analyzing and documenting specific game mechanics within the Forge codebase, focusing on the desktop UI.

## Analysis Goals

Investigate and document the implementation of the following:

1.  **Game Types/Formats:** How are various MTG game types (Commander, Oathbreaker, etc.) handled in the UI and internally?
2.  **Mulligan:** How is the mulligan process implemented in the UI and internally?
3.  **Starting Life/Hand Size:** How are starting life totals and hand sizes determined?
4.  **Start of Game Round:** How is the beginning of a game round, including the initial draw, handled?
5.  **Deck Size:** How are deck size constraints enforced?

## Proposed Plan

**Phase 1: Information Gathering & Analysis (Completed)**

1.  **Explore Key Directories:** Identified `forge-game/src/main/java` and `forge-gui-desktop/src/main/java` (and subsequently `forge-gui/src/main/java`) as key areas.
2.  **Keyword Searching:** Performed searches for relevant keywords ("GameType", "Mulligan", "DeckFormat", etc.).
3.  **Code Analysis:** Examined key files (`GameType.java`, `DeckFormat.java`, `MulliganService.java`, `PlayerControllerHuman.java`, `RegisteredPlayer.java`, `GameAction.java`) to understand the logic.

**Phase 2: Documentation (Current Phase)**

1.  **Create Memory Bank Structure:** Set up the `memory-bank` folder and core documentation files (`projectBrief.md`, `productContext.md`, `activeContext.md`, `systemPatterns.md`, `techContext.md`, `progress.md`).
2.  **Document Findings:** Create specific Markdown files within `memory-bank/` for each analysis goal:
    *   `memory-bank/game_formats.md`
    *   `memory-bank/mulligan.md`
    *   `memory-bank/starting_rules.md`
    *   `memory-bank/game_start.md`
    *   `memory-bank/deck_rules.md`
    *   Each file will include summaries and code references.
3.  **Update Core Files:** Update `activeContext.md` and `progress.md` to reflect the analysis task and link to detailed documentation.

**Phase 3: Review and Handoff**

1.  **Present Findings:** Present the documented analysis for user review.
2.  **Refine:** Incorporate user feedback if necessary.
3.  **Switch Mode:** Suggest switching to a different mode (e.g., `code`) for implementing the new game format based on the documented understanding.

## Analysis Summary (Preliminary)

*   **Game Types:** `forge.game.GameType` enum links to `forge.deck.DeckFormat` for rules. `forge.game.GameRules` manages applied variants.
*   **Mulligan:** `forge.game.mulligan.MulliganService` handles logic; `forge.gui.player.PlayerControllerHuman` handles UI via `InputConfirmMulligan`/`InputLondonMulligan`.
*   **Starting Life/Hand:** `forge.game.player.RegisteredPlayer` sets defaults (20 life, 7 cards) modified by variants (life) and Vanguard avatars (life/hand).
*   **Game Start:** `forge.game.GameAction.startGame` orchestrates first player determination, initial draw, mulligans, opening hand actions, and first turn start.
*   **Deck Size:** Defined in `forge.deck.DeckFormat` (`mainRange`) linked via `GameType`.