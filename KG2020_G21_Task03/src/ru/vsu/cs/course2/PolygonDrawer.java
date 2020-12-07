package ru.vsu.cs.course2;

import ru.vsu.cs.course2.converter.RealPoint;
import ru.vsu.cs.course2.converter.ScreenConverter;
import ru.vsu.cs.course2.converter.ScreenPoint;
import ru.vsu.cs.course2.drawers.arc.ArcDrawer;
import ru.vsu.cs.course2.drawers.line.LineDrawer;

import java.awt.*;
import java.util.List;

public class PolygonDrawer {
    public static void draw(Polygon p, ScreenConverter sc, LineDrawer ld, ArcDrawer ad) {
        List<RealPoint> points = p.getPoints();
        int r = p.getR();
        RealPoint a = points.get(points.size() - 2);
        RealPoint b = points.get(points.size() - 1);
        ScreenPoint prevArcEnd = null;
        ScreenPoint firstArcStart = getArcBorder(a, b, getCenter(a, b, points.get(0), r, sc), sc);
        for (RealPoint c : points) {
            ScreenPoint center = getCenter(a, b, c, r, sc);
            ScreenPoint arcStart = getArcBorder(a, b, center, sc);
            ScreenPoint arcEnd = getArcBorder(c, b, center, sc);
            if (prevArcEnd != null) {
                ld.drawLine(prevArcEnd.getX(), prevArcEnd.getY(), arcStart.getX(), arcStart.getY(), Color.BLACK);
            }
            prevArcEnd = arcEnd;
            if (isPointRight(center, arcStart, arcEnd, sc)) {
                ScreenPoint temp = arcStart;
                arcStart = arcEnd;
                arcEnd = temp;
            }
            drawArc(ad, center, arcStart, arcEnd, r);
            ScreenPoint screenPoint = sc.r2s(b);
            ad.drawArc(screenPoint.getX() - 5, screenPoint.getY() - 5, 10, 10, 0, 360);
//            ld.drawLine(arcStart.getX(), arcStart.getY(), screenPoint.getX(), screenPoint.getY(), Color.RED);
//            ld.drawLine(arcEnd.getX(), arcEnd.getY(), screenPoint.getX(), screenPoint.getY(), Color.RED);
            a = b;
            b = c;
        }
        if (prevArcEnd != null) {
            ld.drawLine(prevArcEnd.getX(), prevArcEnd.getY(), firstArcStart.getX(), firstArcStart.getY(), Color.BLACK);
        }
    }

    /*
    public static void draw(Polygon p, ScreenConverter sc, LineDrawer ld, ArcDrawer ad) {
        List<RealPoint> points = p.getPoints();
        int r = p.getR();
        RealPoint a = points.get(points.size() - 2);
        RealPoint b = points.get(points.size() - 1);
//        ScreenPoint prevArcEnd = null;
        for (RealPoint c : points) {
            ScreenPoint center = getCenter(a, b, c, r, sc);
            ScreenPoint arcStart = getArcBorder(a, b, center, sc);
            ScreenPoint arcEnd = getArcBorder(c, b, center, sc);
//            prevArcEnd = arcEnd;
            if (isPointRight(center, arcStart, arcEnd, sc)) {
                ScreenPoint temp = arcStart;
                arcStart = arcEnd;
                arcEnd = temp;
            }
            drawArc(ad, center, arcStart, arcEnd, r);
            a = b;
            b = c;
        }
    }
     */

    private static void drawArc(ArcDrawer ad, ScreenPoint center, ScreenPoint start, ScreenPoint end, int r) {
        int x = center.getX() - r;
        int y = center.getY() - r;
        int w = 2 * r;
        int h = 2 * r;
        double startAngle = Math.atan2(center.getY() - start.getY(), start.getX() - center.getX()) * 180 / Math.PI;
        double endAngle = Math.atan2(center.getY() - end.getY(), end.getX() - center.getX()) * 180 / Math.PI;
        if (startAngle < 0) {
            startAngle = 360 + startAngle;
        }
        if (endAngle < 0) {
            endAngle = 360 + endAngle;
        }
        double arcAngle = endAngle - startAngle;
        if (startAngle > endAngle) {
            arcAngle = 360 - startAngle + endAngle;
        }
        ad.drawArc(x, y, w, h, (int)startAngle, (int) arcAngle);
    }

    private static ScreenPoint getCenter(RealPoint rpA, RealPoint rpB, RealPoint rpC, int r, ScreenConverter sc) {
        if (!isPointRight(rpA, rpB, rpC)) {
            RealPoint temp = rpA;
            rpA = rpC;
            rpC = temp;
        }
        ScreenPoint spA = sc.r2s(rpA);
        ScreenPoint spB = sc.r2s(rpB);
        ScreenPoint spC = sc.r2s(rpC);
        double a1 = spA.getY() - spB.getY();
        double b1 = spB.getX() - spA.getX();
        double c1 = spA.getX() * spB.getY() - spB.getX() * spA.getY() - r * Math.sqrt(Math.pow(a1, 2) + Math.pow(b1, 2));
        double a2 = spC.getY() - spB.getY();
        double b2 = spB.getX() - spC.getX();
        double c2 = spC.getX() * spB.getY() - spB.getX() * spC.getY() + r * Math.sqrt(Math.pow(a2, 2) + Math.pow(b2, 2));
        double dem = a1 * b2 - a2 * b1;
        double x = -(c1 * b2 - c2 * b1) / dem;
        double y = -(a1 * c2 - a2 * c1) / dem;
        return new ScreenPoint((int) x, (int) y);
    }

    private static ScreenPoint getArcBorder(RealPoint rpA, RealPoint rpB, ScreenPoint spC, ScreenConverter sc) {
        ScreenPoint spA = sc.r2s(rpA);
        ScreenPoint spB = sc.r2s(rpB);
        double a1 = spA.getY() - spB.getY();
        double b1 = spB.getX() - spA.getX();
        double c1 = spA.getX() * spB.getY() - spB.getX() * spA.getY();
        double a2 = b1;
        double b2 = -a1;
        double c2 = a1 * spC.getY() - b1 * spC.getX();
        double dem = a1 * b2 - a2 * b1;
        double x = -(c1 * b2 - c2 * b1) / dem;
        double y = -(a1 * c2 - a2 * c1) / dem;
        return new ScreenPoint((int) x, (int) y);
    }

    private static boolean isPointRight(ScreenPoint p1, ScreenPoint p2, ScreenPoint p, ScreenConverter sc) {
        return isPointRight(sc.s2r(p1), sc.s2r(p2), sc.s2r(p));
    }

    private static boolean isPointRight(RealPoint p1, RealPoint p2, RealPoint p) {
        return (p.getX() * (p2.getY() - p1.getY())
                + p.getY() * (p1.getX() - p2.getX())
                + p1.getY() * p2.getX() - p1.getX() * p2.getY()) >= 0;
    }
}
