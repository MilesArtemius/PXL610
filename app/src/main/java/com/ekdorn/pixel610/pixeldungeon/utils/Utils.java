/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
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
package com.ekdorn.pixel610.pixeldungeon.utils;

import com.ekdorn.pixel610.pixeldungeon.Babylon;

import java.util.Locale;

public class Utils {

	public static String capitalize( String str ) {
		return Character.toUpperCase( str.charAt( 0 ) ) + str.substring( 1 );
	}
	
	public static String format( String format, Object...args ) {
		return String.format(Babylon.get().getCurrent(), format, args );
	}
	
	public static String VOWELS	= "aoeiu";
	
	public static String indefinite( String noun ) { //TODO: manage;
		if (Babylon.get().getCurrent().equals(Locale.ENGLISH)) {
			if (noun.length() == 0) {
				return "a";
			} else {
				return (VOWELS.indexOf(Character.toLowerCase(noun.charAt(0))) != -1 ? "an " : "a ") + noun;
			}
		} else return "";
	}
}
