package client.GUI;

import java.awt.*;

class AnimatedCircle {
    int x;
    int y;
    int radius;
    int colorIndex;
    boolean growing = true;

    public AnimatedCircle(int x, int y, int radius, int colorIndex) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.colorIndex = colorIndex;
    }
}
