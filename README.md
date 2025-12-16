# ♟️ SimpleChess

A lightweight **Java-based Chess Game** built using **Swing**, **Graphics2D**, and **javax.sound**. This project focuses on clean structure, classic Java desktop UI techniques, and object-oriented design principles—no external libraries required.

---

## 📌 Features

* Full 2-player chess gameplay (local)
* Graphical board and pieces rendered with **Graphics2D**
* Interactive UI using **SwingUtilities** and Swing components
* Sound effects powered by **javax.sound**
* Modular package structure for clarity and scalability

---

## 🧱 Project Structure

```
SimpleChess/
├── main/
│   ├── Board.java
│   ├── GamePanel.java
│   ├── MenuPanel.java
│   ├── Mouse.java
│   ├── Sound.java
│   ├── StatusPanel.java
│   ├── TitleWindow.java
│   └── Type.java
├── piece/
│   ├── Piece.java
│   ├── Pawn.java
│   ├── Rook.java
│   ├── Knight.java
│   ├── Bishop.java
│   ├── Queen.java
│   └── King.java
└── res/
    ├── images/
    │   └── (chess piece sprites)
    └── sounds/
        └── (move, capture, check sounds)
```
---

## 📂 Package Overview

### **main**
Core application and UI logic.

- **Board** – Handles board state, piece placement, and move validation
- **GamePanel** – Main game rendering surface using `Graphics2D`
- **MenuPanel** – Game menu and navigation UI
- **Mouse** – Mouse input handling (click, drag, release)
- **Sound** – Audio playback using `javax.sound.sampled`
- **StatusPanel** – Displays game status (turn, check, messages)
- **TitleWindow** – Initial title screen and window setup
- **Type** – Enumerations/constants for piece types, colors, or game states

### **piece**
Encapsulates all chess piece behavior.

- `Piece` acts as the abstract/base class
- Individual piece classes implement their specific movement rules
- Promotes clean inheritance and rule separation

### **res**
Static resources used by the game.

- **images/** – Chess piece sprites and board assets
- **sounds/** – Move, capture, and notification sound effects

---

## 🎮 Controls

- **Mouse Click** – Select and move pieces
- Valid moves are enforced programmatically
- Illegal moves are automatically rejected

---

## 🔊 Sound System

The game uses **javax.sound.sampled** to play sound effects:
- Piece movement
- Captures
- Game notifications

Audio files are preloaded for minimal latency.

---

## 🖥️ Graphics & Rendering

- **SwingUtilities** ensures safe UI updates on the Event Dispatch Thread (EDT)
- **Graphics2D** handles:
  - Anti-aliased rendering
  - Board drawing
  - Piece scaling and positioning

---

## ⚙️ Requirements

- Java Development Kit (JDK) **8 or higher**
- Any Java-compatible IDE (IntelliJ IDEA, Eclipse, NetBeans) or command-line tools

---

## ▶️ How to Run

1. Clone or download the project
2. Open it in your preferred Java IDE
3. Ensure the `res` folder is included in the classpath
4. Run `Main.java`

Or via terminal:
```bash
javac main/Main.java
java main.Main
````

---

## 🛠️ Possible Improvements

* Add AI opponent (Minimax / Alpha-Beta)
* Highlight valid moves
* Implement check, checkmate, and stalemate indicators
* Add move history and undo functionality

---

## 📜 License

This project is intended for **educational and personal use**. You are free to modify and extend it.

---

Happy coding—and may your queen never hang unexpectedly. ♜

