package ClientSide;

public class Index2D implements Pixel2D
{
    private final int _x;
    private final int _y;

    public Index2D() {this(0,0);}
    public Index2D(int x, int y) {_x=x;_y=y;}
    public Index2D(Pixel2D t) {this(t.getX(), t.getY());}

    @Override
    public int getX()
    {
        return _x;
    }

    @Override
    public int getY()
    {
        return _y;
    }

    public double distance2D(Pixel2D t)
    {
        if (t == null)
            throw new NullPointerException("Pixel2D p2 is null");
        return Math.sqrt(Math.pow(_x-t.getX(), 2) + Math.pow(_y-t.getY(), 2));
    }

    @Override
    public String toString()
    {
        return getX()+","+getY();
    }

    @Override
    public boolean equals(Object t)
    {
        boolean ans = false;
        if(t instanceof Pixel2D) {
            Pixel2D p = (Pixel2D) t;
            ans = (this.distance2D(p)==0);
        }
        return ans;
    }
}
