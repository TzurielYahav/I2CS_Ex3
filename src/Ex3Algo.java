import exe.ex3.game.Game;
import exe.ex3.game.GhostCL;
import exe.ex3.game.PacManAlgo;
import exe.ex3.game.PacmanGame;

import java.awt.*;
import java.util.ArrayList;

/**
 * This is the major algorithmic class for Ex3 - the PacMan game:
 * 316560499, 4, 8358, 0, 482, 1767628763652, 6, -915428924, 2247922988121988
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
    private int _state;             // 0 - chasing fruit, 1 - chasing ghosts
    private PacManAlgo _algo;
    private static final int CODE = 0;
    private final int STATE_CHASE_FRUIT = 0;
    private final int STATE_CHASE_GHOST = 1;
    private final int ESCAPE_DISTANCE = 4;
    private final int CHASE_DISTANCE = 3;
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
        return pacmanMoveState(); // TO DO RUNNING ONLY FROM TRAIL AHEAD
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

    private boolean hasNoGhosts(Pixel2D p)
    {
        for (GhostCL ghost : _ghosts)
        {
            if (p.distance2D(pixelFromString(ghost.getPos(CODE))) <= 1)
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
        int up = ghostDistances.getPixel(_pos.getX() + 1,_pos.getY());
        int down = ghostDistances.getPixel(_pos.getX() - 1,_pos.getY());
        int left = ghostDistances.getPixel(_pos.getX(),_pos.getY() - 1);
        int right = ghostDistances.getPixel(_pos.getX(),_pos.getY() + 1);
        // ================================================================
        for(int i = 0; i < 4 && i < _waypoints.size(); i++)
        {
            if (ghostDistances.getPixel(_waypoints.get(i)) <= i + 1)
                return false;
        }
//        return true;
        // ================================================================
        return ((up >= ESCAPE_DISTANCE ||  up < 0) && (down >= ESCAPE_DISTANCE ||  down < 0) && (left >= ESCAPE_DISTANCE ||  left < 0) && (right >= ESCAPE_DISTANCE ||  right < 0));
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
//        if (_waypoints.isEmpty() || _state != STATE_CHASE_FRUIT)
//        {
//            _waypoints.addAll(_map.shortestPath(_pos, FRUIT_COLORS, OBSTACLE_COLOR));
//            _waypoints.removeFirst();
//            _state = STATE_CHASE_FRUIT;
//        }
//        if (!_waypoints.isEmpty())
//        {
//            return getDirection(_waypoints.removeFirst());
//        }
        // ===========================================
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
        _waypoints.clear();
        ArrayList<Pixel2D> newWay = tmpMap.shortestPath(_pos, FRUIT_COLORS, OBSTACLE_COLOR);
        if (newWay != null && !newWay.isEmpty())
        {
            _waypoints.addAll(newWay);
            _waypoints.removeFirst();
        }
//        _state = STATE_CHASE_FRUIT;
        // ===========================================
    }

    private int pacmanMoveState()
    {
        _waypoints.clear();
        boolean isChasing = _ghosts[0].remainTimeAsEatable(CODE) > 0.5;
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
                Pixel2D ghostPos = pixelFromString(ghost.getPos(CODE));
                Map2D currentEscape = _map.allDistance(ghostPos, OBSTACLE_COLOR);
                if (!ghostTooFar(currentEscape))
                {
                    isEscaping = true;
                    escapePlan.addMap2D(currentEscape);
                }
            }
//            printGhosts(_ghosts);
            if (isEscaping)
            {
                direction = escapeGhosts(escapePlan);
//                System.out.println("Escape " + direction);
                return direction;
            }
            else
            {
                direction = getDirection(_waypoints.removeFirst());
//                System.out.println(" Fruit " + direction);
                return direction;
            }

//            if (isEscaping)
//                return escapeGhosts(escapePlan);
//            else
//                return chaseFruit();
        }
        return direction;
    }

    private int escapeGhosts(Map2D escapePlan)
    {
        int bestRoute = 0;
        int direction = 0;
//        int avalibleSides = 0;

        Pixel2D[] sides = new Index2D[4];
        sides[0] = new Index2D(_pos.getX(), _pos.getY() + 1); // the UP neighbor of the current cell
        sides[1] = new Index2D(_pos.getX(), _pos.getY() - 1); // the DOWN neighbor of the current cell
        sides[2] = new Index2D(_pos.getX() - 1, _pos.getY()); // the LEFT neighbor of the current cell
        sides[3] = new Index2D(_pos.getX() + 1, _pos.getY()); // the RIGHT neighbor of the current cell

        for (int i = 0; i < sides.length; i++)
        {
            if (bestRoute < escapePlan.getPixel(sides[i]) && hasNoGhosts(sides[i]) && notInCenter(sides[i]))
            {
                bestRoute = escapePlan.getPixel(sides[i]);
                direction = getDirection(sides[i]);
            }
        }
//        Pixel2D up =
////        avalibleSides += escapePlan.getPixel(up) > 0 ? 1 : 0;
//        if (bestRoute < escapePlan.getPixel(up) && hasNoGhosts(up) && notInCenter(up))
//        {
//            bestRoute = escapePlan.getPixel(up);
//            direction = getDirection(up);
//        }
//        Pixel2D down =
////        avalibleSides += escapePlan.getPixel(down) > 0 ? 1 : 0;
//        if (bestRoute < escapePlan.getPixel(down) && hasNoGhosts(down) && notInCenter(down))
//        {
//            bestRoute = escapePlan.getPixel(down);
//            direction = getDirection(down);
//        }
//        Pixel2D left =
////        avalibleSides += escapePlan.getPixel(left) > 0 ? 1 : 0;
//        if (bestRoute < escapePlan.getPixel(left) && hasNoGhosts(left) && notInCenter(left))
//        {
//            bestRoute = escapePlan.getPixel(left);
//            direction = getDirection(left);
//        }
//        Pixel2D right =
////        avalibleSides += escapePlan.getPixel(right) > 0 ? 1 : 0;
//        if (bestRoute < escapePlan.getPixel(right) && hasNoGhosts(right) && notInCenter(right))
//        {
//            bestRoute = escapePlan.getPixel(right);
//            direction = getDirection(right);
//        }
////        if (bestRoute < escapePlan.getPixel(_pos))
////        {
////            direction = 0;
////        }
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