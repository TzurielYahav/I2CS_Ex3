package ServerSide;

/**
 *
 */
public class GameMap
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
            throw new IllegalArgumentException("Pixel2D is not inside the map");
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
            throw new IllegalArgumentException("Pixel2D is not inside the map");
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
            throw new NullPointerException("Pixel2D p is null");
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
