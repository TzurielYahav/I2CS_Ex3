package ServerSide;

import java.awt.*;

public class Game implements IGame
{
    private static final String[] images = {
            "player.png"
    };
    private static final int FLOOR_VALUE = 0;
    private static final int OBSTACLE_VALUE = 1;
    private static final int FRUIT_VALUE = 2;
    private static final int BIG_FRUIT_VALUE = 3;
    private static final Color[] COLORS = new Color[4];
    private static final int INFO_Y_HEIGHT = 50;
    private static final int CANVAS_WIDTH = 800;
    private static final int CANVAS_HEIGHT = CANVAS_WIDTH + INFO_Y_HEIGHT;

    private int _gameStatus;
    private long _gameStartTime;
    private long _lastFrameTime = 0;
    private int _dt;
    private double Y_Scale;
    private double X_Scale;
    private GameMap _map;
    private PointInt2D _playerPos;
    private int _playerDirection;
    private IGhost _ghosts;


    public Game()
    {
        init(50);
    }

    public Game(int dt)
    {
        init(dt);
    }

    public void init(int dt)
    {
        _gameStatus = INIT;
        _dt = dt;
        _map = new GameMap();
        createLevel(1);

        // ====== Canvas Init ======
        StdDraw.setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

        // ====== COLORS Init ======
        COLORS[FLOOR_VALUE] = StdDraw.BLACK;
        COLORS[OBSTACLE_VALUE] = StdDraw.BLUE;
        COLORS[FRUIT_VALUE] = StdDraw.PINK;
        COLORS[BIG_FRUIT_VALUE] = StdDraw.GREEN;

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

    public int getStatus()
    {
        return _gameStatus;
    }

    public Character getKeyChar()
    {
        if (StdDraw.hasNextKeyTyped())
            return StdDraw.nextKeyTyped();
        return null;
    }

    public void play()
    {
        _gameStatus = IGame.PLAY;
        _gameStartTime = System.currentTimeMillis();
        _lastFrameTime = System.currentTimeMillis();
        DrawGame();

    }

    public void end()
    {
        _gameStatus = IGame.DONE;
    }

    public void move(int direction)
    {
        long currentTimeMillis = System.currentTimeMillis();
        long elapsedFrameTimeMillis = currentTimeMillis - _lastFrameTime;
        if (elapsedFrameTimeMillis < _dt)
        {
            StdDraw.pause((int) (_dt - elapsedFrameTimeMillis));
        }
        _lastFrameTime = currentTimeMillis;

        // ========= Player ========
        movePlayer(direction);
    }

    /// ============== private functions ==============

    private void movePlayer(int direction)
    {
        if (direction > 4 || direction < 0)
            direction = 0;
        if (direction != 0)
        {
            PointInt2D[] directions = {
                    new PointInt2D(),
                    new PointInt2D(_playerPos.getX(), _playerPos.getY() + 1),
                    new PointInt2D(_playerPos.getX() - 1, _playerPos.getY()),
                    new PointInt2D(_playerPos.getX(), _playerPos.getY() - 1),
                    new PointInt2D(_playerPos.getX() + 1, _playerPos.getY()),
            };
            PointInt2D newPos = _map.pixelToBounds(directions[direction]);
            if (_map.isInside(newPos))
                drawPlayer(_map.pixelToBounds(newPos), direction);
        }
    }

    private void DrawGame()
    {
        double infoHeight = (double) INFO_Y_HEIGHT / (((double) CANVAS_HEIGHT - (double) INFO_Y_HEIGHT) / Y_Scale);
        StdDraw.setXscale(0,X_Scale);
        StdDraw.setYscale(0,Y_Scale + infoHeight);
        StdDraw.clear();
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.text(X_Scale / 2, Y_Scale + infoHeight / 2, "'WASD' for movement  |  '1-4' to switch levels  |  'Q' to stop and save");
        drawMap();
        drawPlayer(_playerPos, _playerDirection);
    }

    private void drawMap()
    {
        for (int y = 0; y < _map.getHeight(); y++)
        {
            for (int x = 0; x < _map.getWidth(); x++)
            {
                drawCell(new PointInt2D(x, y));
            }
        }
    }

    private void drawCell(PointInt2D pos)
    {
        double cellScale = X_Scale / (_map.getWidth() * 2);
        double fruitScale = X_Scale / (_map.getWidth() * 7);
        double bigFruitScale = X_Scale / (_map.getWidth() * 4);
        double x = pos.getX() + cellScale;
        double y = pos.getY() + cellScale;

        StdDraw.setPenColor(COLORS[FLOOR_VALUE]);
        StdDraw.filledRectangle(x, y, cellScale, cellScale);

        StdDraw.setPenColor(COLORS[_map.getPixel(pos)]);
        if (_map.getPixel(pos) == OBSTACLE_VALUE)
            StdDraw.filledRectangle(x, y, cellScale, cellScale);
        else if (_map.getPixel(pos) == FRUIT_VALUE)
            StdDraw.filledCircle(x, y, fruitScale);
        else if (_map.getPixel(pos) == BIG_FRUIT_VALUE)
            StdDraw.filledCircle(x, y, bigFruitScale);
    }

    private void drawPlayer(PointInt2D pos, int direction)
    {
        double cellScale = X_Scale / (_map.getWidth() * 2);
        double imageScale = X_Scale / (_map.getWidth() * 1.5);

        drawCell(new PointInt2D(_playerPos.getX(), _playerPos.getY()));
        if (_map.getPixel(pos) != OBSTACLE_VALUE)
            _playerPos = pos;

        StdDraw.picture(_playerPos.getX() + cellScale, _playerPos.getY() + cellScale, images[0], imageScale, imageScale, direction * 90);
    }

    private void createLevel(int level)
    {
        if (level == 1)
        {
            int[][] mapArr = {
                    { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 },
                    { 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1 },
                    { 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 2, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1 },
                    { 1, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 1, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 1 },
                    { 1, 1, 1, 2, 1, 2, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1, 2, 1, 2, 1, 1, 1 },
                    { 1, 2, 2, 3, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 3, 2, 2, 1 },
                    { 1, 2, 1, 1, 1, 2, 1, 1, 1, 1, 2, 1, 2, 1, 1, 1, 1, 2, 1, 1, 1, 2, 1 },
                    { 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1 },
                    { 1, 2, 1, 1, 1, 2, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1, 2, 1, 1, 1, 2, 1 },
                    { 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2 },
                    { 1, 1, 2, 1, 1, 2, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1, 2, 1, 1, 2, 1, 1 },
                    { 2, 2, 2, 2, 2, 2, 2, 2, 1, 0, 0, 0, 0, 0, 1, 2, 2, 2, 2, 2, 2, 2, 2 },
                    { 1, 1, 2, 1, 1, 2, 1, 2, 1, 0, 0, 0, 0, 0, 1, 2, 1, 2, 1, 1, 2, 1, 1 },
                    { 1, 2, 2, 2, 2, 2, 1, 2, 1, 1, 1, 0, 1, 1, 1, 2, 1, 2, 2, 2, 2, 2, 1 },
                    { 1, 1, 1, 1, 1, 2, 1, 2, 2, 2, 2, 0, 2, 2, 2, 2, 1, 2, 1, 1, 1, 1, 1 },
                    { 1, 2, 2, 2, 2, 2, 1, 1, 1, 1, 2, 1, 2, 1, 1, 1, 1, 2, 2, 2, 2, 2, 1 },
                    { 1, 2, 1, 2, 1, 2, 1, 2, 2, 2, 2, 1, 2, 2, 2, 2, 1, 2, 1, 2, 1, 2, 1 },
                    { 1, 2, 1, 2, 1, 2, 1, 2, 1, 1, 1, 1, 1, 1, 1, 2, 1, 2, 1, 2, 1, 2, 1 },
                    { 1, 2, 1, 3, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 3, 1, 2, 1 },
                    { 1, 2, 1, 2, 1, 1, 1, 1, 1, 1, 2, 1, 2, 1, 1, 1, 1, 1, 1, 2, 1, 2, 1 },
                    { 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 1 },
                    { 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1 }
            };
            _map.init(mapArr);
            _playerPos = new PointInt2D(11, 14);
            _playerDirection = 4;
//            enemyPos = new PointInt2D(map.getWidth() / 2, map.getHeight() - 1);
            _map.setCyclic(true);

        }
        else if (level == 2)
        {

        }
        Y_Scale = _map.getHeight();
        X_Scale = _map.getWidth();
    }

}
