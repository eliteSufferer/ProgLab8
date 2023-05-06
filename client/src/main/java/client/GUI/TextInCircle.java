package client.GUI;

import java.awt.*;

class TextInCircle {
    public static void drawCenteredString(Graphics2D g, String text, int x, int y, Font font) {
        FontMetrics metrics = g.getFontMetrics(font);
        int textWidth = metrics.stringWidth(text);
        int textHeight = metrics.getHeight();
        g.setFont(font);
        g.drawString(text, x - textWidth / 2, y - textHeight / 2 + metrics.getAscent());
    }
}
