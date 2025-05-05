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

package com.shatteredpixel.shatteredpixeldungeon.effects.particles;

import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.particles.Emitter.Factory;
import com.watabou.noosa.particles.PixelParticle;

public class FlameParticle extends PixelParticle.Shrinking {
	
	// Particle pool for FlameParticles
	private static final java.util.ArrayList<FlameParticle> flamePool = new java.util.ArrayList<>(50);
	private static final int FLAME_POOL_LIMIT = 100;
	
	public static final Emitter.Factory FACTORY = new Factory() {
		@Override
		public void emit( Emitter emitter, int index, float x, float y ) {
			FlameParticle p = getFlameParticle();
			p.reset( x, y );
			emitter.add( p );
		}
		@Override
		public boolean lightMode() {
			return true;
		}
	};
	
	// Get a particle from the pool or create a new one
	private static FlameParticle getFlameParticle() {
		synchronized (flamePool) {
			if (flamePool.isEmpty()) {
				return new FlameParticle();
			} else {
				return flamePool.remove(flamePool.size() - 1);
			}
		}
	}
	
	@Override
	protected void recycle() {
		// Add to flame pool instead of parent class pool
		synchronized (flamePool) {
			if (flamePool.size() < FLAME_POOL_LIMIT) {
				flamePool.add(this);
			}
		}
	}
	
	// Clear the pool (called when game is paused or reset)
	public static void clearPool() {
		synchronized (flamePool) {
			flamePool.clear();
		}
	}
	
	public FlameParticle() {
		super();
		
		color( 0xEE7722 );
		lifespan = 0.6f;
		
		acc.set( 0, -80 );
	}
	
	public void reset( float x, float y ) {
		revive();
		
		this.x = x;
		this.y = y;
		
		left = lifespan;
		
		size = 4;
		speed.set( 0 );
	}
	
	@Override
	public void update() {
		super.update();
		float p = left / lifespan;
		am = p > 0.8f ? (1 - p) * 5 : 1;
	}
}