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
import com.ekdorn.pixel610.pixeldungeon.actors.hero.Hero;
import com.ekdorn.pixel610.pixeldungeon.effects.SpellSprite;
import com.ekdorn.pixel610.pixeldungeon.effects.particles.EnergyParticle;
import com.ekdorn.pixel610.pixeldungeon.utils.GLog;

public class ScrollOfRecharging extends Scroll {

	@Override
	public void finish() {
		name = Babylon.get().getFromResources("scroll_recharging");
	}
	
	@Override
	protected void doRead() {
		
		int count = curUser.belongings.charge( true );		
		charge( curUser );
		
		Sample.INSTANCE.play( Assets.SND_READ );
		Invisibility.dispel();
		
		if (count > 0) {
			GLog.i(Babylon.get().getFromResources("scroll_recharging_recharge") + (count > 1 ? "s" : "") );
			SpellSprite.show( curUser, SpellSprite.CHARGE );
		} else {
			GLog.i(Babylon.get().getFromResources("scroll_recharging_nothing"));
		}
		setKnown();
		
		readAnimation();
	}
	
	@Override
	public String desc() {
		return Babylon.get().getFromResources("scroll_recharging_desc");
	}
	
	public static void charge( Hero hero ) {
		hero.sprite.centerEmitter().burst( EnergyParticle.FACTORY, 15 );
	}
	
	@Override
	public int price() {
		return isKnown() ? 40 * quantity : super.price();
	}
}
