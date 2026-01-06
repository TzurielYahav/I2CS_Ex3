package ClientSide;

import ServerSide.Game;
import ServerSide.IGhost;

import java.util.ArrayList;

public class PacmanAlgo
{
    private boolean _manual = false;
    private final ArrayList<Pixel2D> _waypoints;
    private Pixel2D _pos;
    private Map _map;
    private IGhost[] _ghosts;
    private static final int CODE = 0;
    private static final int ESCAPE_DISTANCE = 6;
    private static final int CHASE_DISTANCE = 3;
    private static final double NO_TIME_TO_CHASE = 0.2;
    private static final int OBSTACLE_COLOR = 1;
    private static final int[] FRUIT_COLORS = {
            Game.FRUIT_VALUE,      // Fruit
            Game.BIG_FRUIT_VALUE       // Big Fruit
    };

    public PacmanAlgo()
    {
        _map = new Map();
        _waypoints = new ArrayList<>();
    }

    public int move(Game game)
    {
        if (_manual)
            return manualMove();
        return autoMove(game);

    }

    public void changeGameMode()
    {
        _manual = !_manual;
    }

    /// ======== private functions ========

    private int manualMove()
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

    private int autoMove(Game game)
    {
        _map = new Map();
        _map.init(game.getMap());
        _pos = pixelFromString(game.getPlayerPos());
        _ghosts = game.getGhosts();
        return pacmanMoveState();
    }

    private static Pixel2D pixelFromString(String s)
    {
        String[] split = s.split(",");
        if(split.length != 2)
            throw new IllegalArgumentException("Invalid coordinate");
        return new Index2D(Integer.parseInt(split[0]),Integer.parseInt(split[1]));
    }

    private int getDirection(Pixel2D dir)
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

    private boolean notInCenter(Pixel2D p)
    {
        return !(p.getX() >= 8 && p.getX() <= 14 && p.getY() >= 10 && p.getY() <= 13);
    }

    private boolean hasNoGhosts(Pixel2D p)
    {
        for (IGhost ghost : _ghosts)
        {
            if (p.distance2D(pixelFromString(ghost.getPos())) <= 1)
            {
                return false;
            }
        }
        int avalibleEscapeRoutes = 0;
        if (_map.getPixel(p.getX() + 1, p.getY()) != OBSTACLE_COLOR)
            avalibleEscapeRoutes++;
        if (_map.getPixel(p.getX() - 1, p.getY()) != OBSTACLE_COLOR)
            avalibleEscapeRoutes++;
        if (_map.getPixel(p.getX(), p.getY() + 1) != OBSTACLE_COLOR)
            avalibleEscapeRoutes++;
        if (_map.getPixel(p.getX(), p.getY() - 1) != OBSTACLE_COLOR)
            avalibleEscapeRoutes++;
        if (avalibleEscapeRoutes < 2)
            return false;
        return true;
    }

    private Pixel2D getGhostPos(int i)
    {
        return pixelFromString(_ghosts[i].getPos());
    }

    private Map dontEatBigFruits()
    {
        Pixel2D[] bigFruits = new Index2D[4];
        bigFruits[0] = new Index2D(3, 5);
        bigFruits[2] = new Index2D(19, 5);
        bigFruits[1] = new Index2D(3, 16);
        bigFruits[3] = new Index2D(19, 16);

        Map tmpMap = new Map(_map.getMap());
        for (Pixel2D bigFruit : bigFruits)
        {
            if (tmpMap.getPixel(bigFruit) == FRUIT_COLORS[1])
                tmpMap.setPixel(bigFruit, OBSTACLE_COLOR);
        }
        return tmpMap;
    }

    private boolean ghostTooFar(Map2D ghostDistances)
    {
        for(int i = 0; i < 4 && i < _waypoints.size(); i++)
        {
            if (ghostDistances.getPixel(_waypoints.get(i)) <= i + 1)
                return false;
        }
        return ghostDistances.getPixel(_pos) >= ESCAPE_DISTANCE;
    }

    private Pixel2D findLoneFruit(Map map)
    {
        Pixel2D[] sides = new Index2D[4];
        sides[0] = new Index2D(_pos.getX() + 1, _pos.getY());
        sides[2] = new Index2D(_pos.getX() - 1, _pos.getY());
        sides[1] = new Index2D(_pos.getX(), _pos.getY() - 1);
        sides[3] = new Index2D(_pos.getX(), _pos.getY() + 1);

        for(int i = 0; i < sides.length; i++)
        {
            if (map.getPixel(sides[i]) == FRUIT_COLORS[0])
            {
                if (isLoneFruit(map, sides[i]))
                    return sides[i];
            }
        }
        return null;
    }

