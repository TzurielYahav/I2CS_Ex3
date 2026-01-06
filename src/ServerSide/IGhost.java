package ServerSide;

public interface IGhost
{
    int INIT = 0;
    int PLAY = 1;
    int PAUSE = 2;
    double EATABLE_TIMEOUT = 5;
    int TYPE_RED = 1;
    int TYPE_GREEN = 2;
    int TYPE_BLUE = 3;

    Pixel2D getPos();

    Pixel2D getPrevPos();

    int getDirection();

    int getType();

    int getStatus();

    double remainTimeAsEatable();
}
