# Project Brief: Forge MTG Engine Analysis

## Project Goal

Analyze the codebase of the Forge Magic: The Gathering engine to understand and document the implementation of specific game mechanics. This analysis serves as preparation for adding a new custom game format.

## Core Requirements

1.  **Analyze Key Mechanics:** Investigate how the following are handled within the Forge codebase, focusing on the desktop UI:
    *   Game Types/Formats (e.g., Commander, Oathbreaker)
    *   Mulligan process
    *   Starting Life and Hand Size determination
    *   Start of Game sequence (including initial draw)
    *   Deck Size constraints
2.  **Document Findings:** Create clear and referenced Markdown documentation for each analyzed mechanic within the `memory-bank/` directory.
3.  **Prepare for Extension:** The ultimate goal is to use this documented understanding to implement a new custom game format with specific rules (10 life, 30 cards, custom draw/discard, max 5 hand size).

## Scope

*   Focus on the core game logic (`forge-game`, `forge-core`) and the desktop UI (`forge-gui-desktop`, `forge-gui`).
*   Ignore mobile UI variants for now.