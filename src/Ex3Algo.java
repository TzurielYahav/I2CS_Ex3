import exe.ex3.game.Game;
import exe.ex3.game.GhostCL;
import exe.ex3.game.PacManAlgo;
import exe.ex3.game.PacmanGame;

import java.awt.*;
import java.util.ArrayList;

/**
 * This is the major algorithmic class for Ex3 - the PacMan game:
 * Level 4 - 100
 * 316560499, 4, 12487, 0, 353, 1767640452092, 10, -915428924, -25315164343659
 * Level 4 - 200
 * 316560499, 4, 6480, 0, 360, 1767640553415, 4, -915428924, -1369122087090259
 * This code is a very simple example (random-walk algorithm).
 * Your task is to implement (here) your PacMan algorithm.
 */
public class Ex3Algo implements PacManAlgo
{
	private int _count;
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
        _count = 0;
        _map = new Map();
    }

	/**
	 *  Add a short description for the algorithm as a String.
	 */
    @Override
	public String getInfo() {
		return null;
	} // TO DO

	/**
	 * This ia the main method - that you should design, implement and test.
	 */
    @Override
	public int move(PacmanGame game)
    {
        _map.init(mapFlip(game.getGame(0)));
        _pos = pixelFromString(game.getPos(CODE));
        _ghosts = game.getGhosts(CODE);
        //
		if(_count == 0 || _count == 300)
        {
			int[][] board = mapFlip(game.getGame(0));
			printBoard(board);
			int blue = Game.getIntColor(Color.BLUE, CODE);
			int pink = Game.getIntColor(Color.PINK, CODE);
			int black = Game.getIntColor(Color.BLACK, CODE);
			int green = Game.getIntColor(Color.GREEN, CODE);
			System.out.println("Blue=" + blue + ", Pink=" + pink + ", Black=" + black + ", Green=" + green);
			String pos = game.getPos(CODE).toString();
			System.out.println("Pacman coordinate: "+pos);
			_ghosts = game.getGhosts(CODE);
			printGhosts(_ghosts);
			int up = Game.UP, left = Game.LEFT, down = Game.DOWN, right = Game.RIGHT;
		}
		_count++;
        return pacmanMoveState();
	}

	private static void printBoard(int[][] b)
    {
		for(int y = 0; y < b.length; y++)
        {
			for(int x = 0; x < b[0].length; x++)
            {
				int v = b[y][x];
				System.out.print(v+"\t");
			}
			System.out.println();
		}
	}

	private static void printGhosts(GhostCL[] gs)
    {
		for(int i=0;i<gs.length;i++)
        {
			GhostCL g = gs[i];
			System.out.println(i+") status: "+g.getStatus()+",  type: "+g.getType()+",  pos: "+g.getPos(0)+",  time: "+g.remainTimeAsEatable(0));
		}
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
                return Game.LEFT;
            else
                return Game.RIGHT;
        }
        else if (dir.getX() < _pos.getX())
        {
            if (dir.getX() + 1 < _pos.getX())
                return Game.RIGHT;
            else
                return Game.LEFT;
        }
        else if (dir.getY() > _pos.getY())
        {
            if (dir.getY() - 1 > _pos.getY())
                return Game.DOWN;
            else
                return Game.UP;
        }
        else if (dir.getY() < _pos.getY())
        {
            if (dir.getY() + 1 < _pos.getY())
                return Game.UP;
            else
                return Game.DOWN;
        }
        else
            return 0;
    }

    private boolean notInCenter(Pixel2D p)
    {
        return !(p.getX() >= 8 && p.getX() <= 14 && p.getY() >= 10 && p.getY() <= 13);
    }

    private boolean isValidEscape(Pixel2D p)
    {
        for (GhostCL ghost : _ghosts)
        {
            if (p.distance2D(pixelFromString(ghost.getPos(CODE))) <= 2)
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
        return pixelFromString(_ghosts[i].getPos(CODE));
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
        boolean isChasing = _ghosts[0].remainTimeAsEatable(CODE) > NO_TIME_TO_CHASE;
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
            for (GhostCL ghost : _ghosts)
            {
                if (ghost.getStatus() != 0)
                {
                    Pixel2D ghostPos = pixelFromString(ghost.getPos(CODE));
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

        System.out.println(_pos);
        if (target != null)
            System.out.println(target);

        int bestRoute = 0;
        int direction = 0;

        Pixel2D[] sides = new Index2D[4];
        sides[0] = new Index2D(_pos.getX(), _pos.getY() + 1); // the UP neighbor of the current cell
        sides[1] = new Index2D(_pos.getX(), _pos.getY() - 1); // the DOWN neighbor of the current cell
        sides[2] = new Index2D(_pos.getX() - 1, _pos.getY()); // the LEFT neighbor of the current cell
        sides[3] = new Index2D(_pos.getX() + 1, _pos.getY()); // the RIGHT neighbor of the current cell

        if (target != null && isValidEscape(target))
        {
            direction = getDirection(target);
        }
        else
        {
            for (Pixel2D side : sides)
            {
                if (bestRoute < escapePlan.getPixel(side) && isValidEscape(side) && notInCenter(side))
                {
                    bestRoute = escapePlan.getPixel(side);
                    direction = getDirection(side);
                    System.out.println(" choose " + direction);
                }
            }
        }
        System.out.println(" out " + direction);
        return direction;
    }

    private int chaseGhost()
    {
        double closestDistance = Double.MAX_VALUE;
        int closestGhost = -1;
        for (int i = 0; i < _ghosts.length; i++)
        {
            double newClosestDistance = pixelFromString(_ghosts[i].getPos(CODE)).distance2D(_pos);
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
        _waypoints.addAll(tmpMap.shortestPath(_pos, pixelFromString(_ghosts[closestGhost].getPos(CODE)), OBSTACLE_COLOR));
        _waypoints.removeFirst();
        if (!_waypoints.isEmpty())
        {
            return getDirection(_waypoints.removeFirst());
        }
        return 0;
    }

}