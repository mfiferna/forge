# Technical Context: Forge MTG Engine

## Core Technologies

*   **Language:** Java
*   **Build System:** Maven (based on `pom.xml` files observed)
*   **UI Framework (Desktop):** Likely Swing or a similar Java desktop UI framework (inferred from `forge-gui-desktop` and common Java practices, needs confirmation if specific UI code is examined). `forge-gui` might use a more abstract UI toolkit or interfaces.

## Key Libraries/Dependencies (Inferred/Observed)

*   **Guava:** Used for collections (`com.google.common.collect`).
*   **Apache Commons Lang:** Used for utilities (`org.apache.commons.lang3`).
*   **JGraphT:** Potentially used for graph-related logic (dependency analysis, etc. - based on `org.jgrapht` imports seen in `GameAction.java`).
*   **Sentry:** Used for error reporting (`io.sentry.Sentry` import).

## Development Environment

*   **OS:** Windows 11 (User specified)
*   **Shell:** Powershell (User specified)
*   **IDE:** Visual Studio Code

## Technical Constraints/Considerations

*   **Cross-Platform UI:** The separation into `forge-gui`, `forge-gui-desktop`, `forge-gui-mobile`, etc., suggests an architecture designed to support multiple UI frontends. Changes to core game logic must consider potential impacts on all UIs.
*   **Performance:** As a game engine simulating complex rules, performance is likely a key consideration.
*   **Extensibility:** The use of enums and strategy patterns suggests an intent for the system to be extensible (e.g., adding new game types, mulligan rules).

*(This file will be updated as more technical details are discovered.)*