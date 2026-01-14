package Ex3;

import exe.ex3.game.GhostCL;
import exe.ex3.game.PacManAlgo;
import exe.ex3.game.PacmanGame;

import java.util.ArrayList;

/**
 * This is the major algorithmic class for Ex3 - the PacMan game:
 * Level 4 - 100
 * 316560499, 4, 12487, 0, 353, 1767640452092, 10, -915428924, -25315164343659
 * Level 4 - 200
 * 316560499, 4, 6480, 0, 360, 1767640553415, 4, -915428924, -1369122087090259
 * 316560499, 4, 8360, 0, 480, 1768414756562, 6, -915428924, 2530446365895044
 * 316560499, 4, 8360, 0, 480, 1768415198128, 6, -915428924, 545015599511205
 * This code is a very simple example (random-walk algorithm).
 * Your task is to implement (here) your PacMan algorithm.
 */
public class Ex3Algo implements PacManAlgo
{
    private final ArrayList<Pixel2D> _waypoints = new ArrayList<>();
    private Pixel2D _pos;
    private Map _map;
    private GhostCL[] _ghosts;
    private static final int CODE = 0;
    private final int ESCAPE_DISTANCE = 6;
    private final int CHASE_DISTANCE = 3;
    private final double NO_TIME_TO_CHASE = 0.2;
    private static final int OBSTACLE_COLOR = 1;
    private static final int[] FRUIT_COLORS = {
            3,      // Fruit
            5       // Big Fruit
    };

    public Ex3Algo()
    {
        _map = new Map();
    }

    public void init(PacmanGame game, String pos)
    {
        _map.init(mapFlip(game.getGame(0)));    // flip the map because my code is built so that length is width and opposite
        _pos = pixelFromString(pos);
        _ghosts = game.getGhosts(CODE);
        _waypoints.clear();
    }

	/**
	 *  A short description for the algorithm as a String.
	 */
    @Override
	public String getInfo()
    {
		return """
                The algorithm for Pacman is:
                
                 1. Check if Pacman is chasing the ghosts.
                 1a. If true find the closest ghost within the chase distance and chase it.
                 1b. If true but there are no ghosts to chase, go to the nearest fruit avoiding big fruits.
                 2. Otherwise we are eatable, find the way to the nearest fruit avoiding the ghosts.
                 3. Check if there is a close enough ghost that we should escape it.
                 3a. If true escape it preferring to escape towards the closest fruit.
                 3b. Otherwise go to the nearest fruit.""";
	}

	/**
	 * This is the main method.
     * the method order is:
     * 1. check if we are chasing the ghosts
     * 2a. if there is a ghost close enough chase it
     * 2b. otherwise go to the nearest fruit
     * 3. if we are eatable check if we should run away
     * 4a. if true escape
     * 4b. otherwise go to the nearest fruit
	 */
    @Override
	public int move(PacmanGame game)
    {
        _map.init(mapFlip(game.getGame(0)));    // flip the map because my code is built so that length is width and opposite
        _pos = pixelFromString(game.getPos(CODE));
        _ghosts = game.getGhosts(CODE);
        _waypoints.clear();

        boolean isChasing = _ghosts[0].remainTimeAsEatable(CODE) > NO_TIME_TO_CHASE;    // if we are chasing
        int direction = 0;
        if(isChasing)                   // if chasing search for a ghost to chase
            direction = chaseGhost();
        if (direction == 0)             // we are not chasing or didn't find a ghost to chase
        {
            chaseFruit(isChasing);      // find the way to the nearest fruit avoiding ghosts
            if (isChasing)              // if chasing go to the nearest fruit
                return getDirection(_waypoints.removeFirst());

            Map2D escapePlan = new Map(_map.getWidth(), _map.getHeight(), 0);
            boolean isEscaping = false;
            for (GhostCL ghost : _ghosts)
            {
                if (ghost.getStatus() != 0)     // if the ghost is active
                {
                    Pixel2D ghostPos = pixelFromString(ghost.getPos(CODE));
                    Map2D currentEscape = _map.allDistance(ghostPos, OBSTACLE_COLOR);   // the alldistance map from the ghost
                    if (!ghostTooFar(currentEscape))                                    // if the ghost is NOT too far from us to run
                    {
                        isEscaping = true;
                        escapePlan.addMap2D(currentEscape);                             // add the alldistance to the master plan
                    }
                }
            }
            if (isEscaping)     // if escape a close ghost
            {
                direction = escapeGhosts(escapePlan);
                return direction;
            }
            else                // no ghosts to run from, go to the nearest fruit
            {
                if (!_waypoints.isEmpty())
                    direction = getDirection(_waypoints.removeFirst());
            }
        }
        return direction;
	}

    private Pixel2D getGhostPos(int i)
    {
        return pixelFromString(_ghosts[i].getPos(CODE));
    }

    /*//////// Move Functions ////////*/

