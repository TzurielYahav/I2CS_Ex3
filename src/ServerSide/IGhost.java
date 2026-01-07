package ServerSide;

public interface IGhost
{
    int INIT = 0;
    int PLAY = 1;
    int PAUSE = 2;
    int FLEE = 3;
    int CONFUSED = 4;
    int SCATTER = 5;
    double EATABLE_TIMEOUT = 6000;
    int MOVE_DELAY = 2;
    int TYPE_STUPID = 0;
    int TYPE_RED = 1;
    int TYPE_GREEN = 2;
    int TYPE_BLUE = 3;
    int TYPE_ORANGE = 4;
    int TYPE_SMART = 5;
    int TYPE_TERMINATOR = 6;
    PointInt2D BASE = new PointInt2D(11,11);


    String getPos();

    int getDirection();

    int getType();

    int getStatus();

    double remainTimeAsEatable();
}
