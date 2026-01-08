package ServerSide;

import java.awt.*;

public interface IGame
{
    int INIT = 0;
    int PLAY = 1;
    int PAUSE = 2;
    int DONE = 3;
    int ERR = -1;
    int STAY = 0;
    int UP = 1;
    int LEFT = 2;
    int DOWN = 3;
    int RIGHT = 4;
    String PLAYER_IMG = "player.png";
    String MENU_IMG = "PacmanMenu.jpg";
    String MENU_IMG2 = "PacmanMenu2.jpg";
    String GAMEOVER_IMG = "PacmanGameOver.jpg";
    String[] ghostImages = {
            "ConfusedGhost.png",
            "FleeingGhost.png",
            "GreenUp.png",
            "RedUp.png",
            "PinkUp.png",
            "BlueUp.png",
            "OrangeUp.png",
            "PurpleUp.png",
            "TerminatorUp.png",
            "GreenLeft.png",
            "RedLeft.png",
            "PinkLeft.png",
            "BlueLeft.png",
            "OrangeLeft.png",
            "PurpleLeft.png",
            "TerminatorLeft.png",
            "GreenDown.png",
            "RedDown.png",
            "PinkDown.png",
            "BlueDown.png",
            "OrangeDown.png",
            "PurpleDown.png",
            "TerminatorDown.png",
            "GreenRight.png",
            "RedRight.png",
            "PinkRight.png",
            "BlueRight.png",
            "OrangeRight.png",
            "PurpleRight.png",
            "TerminatorRight.png",
    };
    int FLOOR_VALUE = 0;
    int OBSTACLE_VALUE = 1;
    int FRUIT_VALUE = 2;
    int BIG_FRUIT_VALUE = 3;
    Color[] COLORS = new Color[4];
    int PLAYER_MOVE_DELAY = 0;
    int GHOST_START_SCATTER_TIME = 25000;
    int GHOST_END_SCATTER_TIME = 4000;
    int FRUIT_SCORE = 10;
    int BIG_FRUIT_SCORE = 50;
    int GHOST_SCORE = 200;
    int INFO_Y_HEIGHT = 50;
    int CANVAS_WIDTH = 800;
    int CANVAS_HEIGHT = CANVAS_WIDTH + INFO_Y_HEIGHT;
}
