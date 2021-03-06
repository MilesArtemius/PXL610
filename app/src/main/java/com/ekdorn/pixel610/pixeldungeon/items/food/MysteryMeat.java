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
import com.ekdorn.pixel610.pixeldungeon.actors.buffs.Buff;
import com.ekdorn.pixel610.pixeldungeon.actors.buffs.Burning;
import com.ekdorn.pixel610.pixeldungeon.actors.buffs.Hunger;
import com.ekdorn.pixel610.pixeldungeon.actors.buffs.Paralysis;
import com.ekdorn.pixel610.pixeldungeon.actors.buffs.Poison;
import com.ekdorn.pixel610.pixeldungeon.actors.buffs.Roots;
import com.ekdorn.pixel610.pixeldungeon.actors.buffs.Slow;
import com.ekdorn.pixel610.pixeldungeon.actors.hero.Hero;
import com.ekdorn.pixel610.pixeldungeon.sprites.ItemSpriteSheet;
import com.ekdorn.pixel610.pixeldungeon.utils.GLog;
import com.ekdorn.pixel610.utils.Random;

public class MysteryMeat extends Food {

	@Override
	public void finish() {
		name = Babylon.get().getFromResources("food_meat");
		image = ItemSpriteSheet.MEAT;
		energy = Hunger.STARVING - Hunger.HUNGRY;
		message = Babylon.get().getFromResources("food_meat_message");
	}
	
	@Override
	public void execute( Hero hero, String action ) {
		
		super.execute( hero, action );
		
		if (action.equals( Babylon.get().getFromResources("food_action") )) {
			
			switch (Random.Int( 5 )) {
			case 0:
				GLog.w(Babylon.get().getFromResources("food_meat_eff0"));
				Buff.affect( hero, Burning.class ).reignite( hero );
				break;
			case 1:
				GLog.w(Babylon.get().getFromResources("food_meat_eff1"));
				Buff.prolong( hero, Roots.class, Paralysis.duration( hero ) );
				break;
			case 2:
				GLog.w(Babylon.get().getFromResources("food_meat_eff2"));
				Buff.affect( hero, Poison.class ).set( Poison.durationFactor( hero ) * hero.HT / 5 );
				break;
			case 3:
				GLog.w(Babylon.get().getFromResources("food_meat_eff3"));
				Buff.prolong( hero, Slow.class, Slow.duration( hero ) );
				break;
			}
		}
	}
	
	@Override
	public String info() {
		return Babylon.get().getFromResources("food_meat_desc");
	}
	
	public int price() {
		return 5 * quantity;
	};
}
