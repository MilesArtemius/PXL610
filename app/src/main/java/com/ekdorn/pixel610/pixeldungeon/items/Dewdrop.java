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
package com.ekdorn.pixel610.pixeldungeon.items;

import com.ekdorn.pixel610.noosa.audio.Sample;
import com.ekdorn.pixel610.pixeldungeon.Assets;
import com.ekdorn.pixel610.pixeldungeon.Babylon;
import com.ekdorn.pixel610.pixeldungeon.Dungeon;
import com.ekdorn.pixel610.pixeldungeon.actors.hero.Hero;
import com.ekdorn.pixel610.pixeldungeon.actors.hero.HeroClass;
import com.ekdorn.pixel610.pixeldungeon.effects.Speck;
import com.ekdorn.pixel610.pixeldungeon.sprites.CharSprite;
import com.ekdorn.pixel610.pixeldungeon.sprites.ItemSpriteSheet;

public class Dewdrop extends Item {

	@Override
	public void finish() {
		switch (depth) {
			case 0:
				name = Babylon.get().getFromResources("item_dewdrop_0");
				break;
			case 1:
				name = Babylon.get().getFromResources("item_dewdrop_1");
				break;
			case 2:
				name = Babylon.get().getFromResources("item_dewdrop_2");
				break;
			case 3:
				name = Babylon.get().getFromResources("item_dewdrop_3");
				break;
			case 4:
				name = Babylon.get().getFromResources("item_dewdrop_4");
				break;
			default:
				name = Babylon.get().getFromResources("item_dewdrop_0");
				break;
		}
		image = ItemSpriteSheet.DEWDROP;
		
		stackable = true;
	}
	
	@Override
	public boolean doPickUp( Hero hero ) {
		
		DewVial vial = hero.belongings.getItem( DewVial.class );
		
		if (hero.HP < hero.HT || vial == null || vial.isFull()) {
			
			int value = 1 + (Dungeon.depth - 1) / 5;
			if (hero.heroClass == HeroClass.HUNTRESS) {
				value++;
			}
			
			int effect = Math.min( hero.HT - hero.HP, value * quantity );
			if (effect > 0) {
				hero.HP += effect;
				hero.sprite.emitter().burst( Speck.factory( Speck.HEALING ), 1 );
				hero.sprite.showStatus( CharSprite.POSITIVE, Babylon.get().getFromResources("item_dewdrop_value"), effect );
			}
			
		} else if (vial != null) {
			
			vial.collectDew( this );
			
		}
		
		Sample.INSTANCE.play( Assets.SND_DEWDROP );
		hero.spendAndNext( TIME_TO_PICK_UP );
		
		return true;
	}
	
	@Override
	public String info() {
		switch (depth) {
			case 0:
				return Babylon.get().getFromResources("item_dewdrop_desc_0");
			case 1:
				return Babylon.get().getFromResources("item_dewdrop_desc_1");
			case 2:
				return Babylon.get().getFromResources("item_dewdrop_desc_2");
			case 3:
				return Babylon.get().getFromResources("item_dewdrop_desc_3");
			case 4:
				return Babylon.get().getFromResources("item_dewdrop_desc_4");
			default:
				return Babylon.get().getFromResources("item_dewdrop_desc_0");
		}
	}
}
