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
package com.ekdorn.pixel610.pixeldungeon.actors.mobs.npcs;

import com.ekdorn.pixel610.pixeldungeon.Babylon;
import com.ekdorn.pixel610.pixeldungeon.Dungeon;
import com.ekdorn.pixel610.pixeldungeon.actors.Char;
import com.ekdorn.pixel610.pixeldungeon.actors.buffs.Buff;

import com.ekdorn.pixel610.pixeldungeon.sprites.RatKingSprite;
import com.ekdorn.pixel610.pixeldungeon.utils.Utils;
import com.ekdorn.pixel610.utils.Random;

public class RatKing extends NPC {

	public RatKing() {
		switch (Random.Int( 3 )) {
			case 0:
				name = Babylon.get().getFromResources("mob_ratking_customname0");
				break;
			case 1:
				name = Babylon.get().getFromResources("mob_ratking_customname1");
				break;
			case 2:
				name = Babylon.get().getFromResources("mob_ratking_customname2");
				break;
			case 3:
				name = Babylon.get().getFromResources("mob_ratking_customname3");
				break;
			default:
				name = Babylon.get().getFromResources("mob_ratking");
				break;
		}
	}

	{
		name = Babylon.get().getFromResources("mob_ratking");
		spriteClass = RatKingSprite.class;
		
		state = SLEEPEING;
	}
	
	@Override
	public int defenseSkill( Char enemy ) {
		return 1000;
	}
	
	@Override
	public float speed() {
		return 2f;
	}
	
	@Override
	protected Char chooseEnemy() {
		return null;
	}
	
	@Override
	public void damage( int dmg, Object src ) {
	}
	
	@Override
	public void add( Buff buff ) {
	}
	
	@Override
	public boolean reset() {
		return true;
	}
	
	@Override
	public void interact() {
		sprite.turnTo( pos, Dungeon.hero.pos );
		if (state == SLEEPEING) {
			notice();
			yell(Utils.format(Babylon.get().getFromResources("mob_ratking_idle0"), Dungeon.hero.heroClass.title()));
			state = WANDERING;
		} else {
			yell(Babylon.get().getFromResources("mob_ratking_idle1"));
		}
	}
	
	@Override
	public String description() {
		return
				Babylon.get().getFromResources("mob_ratking_desc");
	}
}