    private boolean isLoneFruit(Map map, Pixel2D p)
    {
        Pixel2D[] sides = new Index2D[4];
        sides[0] = new Index2D(p.getX() + 1, p.getY());
        sides[2] = new Index2D(p.getX() - 1, p.getY());
        sides[1] = new Index2D(p.getX(), p.getY() - 1);
        sides[3] = new Index2D(p.getX(), p.getY() + 1);

        for(int i = 0; i < sides.length; i++)
        {
            if (map.getPixel(sides[i]) == FRUIT_COLORS[0])
            {
                return false;
            }
        }
        return true;
    }

    private static int[][] mapFlip(int[][] map)
    {
        int[][] flipped = new int[map[0].length][map.length];
        for (int y = 0; y < flipped.length; y++)
        {
            for (int x = 0; x < flipped[0].length; x++)
            {
                flipped[y][x] = map[x][y];
            }
        }
        return flipped;
    }

    /*//////// Move Functions ////////*/

    private void chaseFruit(boolean isChasing)
    {
        Map tmpMap;
        if (isChasing)
        {
            tmpMap = dontEatBigFruits();
        }
        else
        {
            tmpMap = new Map(_map.getMap());
            for (int i = 0; i < _ghosts.length; i++)
            {
                tmpMap.setPixel(getGhostPos(i), OBSTACLE_COLOR);
            }
        }
        Pixel2D loneFruit = findLoneFruit(tmpMap);
        if (loneFruit != null)
            _waypoints.add(loneFruit);
        else
        {
            _waypoints.clear();
            ArrayList<Pixel2D> newWay = tmpMap.shortestPath(_pos, FRUIT_COLORS, OBSTACLE_COLOR);
            if (newWay != null && !newWay.isEmpty())
            {
                _waypoints.addAll(newWay);
                _waypoints.removeFirst();
            }
        }
    }

    private int pacmanMoveState()
    {
        _waypoints.clear();
        boolean isChasing = _ghosts[0].remainTimeAsEatable() > NO_TIME_TO_CHASE;
        int direction = 0;
        if(isChasing)
            direction = chaseGhost();
        if (direction == 0)
        {
            chaseFruit(isChasing);
            if (isChasing)
                return getDirection(_waypoints.removeFirst());

            Map2D escapePlan = new Map(_map.getWidth(), _map.getHeight(), 0);
            boolean isEscaping = false;
            for (IGhost ghost : _ghosts)
            {
                if (ghost.getStatus() != 0)
                {
                    Pixel2D ghostPos = pixelFromString(ghost.getPos());
                    Map2D currentEscape = _map.allDistance(ghostPos, OBSTACLE_COLOR);
                    if (!ghostTooFar(currentEscape))
                    {
                        isEscaping = true;
                        escapePlan.addMap2D(currentEscape);
                    }
                }
            }
            if (isEscaping)
            {
                direction = escapeGhosts(escapePlan);
                return direction;
            }
            else
            {
                if (!_waypoints.isEmpty())
                    direction = getDirection(_waypoints.removeFirst());
                return direction;
            }
        }
        return direction;
    }

    private int escapeGhosts(Map2D escapePlan)
    {
        Pixel2D target = null;
        if (!_waypoints.isEmpty())
            target = _waypoints.removeFirst();

        int bestRoute = 0;
        int direction = 0;

        Pixel2D[] sides = new Index2D[4];
        sides[0] = new Index2D(_pos.getX(), _pos.getY() + 1); // the UP neighbor of the current cell
        sides[1] = new Index2D(_pos.getX(), _pos.getY() - 1); // the DOWN neighbor of the current cell
        sides[2] = new Index2D(_pos.getX() - 1, _pos.getY()); // the LEFT neighbor of the current cell
        sides[3] = new Index2D(_pos.getX() + 1, _pos.getY()); // the RIGHT neighbor of the current cell

        if (target != null && hasNoGhosts(target))
        {
            direction = getDirection(target);
        }
        else
        {
            for (Pixel2D side : sides)
            {
                if (bestRoute < escapePlan.getPixel(side) && hasNoGhosts(side) && notInCenter(side))
                {
                    bestRoute = escapePlan.getPixel(side);
                    direction = getDirection(side);
                }
            }
        }

        return direction;
    }

    private int chaseGhost()
    {
        double closestDistance = Double.MAX_VALUE;
        int closestGhost = -1;
        for (int i = 0; i < _ghosts.length; i++)
        {
            double newClosestDistance = pixelFromString(_ghosts[i].getPos()).distance2D(_pos);
            if (closestDistance > newClosestDistance && newClosestDistance < CHASE_DISTANCE && notInCenter(getGhostPos(i)))
            {
                closestDistance = newClosestDistance;
                closestGhost = i;
            }
        }
        if (closestGhost == -1)
            return 0;

        Map tmpMap = dontEatBigFruits();
        _waypoints.clear();
        _waypoints.addAll(tmpMap.shortestPath(_pos, pixelFromString(_ghosts[closestGhost].getPos()), OBSTACLE_COLOR));
        _waypoints.removeFirst();
        if (!_waypoints.isEmpty())
        {
            return getDirection(_waypoints.removeFirst());
        }
        return 0;
    }

}
