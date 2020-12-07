//package ru.vsu.cs.course2;
//
//import ru.vsu.cs.course2.converter.RealPoint;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class Logic {
//    //логика построения контура
//    //значения понятий:
//    //угол - точка, где пересекаются 2 вектора одного треугольника
//    //точка пересечения - точка, где пересекаются 2 вектора разных треугоьников
//
//    //метод сбора общего контура
//    public static List<RealPoint> findContour(Triangle t1, Triangle t2) {
//        List<RealPoint> points1 = t1.getPoints();
//        List<RealPoint> points2 = t2.getPoints();
//        List<RealPoint> contour = isOneInAnother(points1, points2);
//        //проверяем что треугольники не лежат друг в друге
//        if (contour == null) {
//            contour = new ArrayList<>();
//        } else {
//            return contour;
//        }
//        RealPoint point = points1.get(0);
//        boolean flag = true;
//        boolean intersection = false;
//        while (flag) {
//            for (RealPoint p : points1) {
//                flag = false;
//                if (!equals(point, p)) {
//                    List<RealPoint> intersectionPoints = getIntersectionPoints(point, p, points2);
//                    RealPoint intersectionPoint = intersectionPoints.get(0);
//                    if (intersectionPoint == null && !isPointInTriangle(p, points2) && !contains(contour, p)) {
//                        //вектор не пересекает треугольник, конец лежит вне треугольника, конец не содержится в контуре
//                        //добавляем угол в контур, поднимаем флаг
//                        point = p;
//                        contour.add(point);
//                        flag = true;
//                    } else if (intersectionPoint != null && !contains(contour, intersectionPoint)) {
//                        intersection = true;
//                        //вектор пересекает треугольник, конец не содержится в контуре
//                        //меняем треугольники (мы рассматриваем points1, а пересекает он points2)
//                        List<RealPoint> temp = new ArrayList<>(points1);
//                        points1 = new ArrayList<>(points2);
//                        points2 = new ArrayList<>(temp);
//                        //добаляем точку пересечения в контур
//                        point = intersectionPoint;
//                        contour.add(point);
//                        //теперь ищем следующий угол
//                        for (int i = 0; i < 2; i++) {
//                            RealPoint point1 = intersectionPoints.get(i + 1);
//                            List<RealPoint> intersectionPoints1 = getIntersectionPoints(point, point1, points2);
//                            RealPoint intersectionPoint1 = intersectionPoints1.get(0);
//                            if (intersectionPoint1 == null && !isPointInTriangle(point1, points2)
//                                    && !contains(contour, point1)) {
//                                //ветор пересекает треугольник, конец вне треугольника, конец не содержится в контуре
//                                //добавляем угол в контур
//                                point = point1;
//                                contour.add(point);
//                                flag = true;
//                            }
//                        }
//                    }
//                }
//                //если уже добавили точку, то выходим из фора, чтоб флаг снова не перезаписался как false
//                if (flag) {
//                    break;
//                }
//            }
//        }
//        //если пересечений не было - контура нет
//        if (!intersection) {
//            contour.clear();
//        }
//        return contour;
//    }
//
//    //проверка на треугольник в треугольнике
//    private static List<RealPoint> isOneInAnother(List<RealPoint> points1, List<RealPoint> points2) {
//        int counter1 = 0, counter2 = 0;
//        for (RealPoint p : points1) {
//            if (isPointInTriangle(p, points2)) {
//                counter1++;
//            }
//        }
//        //если все 3 точки одного лежат в другом - то оставляем внешний
//        if (counter1 == 3) {
//            return points2;
//        }
//        for (RealPoint p : points2) {
//            if (isPointInTriangle(p, points1)) {
//                counter2++;
//            }
//        }
//        //аналогично
//        if (counter2 == 3) {
//            return points1;
//        }
//        //если они не друг в друге, то начинаем искать контур
//        return null;
//    }
//
//    //true если точки равны (с погрешностью eps) (можно перенести в класс точки)
//    private static boolean equals(RealPoint p1, RealPoint p2) {
//        double eps = Math.pow(10, -10);
//        return Math.abs(p1.getX() - p2.getX()) < eps && Math.abs(p1.getY() - p2.getY()) < eps;
//    }
//
//    //true если в списке точек содержится данная точка
//    private static boolean contains(List<RealPoint> list, RealPoint p) {
//        for (RealPoint point : list) {
//            if (equals(point, p)) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    //метод получения ближайшей к основанию вектора точки пересечения данного вектора с треугольником
//    public static List<RealPoint> getIntersectionPoints(RealPoint p1, RealPoint p2, List<RealPoint> points) {
//        List<RealPoint> list = new ArrayList<>();
//        double minLength = Double.MAX_VALUE;
//        RealPoint res = null;
//        list.add(res);
//        //проходим по всем граням треугольника и проверяем их на пересечение с данным ветором
//        RealPoint prev = points.get(2);
//        for (RealPoint p : points) {
//            res = getIntersectionPoint(p1, p2, prev, p);
//            if (res != null) {
//                //если нашли точку пересечения, то проверяем, что она - ближайсшая к началу вектора
//                double length = getLength(p1, res);
//                if (!equals(res, p1) && length < minLength) {
//                    minLength = length;
//                    list.clear();
//                    list.add(res);
//                    list.add(prev);
//                    list.add(p);
//                }
//            }
//            prev = p;
//        }
//        return list;
//    }
//
//    //метод получения длины вектора
//    private static double getLength(RealPoint p1, RealPoint p2) {
//        return Math.sqrt(Math.pow(p1.getX() - p2.getX(), 2) + Math.pow(p1.getY() - p2.getY(), 2));
//    }
//
//    //метод получения точки пересечения двух векторов
//    public static RealPoint getIntersectionPoint(RealPoint p1, RealPoint p2, RealPoint p3, RealPoint p4) {
//        //ищем точку пересечения как для прямых, но с доп условием ниже
//        RealPoint ans = null;
//        double eps = Math.pow(10, -10);
//        double a1 = p1.getY() - p2.getY();
//        double b1 = p2.getX() - p1.getX();
//        double c1 = p1.getX() * p2.getY() - p2.getX() * p1.getY();
//        double a2 = p3.getY() - p4.getY();
//        double b2 = p4.getX() - p3.getX();
//        double c2 = p3.getX() * p4.getY() - p4.getX() * p3.getY();
//        double denominator = a1 * b2 - a2 * b1;
//        if (Math.abs(denominator) > eps) {
//            double x = -(c1 * b2 - c2 * b1) / denominator;
//            double y = -(a1 * c2 - a2 * c1) / denominator;
//            //дополнительно проверяем, что точка песечения лежит на обоих этих векторах
//            //(чтобы не получать точку пересечения если они просто не параллельны друг другу)
//            if (!(x > Math.max(p1.getX(), p2.getX()) || x < Math.min(p1.getX(), p2.getX())
//                    || x > Math.max(p3.getX(), p4.getX()) || x < Math.min(p3.getX(), p4.getX())
//                    || y > Math.max(p1.getY(), p2.getY()) || y < Math.min(p1.getY(), p2.getY())
//                    || y > Math.max(p3.getY(), p4.getY()) || y < Math.min(p3.getY(), p4.getY()))) {
//                ans = new RealPoint(x, y);
//            }
//        }
//        return ans;
//    }
//
//    //определяет принадлежность точки к области треугольника (треугольник задаём как список точек)
//    private static boolean isPointInTriangle(RealPoint p, List<RealPoint> points) {
//        double q1 = func(points.get(0), points.get(1), p);
//        double q2 = func(points.get(1), points.get(2), p);
//        double q3 = func(points.get(2), points.get(0), p);
//        return ((q1 >= 0 && q2 >= 0 && q3 >= 0) || (q1 < 0 && q2 < 0 && q3 < 0));
//    }
//
//    //функция для определения точки относительно вектора (справа или слева), как назвать - не знаю, поэтому просто func
//    //позволяем нам определить, что точка внутри треугольника
//    private static double func(RealPoint p1, RealPoint p2, RealPoint p3) {
//        return p3.getX() * (p2.getY() - p1.getY())
//                + p3.getY() * (p1.getX() - p2.getX())
//                + p1.getY() * p2.getX() - p1.getX() * p2.getY();
//    }
//}
