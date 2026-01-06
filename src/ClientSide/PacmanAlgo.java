package ClientSide;

import ServerSide.Game;

public class PacmanAlgo 
{
    public static int move(Game game)
    {
        return manualMove();
    }
    
    public static int manualMove() 
    {
        int ans = Game.ERR;
        Character cmd = GameMain.getCMD();
        if (cmd != null) {
            if (cmd == 'w') {ans = Game.UP;}
            if (cmd == 's') {ans = Game.DOWN;}
            if (cmd == 'a') {ans = Game.LEFT;}
            if (cmd == 'd') {ans = Game.RIGHT;}
        }
        return ans;
    }
}
