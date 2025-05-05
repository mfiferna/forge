# Plan: Grinder Game Type Enhancements (v1)

This plan outlines the steps to implement the requested enhancements for the Grinder game type in Forge.

**Goal 1: Ensure Exclusive Grinder Game Type Selection (Requirement #1)**
*   **Analysis:** Examine UI logic in `forge-gui-desktop/src/main/java/forge/screens/home/VLobby.java` and potentially shared logic in `forge-gui/src/main/java/forge/gamemodes/match/GameLobby.java` for GameType selection checkboxes/list.
*   **Implementation:** Modify event handlers to deselect/disable other incompatible GameTypes when `GameType.GRINDER` is selected, and vice-versa.

**Goal 2: Integrate Grinder into Deck Editor UI**
*   **Analysis:**
    *   Identify the UI component (likely in `forge-gui-desktop`) responsible for managing the Deck Editor tabs (e.g., "Constructed", "Commander").
    *   Determine how new tabs are added and associated with a specific `DeckFormat`.
*   **Implementation:**
    *   Add a new "Grinder" tab to the Deck Editor UI.
    *   Ensure this tab correctly uses `DeckFormat.GRINDER` for deck validation feedback during editing and saving.
    *   Add necessary display strings to `forge-gui/res/languages/en-US.properties` (e.g., `deckEditor_tab_Grinder=Grinder`).

**Goal 3: Implement Grinder Deck Selection in Lobby (Requirement #2 - Lobby Part)**
*   **Analysis:**
    *   Locate the code in `VLobby.java` / `GameLobby.java` that populates the deck source dropdown/selection component.
    *   Identify how user decks are loaded and filtered based on the selected source.
*   **Implementation:**
    *   **Add Deck Source Options:**
        *   Define identifiers/enum constants for "Grinder Decks" and "Random Grinder Decks" in the deck source list/enum.
        *   Add corresponding display strings to `forge-gui/res/languages/en-US.properties` (e.g., `lobby_deckSource_Grinder=Grinder Decks`, `lobby_deckSource_RandomGrinder=Random Grinder Decks`).
    *   **Implement "Grinder Decks" Logic:**
        *   When selected, filter the user's deck list to show only decks conforming to `DeckFormat.GRINDER` (using `DeckFormat.GRINDER.getDeckConformanceProblem(deck)`).
    *   **Implement "Random Grinder Decks" Logic:**
        *   When selected, first get the list of all user decks valid for `DeckFormat.GRINDER`.
        *   If the list is not empty, randomly select one deck from this filtered list to assign to the player. Handle the case where no valid Grinder decks exist.

**Goal 4: Update Grinder Deck Validation Rules (Requirement #3)**
*   **Analysis:** Confirm `DeckFormat.GRINDER` location in `forge-core/src/main/java/forge/deck/DeckFormat.java`.
*   **Implementation:** Modify the `GRINDER` enum constant's `mainRange` parameter from `Range.is(30)` to `Range.between(20, 30)`.

**Diagram:**
```mermaid
graph TD
    subgraph "Goal 1: Exclusive Game Type"
        A1["Analyze VLobby/GameLobby UI"] --> B1["Modify GameType Selection Logic"]
    end

    subgraph "Goal 2: Deck Editor Tab"
        A2["Analyze Deck Editor Tab UI"] --> B2["Add 'Grinder' Tab"]
        B2 --> C2["Link Tab to DeckFormat.GRINDER"]
        B2 --> D2["Update Language File (Tab Name)"]
    end

    subgraph "Goal 3: Lobby Deck Selection"
        A3["Analyze Lobby Deck Source UI"] --> B3["Add 'Grinder Decks' & 'Random Grinder Decks' Options"]
        B3 --> C3a["Implement 'Grinder Decks' Filtering Logic"]
        B3 --> C3b["Implement 'Random Grinder Decks' Selection Logic (Filter + Random Choice)"]
        B3 --> D3["Update Language File (Source Names)"]
    end

    subgraph "Goal 4: Deck Validation Rule"
        A4["Locate DeckFormat.java"] --> B4["Modify GRINDER mainRange to 20-30"]
    end

    B1 --> SwitchMode["Suggest Switch to Code Mode"]
    C2 --> SwitchMode
    D2 --> SwitchMode
    C3a --> SwitchMode
    C3b --> SwitchMode
    D3 --> SwitchMode
    B4 --> SwitchMode