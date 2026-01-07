package ServerSide;

public class PointInt2D
{
    private final int _x;
    private final int _y;

    public PointInt2D() {this(0,0);}
    public PointInt2D(int x, int y) {_x=x;_y=y;}
    public PointInt2D(PointInt2D t) {this(t.getX(), t.getY());}


    public int getX()
    {
        return _x;
    }

    public int getY()
    {
        return _y;
    }

    public double distance2D(PointInt2D t)
    {
        if (t == null)
            throw new NullPointerException("PointInt2D t is null");
        return Math.sqrt(Math.pow(_x-t.getX(), 2) + Math.pow(_y-t.getY(), 2));
    }

    public double angle2D(PointInt2D t)
    {
        if (t == null)
            throw new NullPointerException("PointInt2D t is null");
        return Math.atan2(t.getY()-_y, t.getX()-_x);
    }

    public String toString()
    {
        return getX()+","+getY();
    }

    public boolean equals(Object t)
    {
        boolean ans = false;
        if(t instanceof PointInt2D p) {
            ans = (this.distance2D(p)==0);
        }
        return ans;
    }
}
