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

import java.util.Collection;
import java.util.LinkedList;

import com.ekdorn.pixel610.noosa.audio.Sample;
import com.ekdorn.pixel610.noosa.tweeners.AlphaTweener;
import com.ekdorn.pixel610.pixeldungeon.Assets;
import com.ekdorn.pixel610.pixeldungeon.Babylon;
import com.ekdorn.pixel610.pixeldungeon.Badges;
import com.ekdorn.pixel610.pixeldungeon.Dungeon;
import com.ekdorn.pixel610.pixeldungeon.Statistics;
import com.ekdorn.pixel610.pixeldungeon.actors.buffs.Buff;
import com.ekdorn.pixel610.pixeldungeon.actors.buffs.Burning;
import com.ekdorn.pixel610.pixeldungeon.actors.buffs.Frost;
import com.ekdorn.pixel610.pixeldungeon.actors.hero.Hero;
import com.ekdorn.pixel610.pixeldungeon.actors.mobs.Mimic;
import com.ekdorn.pixel610.pixeldungeon.actors.mobs.Wraith;
import com.ekdorn.pixel610.pixeldungeon.effects.CellEmitter;
import com.ekdorn.pixel610.pixeldungeon.effects.Speck;
import com.ekdorn.pixel610.pixeldungeon.effects.Splash;
import com.ekdorn.pixel610.pixeldungeon.effects.particles.ElmoParticle;
import com.ekdorn.pixel610.pixeldungeon.effects.particles.FlameParticle;
import com.ekdorn.pixel610.pixeldungeon.effects.particles.ShadowParticle;
import com.ekdorn.pixel610.pixeldungeon.items.food.ChargrilledMeat;
import com.ekdorn.pixel610.pixeldungeon.items.food.FrozenCarpaccio;
import com.ekdorn.pixel610.pixeldungeon.items.food.MysteryMeat;
import com.ekdorn.pixel610.pixeldungeon.items.scrolls.Scroll;
import com.ekdorn.pixel610.pixeldungeon.plants.Plant.Seed;
import com.ekdorn.pixel610.pixeldungeon.sprites.ItemSprite;
import com.ekdorn.pixel610.pixeldungeon.sprites.ItemSpriteSheet;
import com.ekdorn.pixel610.pixeldungeon.utils.GLog;
import com.ekdorn.pixel610.utils.Bundlable;
import com.ekdorn.pixel610.utils.Bundle;
import com.ekdorn.pixel610.utils.Random;

public class Heap implements Bundlable {
	
	private static final int SEEDS_TO_POTION = 3;
	
	private static final float FADE_TIME = 0.6f;
	
	public enum Type {
		HEAP, 
		FOR_SALE, 
		CHEST, 
		LOCKED_CHEST, 
		CRYSTAL_CHEST,
		TOMB, 
		SKELETON,
		MIMIC,
		HIDDEN
	}
	public Type type = Type.HEAP;
	
	public int pos = 0;
	
	public ItemSprite sprite;
	
	public LinkedList<Item> items = new LinkedList<Item>();
	
	public int image() {
		int ret;
		switch (type) {
			case HEAP:
			case FOR_SALE:
				return size() > 0 ? items.peek().image() : 0;
			case CHEST:
			case MIMIC:
				ret = ItemSpriteSheet.CHEST;
				break;
			case LOCKED_CHEST:
				ret = ItemSpriteSheet.LOCKED_CHEST;
				break;
			case CRYSTAL_CHEST:
				ret = ItemSpriteSheet.CRYSTAL_CHEST;
				break;
			case TOMB:
				ret = ItemSpriteSheet.TOMB;
				break;
			case SKELETON:
				ret = ItemSpriteSheet.BONES;
				break;
			case HIDDEN:
				ret = ItemSpriteSheet.HIDDEN;
				break;
			default:
				ret = 0;
		}
		return ret + ((Dungeon.depth - 1) / 5) * Assets.ITEMS_LENGTH;
	}
	
	public ItemSprite.Glowing glowing() {
		return (type == Type.HEAP || type == Type.FOR_SALE) && items.size() > 0 ? items.peek().glowing() : null;
	}
	
	public void open( Hero hero ) {
		switch (type) {
		case MIMIC:
			if (Mimic.spawnAt( pos, items ) != null) {
				GLog.n( Babylon.get().getFromResources("item_heap_mimic") );
				destroy();
			} else {
				type = Type.CHEST;
			}
			break;
		case TOMB:
			Wraith.spawnAround( hero.pos );
			break;
		case SKELETON:
			CellEmitter.center( pos ).start( Speck.factory( Speck.RATTLE ), 0.1f, 3 );
			for (Item item : items) {
				if (item.cursed) {
					if (Wraith.spawnAt( pos ) == null) {
						hero.sprite.emitter().burst( ShadowParticle.CURSE, 6 );
						hero.damage( hero.HP / 2, this );
					}
					Sample.INSTANCE.play( Assets.SND_CURSED );
					break;
				}
			}
			break;
		case HIDDEN:
			sprite.alpha( 0 );
			sprite.parent.add( new AlphaTweener( sprite, 1, FADE_TIME ) );
			break;
		default:
		}
		
		if (type != Type.MIMIC) {
			type = Type.HEAP;
			sprite.link();
			sprite.drop();
		}
	}
	
	public int size() {
		return items.size();
	}
	
	public Item pickUp() {
		
		Item item = items.removeFirst();
		if (items.isEmpty()) {
			destroy();
		} else if (sprite != null) {
			sprite.view( image(), glowing() );
		}
		
		return item;
	}
	
