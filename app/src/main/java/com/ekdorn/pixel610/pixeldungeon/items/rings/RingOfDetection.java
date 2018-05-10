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
package com.ekdorn.pixel610.pixeldungeon.items.rings;

import com.ekdorn.pixel610.pixeldungeon.Babylon;
import com.ekdorn.pixel610.pixeldungeon.Dungeon;
import com.ekdorn.pixel610.pixeldungeon.actors.hero.Hero;

public class RingOfDetection extends Ring {

	{
		name = Babylon.get().getFromResources("ring_detection");
	}
	
	@Override
	public boolean doEquip( Hero hero ) {
		if (super.doEquip( hero )) {
			Dungeon.hero.search( false );
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	protected RingBuff buff( ) {
		return new Detection();
	}
	
	@Override
	public String desc() {
		return isKnown() ?
				Babylon.get().getFromResources("ring_detection_desc") :
			super.desc();
	}
	
	public class Detection extends RingBuff {
	}
}
