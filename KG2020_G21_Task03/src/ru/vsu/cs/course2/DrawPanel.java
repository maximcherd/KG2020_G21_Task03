package ru.vsu.cs.course2;

import ru.vsu.cs.course2.converter.Line;
import ru.vsu.cs.course2.converter.RealPoint;
import ru.vsu.cs.course2.converter.ScreenConverter;
import ru.vsu.cs.course2.converter.ScreenPoint;
import ru.vsu.cs.course2.drawers.arc.ArcDrawer;
import ru.vsu.cs.course2.drawers.arc.GraphicsArcDrawer;
import ru.vsu.cs.course2.drawers.line.*;
import ru.vsu.cs.course2.drawers.pixel.GraphicsPixelDrawer;
import ru.vsu.cs.course2.drawers.pixel.PixelDrawer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

public class DrawPanel extends JPanel implements MouseMotionListener, MouseListener, MouseWheelListener {
    private ScreenConverter sc = new ScreenConverter(
            -10, 190, 200, 200, 800, 600);

    private Line xAxis = new Line(0, 0, 200, 0);
    private Line yAxis = new Line(0, 0, 0, 200);
    private ScreenPoint prevPoint = null;

    private int x = 0, y = 0;//координаты мыши
    private List<Polygon> polygons = new ArrayList<>();
    private boolean complete = true;
    private int x0 = 0, y0 = 0;//координаты 1ой точки незаконченного многоугольника
    private Polygon changePolygon;
    private RealPoint changePoint;//изменяемая точка
    private int checkRadius = 15;//радиус совпадения точек для клика мыши


    public DrawPanel() {
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
        this.addMouseWheelListener(this);
    }

    @Override
    public void paint(Graphics g) {
        BufferedImage bf = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics gr = bf.createGraphics();
        gr.setColor(Color.WHITE);
        gr.fillRect(0, 0, getWidth(), getHeight());
        PixelDrawer pd = new GraphicsPixelDrawer(gr);

        ArcDrawer ad = new GraphicsArcDrawer(gr);
        LineDrawer ld;

        int change = 4;
        switch (change) {
            case 1:
                ld = new BresenhamLineDrawer(pd);
                break;
            case 2:
                ld = new DDALineDrawer(pd);
                break;
            case 3:
                ld = new WuLineDrawer(pd);
                break;
            default:
                ld = new GraphicsLineDrawer(gr);
        }

        drawAll(ld, ad);
        g.drawImage(bf, 0, 0, null);
        gr.dispose();
    }

    public void drawAll(LineDrawer ld, ArcDrawer ad) {
        drawLine(ld, xAxis);
        drawLine(ld, yAxis);

        drawPolygons(ld, ad);
        drawLastPolygon(ld, ad);
        drawChange(ld, ad);
    }

    private void drawLine(LineDrawer ld, Line l) {
        ScreenPoint p1 = sc.r2s(l.getP1());
        ScreenPoint p2 = sc.r2s(l.getP2());
        ld.drawLine(p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }

    private void drawPolygons(LineDrawer ld, ArcDrawer ad) {
        int counter = 0;
        for (Polygon p : polygons) {
            if (counter++ != polygons.size() - (complete ? 0 : 1)) {
                PolygonDrawer.draw(p, sc, ld, ad);
            }
        }
    }

    private void drawLastPolygon(LineDrawer ld, ArcDrawer ad) {
        if (polygons.size() > 0 && !complete) {
            Polygon pl = polygons.get(polygons.size() - 1);
            pl.draw(sc, ld, ad, false);
            List<RealPoint> points = pl.getPoints();
            if (points.size() > 0) {
                RealPoint p = points.get(points.size() - 1);
                ScreenPoint sp = sc.r2s(p);
                ld.drawLine(sp.getX(), sp.getY(), x, y);
            }
        }
    }

    private void drawChange(LineDrawer ld, ArcDrawer ad) {
        if (changePoint != null) {
            RealPoint p = sc.s2r(new ScreenPoint(x, y));
            changePoint.setX(p.getX());
            changePoint.setY(p.getY());
        }
    }

    private RealPoint nearPoint(int x, int y) {
        for (Polygon pl : polygons) {
            changePolygon = pl;
            for (RealPoint p : pl.getPoints()) {
                ScreenPoint sp = sc.r2s(p);
                if (Math.abs(x - sp.getX()) < checkRadius && Math.abs(y - sp.getY()) < checkRadius) {
                    return p;
                }
            }
        }
        return null;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            x = e.getX();
            y = e.getY();
            if (changePoint != null) {
                if (!(Math.abs(x - changePoint.getX()) < checkRadius && Math.abs(y - changePoint.getY()) < checkRadius)) {
                    RealPoint p = sc.s2r(new ScreenPoint(x, y));
                    changePoint.setX(p.getX());
                    changePoint.setY(p.getY());
                }
                changePoint = null;
            } else if (complete) {
                changePoint = nearPoint(x, y);
                if (changePoint == null) {
                    polygons.add(new Polygon());
                    x0 = x;
                    y0 = y;
                    RealPoint p = sc.s2r(new ScreenPoint(x, y));
                    polygons.get(polygons.size() - 1).addPoint(p);
                    complete = false;
                }
            } else {
                if (Math.abs(x - x0) < checkRadius && Math.abs(y - y0) < checkRadius) {
                    complete = true;
                    if (polygons.get(polygons.size() - 1).getPoints().size() < 3) {
                        polygons.remove(polygons.size() - 1);
                    }
                } else {
                    RealPoint p = sc.s2r(new ScreenPoint(x, y));
                    polygons.get(polygons.size() - 1).addPoint(p);
                }
            }
            repaint();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            if (!complete) {
                polygons.remove(polygons.size() - 1);
                complete = true;
            }
            prevPoint = new ScreenPoint(e.getX(), e.getY());
            repaint();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (SwingUtilities.isRightMouseButton(e)) {
            ScreenPoint currentPoint = new ScreenPoint(e.getX(), e.getY());
            if (prevPoint != null) {
                ScreenPoint deltaScreen = new ScreenPoint(
                        currentPoint.getX() - prevPoint.getX(),
                        currentPoint.getY() - prevPoint.getY()
                );
                RealPoint deltaReal = sc.s2r(deltaScreen);
                double vectorX = deltaReal.getX() - sc.getCornerX();
                double vectorY = deltaReal.getY() - sc.getCornerY();

                sc.setCornerX(sc.getCornerX() - vectorX);
                sc.setCornerY(sc.getCornerY() - vectorY);
                prevPoint = currentPoint;
            }
            repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        x = e.getX();
        y = e.getY();
        if (!complete || changePoint != null) {
            repaint();
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        int clicks = e.getWheelRotation();
        double scale = 1;
        double step = (clicks < 0) ? 0.9 : 1.1;
        for (int i = Math.abs(clicks); i > 0; i--) {
            scale *= step;
        }
        sc.setRealW(scale * sc.getRealW());
        sc.setRealH(scale * sc.getRealH());
        repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }
}
