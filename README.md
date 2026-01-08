# Assignment 3
This is a repository for project 3 of the course - Introduction to Computer Science in Java (Ariel University, 2026 - fall semester - A).

<br></br>
## Contents
### Classes
* Ex2_GUI
* Index2D
* Index2DTest
* Map
* MapTest
* StdDraw

### Interfaces
* Map2D
* Pixel2D

<br></br>
## Usage
### Map
#### Description
This is the main class in this project. This class is responsible for the pathfinding algorithms and creating a new map object
<br></br>
#### Main Functions

    public int fill(Pixel2D p, int new_v, boolean cyclic);

 * This function fills an area of connected cells in the map with a new value

<br></br>

    public Pixel2D[] shortestPath(Pixel2D p1, Pixel2D p2, int obsColor, boolean cyclic);

 * This function Computes the shortest valid path between p1 and p2, using the BFS algorithm.
 * A valid path between p1 and p2 is defined as an ordered set of pixels where each consecutive pixels in the path are neighbors in this map.
 * a path between p1 and p2 does NOT contain the absColor.
 * Two pixels are neighbors in the map, iff they are a single pixel apart (up,down, left, right).

<br></br>

    public Map2D allDistance(Pixel2D start, int obsColor, boolean cyclic);

* This function creates a map in which each cell contains a number that represents the distance from the start cell.
* To do so this function uses the BFS algorithm

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

