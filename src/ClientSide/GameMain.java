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
        Game pacman = new Game();                   //new Game(level);
        pacman.init();
//        pacman.init(GameInfo.CASE_SCENARIO, GameInfo.MY_ID, GameInfo.CYCLIC_MODE, GameInfo.RANDOM_SEED, GameInfo.RESOLUTION_NORM, GameInfo.DT, -1);
//        PacManAlgo man = GameInfo.ALGO;
        while(pacman.getStatus()!= IGame.DONE)
        {
            _cmd = pacman.getKeyChar();
            if (_cmd !=null)
            {
                if(_cmd == ' ') {pacman.play();}
                if(_cmd == 'q') {pacman.end();}

                if (_cmd == 'h')
                {
                    System.out.println("Pacman help: keys: ' '-start, 'w,a,s,d'-directions, all other parameters should be configured via GameInfo.java, ");
                }
            }
//            int  dir = man.move(pacman);
//            pacman.move(dir);
        }
//        pacman.end();
    }

    public static Character getCMD() {return _cmd;}
}
