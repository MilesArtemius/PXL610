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
package com.ekdorn.pixel610.pixeldungeon.items.scrolls;

import com.ekdorn.pixel610.noosa.audio.Sample;
import com.ekdorn.pixel610.pixeldungeon.Assets;
import com.ekdorn.pixel610.pixeldungeon.Babylon;
import com.ekdorn.pixel610.pixeldungeon.Dungeon;
import com.ekdorn.pixel610.pixeldungeon.actors.buffs.Buff;
import com.ekdorn.pixel610.pixeldungeon.actors.buffs.Invisibility;
import com.ekdorn.pixel610.pixeldungeon.actors.buffs.Terror;
import com.ekdorn.pixel610.pixeldungeon.actors.mobs.Mob;
import com.ekdorn.pixel610.pixeldungeon.effects.Flare;
import com.ekdorn.pixel610.pixeldungeon.levels.Level;
import com.ekdorn.pixel610.pixeldungeon.utils.GLog;

public class ScrollOfTerror extends Scroll {

	@Override
	public void finish() {
		name = Babylon.get().getFromResources("scroll_terror");
	}
	
	@Override
	protected void doRead() {
		
		new Flare( 5, 32 ).color( 0xFF0000, true ).show( curUser.sprite, 2f );
		Sample.INSTANCE.play( Assets.SND_READ );
		Invisibility.dispel();
		
		int count = 0;
		Mob affected = null;
		for (Mob mob : Dungeon.level.mobs.toArray( new Mob[0] )) {
			if (Level.fieldOfView[mob.pos]) {
				Buff.affect( mob, Terror.class, Terror.DURATION ).object = curUser.id();
				
				count++;
				affected = mob;
			}
		}
		
		switch (count) {
		case 0:
			GLog.i(Babylon.get().getFromResources("scroll_terror_aff0"));
			break;
		case 1:
			GLog.i(Babylon.get().getFromResources("scroll_terror_aff1"), affected.name );
			break;
		default:
			GLog.i(Babylon.get().getFromResources("scroll_terror_affmany"));
		}
		setKnown();
		
		readAnimation();
	}
	
	@Override
	public String desc() {
		return Babylon.get().getFromResources("scroll_terror_desc");
	}
	
	@Override
	public int price() {
		return isKnown() ? 50 * quantity : super.price();
	}
}
