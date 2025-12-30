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
    private static final int _CODE = 0;
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
        _map.init(game.getGame(0));
        _pos = pixelFromString(game.getPos(_CODE));
        //
		if(_count == 0 || _count == 300)
        {
			int[][] board = game.getGame(0);
			printBoard(board);
			int blue = Game.getIntColor(Color.BLUE, _CODE);
			int pink = Game.getIntColor(Color.PINK, _CODE);
			int black = Game.getIntColor(Color.BLACK, _CODE);
			int green = Game.getIntColor(Color.GREEN, _CODE);
			System.out.println("Blue=" + blue + ", Pink=" + pink + ", Black=" + black + ", Green=" + green);
			String pos = game.getPos(_CODE).toString();
			System.out.println("Pacman coordinate: "+pos);
			GhostCL[] ghosts = game.getGhosts(_CODE);
			printGhosts(ghosts);
			int up = Game.UP, left = Game.LEFT, down = Game.DOWN, right = Game.RIGHT;
		}
		_count++;
        //

        //
        return chaseFruit(game);
	}

	private static void printBoard(int[][] b)
    {
		for(int y =0;y<b[0].length;y++)
        {
			for(int x =0;x<b.length;x++)
            {
				int v = b[x][y];
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
            return Game.RIGHT;
        else if (dir.getX() < _pos.getX())
            return Game.LEFT;
        else if (dir.getY() > _pos.getY())
            return Game.UP;
        else if (dir.getY() < _pos.getY())
            return Game.DOWN;
        else
            return 0;
    }

    /*//////// Move Functions ////////*/

    private int chaseFruit(PacmanGame game)
    {
        if (_waypoints.isEmpty())
        {
            _waypoints.addAll(_map.shortestPath(_pos, FRUIT_COLORS, OBSTACLE_COLOR));
        }

        if (!_waypoints.isEmpty())
        {
            return getDirection(_waypoints.removeFirst());
        }
        return 0;
    }
}