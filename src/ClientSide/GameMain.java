package ClientSide;

import ServerSide.Game;
import ServerSide.IGame;

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
//            System.out.println(game.getStatus());
            _cmd = game.getKeyChar();
            if (_cmd !=null)
            {
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
        }
        System.out.println("Pacman finished.");
//        game.end();
    }

    public static Character getCMD() {return _cmd;}
}
