package ru.vsu.cs.course2;

import ru.vsu.cs.course2.converter.RealPoint;
import ru.vsu.cs.course2.converter.ScreenConverter;
import ru.vsu.cs.course2.converter.ScreenPoint;
import ru.vsu.cs.course2.drawers.arc.ArcDrawer;
import ru.vsu.cs.course2.drawers.line.LineDrawer;

import java.util.ArrayList;
import java.util.List;

public class Polygon {
    private List<RealPoint> points = new ArrayList<>();
    private int r = 15;

    public Polygon() {
    }

    public Polygon(int r) {
        this.r = r;
    }

    public Polygon(List<RealPoint> points) {
        this.points = points;
    }

    public void draw(ScreenConverter sc, LineDrawer ld, ArcDrawer ad, boolean complete) {
        RealPoint prev = null;
        for (RealPoint p : points) {
            if (prev != null) {
                ScreenPoint p1 = sc.r2s(prev);
                ScreenPoint p2 = sc.r2s(p);
                ld.drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
            }
            prev = p;
        }
        if (complete) {
            ScreenPoint p1 = sc.r2s(prev);
            ScreenPoint p2 = sc.r2s(points.get(0));
            ld.drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
        }
    }

    public void addPoint(RealPoint p) {
        points.add(p);
    }

    public void setR(int r) {
        this.r = r;
    }

    public List<RealPoint> getPoints() {
        return points;
    }

    public int getR() {
        return r;
    }
}
