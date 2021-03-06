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
package com.ekdorn.pixel610.pixeldungeon.scenes;

import java.util.ArrayList;
import java.util.HashMap;

import com.ekdorn.pixel610.noosa.BitmapText;
import com.ekdorn.pixel610.noosa.BitmapTextMultiline;
import com.ekdorn.pixel610.noosa.Camera;
import com.ekdorn.pixel610.noosa.Game;
import com.ekdorn.pixel610.noosa.Group;
import com.ekdorn.pixel610.noosa.Image;
import com.ekdorn.pixel610.noosa.audio.Sample;
import com.ekdorn.pixel610.noosa.particles.BitmaskEmitter;
import com.ekdorn.pixel610.noosa.particles.Emitter;
import com.ekdorn.pixel610.noosa.ui.Button;
import com.ekdorn.pixel610.pixeldungeon.Assets;
import com.ekdorn.pixel610.pixeldungeon.Babylon;
import com.ekdorn.pixel610.pixeldungeon.Badges;
import com.ekdorn.pixel610.pixeldungeon.Dungeon;
import com.ekdorn.pixel610.pixeldungeon.PXL610;
import com.ekdorn.pixel610.pixeldungeon.actors.hero.HeroClass;
import com.ekdorn.pixel610.pixeldungeon.additional.GameMode;
import com.ekdorn.pixel610.pixeldungeon.effects.BannerSprites;
import com.ekdorn.pixel610.pixeldungeon.effects.Speck;
import com.ekdorn.pixel610.pixeldungeon.effects.BannerSprites.Type;
import com.ekdorn.pixel610.pixeldungeon.ui.Archs;
import com.ekdorn.pixel610.pixeldungeon.ui.ExitButton;
import com.ekdorn.pixel610.pixeldungeon.ui.Icons;
import com.ekdorn.pixel610.pixeldungeon.ui.RedButton;
import com.ekdorn.pixel610.pixeldungeon.windows.WndChallenges;
import com.ekdorn.pixel610.pixeldungeon.windows.WndClass;
import com.ekdorn.pixel610.pixeldungeon.windows.WndMessage;
import com.ekdorn.pixel610.pixeldungeon.windows.WndOptions;
import com.ekdorn.pixel610.pixeldungeon.windows.WndSaver;
import com.ekdorn.pixel610.utils.Callback;
import com.ekdorn.pixel610.pixeldungeon.internet.InDev;

public class StartScene extends PixelScene {

	protected static final float BUTTON_HEIGHT	= 24;
	private static final float GAP				= 2;

	protected static final float WIDTH_P	= 150;
	protected static final float HEIGHT_P	= 220;

	protected static final float WIDTH_L	= 224;
	protected static final float HEIGHT_L	= 124;
	
	private static HashMap<HeroClass, ClassShield> shields = new HashMap<HeroClass, ClassShield>();

	protected float buttonX;
	protected float buttonY;
	
	protected GameButton btnLoad;
	protected GameButton btnNewGame;
	
	private boolean huntressUnlocked;
	protected Group unlock;
	
	public static HeroClass curClass;
	
