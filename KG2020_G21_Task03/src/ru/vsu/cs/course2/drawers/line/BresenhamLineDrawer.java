package ru.vsu.cs.course2.drawers.line;

import ru.vsu.cs.course2.drawers.pixel.PixelDrawer;

import java.awt.*;

public class BresenhamLineDrawer implements LineDrawer {
    private PixelDrawer pd;

    public BresenhamLineDrawer(PixelDrawer pd) {
        this.pd = pd;
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2) {
        int dy = y2 - y1;
        int dx = x2 - x1;
        if (Math.abs(dy) <= Math.abs(dx)) {
            draw(x1, y1, x2, y2, false);
        } else {
            draw(y1, x1, y2, x2, true);
        }
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2, Color color) {
        drawLine(x1,y1,x2,y2);
        // FIXME: 14.11.2020
    }

    private void draw(int x1, int y1, int x2, int y2, boolean swap) {
        int step, dx, dy;
        if (x1 > x2) {
            int temp = x1;
            x1 = x2;
            x2 = temp;
            temp = y1;
            y1 = y2;
            y2 = temp;
        }
        step = (y2 >= y1) ? 1 : -1;
        dx = x2 - x1;
        dy = step * (y2 - y1);
        int x = x1, y = y1;
        int d = 2 * dy - dx;
        int d1 = 2 * dy;
        int d2 = 2 * (dy - dx);
        while (x < x2) {
            if (d < 0) {
                d = d + d1;
            } else {
                d = d + d2;
                y = y + step;
            }
            if (swap) {
                pd.drawPixel(y, x, Color.RED);
            } else {
                pd.drawPixel(x, y, Color.BLUE);
            }
            x++;
        }
    }
}
