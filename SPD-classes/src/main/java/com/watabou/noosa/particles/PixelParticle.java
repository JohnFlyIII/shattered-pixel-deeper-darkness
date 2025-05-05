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

package com.watabou.noosa.particles;

import com.watabou.noosa.Game;
import com.watabou.noosa.PseudoPixel;

import java.util.ArrayList;

public class PixelParticle extends PseudoPixel {

	protected float size;
	
	protected float lifespan;
	protected float left;
	
	// Particle pooling system
	private static final ArrayList<PixelParticle> regularPool = new ArrayList<>(50);
	private static final ArrayList<Shrinking> shrinkingPool = new ArrayList<>(50);
	
	// Pool size limit to prevent memory bloat
	private static final int POOL_LIMIT = 300;
	
	public PixelParticle() {
		super();
		
		origin.set( +0.5f );
	}
	
	/**
	 * Get a regular particle from the pool or create a new one
	 */
	public static PixelParticle get() {
		synchronized (regularPool) {
			if (regularPool.isEmpty()) {
				return new PixelParticle();
			} else {
				return regularPool.remove(regularPool.size() - 1);
			}
		}
	}
	
	/**
	 * Get a shrinking particle from the pool or create a new one
	 */
	public static Shrinking getShrinking() {
		synchronized (shrinkingPool) {
			if (shrinkingPool.isEmpty()) {
				return new Shrinking();
			} else {
				return shrinkingPool.remove(shrinkingPool.size() - 1);
			}
		}
	}
	
	/**
	 * Return this particle to the appropriate pool
	 */
	@Override
	public void kill() {
		super.kill();
		recycle();
	}
	
	/**
	 * Recycle this particle into the pool
	 */
	protected void recycle() {
		// Only add to pool if it's not too big
		if (this instanceof Shrinking) {
			synchronized (shrinkingPool) {
				if (shrinkingPool.size() < POOL_LIMIT) {
					shrinkingPool.add((Shrinking)this);
				}
			}
		} else if (getClass() == PixelParticle.class) { // Only base class, not other subclasses
			synchronized (regularPool) {
				if (regularPool.size() < POOL_LIMIT) {
					regularPool.add(this);
				}
			}
		}
	}
	
	/**
	 * Clear all particle pools
	 */
	public static void clearPools() {
		synchronized (regularPool) { regularPool.clear(); }
		synchronized (shrinkingPool) { shrinkingPool.clear(); }
	}
	
	public void reset( float x, float y, int color, float size, float lifespan ) {
		revive();
		
		this.x = x;
		this.y = y;
		
		color( color );
		size( this.size = size );
			
		this.left = this.lifespan = lifespan;
	}
	
	@Override
	public void update() {
		super.update();

		if ((left -= Game.elapsed) <= 0) {
			kill();
		}
	}
	
	public static class Shrinking extends PixelParticle {
		@Override
		public void update() {
			super.update();
			size( size * left / lifespan );
		}
	}
}