	@Override
	public void create() {
		
		super.create();
		
		Badges.loadGlobal();
		
		uiCamera.visible = false;
		
		int w = Camera.main.width;
		int h = Camera.main.height;
		
		float width, height;
		if (PXL610.landscape()) {
			width = WIDTH_L;
			height = HEIGHT_L;
		} else {
			width = WIDTH_P;//
			height = HEIGHT_P;
		}

		float left = (w - width) / 2;
		float top = (h - height) / 2; 
		float bottom = h - top;
		
		Archs archs = new Archs();
		archs.setSize( w, h );
		add( archs ); 
		
		Image title = BannerSprites.get( Type.SELECT_YOUR_HERO );
		title.x = align( (w - title.width()) / 2 );
		title.y = align( top );
		add( title );
		
		buttonX = left + 16;
		buttonY = bottom - BUTTON_HEIGHT;
		
		btnNewGame = new GameButton( Babylon.get().getFromResources("startscene_new") ) {
			@Override
			protected void onClick() {
				if (Game.instance.getFileStreamPath(Dungeon.gameFile(curClass)).exists()) {
					StartScene.this.add( new WndOptions( Babylon.get().getFromResources("startscene_really"), Babylon.get().getFromResources("startscene_warning"),
							Babylon.get().getFromResources("startscene_yes"), Babylon.get().getFromResources("startscene_no") ) {
						@Override
						protected void onSelect( int index ) {
							if (index == 0) {
								startNewGame();
							}
						}
					} );
					
				} else {
					startNewGame();
				}
			}
		};
		add( btnNewGame );

		btnLoad = new GameButton( Babylon.get().getFromResources("startscene_load") ) {
			@Override
			protected void onClick() {
				StartScene.this.add(new WndSaver(Babylon.get().getFromResources("save_restoregame"), false, curClass, false));
			}
		};
		add( btnLoad );	
		
		float centralHeight = buttonY - title.y - title.height();

		ArrayList<HeroClass> classes = new ArrayList<>();
		if (PXL610.gamemode().equals(GameMode.original)) {
			classes.add(HeroClass.WARRIOR);
			classes.add(HeroClass.MAGE);
			classes.add(HeroClass.ROGUE);
			classes.add(HeroClass.HUNTRESS);
		} else {
			classes.add(HeroClass.MALE);
			classes.add(HeroClass.FEMALE);
		}

		for (HeroClass cl : classes) {
			ClassShield shield = new ClassShield( cl );
			shields.put( cl, shield );
			add( shield );
		}

		float shieldW, shieldH = 0;
		if (PXL610.landscape()) {
			shieldW = width / classes.size();
			shieldH = Math.min( centralHeight, shieldW );
			top = title.y + title.height + (centralHeight - shieldH) / 2;
			for (int i=0; i < classes.size(); i++) {
				ClassShield shield = shields.get( classes.get(i) );
				shield.setRect( left + i * shieldW, top, shieldW, shieldH );
			}

		} else {
			shieldW = width / 2;
			shieldH = Math.min( centralHeight / 2, shieldW * 1.2f );
			top = title.y + title.height() + centralHeight / 2 - shieldH;
			for (int i=0; i < classes.size(); i++) {
				ClassShield shield = shields.get( classes.get(i) );
				if (PXL610.gamemode().equals(GameMode.original)) {
					shield.setRect(
							left + (i % 2) * shieldW,
							top + (i / 2) * shieldH,
							shieldW, shieldH);
				} else {
					shield.setRect(
							(float) (left + 0.5 * shieldW),
							top + i * shieldH,
							shieldW, shieldH);
				}
			}
		}

        if (PXL610.gamemode().equals(GameMode.original)) {
            ChallengeButton challenge = new ChallengeButton();
            challenge.setPos(
                    w / 2 - challenge.width() / 2,
                    top + shieldH - challenge.height() / 2);
            add(challenge);
        }

		unlock = new Group();
		add( unlock );

		if (!(huntressUnlocked = /*Badges.isUnlocked( Badges.Badge.BOSS_SLAIN_3 )*/false)) {

			BitmapTextMultiline text = PixelScene.createMultiline( Babylon.get().getFromResources("startscene_unlock"), 9 );
			text.maxWidth = (int)width;
			text.measure();

			float pos = (bottom - BUTTON_HEIGHT) + (BUTTON_HEIGHT - text.height()) / 2;
			for (BitmapText line : text.new LineSplitter().split()) {
				line.measure();
				line.hardlight( 0xFFFF00 );
				line.x = PixelScene.align( w / 2 - line.width() / 2 );
				line.y = PixelScene.align( pos );
				unlock.add( line );

				pos += line.height();
			}
		}
		
		ExitButton btnExit = new ExitButton();
		btnExit.setPos( Camera.main.width - btnExit.width(), 0 );
		add( btnExit );
		
		curClass = null;
		updateClass(classes.get(PXL610.lastClass(PXL610.gamemode())));
		
		fadeIn();
		
		Badges.loadingListener = new Callback() {
			@Override
			public void call() {
				if (Game.scene() == StartScene.this) {
					PXL610.switchNoFade( StartScene.class );
				}
			}
		};
	}
	
