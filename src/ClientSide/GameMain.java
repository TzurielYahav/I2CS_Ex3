package ClientSide;

import ServerSide.Game;
import ServerSide.PacmanAlgo;
import ServerSide.StdDraw;

public class GameMain
{
    static void main(String[] args)
    {
        Game game = new Game(GameInfo._DT);
        PacmanAlgo man = new PacmanAutoAlgo();
        StdDraw.enableDoubleBuffering();
        game.startGame(man);   //new Game(level);
    }
}
