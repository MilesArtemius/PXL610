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
package com.ekdorn.pixel610.pixeldungeon.items.keys;

import com.ekdorn.pixel610.pixeldungeon.Babylon;
import com.ekdorn.pixel610.pixeldungeon.sprites.ItemSpriteSheet;

public class SkeletonKey extends Key {

	@Override
	public void finish() {
		switch (depth) {
			case 0:
				name = Babylon.get().getFromResources("key_skeleton_0");
				break;
			case 1:
				name = Babylon.get().getFromResources("key_skeleton_1");
				break;
			default:
				name = Babylon.get().getFromResources("key_skeleton_0");
				break;
		}
		image = ItemSpriteSheet.SKELETON_KEY;
	}
	
	@Override
	public String info() {
		return Babylon.get().getFromResources("key_skeleton_desc");
	}
}
