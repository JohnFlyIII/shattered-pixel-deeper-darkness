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

import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.watabou.noosa.Game;

//wrapper for libGDX reflection
public class Reflection {
	
	public static boolean isMemberClass( Class<?> cls ){
		return ClassReflection.isMemberClass(cls);
	}
	
	public static boolean isStatic( Class<?> cls ){
		return ClassReflection.isStaticClass(cls);
	}
	
	public static <T> T newInstance( Class<T> cls ){
		try {
			return ClassReflection.newInstance(cls);
		} catch (Exception e) {
			Game.reportException(e);
			return null;
		}
	}
	
	public static <T> T newInstanceUnhandled( Class<T> cls ) throws Exception {
		return ClassReflection.newInstance(cls);
	}
	
	/**
	 * Creates a new instance of a class with type safety.
	 * This is a type-safe alternative to newInstance for raw Class objects.
	 *
	 * @param <T> The expected type of the instance
	 * @param cls The class to instantiate
	 * @param superType The superclass or interface that the returned instance should extend or implement
	 * @return A new instance of the specified class, or null if it cannot be instantiated
	 */
	public static <T> T newInstanceSafe(Class<?> cls, Class<T> superType) {
		if (cls != null && superType.isAssignableFrom(cls)) {
			try {
				@SuppressWarnings("unchecked")
				T instance = (T) ClassReflection.newInstance(cls);
				return instance;
			} catch (Exception e) {
				Game.reportException(e);
				return null;
			}
		}
		return null;
	}
	
	public static Class<?> forName( String name ){
		try {
			return ClassReflection.forName( name );
		} catch (Exception e) {
			Game.reportException(e);
			return null;
		}
	}
	
	public static Class<?> forNameUnhandled( String name ) throws Exception {
		return ClassReflection.forName( name );
	}
	
	/**
	 * Returns a class of the specified type from the given class name.
	 * This is a type-safe alternative to forName.
	 * 
	 * @param <T> The expected type of the class
	 * @param name The fully qualified class name
	 * @param superType The superclass or interface that the returned class should extend or implement
	 * @return A class object of the specified type, or null if not found or not of the right type
	 */
	public static <T> Class<? extends T> forNameSafe(String name, Class<T> superType) {
		Class<?> cls = forName(name);
		if (cls != null && superType.isAssignableFrom(cls)) {
			@SuppressWarnings("unchecked")
			Class<? extends T> result = (Class<? extends T>) cls;
			return result;
		}
		return null;
	}
	
	/**
	 * Creates a properly typed array of classes from the specified class objects.
	 * This avoids unchecked generic array creation warnings.
	 * 
	 * @param <T> The type that all classes should extend or implement
	 * @param componentType The superclass or interface that the array should be typed as
	 * @param classes The classes to include in the array
	 * @return A properly typed array of the specified classes
	 */
	@SafeVarargs
	public static <T> Class<? extends T>[] arrayOf(Class<T> componentType, Class<? extends T>... classes) {
		return classes;
	}
}
