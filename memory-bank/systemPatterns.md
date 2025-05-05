# System Patterns: Forge Game Mechanics (Initial Observations)

## Architecture Overview

*   **Modular Design:** The project appears to follow a modular structure (e.g., `forge-core`, `forge-game`, `forge-gui`, `forge-gui-desktop`).
*   **Core Logic Separation:** Core game rules and logic seem concentrated in `forge-game` and `forge-core`.
*   **UI Abstraction:** UI logic seems separated, with `forge-gui` likely containing shared components and `forge-gui-desktop` providing the specific desktop implementation.

## Key Technical Decisions/Patterns Observed

*   **Enums for Game Rules:** `GameType` and `DeckFormat` enums are used extensively to define and manage different game modes and their associated rules (deck size, singleton status, etc.).
*   **Strategy Pattern (Mulligan):** The mulligan logic uses different strategy classes (`ParisMulligan`, `LondonMulligan`, etc.) selected based on configuration, managed by `MulliganService`.
*   **Controller Pattern (MVC/MVP variant?):** `PlayerControllerHuman` acts as an intermediary between the game logic (`Player`, `MulliganService`) and the UI input/output mechanisms (`InputConfirmMulligan`, `IGuiGame`).
*   **Configuration/Data Classes:** Classes like `RegisteredPlayer` seem to act as data holders for player-specific setup information (starting life, hand size, assigned commanders/avatars) based on the selected game type and variants.
*   **Centralized Game Actions:** `GameAction` class appears to orchestrate major game state transitions like starting the game, moving cards between zones, and handling state-based actions.

*(This file will be updated as more patterns are identified during further analysis or implementation.)*