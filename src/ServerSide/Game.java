package ServerSide;

public class Game implements IGame
{
    private static final int FLOOR_VALUE = 0;
    private static final int OBSTACLE_VALUE = 1;
    private static final int FRUIT_VALUE = 2;
    private static final int BIG_FRUIT_VALUE = 3;



    private int _gameStatus;
    private GameMap _map;
    private PointInt2D _playernPos;
    private IGhost _ghosts;


    public Game()
    {
        init();
    }

    public void init()
    {
        _gameStatus = INIT;
        _map = new GameMap();

        // ====================================================
//        enemyDelayTimer = 0;
//        playerPos = new PointInt2D(0, 0);
//        enemyPos = new PointInt2D(_map.getWidth() - 1, _map.getHeight() - 1);
//        for (int i = 0; i < _map.getWidth(); i++)
//        {
//            for (int j = 0; j < _map.getHeight(); j++)
//            {
//                if (_map.getPixel(i, j) == PLAYER_VALUE)
//                    playerPos = new PointInt2D(i, j);
//                else if (_map.getPixel(i, j) == ENEMY_VALUE)
//                    enemyPos = new PointInt2D(i, j);
//            }
//        }
//        targetPos = playerPos;
//        drawMap();
//        drawCharacter(playerPos, playerPos, PLAYER_VALUE);
//        drawCharacter(enemyPos, enemyPos, ENEMY_VALUE);
    }

    /// ============== private functions ==============
    private void createMap(int level)
    {
        if (level == 0)
        {
            int[][] mapArr = {
                    { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
                    { 1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1 },
                    { 1, 3, 1, 3, 1, 1, 1, 1, 1, 1, 3, 1, 3, 1, 1, 1, 1, 1, 1, 3, 1, 3, 1 },
                    { 1, 3, 1, 5, 1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1, 5, 1, 3, 1 },
                    { 1, 3, 1, 3, 1, 3, 1, 3, 1, 1, 1, 1, 1, 1, 1, 3, 1, 3, 1, 3, 1, 3, 1 },
                    { 1, 3, 1, 3, 1, 3, 1, 3, 3, 3, 3, 1, 3, 3, 3, 3, 1, 3, 1, 3, 1, 3, 1 },
                    { 1, 3, 3, 3, 3, 3, 1, 1, 1, 1, 3, 1, 3, 1, 1, 1, 1, 3, 3, 3, 3, 3, 1 },
                    { 1, 1, 1, 1, 1, 3, 1, 3, 3, 3, 3, 0, 3, 3, 3, 3, 1, 3, 1, 1, 1, 1, 1 },
                    { 1, 3, 3, 3, 3, 3, 1, 3, 1, 1, 1, 0, 1, 1, 1, 3, 1, 3, 3, 3, 3, 3, 1 },
                    { 1, 1, 3, 1, 1, 3, 1, 3, 1, 0, 0, 0, 0, 0, 1, 3, 1, 3, 1, 1, 3, 1, 1 },
                    { 3, 3, 3, 3, 3, 3, 3, 3, 1, 0, 0, 0, 0, 0, 1, 3, 3, 3, 3, 3, 3, 3, 3 },
                    { 1, 1, 3, 1, 1, 3, 1, 3, 1, 1, 1, 1, 1, 1, 1, 3, 1, 3, 1, 1, 3, 1, 1 },
                    { 3, 3, 3, 3, 3, 3, 1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1, 3, 3, 3, 3, 3, 3 },
                    { 1, 3, 1, 1, 1, 3, 1, 3, 1, 1, 1, 1, 1, 1, 1, 3, 1, 3, 1, 1, 1, 3, 1 },
                    { 1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1 },
                    { 1, 3, 1, 1, 1, 3, 1, 1, 1, 1, 3, 1, 3, 1, 1, 1, 1, 3, 1, 1, 1, 3, 1 },
                    { 1, 3, 3, 5, 1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1, 5, 3, 3, 1 },
                    { 1, 1, 1, 3, 1, 3, 1, 3, 1, 1, 1, 1, 1, 1, 1, 3, 1, 3, 1, 3, 1, 1, 1 },
                    { 1, 3, 3, 3, 3, 3, 1, 3, 3, 3, 3, 1, 3, 3, 3, 3, 1, 3, 3, 3, 3, 3, 1 },
                    { 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 3, 1, 3, 1, 1, 1, 1, 1, 1, 1, 1, 3, 1 },
                    { 1, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 1 },
                    { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }
            };
            _map.init(mapArr);
            _playernPos = new PointInt2D(11, 7);
//            enemyPos = new PointInt2D(map.getWidth() / 2, map.getHeight() - 1);
            _map.setCyclic(true);

        }
        else if (level == 1)
        {

        }
    }

    
}