	@Override
	public void destroy() {
		
		Badges.saveGlobal();
		Badges.loadingListener = null;
		
		super.destroy();
	}
	
	private void updateClass( HeroClass cl ) {
		
		if ((curClass == cl) && ((cl == HeroClass.MAGE) || (cl == HeroClass.WARRIOR))) { //EKDORN: not ready
			add( new WndClass( cl ) );
			return;
		}
		
		if (curClass != null) {
			shields.get( curClass ).highlight( false );
		}
		shields.get( curClass = cl ).highlight( true );
		
		if ((cl == HeroClass.MAGE) || (cl == HeroClass.WARRIOR) || (cl == HeroClass.MALE)) {
		
			unlock.visible = false;

			if (WndSaver.saveCheck(cl)) {
				
				btnLoad.visible = true;
				btnLoad.secondary( null, false );
				
				btnNewGame.visible = true;
				btnNewGame.secondary( Babylon.get().getFromResources("startscene_erase"), false );
				
				float w = (Camera.main.width - GAP) / 2 - buttonX;
				
				btnLoad.setRect(
					buttonX, buttonY, w, BUTTON_HEIGHT );
				btnNewGame.setRect(
					btnLoad.right() + GAP, buttonY, w, BUTTON_HEIGHT );
				
			} else {
				btnLoad.visible = false;
				
				btnNewGame.visible = true;
				btnNewGame.secondary( null, false );
				btnNewGame.setRect( buttonX, buttonY, Camera.main.width - buttonX * 2, BUTTON_HEIGHT );
			}
			
		} else {
			
			unlock.visible = true;
			btnLoad.visible = false;
			btnNewGame.visible = false;
			
		}
	}
	
	private void startNewGame() {

		Dungeon.hero = null;
		InterlevelScene.mode = InterlevelScene.Mode.DESCEND;
		
		if (PXL610.gameIntro()) {
			PXL610.gameIntro( false );
			Game.switchScene( IntroScene.class );
		} else {
			Game.switchScene( InterlevelScene.class );
		}	
	}
	
	@Override
	protected void onBackPressed() {
		PXL610.switchNoFade( InDev.isDeveloper() ? ModeScene.class : TitleScene.class );
	}
	
	public static class GameButton extends RedButton {
		
		private static final int SECONDARY_COLOR_N	= 0xCACFC2;
		private static final int SECONDARY_COLOR_H	= 0xFFFF88;
		
		private BitmapText secondary;
		
		public GameButton( String primary ) {
			super( primary );
			
			this.secondary.text( null );
		}
		
		@Override
		protected void createChildren() {
			super.createChildren();
			
			secondary = createText( 6 );
			add( secondary );
		}
		
		@Override
		protected void layout() {
			super.layout();
			
			if (secondary.text().length() > 0) {
				text.y = align( y + (height - text.height() - secondary.baseLine()) / 2 );
				
				secondary.x = align( x + (width - secondary.width()) / 2 );
				secondary.y = align( text.y + text.height() ); 
			} else {
				text.y = align( y + (height - text.baseLine()) / 2 );
			}
		}
		
		public void secondary( String text, boolean highlighted ) {
			secondary.text( text );
			secondary.measure();
			
			secondary.hardlight( highlighted ? SECONDARY_COLOR_H : SECONDARY_COLOR_N );
		}
	}
	
	public class ClassShield extends Button {
		
		private static final float MIN_BRIGHTNESS	= 0.6f;
		
		private static final int BASIC_NORMAL		= 0x444444;
		private static final int BASIC_HIGHLIGHTED	= 0xCACFC2;
		
		private static final int MASTERY_NORMAL		= 0x666644;
		private static final int MASTERY_HIGHLIGHTED= 0xFFFF88;
		
