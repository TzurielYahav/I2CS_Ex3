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
    private static Character _cmd;

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

            if (_cmd !=null)
            {
                System.out.println(" CMD  " + _cmd);
                if (_cmd == ' ')
                    game.play();
                if (_cmd == 'q') {
                    game.end(2);}
                if (_cmd == 'm')
                    man.changeGameMode();
                if (_cmd == 'h')
                    System.out.println("Pacman help: keys: ' '-start, 'w,a,s,d'-directions, all other parameters should be configured via GameInfo.java, ");
                if (_cmd >= '1' && _cmd <= '9')
                    game.changeLevel(Character.getNumericValue(_cmd));
            }
            game.move(man.move(game));
            StdDraw.pause(GameInfo._DT - 20);
        }
        System.out.println("Pacman finished.");
//        game.end();
    }

    public static Character getCMD() {return _cmd;}

    private void processInput(ArrayList<Character> characters, Game game, PacmanAlgo man)
    {
        for (Character character : characters)
        {
            if (character == ' ')
                game.play();
            if (character == 'q') {
                game.end(2);}
            if (character == 'm')
                man.changeGameMode();
            if (character == 'h')
                System.out.println("Pacman help: keys: ' '-start, 'w,a,s,d'-directions, all other parameters should be configured via GameInfo.java, ");
            if (character >= '1' && character <= '9')
                game.changeLevel(Character.getNumericValue(character));

            if (character == 'w') {_cmd = Game.UP;}
            if (character == 's') {_cmd = Game.DOWN;}
            if (character == 'a') {_cmd = Game.LEFT;}
            if (character == 'd') {_cmd = Game.RIGHT;}
        }
    }
}
