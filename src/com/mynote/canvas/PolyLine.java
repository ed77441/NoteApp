package com.mynote.canvas;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;

public class PolyLine extends ArrayList<Point> {
    Color color;
    int thickness;

    PolyLine(Color color, int thickness) {
        this.color = color;
        this.thickness = thickness;
    }

    public void draw(Graphics2D g2) {
        g2.setColor(color);
        g2.setStroke(new BasicStroke(thickness, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        drawPolyLine(g2);
    }

    public void drawShadow(Graphics2D g2) {
        g2.setColor(Color.lightGray);
        g2.setStroke(new BasicStroke(thickness + 4, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        drawPolyLine(g2);
    }

    private void drawPolyLine(Graphics2D g2) {
        Iterator<Point> iterator1 = getStartPoints(), iterator2 = getEndPoints();
        while(iterator1.hasNext() && iterator2.hasNext()) {
            Point start = iterator1.next(), end = iterator2.next();
            g2.drawLine(start.x, start.y, end.x, end.y);
        }
    }

    public boolean contains(Point point) {
        Iterator<Point> iterator1 = getStartPoints(), iterator2 = getEndPoints();
        boolean contained = false;
        while(iterator1.hasNext() && iterator2.hasNext() && !contained) {
            contained = getDetectedArea(iterator1.next(), iterator2.next()).contains(point);
        }
        return contained;
    }

    private Shape getDetectedArea(Point start, Point end) {
        double distance =  thickness / 2.0 + 2;
        Shape shape;

        if (start.distance(end) < distance) {
            shape = new Ellipse2D.Double(start.x - distance, start.y - distance,  distance + distance,  distance + distance);
        }
        else {
            double []sideWayStep = getXYStep(start.y - end.y, -start.x + end.x, distance);
            double []peakPointStep = getXYStep( start.x - end.x, start.y - end.y, distance);
            double sideWayStepX = sideWayStep[0], sideWayStepY = sideWayStep[1];
            double peakPointStepX = peakPointStep[0], peakPointStepY = peakPointStep[1];

            Point2D point1 = new Point2D.Double(start.x + sideWayStepX, start.y +sideWayStepY);
            Point2D point2peak = new Point2D.Double(start.x + peakPointStepX, start.y + peakPointStepY);
            Point2D point3 = new Point2D.Double(start.x - sideWayStepX, start.y - sideWayStepY);
            Point2D point4 = new Point2D.Double(end.x - sideWayStepX, end.y - sideWayStepY);
            Point2D point5peak = new Point2D.Double(end.x - peakPointStepX, end.y - peakPointStepY);
            Point2D point6 = new Point2D.Double(end.x + sideWayStepX, end.y + sideWayStepY);

            Path2D path = new Path2D.Double();
            path.moveTo(point1.getX(), point1.getY());
            path.curveTo(point1.getX(), point1.getY(), point2peak.getX(), point2peak.getY(), point3.getX(), point3.getY());
            path.lineTo(point4.getX(), point4.getY());
            path.curveTo(point4.getX(), point4.getY(), point5peak.getX(), point5peak.getY(), point6.getX(), point6.getY());
            path.closePath();

            shape = path;
        }

        return shape;
    }

    public boolean intersects (Rectangle2D rect) {
        Iterator<Point> iterator1 = getStartPoints(), iterator2 = getEndPoints();
        boolean intersects = false;
        while(iterator1.hasNext() && iterator2.hasNext() && !intersects) {
            intersects = getDetectedArea(iterator1.next(), iterator2.next()).intersects(rect);
        }
        return intersects;
    }

    private double[] getXYStep(int offSetX, int offSetY, double distance) {
        double ratio = Math.sqrt(distance * distance / (offSetX * offSetX + offSetY * offSetY));
        return new double[] {offSetX * ratio, offSetY * ratio};
    }

    private Iterator<Point> getStartPoints () {
        return subList(0, size() - 1).iterator();
    }

    private Iterator<Point> getEndPoints () {
        return subList(1, size()).iterator();
    }

    private boolean shouldRemoveLastPoint(Point C) {
        Point A = get(size() - 2);
        Point B = get(size() - 1);
        double distance = thickness / 2.0 + 4;

        if (A.distance(B) < distance && C.distance(B) < distance) {
            return true;
        }
        return (A.y - B.y) * (B.x - C.x) == (B.y - C.y) * (A.x - B.x);
    }

    void addAndRemoveRedundantPoint(Point newPoint) {
        if (shouldRemoveLastPoint(newPoint)) {
            remove(size() - 1);
        }
        add(newPoint);
    }

    void shift(int dx, int dy) {
        for (Point point: this) {
            point.translate(dx, dy);
        }
    }
}
