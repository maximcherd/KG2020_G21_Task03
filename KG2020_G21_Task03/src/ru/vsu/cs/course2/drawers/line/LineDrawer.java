package ru.vsu.cs.course2.drawers.line;

import java.awt.*;

public interface LineDrawer {
    void drawLine (int x1, int y1, int x2, int y2);
    void drawLine (int x1, int y1, int x2, int y2, Color color);
}
