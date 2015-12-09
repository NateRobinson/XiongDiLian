package com.gu.baselibrary.utils;

/**
 * Created by Administrator on 2015/9/19. 坐标距离工具类
 */
public class LocationUtils {

    private LocationUtils() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    private static final double EARTH_RADIUS = 6378137;

    /**
     * 根据两点间经纬度坐标（double值），计算两点间距离，
     *
     * @return 距离：单位为米
     */
    public static double DistanceOfTwoPoints(double lat1, double lng1, double lat2, double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s =
                2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2)
                        * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10000;
        return s;
    }

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }
}
