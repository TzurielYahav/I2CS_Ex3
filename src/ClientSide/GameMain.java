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
        Game game = new Game(GameInfo._DT);                   //new Game(level);
//        game.init(GameInfo.CASE_SCENARIO, GameInfo.MY_ID, GameInfo.CYCLIC_MODE, GameInfo.RANDOM_SEED, GameInfo.RESOLUTION_NORM, GameInfo.DT, -1);
        PacmanAlgo man = new PacmanAlgo();
        while(game.getStatus()!= IGame.DONE)
        {
            processInput(game, man);
            game.move(man.move(game));
            StdDraw.pause(180);
        }
        System.out.println("Pacman finished.");
//        game.end();
    }

    public static int getCMD()
    {
        int cmd = _cmd;
        _cmd = 0;
        return cmd;
    }

    private static void processInput(Game game, PacmanAlgo man)
    {
        while (StdDraw.hasNextKeyTyped())
        {
            char character = StdDraw.nextKeyTyped();
            if (character == ' ')
            {
                game.play();
                return;
            }
            if (character == 'q')
                game.end(2);
            if (character == 'm')
                man.changeGameMode();
            if (character == 'h')
                System.out.println("Pacman help: keys: ' '-start, 'w,a,s,d'-directions, all other parameters should be configured via GameInfo.java, ");
            if (character >= '1' && character <= '9')
            {
                game.changeLevel(Character.getNumericValue(character));
                return;
            }
            if (_cmd == 0)
            {
                if (character == 'w') {_cmd = Game.UP;}
                if (character == 's') {_cmd = Game.DOWN;}
                if (character == 'a') {_cmd = Game.LEFT;}
                if (character == 'd') {_cmd = Game.RIGHT;}
            }
        }

    }
}
