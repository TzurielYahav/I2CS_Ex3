package ServerSide;
import ServerSide.Pixel2D;

public class Ghost implements IGhost
{
    private Pixel2D _pos;
    private Pixel2D _prevPos;
//    private long _eatableTime = 0;
    private int _type = TYPE_RED;
    private long _lastGreen = 0;
    private int _status = INIT;
    private int _dir = 0;

    Ghost(Pixel2D pos)
    {
        this(pos,0);
    }

    Ghost(int x, int y)
    {
        _pos = new PointInt2D(x, y);
        _prevPos = new PointInt2D(x,y);
    }

    Ghost(Pixel2D pos, int type)
    {
        this(pos.getX(), pos.getY(), type);
    }

    Ghost(int x, int y, int type)
    {
        _pos = new PointInt2D(x, y);
        _prevPos = new PointInt2D(x,y);
        _type = type;
        if(_type < 1 || _type > 3)
            _type = 1;
    }

    Ghost(IGhost ghost)
    {
        this(ghost.getPos(), ghost.getType());
    }

    public Pixel2D getPos()
    {
        return _pos;
    }

    public Pixel2D getPrevPos()
    {
        return _prevPos;
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

    @Override
    public double remainTimeAsEatable()
    {
        double difference = (double)(System.currentTimeMillis() - _lastGreen);
        return (EATABLE_TIMEOUT - difference) / 1000;
    }
}
