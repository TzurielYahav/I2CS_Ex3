# Assignment 3
This is a repository for project 3 of the course - Introduction to Computer Science in Java (Ariel University, 2026 - fall semester - A).

---
<br></br>
## Contents
### Classes
* Ex3Algo
* ManualAlgo
* Ex3Main
* GameInfo
* Index2D
* Map
#### Interfaces
* Map2D
* Pixel2D
---
### Package - ServerSide
* Game (including Ghost, GameMap)
* PointInt2D
* StdDraw
* Resource files (images)
#### Interfaces
* IGame
* IGhost
---
### Package - ClientSide
* GameInfo
* GameMain
* Index2D
* Map
* PacmanAlgo
#### Interfaces
* Pixel2D
* Map2D

---

<br></br>
## Usage
### Pacman Algorithm
#### Description
The first part of the project is the Pacman algorithm showcase.<br></br>
To open the project run the *Ex3Main* file.<br></br>
To use the project: Press 'Space' to start, 'H' for help
The program will start the Pacman game with the auto algorithm.

<br></br>
#### The Algorithm
The algorithm is built as a state machine that receives the current state of the game,
 alternates between 3 states and returns the direction that Pacman should go to.
The three states of the algorithm are:
* Chase fruits
* Escape ghosts
* Chase ghosts

The algorithm starts by choosing state with the function

    private int pacmanMoveState()

This function works as follows:
* Starts with neutral direction 0.
* Checks if there is enough time left for the ghosts in eatable state.
* If true we are in chasing mod, and direction is the outcome of the 'Chase ghosts' state.
* If not or if we didn't find a close ghost to chase, Calculate a path to the nearest fruit avoiding ghosts, using the `chaseFruits()` function.
* If we are in chase mode return the direction of the path to the nearest fruit.
* If not, check if we have to escape first.
* For each ghost check if it's active,
* If true calculate the distance from the ghost to every tile using the `allDistance()` function.
* If the ghost is too far from us ignore it. If it's close add the distances map to the escape plan (the sum of all the close ghosts distances) and change state to 'Escape ghosts'.
* After checking all ghosts, if we are in escaping mode call `escapeGhosts()` function.
* If not, return the direction from the 'chase fruits' path.

<br></br>

    private void chaseFruit(boolean isChasing)

This function works as follows:
 * This function Computes the shortest valid path between from Pacman to the nearest fruit, using the BFS algorithm.
 * Make a temporary map to use in the calculation of the way.
 * If we are in chasing mode, we dont want to waste big fruits, so put an obstacle mark on top of them.
 * If not, we want to avoid ghosts, so put an obstacle mark on top of them.
 * If there is a lone fruit (that doesn't have other fruits around it) around us, we don't want to miss it because it wastes time so move to this fruit.
 * If not, calculate the direction to the nearest fruit.

<br></br>

    private int escapeGhosts(Map2D escapePlan)

This function works as follows:
* If there is already a path to the nearest fruit, make it the target.
* Check if there are no ghosts within 1 tile of the target, and if it is not a closed tile (has less than 3 walls around it).
* If true, return the direction to it.
* Else, for each side around us, check if it has no ghosts within 1 tile, if it's not a closed tile, not the entrance to the ghost house, and if it's sum of distances from the closest ghost is the highest yet.
* If true make it our escape tile.
* Return the best escape tile.

<br></br>

    private int chaseGhost()

This function works as follows:
* For each ghost it calculates the distance in a straight line. 
* If it's less then the max chase distance and less than the current ghost and this ghost is not in the ghost house, choose this ghost.
* If no ghost was selected return 0.
* Else make a temporary map that puts an obstacle value on top of the big fruits (to not waste them).
* Calculate the shortest path to that ghost using the BFS algorithm with the `shortestPath()` function.

---

<br></br>
## Result
<br></br>
Result of running level 4 on 100 milliseconds per frame:
<br></br>
`316560499, 4, 12487, 0, 353, 1767640452092, 10, -915428924, -25315164343659`
<br></br>
Result of running level 4 on 200 milliseconds per frame:
<br></br>
`316560499, 4, 6480, 0, 360, 1767640553415, 4, -915428924, -1369122087090259`
<br></br>


https://github.com/user-attachments/assets/4da3ed2b-ea72-4aa6-9c74-1b77666e1433



---

<br></br>
## Server Implementation
### Description
This is the second part of the project that implements the server side of the game.

The server side structure is as follows:
* The `Game` class is the main part that includes all the functions needed to run the game. It implements the `IGame` interface.
* The `Game` class has 2 private subclasses `Ghost` and `GameMap`
* The `Ghost` class is responsible for the functions and path finding of the ghosts in the game. It implements the `IGhost` interface
* The `GameMap` class is responsible for creating and editing the game map for each level.
* The `PointInt2D` class is a structure representing a point in 2D space.

The server works as follows:
* The server initializes the game with `init()`.
* The main game loop starts and the menu is shown.
* After pressing to start the game the first level starts.
* The game loop order is:
    * Process input
    * Draw UI
    * Move player
    * Draw player
    * Check for collision of player with ghosts
    * Move ghosts
    * Draw ghosts
    * Check for collision of player with ghosts

<br></br>
In order to use the game:
* Make a main class that will run the game as follows

```java
    static void main(String[] args)
    {
        Game game = new Game(GameInfo._DT);
        PacmanAlgo man = new PacmanAutoAlgo();
        game.startGame(man);   //new Game(level);
    }
```

    

* Make the class `PacmanAutoAlgo` (implementing the `PacmanAlgo` interface), that will be responsible for the Pacman algorithm.
<br></br>

### Result


https://github.com/user-attachments/assets/181ccf22-124f-477a-9be3-1faf62946679








