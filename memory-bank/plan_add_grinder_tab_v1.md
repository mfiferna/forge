# Plan: Add Grinder Tab to Deck Editor

This plan outlines the steps required to add a "Grinder" tab to the Deck Editor screen in the Forge desktop GUI.

## Steps

1.  **Define `EDocID.EDITOR_GRINDER`:**
    *   Add a new entry `EDITOR_GRINDER(VGrinderDecks.SINGLETON_INSTANCE)` to the `forge.gui.framework.EDocID` enum (`forge-gui-desktop/src/main/java/forge/gui/framework/EDocID.java`).
2.  **Create `VGrinderDecks.java`:**
    *   Create a new view class `VGrinderDecks.java` in `forge-gui-desktop/src/main/java/forge/screens/deckeditor/views/`.
    *   This class should be modeled after existing views like `VCommanderDecks.java` and include a `SINGLETON_INSTANCE`. It will display the list of Grinder format decks.
3.  **Verify `CEditorGrinder.java`:**
    *   Ensure the controller `forge-gui-desktop/src/main/java/forge/screens/deckeditor/controllers/CEditorGrinder.java` exists and is suitable for managing the Grinder deck editor logic. (This step is primarily verification as the file appears to exist).
4.  **Modify `editor.xml`:**
    *   Edit the default layout file `forge-gui/res/defaults/editor.xml`.
    *   Add the line `<doc>EDITOR_GRINDER</doc>` within the appropriate `<cell>` element, likely after `<doc>EDITOR_TINY_LEADERS</doc>`.
5.  **Add Localization:**
    *   Edit the localization file `forge-gui/res/languages/en-US.properties`.
    *   Add a new key-value pair for the tab title, for example: `lblGrinderDecks=Grinder`.

## Diagram of Changes

```mermaid
graph TD
    subgraph "EDocID.java"
        A["Add EDITOR_GRINDER(VGrinderDecks.SINGLETON_INSTANCE)"]
    end

    subgraph "views/"
        B["Create VGrinderDecks.java (similar to VCommanderDecks.java)"]
    end

    subgraph "controllers/"
        C["Verify CEditorGrinder.java exists"]
    end

    subgraph "res/defaults/editor.xml"
        D["Add <doc>EDITOR_GRINDER</doc> to the correct cell"]
    end

    subgraph "res/languages/en-US.properties"
        E["Add lblGrinderDecks=Grinder"]
    end

    A --> B
    A --> D
    B --> C
    D --> E

    style C fill:#eee,stroke:#333,stroke-width:1px,stroke-dasharray: 5, 5