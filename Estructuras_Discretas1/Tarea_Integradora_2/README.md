# 🎮 Hacker City – Key Recovery and Network Infiltration Game

**Discrete Structures and Computing I**

---

## 📘 Overview

**Hacker City** is an interactive educational video game where the player takes on the role of a hacker infiltrating a digital city to collect encryption keys and reach three strategic targets: the Hospital, the Police Station, and the Fire Station.  
The entire system is built on a JavaFX visual representation of a directed, weighted graph.

---

## 🧠 Core Algorithms Used

| Algorithm    | Purpose                                                        |
|--------------|----------------------------------------------------------------|
| **BFS**      | Broad exploration to quickly locate accessible keys            |
| **Dijkstra** | Calculates optimal paths between two selected points           |

---

## 🧩 Features

- 🎨 **JavaFX GUI** with instruction screen and interactive map  
- 🕵️ Riddles to select your initial mission target  
- 🧠 Real-time algorithm switching (`E`: BFS ↔ Dijkstra)  
- 🧱 Graph structure switching (`G`: Adjacency List ↔ Matrix)  
- 🧭 Automatic hacker movement between nodes  
- 🔒 Display of node security levels (edge weights)  
- 🏆 Score system based on collected keys and path efficiency  
- 🧠 Visual feedback for paths, visited nodes, and points  
- 🎞️ Animated hacker with real-time render loop  
- 🗂️ Modular implementation with clean OOP architecture  

---

## 🛠 Technologies

- Java 22  
- JavaFX (FXML, controls, graphics)  
- Custom Graph ADT (Adjacency List + Matrix)  
- Object-Oriented Programming  
- MVC-like modular architecture  

---

## 🗺️ Game Flow

1. 🧠 The player selects a mission target by solving a riddle  
2. 👾 The interactive map is displayed  
3. 📡 The player clicks a node to define the next destination  
4. 🤖 The hacker moves automatically based on the active algorithm  
5. 🔑 Keys and points are registered as nodes are visited  
6. 🏁 The game ends when the chosen target is successfully reached  

---

## 👨‍💻 Authors

- Miguel Pérez Ojeda  
- Ommhes Samuel Leon Diaz  
- Luis Fernando Bedoya Soto  
