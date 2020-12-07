package ru.vsu.cs.course2.drawers.arc;

import java.awt.*;

public class GraphicsArcDrawer implements ArcDrawer {
    private Graphics g;

    public GraphicsArcDrawer(Graphics g) {
        this.g = g;
    }

    @Override
    public void drawArc(int x, int y, int w, int h, int startAngle, int arcAngle) {
        g.setColor(Color.BLACK);
        g.drawArc(x, y, w, h, startAngle, arcAngle);
    }
}