    /**
     * finds the shortest way to the nearest fruit.
     * 1. if chasing go around big fruits
     * 2. otherwise go around ghosts
     * 3. if there is a lone fruit go for it
     * 4. otherwise go to the nearest fruit
     * @param isChasing if we are chasing
     */
    public void chaseFruit(boolean isChasing)
    {
        Map tmpMap;
        if (isChasing)  // if in chasing mode, modify the map so that we dont eat the big fruits (its a waste)
        {
            tmpMap = dontEatBigFruits();
        }
        else            // otherwise modify the map so that we calculate a way around the ghosts
        {
            tmpMap = new Map(_map.getMap());
            for (int i = 0; i < _ghosts.length; i++)
            {
                tmpMap.setPixel(getGhostPos(i), OBSTACLE_COLOR);
            }
        }
        Pixel2D loneFruit = findLoneFruit(tmpMap);  // if there is a lone fruit around us go for it
        if (loneFruit != null)
            _waypoints.add(loneFruit);
        else                                        // otherwise find the shortest path to the nearest fruit with BFS
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

    /**
     * find the best tile to escape to.
     * if already on the way to a fruit, make it our target route,
     * 1. if the target is a valid escape tile go to it.
     * 2. otherwise find the tile that takes us furthest from the ghosts
     * @param escapePlan the alldistances map from the too close ghosts
     * @return the direction to escape to
     */
    public int escapeGhosts(Map2D escapePlan)
    {
        Pixel2D target = null;
        if (!_waypoints.isEmpty())              // if we have waypoints then we are on the way to a fruit, make it our target
            target = _waypoints.removeFirst();

        int bestRoute = 0;
        int direction = 0;

        Pixel2D[] sides = new Index2D[4];
        sides[0] = new Index2D(_pos.getX(), _pos.getY() + 1); // the UP neighbor of the current cell
        sides[1] = new Index2D(_pos.getX(), _pos.getY() - 1); // the DOWN neighbor of the current cell
        sides[2] = new Index2D(_pos.getX() - 1, _pos.getY()); // the LEFT neighbor of the current cell
        sides[3] = new Index2D(_pos.getX() + 1, _pos.getY()); // the RIGHT neighbor of the current cell

        if (target != null && isValidEscape(target) && isSafe(target))    // if on the way to a fruit check if we can escape there
        {
            direction = getDirection(target);
        }
        else
        {
            for (Pixel2D side : sides)                  // otherwise find the best direction to escape
            {
                if (bestRoute < escapePlan.getPixel(side) && isValidEscape(side) && notInCenter(side))  // check if this dir is further from the ghosts than the last, AND if it's a valid escape AND not in the center
                {
                    if (isSafe(side) || direction == 0)
                    {
                        bestRoute = escapePlan.getPixel(side);
                        direction = getDirection(side);
                    }
                }
            }
        }
        return direction;
    }

    /**
     * find the shortest way to the closest ghost, if its within chase distance
     * 1. search for the closest ghost and check if its within chase distance
     * 2. find the shortest way to that ghost
     * @return the direction to go to, if there is none then 0.
     */
    public int chaseGhost()
    {
        double closestDistance = Double.MAX_VALUE;
        int closestGhost = -1;
        for (int i = 0; i < _ghosts.length; i++)
        {
            double newClosestDistance = pixelFromString(_ghosts[i].getPos(CODE)).distance2D(_pos);  // check the distance to the ghost
            if (closestDistance > newClosestDistance && newClosestDistance < CHASE_DISTANCE && notInCenter(getGhostPos(i)))
            {       // check if the new distance is closer than the last ghost, AND if this distance is smaller than the max chase distance
                closestDistance = newClosestDistance;
                closestGhost = i;
            }
        }
        if (closestGhost == -1) // if we didn't find a ghost to chase
            return 0;

        Map tmpMap = dontEatBigFruits();
        _waypoints.clear();
        _waypoints.addAll(tmpMap.shortestPath(_pos, pixelFromString(_ghosts[closestGhost].getPos(CODE)), OBSTACLE_COLOR));  // find the shortest way to the closest ghost
        _waypoints.removeFirst();
        if (!_waypoints.isEmpty())
        {
            return getDirection(_waypoints.removeFirst());
        }
        return 0;
    }

    /// ///////////////////////////////////////////////////////////

    /**
     * edits a map to put an obstacle mark on every big fruit so that pacman will not eat them
     * @return The new edited map
     */
    private Map dontEatBigFruits()
    {
        Pixel2D[] bigFruits = new Index2D[4];   // the indexes of the big fruits on the map
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

    /**
     * checks if a ghost is too far so we dont have to worry about it.
     * 1. if we have a way to the nearest fruit check that the 4 tile ahead of us dont have a ghost within reach
     * 2. check thet the ghost is not too close to us
     * @param ghostDistances the alldistance map to use for checking
     * @return if the ghost is too far from us
     */
    private boolean ghostTooFar(Map2D ghostDistances)
    {
        for(int i = 0; i < 4 && i < _waypoints.size(); i++)
        {
            if (ghostDistances.getPixel(_waypoints.get(i)) <= i + 1)    // check if the ghost can reach the tile ahead of us before we do
                return false;
        }
        return ghostDistances.getPixel(_pos) >= ESCAPE_DISTANCE;
    }

    /**
     * find if there is a lone fruit around us so that we don't leave fruit scattered all over (it wastes time)
     * @param map The map to check
     * @return The point of the lone fruit, if there is none -> null
     */
    private Pixel2D findLoneFruit(Map map)
    {
        Pixel2D[] sides = new Index2D[4];
        sides[0] = new Index2D(_pos.getX() + 1, _pos.getY());
        sides[2] = new Index2D(_pos.getX() - 1, _pos.getY());
        sides[1] = new Index2D(_pos.getX(), _pos.getY() - 1);
        sides[3] = new Index2D(_pos.getX(), _pos.getY() + 1);

        for(int i = 0; i < sides.length; i++)
        {
            if (map.getPixel(sides[i]) == FRUIT_COLORS[0])  // if this is a fruit
            {
                if (isLoneFruit(map, sides[i])) // check if it's a lone fruit
                    return sides[i];
            }
        }
        return null;
    }

    /**
     * gets a position and a map and checks if there are no fruits around this position
     * @param map the map to use
     * @param p the position to check
     * @return if this is a lone fruit
     */
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

    public static Pixel2D pixelFromString(String s)
    {
        String[] split = s.split(",");
        if(split.length != 2)
            throw new IllegalArgumentException("Invalid coordinate");
        return new Index2D(Integer.parseInt(split[0]),Integer.parseInt(split[1]));
    }

    /**
     * find the direction between the current position and the given point
     * @param dir The point to find direction to
     * @return The direction to the point
     */
    public int getDirection(Pixel2D dir)
    {
        if (dir.getX() != _pos.getX())
        {
            if (dir.getX() > _pos.getX())
                if (dir.getX() == _pos.getX() + 1)
                    return PacmanGame.RIGHT;
                else
                    return PacmanGame.LEFT;
            else
            if (dir.getX() + 1 == _pos.getX())
                return PacmanGame.LEFT;
            else
                return PacmanGame.RIGHT;
        }
        else if (dir.getY() != _pos.getY())
        {
            if (dir.getY() > _pos.getY())
                if (dir.getY() == _pos.getY() + 1)
                    return PacmanGame.UP;
                else
                    return PacmanGame.DOWN;
            else
            if (dir.getY() + 1 == _pos.getY())
                return PacmanGame.DOWN;
            else
                return PacmanGame.UP;
        }
        else
            return 0;
    }

    /**
     * Check that this point is not inside the ghost house
     * @param p The point to check
     * @return if the point is in the house
     */
    private boolean notInCenter(Pixel2D p)
    {
        return !(p.getX() >= 8 && p.getX() <= 14 && p.getY() >= 10 && p.getY() <= 13);
    }

    /**
     * checks if this direction is a valid direction to escape to,
     * 1. checks if there are no ghosts within 2 tiles of the point
     * 2. checks that this is not a closed tile (has only one way out)
     * @param p The point to check
     * @return If this is a valid escape tile
     */
    private boolean isValidEscape(Pixel2D p)
    {
        for (GhostCL ghost : _ghosts)
        {
            if (p.distance2D(pixelFromString(ghost.getPos(CODE))) <= 1) // check the distance to the ghost
            {
                return false;
            }
        }
        int avalibleEscapeRoutes = 0;                                   // count how many neighbors of this tile are not walls
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

    private boolean isSafe(Pixel2D p)
    {
        for (GhostCL ghost : _ghosts)
        {
            if (p.distance2D(pixelFromString(ghost.getPos(CODE))) <= 2) // check the distance to the ghost
            {
                return false;
            }
        }
        return true;
    }

    public void setPixel(Pixel2D p, int v)
    {
        _map.setPixel(p, v);
    }

    public Pixel2D getWaypoint()
    {
        if (!_waypoints.isEmpty())
            return _waypoints.getFirst();
        else
            return null;
    }

    public void clearWaypoints()
    {
        _waypoints.clear();
    }

    public void setPos(String pos)
    {
        _pos = pixelFromString(pos);
    }

    public Map2D getEscapePlan()
    {
        Map2D escapePlan = new Map(_map.getWidth(), _map.getHeight(), 0);
        for (GhostCL ghost : _ghosts)
        {
            if (ghost.getStatus() != 0)     // if the ghost is active
            {
                Pixel2D ghostPos = pixelFromString(ghost.getPos(CODE));
                Map2D currentEscape = _map.allDistance(ghostPos, OBSTACLE_COLOR);   // the alldistance map from the ghost
                if (!ghostTooFar(currentEscape))                                    // if the ghost is NOT too far from us to run
                {
                    escapePlan.addMap2D(currentEscape);                             // add the alldistance to the master plan
                }
            }
        }
        return escapePlan;
    }
}