package ServerSide;

import java.awt.*;

public interface IGame
{
    int INIT = 0;
    int PLAY = 1;
    int PAUSE = 2;
    int DONE = 3;
    int GAME_OVER = 4;
    int ERR = -1;
    int STAY = 0;
    int UP = 1;
    int LEFT = 2;
    int DOWN = 3;
    int RIGHT = 4;
    String PATH = "images/";
    String PLAYER_IMG = PATH + "player.png";
    String MENU_IMG = PATH + "PacmanMenu.jpg";
    String MENU_IMG2 = PATH + "PacmanMenu2.jpg";
    String GAMEOVER_IMG = PATH + "PacmanGameOver.jpg";
    String[] ghostImages = {
            PATH + "ConfusedGhost.png",
            PATH + "FleeingGhost.png",
            PATH + "GreenUp.png",
            PATH + "RedUp.png",
            PATH + "PinkUp.png",
            PATH + "BlueUp.png",
            PATH + "OrangeUp.png",
            PATH + "PurpleUp.png",
            PATH + "TerminatorUp.png",
            PATH + "GreenLeft.png",
            PATH + "RedLeft.png",
            PATH + "PinkLeft.png",
            PATH + "BlueLeft.png",
            PATH + "OrangeLeft.png",
            PATH + "PurpleLeft.png",
            PATH + "TerminatorLeft.png",
            PATH + "GreenDown.png",
            PATH + "RedDown.png",
            PATH + "PinkDown.png",
            PATH + "BlueDown.png",
            PATH + "OrangeDown.png",
            PATH + "PurpleDown.png",
            PATH + "TerminatorDown.png",
            PATH + "GreenRight.png",
            PATH + "RedRight.png",
            PATH + "PinkRight.png",
            PATH + "BlueRight.png",
            PATH + "OrangeRight.png",
            PATH + "PurpleRight.png",
            PATH + "TerminatorRight.png",
    };
    int FLOOR_VALUE = 0;
    int OBSTACLE_VALUE = 1;
    int FRUIT_VALUE = 2;
    int BIG_FRUIT_VALUE = 3;
    Color[] COLORS = new Color[4];
    int PLAYER_MOVE_DELAY = 0;
    int GHOST_START_SCATTER_TIME = 60000;
    int GHOST_END_SCATTER_TIME = 4000;
    int FRUIT_SCORE = 10;
    int BIG_FRUIT_SCORE = 50;
    int STUPID_GHOST_SCORE = 200;
    int GHOST_SCORE = 400;
    int SMART_GHOST_SCORE = 600;
    int TERMINATOR_GHOST_SCORE = 1000;
    int INFO_Y_HEIGHT = 50;
    int CANVAS_WIDTH = 800;
    int CANVAS_HEIGHT = CANVAS_WIDTH + INFO_Y_HEIGHT;
    String SAVE_FILE = "SAVE_FILE.txt";
}
