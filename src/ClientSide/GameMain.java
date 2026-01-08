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
    private static int _cmd;

    static void main(String[] args) {
        play();
    }

    public static void play()
    {
        Game game = new Game(GameInfo._DT);
        game.startGame();   //new Game(level);
//        game.init(GameInfo.CASE_SCENARIO, GameInfo.MY_ID, GameInfo.CYCLIC_MODE, GameInfo.RANDOM_SEED, GameInfo.RESOLUTION_NORM, GameInfo.DT, -1);

    }

    public static int getCMD()
    {
        int cmd = _cmd;
        _cmd = 0;
        return cmd;
    }
}
