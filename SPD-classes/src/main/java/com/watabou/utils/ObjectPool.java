/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2024 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.watabou.utils;

import java.util.ArrayList;

/**
 * Object pooling implementation for commonly used geometry objects.
 * This class provides pools for Point, PointF, Rect, and RectF objects
 * to reduce garbage collection overhead by reusing objects.
 */
public class ObjectPool {

    // Default initial pool size
    private static final int INITIAL_POOL_SIZE = 32;
    
    // Singleton pools for each type
    private static final ArrayList<Point> POINT_POOL = new ArrayList<>(INITIAL_POOL_SIZE);
    private static final ArrayList<PointF> POINTF_POOL = new ArrayList<>(INITIAL_POOL_SIZE);
    private static final ArrayList<Rect> RECT_POOL = new ArrayList<>(INITIAL_POOL_SIZE);
    private static final ArrayList<RectF> RECTF_POOL = new ArrayList<>(INITIAL_POOL_SIZE);
    
    /**
     * Obtains a Point object from the pool, or creates a new one if none are available.
     * @return A Point instance
     */
    public static Point obtainPoint() {
        synchronized (POINT_POOL) {
            if (POINT_POOL.isEmpty()) {
                return new Point();
            } else {
                return POINT_POOL.remove(POINT_POOL.size() - 1);
            }
        }
    }
    
    /**
     * Obtains a Point object with initial values.
     * @param x The x coordinate
     * @param y The y coordinate
     * @return A Point instance with the specified coordinates
     */
    public static Point obtainPoint(int x, int y) {
        Point p = obtainPoint();
        p.set(x, y);
        return p;
    }
    
    /**
     * Recycles a Point object back to the pool.
     * @param point The Point to recycle
     */
    public static void recyclePoint(Point point) {
        if (point == null) return;
        
        // Reset to default values
        point.x = 0;
        point.y = 0;
        
        synchronized (POINT_POOL) {
            POINT_POOL.add(point);
        }
    }
    
    /**
     * Obtains a PointF object from the pool, or creates a new one if none are available.
     * @return A PointF instance
     */
    public static PointF obtainPointF() {
        synchronized (POINTF_POOL) {
            if (POINTF_POOL.isEmpty()) {
                return new PointF();
            } else {
                return POINTF_POOL.remove(POINTF_POOL.size() - 1);
            }
        }
    }
    
    /**
     * Obtains a PointF object with initial values.
     * @param x The x coordinate
     * @param y The y coordinate
     * @return A PointF instance with the specified coordinates
     */
    public static PointF obtainPointF(float x, float y) {
        PointF p = obtainPointF();
        p.set(x, y);
        return p;
    }
    
    /**
     * Recycles a PointF object back to the pool.
     * @param point The PointF to recycle
     */
    public static void recyclePointF(PointF point) {
        if (point == null) return;
        
        // Reset to default values
        point.x = 0;
        point.y = 0;
        
        synchronized (POINTF_POOL) {
            POINTF_POOL.add(point);
        }
    }
    
    /**
     * Obtains a Rect object from the pool, or creates a new one if none are available.
     * @return A Rect instance
     */
    public static Rect obtainRect() {
        synchronized (RECT_POOL) {
            if (RECT_POOL.isEmpty()) {
                return new Rect();
            } else {
                return RECT_POOL.remove(RECT_POOL.size() - 1);
            }
        }
    }
    
    /**
     * Obtains a Rect object with initial values.
     * @param left The left coordinate
     * @param top The top coordinate
     * @param right The right coordinate
     * @param bottom The bottom coordinate
     * @return A Rect instance with the specified coordinates
     */
    public static Rect obtainRect(int left, int top, int right, int bottom) {
        Rect r = obtainRect();
        r.set(left, top, right, bottom);
        return r;
    }
    
    /**
     * Recycles a Rect object back to the pool.
     * @param rect The Rect to recycle
     */
    public static void recycleRect(Rect rect) {
        if (rect == null) return;
        
        // Reset to default values
        rect.setEmpty();
        
        synchronized (RECT_POOL) {
            RECT_POOL.add(rect);
        }
    }
    
    /**
     * Obtains a RectF object from the pool, or creates a new one if none are available.
     * @return A RectF instance
     */
    public static RectF obtainRectF() {
        synchronized (RECTF_POOL) {
            if (RECTF_POOL.isEmpty()) {
                return new RectF();
            } else {
                return RECTF_POOL.remove(RECTF_POOL.size() - 1);
            }
        }
    }
    
    /**
     * Obtains a RectF object with initial values.
     * @param left The left coordinate
     * @param top The top coordinate
     * @param right The right coordinate
     * @param bottom The bottom coordinate
     * @return A RectF instance with the specified coordinates
     */
    public static RectF obtainRectF(float left, float top, float right, float bottom) {
        RectF r = obtainRectF();
        r.set(left, top, right, bottom);
        return r;
    }
    
    /**
     * Recycles a RectF object back to the pool.
     * @param rect The RectF to recycle
     */
    public static void recycleRectF(RectF rect) {
        if (rect == null) return;
        
        // Reset to default values
        rect.setEmpty();
        
        synchronized (RECTF_POOL) {
            RECTF_POOL.add(rect);
        }
    }
    
    /**
     * Clears all object pools, releasing all references.
     * This should be called when the game is exiting or when the pools are no longer needed.
     */
    public static void clearPools() {
        synchronized (POINT_POOL) {
            POINT_POOL.clear();
        }
        synchronized (POINTF_POOL) {
            POINTF_POOL.clear();
        }
        synchronized (RECT_POOL) {
            RECT_POOL.clear();
        }
        synchronized (RECTF_POOL) {
            RECTF_POOL.clear();
        }
    }
    
    /**
     * Gets the current size of the Point object pool.
     * @return The number of Point objects currently in the pool
     */
    public static int getPointPoolSize() {
        synchronized (POINT_POOL) {
            return POINT_POOL.size();
        }
    }
    
    /**
     * Gets the current size of the PointF object pool.
     * @return The number of PointF objects currently in the pool
     */
    public static int getPointFPoolSize() {
        synchronized (POINTF_POOL) {
            return POINTF_POOL.size();
        }
    }
    
    /**
     * Gets the current size of the Rect object pool.
     * @return The number of Rect objects currently in the pool
     */
    public static int getRectPoolSize() {
        synchronized (RECT_POOL) {
            return RECT_POOL.size();
        }
    }
    
    /**
     * Gets the current size of the RectF object pool.
     * @return The number of RectF objects currently in the pool
     */
    public static int getRectFPoolSize() {
        synchronized (RECTF_POOL) {
            return RECTF_POOL.size();
        }
    }
}