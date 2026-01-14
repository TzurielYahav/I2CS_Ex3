package Ex3;

import exe.ex3.game.Game;
import exe.ex3.game.PacmanGame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class Ex3AlgoTest
{
    private final int[][] _map_3x3_1 = {{0,1,0}, {1,0,1}, {0,1,0}};
    private final int[][] _map_4x4_1 = {{0,1,0,0}, {0,1,0,0}, {0,1,1,0}, {0,0,0,0}};
    private final int[][] _map_4x4_2 = {{0,0,0,0}, {0,0,0,0}, {0,0,0,0}, {0,0,0,0}};
    private final int[][] _map_4x4_3 = {{0,1,0,0}, {3,1,0,0}, {2,1,1,1}, {2,4,10,6}};
    private Ex3Algo _man;

    @BeforeEach
    public void setup()
    {
        PacmanGame _game = new Game();//new Game(level);
        _game.init(GameInfo.CASE_SCENARIO, GameInfo.MY_ID, GameInfo.CYCLIC_MODE, GameInfo.RANDOM_SEED, GameInfo.RESOLUTION_NORM, GameInfo.DT, -1);
        _man = new Ex3Algo();
        _man.init(_game, "0,9");
    }

    @Test
    void testPixelFromString()
    {
        assertEquals(new Index2D(0,0), Ex3Algo.pixelFromString("0,0"));
        assertEquals(new Index2D(0,1), Ex3Algo.pixelFromString("0,1"));
    }

    @Test
    void testChaseFruit()
    {
        _man.setPixel(new Index2D(0,9), 0);
        _man.setPixel(new Index2D(2,9), 0);
        _man.setPixel(new Index2D(1,8), 0);
        _man.clearWaypoints();
        _man.chaseFruit(false);
        assertEquals(4, _man.getDirection(_man.getWaypoint()));

        _man.setPixel(new Index2D(1,9), 0);
        _man.clearWaypoints();
        _man.chaseFruit(false);
        assertEquals(2, _man.getDirection(_man.getWaypoint()));

        _man.setPixel(new Index2D(1,9), 3);
        _man.setPixel(new Index2D(22,9), 0);
        _man.clearWaypoints();
        _man.chaseFruit(false);
        assertEquals(4, _man.getDirection(_man.getWaypoint()));

    }

    @Test
    void testEscapeGhosts()
    {
        _man.setPos("11,13");
        assertEquals(0, _man.escapeGhosts(_man.getEscapePlan()));

        _man.setPos("11,14");
        _man.chaseFruit(false);
        assertEquals(2, _man.escapeGhosts(_man.getEscapePlan()));

        _man.setPos("11,9");
        _man.setPixel(new Index2D(11,9), 0);
        _man.setPixel(new Index2D(10,9), 0);

        _man.clearWaypoints();
        _man.chaseFruit(false);
        assertEquals(4, _man.escapeGhosts(_man.getEscapePlan()));
    }

    @Test
    void testChaseGhosts()
    {
        _man.setPos("11,13");
        assertEquals(0, _man.chaseGhost());

        _man.setPos("11,14");
        _man.chaseFruit(false);
        assertEquals(0, _man.chaseGhost());

        _man.setPos("11,9");
        _man.setPixel(new Index2D(11,9), 0);
        _man.setPixel(new Index2D(10,9), 0);

        _man.clearWaypoints();
        _man.chaseFruit(false);
        assertEquals(0, _man.chaseGhost());
    }

}
