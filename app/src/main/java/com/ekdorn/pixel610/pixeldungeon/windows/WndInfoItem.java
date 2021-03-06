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
package com.ekdorn.pixel610.pixeldungeon.windows;

import android.util.Log;

import com.ekdorn.pixel610.noosa.BitmapTextMultiline;
import com.ekdorn.pixel610.pixeldungeon.Babylon;
import com.ekdorn.pixel610.pixeldungeon.Dungeon;
import com.ekdorn.pixel610.pixeldungeon.items.Heap;
import com.ekdorn.pixel610.pixeldungeon.items.Heap.Type;
import com.ekdorn.pixel610.pixeldungeon.items.Item;
import com.ekdorn.pixel610.pixeldungeon.scenes.PixelScene;
import com.ekdorn.pixel610.pixeldungeon.sprites.ItemSprite;
import com.ekdorn.pixel610.pixeldungeon.ui.ItemSlot;
import com.ekdorn.pixel610.pixeldungeon.ui.Window;
import com.ekdorn.pixel610.pixeldungeon.utils.Utils;

public class WndInfoItem extends Window {
	
	private static final float GAP	= 2;
	
	private static final int WIDTH = 120;
	
	public WndInfoItem( Heap heap ) {
		
		super();
		
		if (heap.type == Heap.Type.HEAP || heap.type == Heap.Type.FOR_SALE) {
			
			Item item = heap.peek();
			
			int color = TITLE_COLOR;
			if (item.levelKnown) {
				if (item.level() < 0) {
					color = ItemSlot.DEGRADED;				
				} else if (item.level() > 0) {
					color = item.isBroken() ? ItemSlot.WARNING : ItemSlot.UPGRADED;				
				}
			}
			fillFields( item.image(), item.glowing(), color, item.toString(), item.info() );
			
		} else {
			
			String title;
			String info;
			
			if (heap.type == Type.CHEST || heap.type == Type.MIMIC) {
				title = Babylon.get().getFromResources("wnd_infoitem_chest");
				info = Babylon.get().getFromResources("wnd_infoitem_wontknow");
			} else if (heap.type == Type.TOMB) {
				title = Babylon.get().getFromResources("wnd_infoitem_tomb");
				info = Babylon.get().getFromResources("wnd_infoitem_owner");
			} else if (heap.type == Type.SKELETON) {
				title = Babylon.get().getFromResources("wnd_infoitem_skeletalremains");
				info = Babylon.get().getFromResources("wnd_infoitem_remains");
			} else if (heap.type == Type.CRYSTAL_CHEST) {
				title = Babylon.get().getFromResources("wnd_infoitem_crystalchest");
				info = Utils.format( Babylon.get().getFromResources("wnd_infoitem_inside"), Utils.indefinite( heap.peek().name() ) );
			} else {
				title = Babylon.get().getFromResources("wnd_infoitem_lockedchest");
				info = Babylon.get().getFromResources("wnd_infoitem_needkey");
			}
			
			fillFields( heap.image(), heap.glowing(), TITLE_COLOR, title, info );
			
		}
	}
	
	public WndInfoItem( Item item ) {
		
		super();
		
		int color = TITLE_COLOR;
		if (item.levelKnown) {
			if (item.level() < 0 || item.isBroken()) {
				color = ItemSlot.DEGRADED;				
			} else if (item.level() > 0) {
				color = ItemSlot.UPGRADED;				
			}
		}
		
		fillFields( item.image(), item.glowing(), color, item.toString(), item.info() );
	}
	
	private void fillFields( int image, ItemSprite.Glowing glowing, int titleColor, String title, String info ) {
		
		IconTitle titlebar = new IconTitle();
		titlebar.icon( new ItemSprite( image, glowing ) );
		titlebar.label( Utils.capitalize( title ), titleColor );
		titlebar.setRect( 0, 0, WIDTH, 0 );
		add( titlebar );
		
		BitmapTextMultiline txtInfo = PixelScene.createMultiline( info, 6 );
		txtInfo.maxWidth = WIDTH;
		txtInfo.measure();
		txtInfo.x = titlebar.left();
		txtInfo.y = titlebar.bottom() + GAP;
		add( txtInfo );
		
		resize( WIDTH, (int)(txtInfo.y + txtInfo.height()) );
	}
}
