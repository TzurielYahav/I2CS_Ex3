package ClientSide;

import ServerSide.Game;
import ServerSide.IGame;
import ServerSide.StdDraw;

import java.util.ArrayList;

//import exe.ex3.game.Game;
//import exe.ex3.game.PacManAlgo;
//import exe.ex3.game.PacmanGame;

public class GameMain
{
    static void main(String[] args) {
        play();
    }

    public static void play()
    {
        Game game = new Game(GameInfo._DT);
        game.startGame();   //new Game(level);
    }
}
