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
import com.ekdorn.pixel610.pixeldungeon.actors.buffs.Invisibility;
import com.ekdorn.pixel610.pixeldungeon.actors.buffs.Weakness;
import com.ekdorn.pixel610.pixeldungeon.actors.hero.Hero;
import com.ekdorn.pixel610.pixeldungeon.effects.Flare;
import com.ekdorn.pixel610.pixeldungeon.effects.particles.ShadowParticle;
import com.ekdorn.pixel610.pixeldungeon.items.Item;
import com.ekdorn.pixel610.pixeldungeon.utils.GLog;

public class ScrollOfRemoveCurse extends Scroll {

	@Override
	public void finish() {
		name = Babylon.get().getFromResources("scroll_removecurse");
	}
	
	@Override
	protected void doRead() {
		
		new Flare( 6, 32 ).show( curUser.sprite, 2f ) ;
		Sample.INSTANCE.play( Assets.SND_READ );
		Invisibility.dispel();
		
		boolean procced = uncurse( curUser, curUser.belongings.backpack.items.toArray( new Item[0] ) ); 
		procced = uncurse( curUser, 
			curUser.belongings.weapon, 
			curUser.belongings.armor, 
			curUser.belongings.ring1, 
			curUser.belongings.ring2 ) || procced;
		
		Weakness.detach( curUser, Weakness.class );
		
		if (procced) {
			GLog.p( Babylon.get().getFromResources("scroll_removecurse_proceed") );
		} else {		
			GLog.i( Babylon.get().getFromResources("scroll_removecurse_nothing") );
		}
		
		setKnown();
		
		readAnimation();
	}
	
	@Override
	public String desc() {
		return Babylon.get().getFromResources("scroll_removecurse_desc");
	}
	
	public static boolean uncurse( Hero hero, Item... items ) {
		
		boolean procced = false;
		for (int i=0; i < items.length; i++) {
			Item item = items[i];
			if (item != null && item.cursed) {
				item.cursed = false;
				procced = true;
			}
		}
		
		if (procced) {
			hero.sprite.emitter().start( ShadowParticle.UP, 0.05f, 10 );
		}
		
		return procced;
	}
	
	@Override
	public int price() {
		return isKnown() ? 30 * quantity : super.price();
	}
}
