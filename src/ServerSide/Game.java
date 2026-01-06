package ServerSide;

public class Game implements IGame
{
    private int _gameStatus;
    private GameMap _map;

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
//        playerPos = new Index2D(0, 0);
//        enemyPos = new Index2D(_map.getWidth() - 1, _map.getHeight() - 1);
//        for (int i = 0; i < _map.getWidth(); i++)
//        {
//            for (int j = 0; j < _map.getHeight(); j++)
//            {
//                if (_map.getPixel(i, j) == PLAYER_VALUE)
//                    playerPos = new Index2D(i, j);
//                else if (_map.getPixel(i, j) == ENEMY_VALUE)
//                    enemyPos = new Index2D(i, j);
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
            int[][] mapArr = new int[23][22];
            _map.init(mapArr);
//            playerPos = new Index2D(0, 0);
//            enemyPos = new Index2D(map.getWidth() / 2, map.getHeight() - 1);
            _map._cyclicFlag = true;
            map.drawRect(new Index2D(0,10), new Index2D(4,13), OBSTACLE_VALUE);
            map.drawRect(new Index2D(25,10), new Index2D(21,13), OBSTACLE_VALUE);
            map.drawRect(new Index2D(1,1), new Index2D(10,2), OBSTACLE_VALUE);
            map.drawRect(new Index2D(15,1), new Index2D(24,2), OBSTACLE_VALUE);
            map.drawRect(new Index2D(6,3), new Index2D(7,5), OBSTACLE_VALUE);
            map.drawRect(new Index2D(18,3), new Index2D(19,5), OBSTACLE_VALUE);
            map.drawRect(new Index2D(12,1), new Index2D(13,3), OBSTACLE_VALUE);
            map.drawRect(new Index2D(9,4), new Index2D(16,5), OBSTACLE_VALUE);
            map.drawRect(new Index2D(0,4), new Index2D(1,5), OBSTACLE_VALUE);
            map.drawRect(new Index2D(24,4), new Index2D(25,5), OBSTACLE_VALUE);
            map.drawRect(new Index2D(3,4), new Index2D(4,8), OBSTACLE_VALUE);
            map.drawRect(new Index2D(21,4), new Index2D(22,8), OBSTACLE_VALUE);
            map.drawRect(new Index2D(1,7), new Index2D(4,8), OBSTACLE_VALUE);
            map.drawRect(new Index2D(21,7), new Index2D(24,8), OBSTACLE_VALUE);
            map.drawRect(new Index2D(6,7), new Index2D(10,8), OBSTACLE_VALUE);
            map.drawRect(new Index2D(15,7), new Index2D(19,8), OBSTACLE_VALUE);
            map.drawRect(new Index2D(12,7), new Index2D(13,9), OBSTACLE_VALUE);
            map.drawRect(new Index2D(9,10), new Index2D(16,11), OBSTACLE_VALUE);
            map.drawRect(new Index2D(6,10), new Index2D(7,13), OBSTACLE_VALUE);
            map.drawRect(new Index2D(18,10), new Index2D(19,13), OBSTACLE_VALUE);
            map.fill(playerPos, FRUIT_VALUE, cyclic);
        }
        else if (level == 1)
        {

        }
    }

    /**
     *
     */
    private class GameMap
    {
        private int[][] _map;
        private boolean _cyclicFlag = true;

        public GameMap()
        {
            this(1);
        }

        public GameMap(int w, int h, int v) {init(w,h,v);}

        /**
         * Constructs a square map (size*size) with values of 0.
         * @param size The size of the square map
         */
        public GameMap(int size)
        {
            this(size,size, 0);
        }

        public void init(int w, int h, int v)
        {
            if (w <= 0 || h <= 0)
                throw new IllegalArgumentException("Invalid width or height parameter");
            _map = new int[h][w];
            for (int i = 0; i < w; i++)
            {
                for (int j = 0; j < h; j++)
                {
                    _map[j][i] = v;
                }
            }
        }

        /**
         * Creates a new 2D int array with values from a given 2D array.
         *
         * @param arr a 2D int array.
         */
        public void init(int[][] arr)
        {
            _map = new int[arr.length][arr[0].length];
            for (int i = 0; i < arr.length; i++)
            {
                if (arr[i].length != arr[0].length)
                    throw new IllegalArgumentException("Invalid array length");
                _map[i] = arr[i].clone();
            }
        }

        public int[][] getMap()
        {
            int[][] ans = new int[_map.length][_map[0].length];
            for (int i = 0; i < ans.length; i++)
            {
                ans[i] = _map[i].clone();
            }
            return ans;
        }

        public int getWidth()
        {
            return _map[0].length;
        }

        public int getHeight()
        {
            return _map.length;
        }

        public int getPixel(int x, int y)
        {
            Pixel2D p = new Index2D(x,y);
            return getPixel(p);
        }

        public int getPixel(Pixel2D p)
        {
            p = pixelToBounds(p.getX(), p.getY());
            if (!isInside(p))
                throw new IllegalArgumentException("Pixel2D is not inside the map");
            return _map[p.getY()][p.getX()];
        }

        public void setPixel(int x, int y, int v)
        {
            if (x < 0 || y < 0 || x >= getWidth() || y >= getHeight())
                throw new IllegalArgumentException("x or y is not inside the map");
            _map[y][x] = v;
        }

        public void setPixel(Pixel2D p, int v)
        {
            if (!isInside(p))
                throw new IllegalArgumentException("Pixel2D is not inside the map");
            _map[p.getY()][p.getX()] = v;
        }

        public boolean isCyclic()
        {
            return _cyclicFlag;
        }

        public void setCyclic(boolean cy) {_cyclicFlag = cy;}
    }
}
