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
package com.ekdorn.pixel610.pixeldungeon.items.weapon.missiles;

import com.ekdorn.pixel610.pixeldungeon.Babylon;
import com.ekdorn.pixel610.pixeldungeon.actors.Char;
import com.ekdorn.pixel610.pixeldungeon.actors.buffs.Bleeding;
import com.ekdorn.pixel610.pixeldungeon.actors.buffs.Buff;
import com.ekdorn.pixel610.pixeldungeon.items.Item;
import com.ekdorn.pixel610.pixeldungeon.sprites.ItemSpriteSheet;
import com.ekdorn.pixel610.utils.Random;

public class Tamahawk extends MissileWeapon {

	@Override
	public void finish() {
		name = Babylon.get().getFromResources("weapon_tomahawk");
		image = ItemSpriteSheet.TOMAHAWK;
		
		STR = 17;
	}
	
	public Tamahawk() {
		this( 1 );
	}
	
	public Tamahawk( int number ) {
		super();
		quantity = number;
	}
	
	@Override
	public int min() {
		return 4;
	}
	
	@Override
	public int max() {
		return 20;
	}
	
	@Override
	public void proc( Char attacker, Char defender, int damage ) {
		super.proc( attacker, defender, damage );
		Buff.affect( defender, Bleeding.class ).set( damage );
	}	
	
	@Override
	public String desc() {
		return Babylon.get().getFromResources("weapon_tomahawk_desc");
	}
	
	@Override
	public Item random() {
		quantity = Random.Int( 5, 12 );
		return this;
	}
	
	@Override
	public int price() {
		return 20 * quantity;
	}
}
