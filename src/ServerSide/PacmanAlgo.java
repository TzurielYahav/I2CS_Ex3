package ServerSide;


public interface PacmanAlgo
{
    /**
     * A function responsible for calculating the direction to which Pacman should go in the current frame of the game based on the game variable.
     * @param game The game class that holds all the information of the game
     * @return The direction to which Pacman should go where (Up = 1, Left = 2, Down = 3, Right = 4)
     */
    int move(Game game);
}
