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

import com.ekdorn.pixel610.pixeldungeon.Babylon;
import com.ekdorn.pixel610.pixeldungeon.actors.hero.HeroSubClass;
import com.ekdorn.pixel610.pixeldungeon.items.TomeOfMastery;
import com.ekdorn.pixel610.pixeldungeon.sprites.ItemSprite;
import com.ekdorn.pixel610.pixeldungeon.ui.HighlightedText;
import com.ekdorn.pixel610.pixeldungeon.ui.RedButton;
import com.ekdorn.pixel610.pixeldungeon.ui.Window;
import com.ekdorn.pixel610.pixeldungeon.utils.Utils;

public class WndChooseWay extends Window {
	
	private static final int WIDTH		= 120;
	private static final int BTN_HEIGHT	= 18;
	private static final float GAP		= 2;
	
	public WndChooseWay( final TomeOfMastery tome, final HeroSubClass way1, final HeroSubClass way2 ) {
		
		super();
		
		float bottom = createCommonStuff( tome, way1.desc() + "\n\n" + way2.desc() + "\n\n" + Babylon.get().getFromResources("wnd_chooseway_mastery") );
		
		RedButton btnWay1 = new RedButton( Utils.capitalize( way1.title() ) ) {
			@Override
			protected void onClick() {
				hide();
				tome.choose( way1 );
			}
		};
		btnWay1.setRect( 0, bottom + GAP, (WIDTH - GAP) / 2, BTN_HEIGHT );
		add( btnWay1 );
		
		RedButton btnWay2 = new RedButton( Utils.capitalize( way2.title() ) ) {
			@Override
			protected void onClick() {
				hide();
				tome.choose( way2 );
			}
		};
		btnWay2.setRect( btnWay1.right() + GAP, btnWay1.top(), btnWay1.width(), BTN_HEIGHT );
		add( btnWay2 );
		
		RedButton btnCancel = new RedButton( Babylon.get().getFromResources("wnd_chooseway_cancel") ) {
			@Override
			protected void onClick() {
				hide();
			}
		};
		btnCancel.setRect( 0, btnWay2.bottom() + GAP, WIDTH, BTN_HEIGHT );
		add( btnCancel );
		
		resize( WIDTH, (int)btnCancel.bottom() );
	}
	
	public WndChooseWay( final TomeOfMastery tome, final HeroSubClass way ) {
		
		super();
		
		float bottom = createCommonStuff( tome, way.desc() + "\n\n" + Utils.format( Babylon.get().getFromResources("wnd_chooseway_remastery"), Utils.indefinite( way.title() ) ) );
		
		RedButton btnWay = new RedButton( Babylon.get().getFromResources("wnd_chooseway_yes") ) {
			@Override
			protected void onClick() {
				hide();
				tome.choose( way );
			}
		};
		btnWay.setRect( 0, bottom + GAP, WIDTH, BTN_HEIGHT );
		add( btnWay );
		
		RedButton btnCancel = new RedButton( Babylon.get().getFromResources("wnd_chooseway_no") ) {
			@Override
			protected void onClick() {
				hide();
			}
		};
		btnCancel.setRect( 0, btnWay.bottom() + GAP, WIDTH, BTN_HEIGHT );
		add( btnCancel );
		
		resize( WIDTH, (int)btnCancel.bottom() );
	}
	
	private float createCommonStuff( TomeOfMastery tome, String text ) {
		IconTitle titlebar = new IconTitle();
		titlebar.icon( new ItemSprite( tome.image(), null ) );
		titlebar.label( tome.name() );
		titlebar.setRect( 0, 0, WIDTH, 0 );
		add( titlebar );
		
		HighlightedText hl = new HighlightedText( 6 );
		hl.text( text, WIDTH );
		hl.setPos( titlebar.left(), titlebar.bottom() + GAP );
		add( hl );
		
		return hl.bottom();
	}
}