		private static final int WIDTH	= 24;
		private static final int HEIGHT	= 28;
		private static final int SCALE	= 2;
		
		private HeroClass cl;
		
		private Image avatar;
		private BitmapText name;
		private Emitter emitter;
		
		private float brightness;
		
		private int normal;
		private int highlighted;
		
		public ClassShield( HeroClass cl ) {
			super();
		
			this.cl = cl;
			
			avatar.frame( cl.ordinal() * WIDTH, 0, WIDTH, HEIGHT );
			avatar.scale.set( SCALE );
			
			if (Badges.isUnlocked( cl.masteryBadge() )) {
				normal = MASTERY_NORMAL;
				highlighted = MASTERY_HIGHLIGHTED;
			} else {
				normal = BASIC_NORMAL;
				highlighted = BASIC_HIGHLIGHTED;
			}
			
			name.text( (cl.title()).toUpperCase(Babylon.get().getCurrent()) );
			name.measure();
			name.hardlight( normal );
			
			brightness = MIN_BRIGHTNESS;
			updateBrightness();
		}
		
		@Override
		protected void createChildren() {
			
			super.createChildren();
			
			avatar = new Image( Assets.AVATARS );
			add( avatar );
			
			name = PixelScene.createText( 9 );
			add( name );
			
			emitter = new BitmaskEmitter( avatar );
			add( emitter );
		}
		
		@Override
		protected void layout() {
			
			super.layout();
			
			avatar.x = align( x + (width - avatar.width()) / 2 );
			avatar.y = align( y + (height - avatar.height() - name.height()) / 2 );
			
			name.x = align( x + (width - name.width()) / 2 );
			name.y = avatar.y + avatar.height() + SCALE;
		}
		
		@Override
		protected void onTouchDown() {
			
			emitter.revive();
			emitter.start( Speck.factory( Speck.LIGHT ), 0.05f, 7 );
			
			Sample.INSTANCE.play( Assets.SND_CLICK, 1, 1, 1.2f );
			updateClass( cl );
		}
		
		@Override
		public void update() {
			super.update();
			
			if (brightness < 1.0f && brightness > MIN_BRIGHTNESS) {
				if ((brightness -= Game.elapsed) <= MIN_BRIGHTNESS) {
					brightness = MIN_BRIGHTNESS;
				}
				updateBrightness();
			}
		}
		
		public void highlight( boolean value ) {
			if (value) {
				brightness = 1.0f;
				name.hardlight( highlighted );
			} else {
				brightness = 0.999f;
				name.hardlight( normal );
			}

			updateBrightness();
		}
		
		private void updateBrightness() {
			avatar.gm = avatar.bm = avatar.rm = avatar.am = brightness;
		}
	}
	
	public class ChallengeButton extends Button {
		
		private Image image;
		
		public ChallengeButton() {
			super();
			
			width = image.width;
			height = image.height;
			
			image.am = Badges.isUnlocked( Badges.Badge.VICTORY ) ? 1.0f : 0.5f;
		}
		
		@Override
		protected void createChildren() {
			
			super.createChildren();
			
			image = Icons.get( PXL610.challenges() > 0 ? Icons.CHALLENGE_ON :Icons.CHALLENGE_OFF );
			add( image );
		}
		
		@Override
		protected void layout() {
			
			super.layout();
			
			image.x = align( x );
			image.y = align( y  );
		}
		
		@Override
		protected void onClick() {
			if (Badges.isUnlocked( Badges.Badge.VICTORY )) {
				StartScene.this.add( new WndChallenges( PXL610.challenges(), true ) {
					public void onBackPressed() {
						super.onBackPressed();
						image.copy( Icons.get( PXL610.challenges() > 0 ?
							Icons.CHALLENGE_ON :Icons.CHALLENGE_OFF ) );
					};
				} );
			} else {
				StartScene.this.add( new WndMessage( Babylon.get().getFromResources("startscene_game") ) );
			}
		}
		
		@Override
		protected void onTouchDown() {
			Sample.INSTANCE.play( Assets.SND_CLICK );
		}
	}
}
