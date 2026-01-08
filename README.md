# Assignment 3
This is a repository for project 3 of the course - Introduction to Computer Science in Java (Ariel University, 2026 - fall semester - A).

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

### Package - ServerSide
* Game (including Ghost, GameMap)
* PointInt2D
* StdDraw
* Resource files (images)
#### Interfaces
* IGame
* IGhost

### Package - ClientSide
* GameInfo
* GameMain
* Index2D
* Map
* PacmanAlgo
#### Interfaces
* Pixel2D
* Map2D


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
* If not or if we didn't find a close ghost to chase, Calculate a path to the nearest fruit avoiding ghosts, using the chaseFruits() function.
* If we are in chase mode return the direction of the path to the nearest fruit.
* If not, check if we have to escape first.
* For each ghost check if it's active,
* If true calculate the distance from the ghost to every tile using the allDistance() function.
* If the ghost is too far from us ignore it. If it's close add the distances map to the escape plan (the sum of all the close ghosts distances) and change state to 'Escape ghosts'.
* After checking all ghosts, if we are in escaping mode call escapeGhosts() function.
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
    {
        double closestDistance = Double.MAX_VALUE;
        int closestGhost = -1;
        for (int i = 0; i < _ghosts.length; i++)
        {
            double newClosestDistance = pixelFromString(_ghosts[i].getPos(CODE)).distance2D(_pos);
            if (closestDistance > newClosestDistance && newClosestDistance < CHASE_DISTANCE && notInCenter(getGhostPos(i)))
            {
                closestDistance = newClosestDistance;
                closestGhost = i;
            }
        }
        if (closestGhost == -1)
            return 0;

        Map tmpMap = dontEatBigFruits();
        _waypoints.clear();
        _waypoints.addAll(tmpMap.shortestPath(_pos, pixelFromString(_ghosts[closestGhost].getPos(CODE)), OBSTACLE_COLOR));
        _waypoints.removeFirst();
        if (!_waypoints.isEmpty())
        {
            return getDirection(_waypoints.removeFirst());
        }
        return 0;
    }

This function works as follows:
* If there is already a path to the nearest fruit, make it the target.
* Check if there are no ghosts within 1 tile of the target, and if it is not a closed tile (has less than 3 walls around it).
* If true, return the direction to it.
* Else, for each side around us, check if it has no ghosts within 1 tile, if it's not a closed tile, not the entrance to the ghost house, and if it's sum of distances from the closest ghost is the highest yet.
* If true make it our escape tile.
* Return the best escape tile.

<br></br>
### Ex2_GUI
#### Description
This class is responsible for drawing the map and interface to interact with, using StdDraw.
<br></br>

#### Main Functions

    public static void drawMap()

* This function Draws the Map object of the Ex2_GUI class

<br></br>


    public static Map2D loadMap(String mapFileName) throws FileNotFoundException

* This function loads a map from a txt file

<br></br>

    public static void saveMap(Map2D map, String mapFileName)

* This function saves the map into a txt file

<br></br>


#### Running Ex2_GUI

<br></br>
  <img width="514" height="568" alt="image" src="https://github.com/user-attachments/assets/10a2f493-e7aa-45a1-9754-fe4ffc60a70e" />
  <br></br>
    <p>
    this image shows the first screen of the program. you can move between screens with '1-4'. In this screen you are the blue circle and you are running from the red circle, which is using the shortest path algorithm to find you.
    </p>
    <br></br>
    <img width="513" height="567" alt="image" src="https://github.com/user-attachments/assets/755951ef-7115-45c1-b9ee-dcd0ed498c8b" />
    <br></br>
    <p>  
    this image shows the second screen. In this screen you are the blue circle and you are running from the red circle, which is using the shortest path algorithm to find you. in this screen you also have an obstacle in the center.
    </p>
    <br></br>
    <img width="514" height="566" alt="image" src="https://github.com/user-attachments/assets/6853ffdc-b865-477f-8c4f-86b669e33e63" />
    <br></br>
    <p>
    this image shows the third screen. In this screen you are the blue circle and you are running from the red circle, which is using the shortest path algorithm to find you. in this screen you are inside a maze and you have yellow points to collect.
    </p>  
    <br></br><br></br>
    <img width="510" height="564" alt="image" src="https://github.com/user-attachments/assets/fd9701c1-082a-45db-8d01-52054b3442a5" />
    <br></br>
    <p>
    this image shows the fourth screen. In this screen you are the blue circle and you are running from the red circle, which is using the shortest path algorithm to find you. in this screen the map is cyclic.
    </p>

