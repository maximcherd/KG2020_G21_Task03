package ru.vsu.cs.course2.drawers.line;

import ru.vsu.cs.course2.drawers.pixel.PixelDrawer;

import java.awt.*;

public class DDALineDrawer implements LineDrawer {
    private PixelDrawer pd;

    public DDALineDrawer(PixelDrawer pd) {
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
        drawLine(x1, y1, x2, y2);
        // FIXME: 14.11.2020
    }

    private void draw(int x1, int y1, int x2, int y2, boolean swap) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        if (x1 > x2) {
            int temp = x1;
            x1 = x2;
            x2 = temp;
            temp = y1;
            y1 = y2;
            y2 = temp;
        }
        double k = dy / dx;
        for (int i = x1; i < x2; i++) {
            double j = k * (i - x1) + y1;
            if (swap) {
                pd.drawPixel((int) j, i, Color.RED);
            } else {
                pd.drawPixel(i, (int) j, Color.BLUE);
            }
        }
    }
}
