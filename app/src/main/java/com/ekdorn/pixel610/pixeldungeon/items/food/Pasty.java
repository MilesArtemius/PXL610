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
package com.ekdorn.pixel610.pixeldungeon.items.food;

import com.ekdorn.pixel610.pixeldungeon.Babylon;
import com.ekdorn.pixel610.pixeldungeon.actors.buffs.Hunger;
import com.ekdorn.pixel610.pixeldungeon.sprites.ItemSpriteSheet;

public class Pasty extends Food {

	@Override
	public void finish() {
		name = Babylon.get().getFromResources("food_pasty");
		image = ItemSpriteSheet.PASTY;
		energy = Hunger.STARVING;
	}
	
	@Override
	public String info() {
		return Babylon.get().getFromResources("food_pasty_desc");
	}
	
	@Override
	public int price() {
		return 20 * quantity;
	}
}
