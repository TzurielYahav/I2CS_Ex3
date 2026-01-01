import exe.ex3.game.Game;
import exe.ex3.game.GhostCL;
import exe.ex3.game.PacManAlgo;
import exe.ex3.game.PacmanGame;

import java.awt.*;
import java.util.ArrayList;

/**
 * This is the major algorithmic class for Ex3 - the PacMan game:
 *
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
        //
        boolean isChasing = _ghosts[0].remainTimeAsEatable(CODE) > 0;
        boolean isEscaping = false;
        if(!isChasing)
        {
            for (GhostCL ghost : _ghosts)
            {
                if (pixelFromString(ghost.getPos(CODE)).distance2D(_pos) <= ESCAPE_DISTANCE)
                {
                    isEscaping = true;
                    break;
                }
            }
        }
        if(isChasing)
            return chaseGhost(game);
        else if (isEscaping)
            return escapeGhosts(game);
        else
            return chaseFruit(game);
        //
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

	private static int randomDir()
    {
		int[] dirs = {Game.UP, Game.LEFT, Game.DOWN, Game.RIGHT};
		int ind = (int)(Math.random()*dirs.length);
		return dirs[ind];
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

    private boolean ghostNotInCenter(GhostCL g)
    {
        Pixel2D p = pixelFromString(g.getPos(CODE));
        return p.getX() >= 8 && p.getX() <= 14 && p.getY() >= 10 && p.getY() <= 13;
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

    private int chaseFruit(PacmanGame game)
    {
        if (_waypoints.isEmpty() || _state != STATE_CHASE_FRUIT)
        {
            _waypoints.addAll(_map.shortestPath(_pos, FRUIT_COLORS, OBSTACLE_COLOR));
            _waypoints.removeFirst();
            _state = STATE_CHASE_FRUIT;
        }
        if (!_waypoints.isEmpty())
        {
            return getDirection(_waypoints.removeFirst());
        }
        return 0;
    }

    private int escapeGhosts(PacmanGame game)
    {
        Map2D escapePlan = new Map(_map.getWidth(), _map.getHeight(), 0);
        for (GhostCL ghost : _ghosts)
        {
            Pixel2D ghostPos = pixelFromString(ghost.getPos(CODE));
            if (ghostPos.distance2D(_pos) <= ESCAPE_DISTANCE)
            {
                Map2D currentEscape = _map.allDistance(ghostPos, OBSTACLE_COLOR);
                if (ghostPos.distance2D(_pos) <= 1)
                    currentEscape.mul(10);
                escapePlan.addMap2D(currentEscape);
            }
        }
        int bestRoute = escapePlan.getPixel(_pos);
        int direction = 0;

        Pixel2D up = new Index2D(_pos.getX(), _pos.getY() + 1);
        if (bestRoute < escapePlan.getPixel(up))
        {
            bestRoute = escapePlan.getPixel(up);
            direction = getDirection(up);
        }
        Pixel2D down = new Index2D(_pos.getX(), _pos.getY() - 1);
        if (bestRoute < escapePlan.getPixel(down))
        {
            bestRoute = escapePlan.getPixel(down);
            direction = getDirection(down);
        }
        Pixel2D left = new Index2D(_pos.getX() - 1, _pos.getY());
        if (bestRoute < escapePlan.getPixel(left))
        {
            bestRoute = escapePlan.getPixel(left);
            direction = getDirection(left);
        }
        Pixel2D right = new Index2D(_pos.getX() + 1, _pos.getY());
        if (bestRoute < escapePlan.getPixel(right))
        {
            bestRoute = escapePlan.getPixel(right);
            direction = getDirection(right);
        }

        if (bestRoute > ESCAPE_DISTANCE)
            direction = chaseFruit(game);
        return direction;
    }

    private int chaseGhost(PacmanGame game)
    {
        if (_waypoints.isEmpty() || _state != STATE_CHASE_GHOST)
        {
            double closestDistance = Double.MAX_VALUE;
            int closestGhost = -1;
            for (int i = 0; i < _ghosts.length; i++)
            {
                double newClosestDistance = pixelFromString(_ghosts[i].getPos(CODE)).distance2D(_pos);
                if (closestDistance > newClosestDistance && newClosestDistance < CHASE_DISTANCE && ghostNotInCenter(_ghosts[i]))
                {
                    closestDistance = newClosestDistance;
                    closestGhost = i;
                }
            }
            if (closestGhost == -1)
                return chaseFruit(game);

            _waypoints.clear();
            _waypoints.addAll(_map.shortestPath(_pos, pixelFromString(_ghosts[closestGhost].getPos(CODE)), OBSTACLE_COLOR));
            _waypoints.removeFirst();
            _state = STATE_CHASE_GHOST;
        }
        if (!_waypoints.isEmpty())
        {
            return getDirection(_waypoints.removeFirst());
        }
        return 0;
    }

}