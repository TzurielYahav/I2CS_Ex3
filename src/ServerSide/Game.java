package ServerSide;

import java.util.ArrayList;

public class Game implements IGame
{
    private PointInt2D SP_TOP_LEFT;
    private PointInt2D SP_TOP_RIGHT;
    private PointInt2D SP_BOTTOM_LEFT;
    private PointInt2D SP_BOTTOM_RIGHT;
    private int _gameStatus;
    private int _score;
    private long _gameStartTime;
    private long _lastFrameTime = 0;
    private final int _ghostStartScatterTime = 20000;
    private final int _ghostEndScatterTime = 4000;
    private long _ghostScatterCounter = 0;
    private int _dt;
    private double Y_Scale;
    private double X_Scale;
    private GameMap _map;
    private PointInt2D _playerPos;
    private PointInt2D _playerLastPos;
    private int _playerDirection;
    private int _playerDelayCount = 0;
    private Ghost[] _ghosts;


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
        // ====== Vars Init ======
        _gameStatus = INIT;
        _dt = dt;
        _map = new GameMap();
        COLORS[FLOOR_VALUE] = StdDraw.BLACK;
        COLORS[OBSTACLE_VALUE] = StdDraw.BLUE;
        COLORS[FRUIT_VALUE] = StdDraw.PINK;
        COLORS[BIG_FRUIT_VALUE] = StdDraw.GREEN;

        createLevel(1);

        // ====== Canvas Init ======
        StdDraw.setCanvasSize(CANVAS_WIDTH, CANVAS_HEIGHT);