	public Item peek() {
		return items.peek();
	}
	
	public void drop( Item item ) {
		
		if (item.stackable) {
			
			Class<?> c = item.getClass();
			for (Item i : items) {
				if (i.getClass() == c) {
					i.quantity += item.quantity;
					item = i;
					break;
				}
			}
			items.remove( item );
			
		}
		
		if (item instanceof Dewdrop) {
			items.add( item );
		} else {
			items.addFirst( item );
		}
		
		if (sprite != null) {
			sprite.view( image(), glowing() );
		}
	}
	
	public void replace( Item a, Item b ) {
		int index = items.indexOf( a );
		if (index != -1) {
			items.remove( index );
			items.add( index, b );
		}
	}
	
	public void burn() {
		
		if (type == Type.MIMIC) {
			Mimic m = Mimic.spawnAt( pos, items );
			if (m != null) {
				Buff.affect( m, Burning.class ).reignite( m );
				m.sprite.emitter().burst( FlameParticle.FACTORY, 5 );
				destroy();
			}
		}
		if (type != Type.HEAP) {
			return;
		}
		
		boolean burnt = false;
		boolean evaporated = false;
		
		for (Item item : items.toArray( new Item[0] )) {
			if (item instanceof Scroll) {
				items.remove( item );
				burnt = true;
			} else if (item instanceof Dewdrop) {
				items.remove( item );
				evaporated = true;
			} else if (item instanceof MysteryMeat) {
				replace( item, ChargrilledMeat.cook( (MysteryMeat)item ) );
				burnt = true;
			}
		}
		
		if (burnt || evaporated) {
			
			if (Dungeon.visible[pos]) {
				if (burnt) {
					burnFX( pos );
				} else {
					evaporateFX( pos );
				}
			}
			
			if (isEmpty()) {
				destroy();
			} else if (sprite != null) {
				sprite.view( image(), glowing() );
			}
			
		}
	}
	
	public void freeze() {
		
		if (type == Type.MIMIC) {
			Mimic m = Mimic.spawnAt( pos, items );
			if (m != null) {
				Buff.prolong( m, Frost.class, Frost.duration( m ) * Random.Float( 1.0f, 1.5f ) );
				destroy();
			}
		}
		if (type != Type.HEAP) {
			return;
		}
		
		boolean frozen = false;
		for (Item item : items.toArray( new Item[0] )) {
			if (item instanceof MysteryMeat) {
				replace( item, FrozenCarpaccio.cook( (MysteryMeat)item ) );
				frozen = true;
			}
		}
		
		if (frozen) {
			if (isEmpty()) {
				destroy();
			} else if (sprite != null) {
				sprite.view( image(), glowing() );
			}	
		}
	}
	
	public Item transmute() {
		
		CellEmitter.get( pos ).burst( Speck.factory( Speck.BUBBLE ), 3 );
		Splash.at( pos, 0xFFFFFF, 3 );
		
		float chances[] = new float[items.size()];
		int count = 0;
		
		int index = 0;
		for (Item item : items) {
			if (item instanceof Seed) {
				count += item.quantity;
				chances[index++] = item.quantity;
			} else {
				count = 0;
				break;
			}
		}
		
		if (count >= SEEDS_TO_POTION) {
			
			CellEmitter.get( pos ).burst( Speck.factory( Speck.WOOL ), 6 );
			Sample.INSTANCE.play( Assets.SND_PUFF );
			
			if (Random.Int( count ) == 0) {
				
				CellEmitter.center( pos ).burst( Speck.factory( Speck.EVOKE ), 3 );
				
				destroy();
				
				Statistics.potionsCooked++;
				Badges.validatePotionsCooked();
				
				return Generator.random( Generator.Category.POTION );
				
			} else {
				
				Seed proto = (Seed)items.get( Random.chances( chances ) );
				Class<? extends Item> itemClass = proto.alchemyClass;
				
				destroy();
				
				Statistics.potionsCooked++;
				Badges.validatePotionsCooked();
				
				if (itemClass == null) {
					return Generator.random( Generator.Category.POTION );
				} else {
					try {
						return itemClass.newInstance();
					} catch (Exception e) {
						return null;
					}
				}
			}		
			
		} else {
			return null;
		}
	}
	
	public static void burnFX( int pos ) {
		CellEmitter.get( pos ).burst( ElmoParticle.FACTORY, 6 );
		Sample.INSTANCE.play( Assets.SND_BURNING );
	}
	
	public static void evaporateFX( int pos ) {
		CellEmitter.get( pos ).burst( Speck.factory( Speck.STEAM ), 5 );
	}
	
	public boolean isEmpty() {
		return items == null || items.size() == 0;
	}
	
	public void destroy() {
		Dungeon.level.heaps.remove( this.pos );
		if (sprite != null) {
			sprite.kill();
		}
		items.clear();
		items = null;
	}

	private static final String POS		= "pos";
	private static final String TYPE	= "type";
	private static final String ITEMS	= "items";
	
	@SuppressWarnings("unchecked")
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		pos = bundle.getInt( POS );
		type = Type.valueOf( bundle.getString( TYPE ) );
		items = new LinkedList<Item>( (Collection<Item>) ((Collection<?>) bundle.getCollection( ITEMS )) );
	}

	@Override
	public void storeInBundle( Bundle bundle ) {
		bundle.put( POS, pos );
		bundle.put( TYPE, type.toString() );
		bundle.put( ITEMS, items );
	}
	
}
