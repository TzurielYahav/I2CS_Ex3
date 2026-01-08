package ClientSide;

import ServerSide.Game;
import ServerSide.PacmanAlgo;

//import exe.ex3.game.Game;
//import exe.ex3.game.PacManAlgo;
//import exe.ex3.game.PacmanGame;

public class GameMain
{
    static void main(String[] args)
    {
        Game game = new Game(GameInfo._DT);
        PacmanAlgo man = new PacmanAutoAlgo();
        game.startGame(man);   //new Game(level);
    }
}