        // =======================================
        DrawGame();
    }

    public int getStatus()
    {
        return _gameStatus;
    }

    public int[][] getMap()
    {
        return _map.getMap();
    }

    public Character getKeyChar()
    {
        if (StdDraw.hasNextKeyTyped())
            return StdDraw.nextKeyTyped();
        return null;
    }

    public String getPlayerPos()
    {
        return _playerPos.toString();
    }

    public IGhost[] getGhosts()
    {
        IGhost[] ghosts = new IGhost[_ghosts.length];
        System.arraycopy(_ghosts, 0, ghosts, 0, _ghosts.length);
        return ghosts;
    }

    public void play()
    {
        _gameStatus = IGame.PLAY;
        _gameStartTime = System.currentTimeMillis();
        _lastFrameTime = System.currentTimeMillis();
        ghostSetScatter();
    }

    public void end(int status)
    {
        _gameStatus = IGame.DONE;
    }

    public void move(int direction)
    {
        if (_gameStatus != IGame.PLAY)
            return;
        long currentTimeMillis = System.currentTimeMillis();
        long elapsedFrameTimeMillis = (currentTimeMillis - _lastFrameTime) / _dt;

        if (elapsedFrameTimeMillis > 1)
        {
            _lastFrameTime = currentTimeMillis;
            // ========= Ghost ========
            moveGhosts();
            if (Ghost._delay == IGhost.MOVE_DELAY)
                Ghost.resetDelay();
            else
                Ghost.addDelayCount();

            // ========= Player ========
            movePlayer(direction);



            // ========= Check victory ========
            drawGhosts();
            drawPlayer();
            drawInfo();
        }
    }

    /// ============== private functions ==============

    private void moveGhosts()
    {
        int _ghostStartTime = 1000;
        if (_ghosts[0]._status == INIT && System.currentTimeMillis() - _gameStartTime > _ghostStartTime)
            for (Ghost ghost : _ghosts) ghost.setActive();
        if (_ghosts[0]._status == INIT)
            return;
        ghostCheckScatter();
        boolean hasEatenPlayer = false;
        for (Ghost ghost : _ghosts)
        {
            ghost.move();
            if (ghost._pos.equals(_playerPos))
            {
                if (ghost.isEatable())
                {
                    if (ghost._status != IGhost.FLEE)
                    {
                        ghost.setFleeing();
                        _score += GHOST_SCORE;
                    }
                }
                else
                    hasEatenPlayer = true;
            }
        }
        if (hasEatenPlayer)
            end(0);
    }
    
    private void movePlayer(int direction)
    {
        if (_playerDelayCount != PLAYER_MOVE_DELAY)
        {
            _playerDelayCount++;
            return;
        }
        else
            _playerDelayCount = 0;
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
            if (_map.isInside(newPos) && _map.isInside(newPos))
            {
                _playerLastPos = new PointInt2D(_playerPos);
                _playerPos = newPos;
                _playerDirection = direction;
            }
        }
        hasEatenFruit();
    }

    private void hasEatenFruit()
    {
        if (_map.getPixel(_playerPos) == FRUIT_VALUE)
        {
            _map.setPixel(_playerPos, FLOOR_VALUE);
            _score += FRUIT_SCORE;
            checkVictory();
        }
        else if (_map.getPixel(_playerPos) == BIG_FRUIT_VALUE)
        {
            _map.setPixel(_playerPos, FLOOR_VALUE);
            _score += BIG_FRUIT_SCORE;
            canEatGhosts();
            checkVictory();
        }
    }

    private void canEatGhosts()
    {
        for (Ghost ghost : _ghosts) ghost.setEatable();
    }

    private void checkVictory()
    {
        if (!_map.hasColors(new int[] {FRUIT_VALUE, BIG_FRUIT_VALUE}))
        {
            end(1);
        }
    }

    private void ghostSetScatter()
    {
        _ghostScatterCounter = System.currentTimeMillis();
        for (Ghost ghost : _ghosts)
            ghost.setScatter();
    }

    private void ghostSetActive()
    {
        _ghostScatterCounter = System.currentTimeMillis();
        for (Ghost ghost : _ghosts)
            ghost.setActive();
    }

    private void ghostCheckScatter()
    {
        if (_ghosts[0]._status == IGhost.SCATTER)
        {
            if (System.currentTimeMillis() > _ghostScatterCounter + _ghostEndScatterTime)
                ghostSetActive();
        }
        else
        {
            if (System.currentTimeMillis() > _ghostScatterCounter + _ghostStartScatterTime)
                ghostSetScatter();
        }
    }

    private void DrawGame()
    {
        double infoHeight = (double) INFO_Y_HEIGHT / (((double) CANVAS_HEIGHT - (double) INFO_Y_HEIGHT) / Y_Scale);
        StdDraw.setXscale(0,X_Scale);
        StdDraw.setYscale(0,Y_Scale + infoHeight);
        StdDraw.clear();
        drawMap();
        drawInfo();
        drawGhosts();
        drawPlayer();
    }

    private void drawInfo()
    {
        double infoHeight = (double) INFO_Y_HEIGHT / (((double) CANVAS_HEIGHT - (double) INFO_Y_HEIGHT) / Y_Scale);
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.filledRectangle(X_Scale / 2, Y_Scale + infoHeight / 2, X_Scale / 2, infoHeight / 2);
        StdDraw.setPenColor(StdDraw.WHITE);
        StdDraw.setPenRadius(0.1);
//        StdDraw.text(X_Scale / 2, Y_Scale + infoHeight / 4, "'WASD' for movement  |  '1-4' to switch levels  |  'm' to switch to manual  |  'Q' to stop and save");
        StdDraw.text(X_Scale / 2, Y_Scale + infoHeight / 4, "Pink  " + _ghosts[0]._status + "  |  Red  " + _ghosts[1]._status + "  |  Blue  " + _ghosts[2]._status + "  |  Orange  " + _ghosts[3]._status);
        StdDraw.text(X_Scale / 2, Y_Scale + infoHeight * 3 / 4, "SCORE  " + _score + "  |  Eatable Time  " + _ghosts[0].remainTimeAsEatable());
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

    private void drawPlayer()
    {
        double cellScale = X_Scale / (_map.getWidth() * 2);
        double imageScale = X_Scale / (_map.getWidth() * 1.5);

        drawCell(new PointInt2D(_playerLastPos.getX(), _playerLastPos.getY()));
        StdDraw.picture(_playerPos.getX() + cellScale, _playerPos.getY() + cellScale, PLAYER_IMG, imageScale, imageScale, _playerDirection * 90);
    }

    private void drawGhosts()
    {
        for (Ghost ghost : _ghosts)
        {
            drawCell(ghost.getPrevPos());
        }
        for (Ghost ghost : _ghosts)
        {
            double cellScale = X_Scale / (_map.getWidth() * 2);
            double imageScale = X_Scale / (_map.getWidth() * 1.5);
            String image;
            if (ghost._status == IGhost.FLEE)
                image = ghostImages[1];
            else if (ghost._status == IGhost.CONFUSED)
                image = ghostImages[0];
            else
                image = ghostImages[(ghost._type + 2) + ((Math.max(0,ghost._dir - 1)) * IGhost.AMOUNT_OF_TYPES)];
            StdDraw.picture(ghost.getPointPos().getX() + cellScale, ghost.getPointPos().getY() + cellScale, image, imageScale, imageScale);
        }
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
            SP_TOP_LEFT = new PointInt2D(0, _map.getHeight());
            SP_TOP_RIGHT = new PointInt2D(_map.getWidth() - 1,_map.getHeight());
            SP_BOTTOM_LEFT = new PointInt2D(0,-1);
            SP_BOTTOM_RIGHT = new PointInt2D(_map.getWidth() - 1,-1);
            _playerPos = new PointInt2D(11, 14);
            _playerLastPos = new PointInt2D(11, 14);
            _playerDirection = 4;
            _ghosts = new Ghost[] {
                    new PinkGhost(11,11),
                    new RedGhost(11,11),
                    new BlueGhost(11,11),
                    new OrangeGhost(11,11),
                    new SmartGhost(11,11),
            };
            _map.setCyclic(true);

        }
        else if (level == 2)
        {

        }
        Y_Scale = _map.getHeight();
        X_Scale = _map.getWidth();
    }


    /**
     * =========== GAMEMAP ============
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
            PointInt2D p = new PointInt2D(x,y);
            return getPixel(p);
        }

        public int getPixel(PointInt2D p)
        {
            p = pixelToBounds(p.getX(), p.getY());
            if (!isInside(p))
                throw new IllegalArgumentException("PointInt2D is not inside the map");
            return _map[p.getY()][p.getX()];
        }

        public void setPixel(int x, int y, int v)
        {
            if (x < 0 || y < 0 || x >= getWidth() || y >= getHeight())
                throw new IllegalArgumentException("x or y is not inside the map");
            _map[y][x] = v;
        }

        public void setPixel(PointInt2D p, int v)
        {
            if (!isInside(p))
                throw new IllegalArgumentException("PointInt2D is not inside the map");
            _map[p.getY()][p.getX()] = v;
        }

        public boolean isCyclic()
        {
            return _cyclicFlag;
        }

        public void setCyclic(boolean cy) {_cyclicFlag = cy;}

        public boolean isInside(PointInt2D p)
        {
            if (p == null)
                throw new NullPointerException("PointInt2D p is null");
            if (p.getX() >= 0 && p.getY() >= 0)
                return p.getX() < _map[0].length && p.getY() < _map.length;
            return false;
        }

        public void drawRect(PointInt2D p1, PointInt2D p2, int color)
        {
            if (!isInside(p1) || !isInside(p2))
                throw new RuntimeException("p1 or p2 is out of range");
            int x1 = Math.min(p1.getX(), p2.getX());    // the x coord of the first corner
            int x2 = Math.max(p1.getX(), p2.getX());    // the x coord of the last corner
            int y1 = Math.min(p1.getY(), p2.getY());    // the y coord of the first corner
            int y2 = Math.max(p1.getY(), p2.getY());    // the y coord of the last corner
            for (int i = x1; i <= x2; i++)
            {
                for (int j = y1; j <= y2; j++)
                {
                    _map[j][i] = color;
                }
            }
        }

        public boolean hasColors(int[] colors)
        {
            for (int j = 0; j < _map.length; j++)
            {
                for (int i = 0; i < _map[0].length; i++)
                {
                    for (int color : colors)
                        if (_map[j][i] == color)
                            return true;
                }
            }
            return false;
        }

        /**
         * Fills this map with the new color (new_v) starting from p.
         * <a href="https://en.wikipedia.org/wiki/Flood_fill">...</a>
         * This function calls the recursive function 'fillPixels'
         * that fills each cell and then moves to check its neighbors
         * @param xy The pos to start filling from
         * @param new_v The new value to fill
         */
        public int fill(PointInt2D xy, int new_v)
        {
            if (!isInside(xy))
                throw new RuntimeException("xy is out of range");
            int ans = 0;
            int oldColor = _map[xy.getY()][xy.getX()];  // the old color to search for when filling
            if (oldColor != new_v)                      // if old color == new color we have nothing to do
            {
                ans = fillPixels(xy, oldColor, new_v);  // calls a recursive function that fills the tiles needed
            }
            return ans;
        }

        public ArrayList<PointInt2D> shortestPath(PointInt2D p1, PointInt2D p2, int obsColor)
        {
            if (!isInside(p1) || !isInside(p2))
                throw new RuntimeException("p1 or p2 is out of range");
            if (getPixel(p1) == obsColor || getPixel(p2) == obsColor)
                throw new RuntimeException("p1 or p2 is unreachable");

            ArrayList<PointInt2D> ansQueue = new ArrayList<PointInt2D>();
            if (p1.equals(p2))
            {
                ansQueue.add(p1);
                ansQueue.add(p2);
                return ansQueue;
            }

            boolean[][] exploreMap = new boolean[_map.length][_map[0].length];  // a map representing all the tiles that were explored

            ArrayList<PointInt2D> exploreQueue = new ArrayList<PointInt2D>();         // the queue of explored tiles and to be explored tiles
            ArrayList<PointInt2D> parentQueue = new ArrayList<PointInt2D>();          // the queue holding the parent for each explored tile (parentQ[i] == parent(exploreQ[i]))

            exploreQueue.add(p1);                                               // add the starting tile to the ExQ
            parentQueue.add(p1);                                                // add the starting tile as the parent of itself
            exploreMap[p1.getY()][p1.getX()] = true;                            // add the starting tile to the explored tiles map

            int currentPixel = 0;                                               // the current index in the explore queue
            boolean found = false;                                              // if we found the target pixel (p2)
            while (!found && currentPixel < exploreQueue.size())                // while we didn't find && there are still elements to explore in the explore queue
            {
                PointInt2D p = exploreQueue.get(currentPixel);                     // get the current cell to explore
                PointInt2D[] childrenCoords = new PointInt2D[4];
                childrenCoords[0] = pixelToBounds(p.getX(), p.getY() + 1); // the UP neighbor of the current cell
                childrenCoords[1] = pixelToBounds(p.getX(), p.getY() - 1); // the DOWN neighbor of the current cell
                childrenCoords[2] = pixelToBounds(p.getX() - 1, p.getY()); // the LEFT neighbor of the current cell
                childrenCoords[3] = pixelToBounds(p.getX() + 1, p.getY()); // the RIGHT neighbor of the current cell

                for (int i = 0; i < 4; i++)                                     // for each neighbor cell
                {
                    if (isInside(childrenCoords[i]) && getPixel(childrenCoords[i]) != obsColor && !exploreMap[childrenCoords[i].getY()][childrenCoords[i].getX()])
                    {                                                           // check if the cell is (inside && not obstacle && not explored yet)
                        exploreMap[childrenCoords[i].getY()][childrenCoords[i].getX()] = true;  // add the cell to the explored cells map
                        exploreQueue.add(childrenCoords[i]);                                    // add the cell to the explore queue
                        parentQueue.add(p);                                                     // add the current cell (p) as the parent of this cell (neighbor)
                        if (childrenCoords[i].equals(p2))                                       // check if this cell is the target (p2)
                        {
                            found = true;
                            break;
                        }
                    }
                }
                currentPixel++;                                                                 // move to the next cell in the explore queue
            }
            if (!found)
                return null;

            // ======== Build the waypoint array ========
            ansQueue.add(exploreQueue.getLast());
            ansQueue.addFirst(parentQueue.getLast());
            for (int i = exploreQueue.size() - 1; i > 0; i--)                                   // a loop that goes back though the explore list and finds the parent of each cell in the shortest path
            {
                if (exploreQueue.get(i).equals(ansQueue.getFirst()))                             // for each cell in answer queue starting from the target find its parent
                {
                    ansQueue.addFirst(parentQueue.get(i));                                           // add the parent to the answer queue
                }
            }
            return ansQueue;
        }
        
        /**
         * Checks if the x,y pos is in the map, if not and is cyclic moves it to the other side
         * @param p The point to move to bounds
         * @return The corrected pos or (-1,y) | (x,-1) for the part that is out of bounds
         */
        public PointInt2D pixelToBounds(PointInt2D p)
        {
            if (_cyclicFlag)
            {
                return new PointInt2D((p.getX() + _map[0].length) % _map[0].length, (p.getY() + _map.length) % _map.length);
            }
            else
            {
                return new PointInt2D(p.getX(), p.getY());
            }
        }

        /// ============== private functions ==============

        /**
         * recursively checks if the current cell is valid to fill,
         * if true fills it and moves to check its neighbors
         * @param p1 The pos of the cell to start from
         * @param oldColor The old color to search for
         * @param color The new color to fill
         * @return The number of cells filled
         */
        private int fillPixels(PointInt2D p1, int oldColor, int color)
        {
            int ans = 0;

            PointInt2D currentP = pixelToBounds(p1.getX(), p1.getY());    // checks if p1 is in the map (if not and is cyclic then moves the point to the other side)
            if (isInside(currentP) && getPixel(currentP) == oldColor)       // checks if the new corrected point is inside the map and if the color is oldColor
            {
                ans = 1;                                                    // add this cell to the number of cells filled
                setPixel(currentP, color);                                  // fill this cell
                ans += fillPixels(new PointInt2D(currentP.getX() + 1, currentP.getY()), oldColor, color);  // move to the right neighbor
                ans += fillPixels(new PointInt2D(currentP.getX(), currentP.getY() + 1), oldColor, color);  // move to the up neighbor
                ans += fillPixels(new PointInt2D(currentP.getX() - 1, currentP.getY()), oldColor, color);  // move to the left neighbor
                ans += fillPixels(new PointInt2D(currentP.getX(), currentP.getY() - 1), oldColor, color);  // move to the down neighbor
            }
            return ans;
        }

        /**
         * Checks if the x,y pos is in the map, if not and is cyclic moves it to the other side
         * @param x The x pos
         * @param y The y pos
         * @return The corrected pos or (-1,y) | (x,-1) for the part that is out of bounds
         */
        private PointInt2D pixelToBounds(int x, int y)
        {
            if (_cyclicFlag)
            {
                return new PointInt2D((x + _map[0].length) % _map[0].length, (y + _map.length) % _map.length);
            }
            else
            {
                return new PointInt2D(x, y);
            }
        }
    }

    /**
     * ============ GHOST ============
     */
    private class Ghost implements IGhost
    {
        private PointInt2D _pos;
        private PointInt2D _prevPos;
        private PointInt2D _scatterPos;
        private final int _type;
        private long _lastGreen = 0;
        private int _status;
        private int _dir = 1;
        private static int _delay = 0;
        private boolean _didTurn180 = false;

        Ghost(PointInt2D pos, int type)
        {
            this(pos.getX(), pos.getY(), type);
        }

        Ghost(int x, int y, int type)
        {
            _pos = new PointInt2D(x, y);
            _prevPos = new PointInt2D(x,y);
            _type = type;
            _status = INIT;
            if (_type == TYPE_STUPID || _type == TYPE_PINK)
                _scatterPos = SP_TOP_LEFT;
            else if (_type == TYPE_RED || _type == TYPE_SMART)
                _scatterPos = SP_TOP_RIGHT;
            else if (_type == TYPE_ORANGE || _type == TYPE_TERMINATOR)
                _scatterPos = SP_BOTTOM_LEFT;
            else
                _scatterPos = SP_BOTTOM_RIGHT;
        }

        Ghost(Ghost ghost)
        {
            this(ghost.getPointPos(), ghost.getType());
        }

        public String getPos()
        {
            return _pos.toString();
        }

        public PointInt2D getPointPos()
        {
            return new PointInt2D(_pos);
        }

        public PointInt2D getPrevPos()
        {
            return new PointInt2D(_prevPos);
        }

        public int getDirection()
        {
            return _dir;
        }

        public int getType()
        {
            return _type;
        }

        public int getStatus()
        {
            return _status;
        }

        public PointInt2D getScatterPos()
        {
            return _scatterPos;
        }

        public double remainTimeAsEatable()
        {
            double difference = (double)(System.currentTimeMillis() - _lastGreen);
            return (EATABLE_TIMEOUT - difference) / 1000;
        }

        public boolean isEatable()
        {
            return remainTimeAsEatable() > 0;
        }

        public void setEatable()
        {
            _lastGreen = System.currentTimeMillis();
            setConfused();
        }

        public void setActive()
        {
            _status = PLAY;
            _didTurn180 = false;
        }

        public void setFleeing()
        {
            _status = FLEE;
            _didTurn180 = false;
        }

        public void setConfused()
        {
            if (_status == PLAY || _status == INIT)
            {
                _status = CONFUSED;
                _didTurn180 = false;
            }
        }

        public void setScatter()
        {
            _status = SCATTER;
            _didTurn180 = false;
        }

        public static void resetDelay()
        {
            _delay = 0;
        }

        public static void addDelayCount()
        {
            _delay++;
        }

        public void move()
        {
            if (_status == FLEE)
                moveToBase();
            else if (_status == CONFUSED)
            {
                if (isEatable())
                    moveConfused();
                else
                    setActive();
            }
            else if (_status == SCATTER)
            {
                if (getPointPos().distance2D(BASE) <= 1)
                    moveToDirection(coloredGhostMove(OUT_OF_GATE));
                else
                    moveToDirection(coloredGhostMove(_scatterPos));
            }
        }

        private void moveToDirection(int direction)
        {
            if (direction != 0)
            {
                PointInt2D[] directions = {
                        new PointInt2D(),
                        new PointInt2D(_pos.getX(), _pos.getY() + 1),
                        new PointInt2D(_pos.getX() - 1, _pos.getY()),
                        new PointInt2D(_pos.getX(), _pos.getY() - 1),
                        new PointInt2D(_pos.getX() + 1, _pos.getY()),
                };
                PointInt2D newPos = _map.pixelToBounds(directions[direction]);
                if (_map.isInside(newPos) && _map.getPixel(newPos) != OBSTACLE_VALUE)
                {
                    _dir = direction;
                    _prevPos = new PointInt2D(_pos);
                    _pos = newPos;
                }
            }
        }

        public void moveToBase()
        {
            if (_didTurn180)
            {
                PointInt2D target = BASE;
                double distToBase = Double.MAX_VALUE;
                PointInt2D[] directions = {
                        new PointInt2D(),
                        new PointInt2D(_pos.getX(), _pos.getY() + 1),
                        new PointInt2D(_pos.getX() - 1, _pos.getY()),
                        new PointInt2D(_pos.getX(), _pos.getY() - 1),
                        new PointInt2D(_pos.getX() + 1, _pos.getY()),
                };
                for (int i = 1; i < 5; i++)
                {
                    if (i % 4 != (_dir + 2) % 4)
                    {
                        PointInt2D p = _map.pixelToBounds(directions[i]);
                        if (_map.getPixel(p) != OBSTACLE_VALUE && p.distance2D(BASE) < distToBase)
                        {
                            target = p;
                            distToBase = p.distance2D(BASE);
                        }
                    }
                }
                if (target.equals(_pos))
                    moveToDirection(turn180());
                else
                    moveToDirection(getDirectionFromPoint(target));
            }
            else
                moveToDirection(turn180());
            if (_pos.equals(BASE))
                setActive();
        }

        public void moveConfused()
        {
            if (_delay == MOVE_DELAY)
            {
                if (!_didTurn180)
                {
                    moveToDirection(turn180());
                    return;
                }
                PointInt2D target = new PointInt2D(_pos);
                double bestOption = Double.MAX_VALUE;
                double distToPlayer = _pos.distance2D(_playerPos);
                PointInt2D[] directions = {
                        new PointInt2D(),
                        new PointInt2D(_pos.getX(), _pos.getY() + 1),
                        new PointInt2D(_pos.getX() - 1, _pos.getY()),
                        new PointInt2D(_pos.getX(), _pos.getY() - 1),
                        new PointInt2D(_pos.getX() + 1, _pos.getY()),
                };
                for (int i = 1; i < 5; i++)
                {
                    if (i % 4 != (_dir + 2) % 4)
                    {
                        PointInt2D p = _map.pixelToBounds(directions[i]);
                        double rand =  Math.random();
                        if (_map.getPixel(p) != OBSTACLE_VALUE && rand < bestOption)
                        {
                            if ((distToPlayer <= 2 && p.distance2D(_playerPos) >= 2) || distToPlayer > 2)
                            {
                                target = p;
                                bestOption = rand;
                            }
                        }
                    }
                }
                if (target.equals(_pos))
                    moveToDirection(turn180());
                else
                    moveToDirection(getDirectionFromPoint(target));
            }
        }

        public int coloredGhostMove(PointInt2D aimPoint)
        {
            if (_delay == MOVE_DELAY)
            {
                if (!_didTurn180)
                    return turn180();

                PointInt2D target = new PointInt2D(_pos);
                double distToPlayer = Double.MAX_VALUE;
                PointInt2D[] directions = {
                        new PointInt2D(),
                        new PointInt2D(_pos.getX(), _pos.getY() + 1),
                        new PointInt2D(_pos.getX() - 1, _pos.getY()),
                        new PointInt2D(_pos.getX(), _pos.getY() - 1),
                        new PointInt2D(_pos.getX() + 1, _pos.getY()),
                };
                for (int i = 1; i < 5; i++)
                {
                    if (i % 4 != (_dir + 2) % 4)
                    {
                        PointInt2D p = _map.pixelToBounds(directions[i]);
                        if (_map.getPixel(p) != OBSTACLE_VALUE && p.distance2D(aimPoint) < distToPlayer)
                        {
                            target = p;
                            distToPlayer = p.distance2D(aimPoint);
                        }
                    }
                }
                return getDirectionFromPoint(target);
            }
            return 0;
        }

        private int getDirectionFromPoint(PointInt2D dir)
        {
            if (dir.getX() > _pos.getX())
            {
                if (dir.getX() - 1 > _pos.getX())
                    return exe.ex3.game.Game.LEFT;
                else
                    return exe.ex3.game.Game.RIGHT;
            }
            else if (dir.getX() < _pos.getX())
            {
                if (dir.getX() + 1 < _pos.getX())
                    return exe.ex3.game.Game.RIGHT;
                else
                    return exe.ex3.game.Game.LEFT;
            }
            else if (dir.getY() > _pos.getY())
            {
                if (dir.getY() - 1 > _pos.getY())
                    return exe.ex3.game.Game.DOWN;
                else
                    return exe.ex3.game.Game.UP;
            }
            else if (dir.getY() < _pos.getY())
            {
                if (dir.getY() + 1 < _pos.getY())
                    return exe.ex3.game.Game.UP;
                else
                    return exe.ex3.game.Game.DOWN;
            }
            else
                return 0;
        }

        private int turn180()
        {
            _didTurn180 = true;
            int dir = (_dir + 2) % 4;
            if (dir == 0)
                dir = 4;
            return dir;
        }
    }

    private class StupidGhost extends Ghost
    {
        StupidGhost(int x, int y)
        {
            super(x, y, TYPE_STUPID);
        }

        @Override
        public void move()
        {
            super.move();
            if (getStatus() == PLAY)
            {
                if (getPointPos().distance2D(BASE) <= 1)
                    super.moveToDirection(coloredGhostMove(OUT_OF_GATE));
                else
                    moveConfused();
            }
        }
    }

    private class SmartGhost extends Ghost
    {
        SmartGhost(int x, int y)
        {
            super(x, y, TYPE_SMART);
        }

        @Override
        public void move()
        {
            super.move();
            if (super._status == PLAY)
                super.moveToDirection(followPlayer());
        }

        private int followPlayer()
        {
            if (super._delay == 1)
            {
                ArrayList<PointInt2D> waypoints = _map.shortestPath(super._pos, _playerPos, OBSTACLE_VALUE);

                if (waypoints != null)
                {
                    waypoints.removeFirst();
                    return super.getDirectionFromPoint(waypoints.getFirst());
                }
                return 0;
            }
            return 0;
        }
    }

    private class RedGhost extends Ghost
    {
        RedGhost(int x, int y)
        {
            super(x, y, TYPE_RED);
        }

        @Override
        public void move()
        {
            super.move();
            if (getStatus() == PLAY)
            {
                if (getPointPos().distance2D(BASE) <= 1)
                    super.moveToDirection(coloredGhostMove(OUT_OF_GATE));
                else
                    super.moveToDirection(redAlgo());
            }
        }

        private int redAlgo()
        {
            return coloredGhostMove(_playerPos);
        }
    }

    private class BlueGhost extends Ghost
    {
        BlueGhost(int x, int y)
        {
            super(x, y, TYPE_BLUE);
        }

        @Override
        public void move()
        {
            super.move();
            if (getStatus() == PLAY)
            {
                if (getPointPos().distance2D(BASE) <= 1)
                    super.moveToDirection(coloredGhostMove(OUT_OF_GATE));
                else
                    super.moveToDirection(blueAlgo());
            }
        }

        private int blueAlgo()
        {
            return coloredGhostMove(findAimPoint());
        }

        private PointInt2D findAimPoint()
        {
            PointInt2D aimPoint;
            if (_playerDirection == 1)
                aimPoint = new PointInt2D(_playerPos.getX(), _playerPos.getY() + 2);
            else if (_playerDirection == 2)
                aimPoint = new PointInt2D(_playerPos.getX() - 2, _playerPos.getY());
            else if (_playerDirection == 3)
                aimPoint = new PointInt2D(_playerPos.getX(), _playerPos.getY() - 2);
            else if (_playerDirection == 4)
                aimPoint = new PointInt2D(_playerPos.getX() + 2, _playerPos.getY());
            else
                aimPoint = new PointInt2D(_playerPos);

            double angleToRed = 0;
            double distToRed = 0;
            for (Ghost ghost : _ghosts)
            {
                if (ghost._type == TYPE_RED)
                {
                    distToRed = aimPoint.distance2D(ghost.getPointPos());
                    angleToRed = aimPoint.angle2D(ghost.getPointPos()) + 180;
                }
            }
            if (distToRed > 0)
                aimPoint = new PointInt2D((int)(distToRed * Math.sin(angleToRed)), (int)(distToRed * Math.cos(angleToRed)));
            return aimPoint;
        }
    }

    private class PinkGhost extends Ghost
    {
        PinkGhost(int x, int y)
        {
            super(x, y, TYPE_PINK);
        }

        @Override
        public void move()
        {
            super.move();
            if (getStatus() == PLAY)
            {
                if (getPointPos().distance2D(BASE) <= 1)
                    super.moveToDirection(coloredGhostMove(OUT_OF_GATE));
                else
                    super.moveToDirection(pinkAlgo());
            }
        }

        private int pinkAlgo()
        {
            return coloredGhostMove(findAimPoint());
        }

        private PointInt2D findAimPoint()
        {
            if (_playerDirection == 1)
                return new PointInt2D(_playerPos.getX(), _playerPos.getY() + 4);
            else if (_playerDirection == 2)
                return new PointInt2D(_playerPos.getX() - 4, _playerPos.getY());
            else if (_playerDirection == 3)
                return new PointInt2D(_playerPos.getX(), _playerPos.getY() - 4);
            else if (_playerDirection == 4)
                return new PointInt2D(_playerPos.getX() + 4, _playerPos.getY());
            else
                return new PointInt2D(_playerPos);
        }
    }

    private class OrangeGhost extends Ghost
    {
        OrangeGhost(int x, int y)
        {
            super(x, y, TYPE_ORANGE);
        }

        @Override
        public void move()
        {
            super.move();
            if (getStatus() == PLAY)
            {
                if (getPointPos().distance2D(BASE) <= 1)
                    super.moveToDirection(coloredGhostMove(OUT_OF_GATE));
                else
                    super.moveToDirection(orangeAlgo());
            }
        }

        private int orangeAlgo()
        {
            return coloredGhostMove(findAimPoint());
        }

        private PointInt2D findAimPoint()
        {
            if (getPointPos().distance2D(_playerPos) > 8)
                return _playerPos;
            return getScatterPos();
        }
    }

    private class TerminatorGhost extends Ghost
    {
        TerminatorGhost(int x, int y)
        {
            super(x, y, TYPE_TERMINATOR);
        }

        @Override
        public void move()
        {
            super.move();
            if (super._status == PLAY)
                super.moveToDirection(followPlayer());
        }

        private int followPlayer()
        {
            ArrayList<PointInt2D> waypoints = _map.shortestPath(super._pos, _playerPos, OBSTACLE_VALUE);

            if (waypoints != null)
            {
                waypoints.removeFirst();
                return super.getDirectionFromPoint(waypoints.getFirst());
            }
            return 0;
        }
    }
}
