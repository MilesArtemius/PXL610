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
package com.ekdorn.pixel610.pixeldungeon.levels;

import com.ekdorn.pixel610.noosa.Scene;
import com.ekdorn.pixel610.noosa.particles.Emitter;
import com.ekdorn.pixel610.pixeldungeon.Assets;
import com.ekdorn.pixel610.pixeldungeon.Babylon;
import com.ekdorn.pixel610.pixeldungeon.Dungeon;
import com.ekdorn.pixel610.pixeldungeon.DungeonTilemap;
import com.ekdorn.pixel610.pixeldungeon.actors.mobs.npcs.Wandmaker;
import com.ekdorn.pixel610.pixeldungeon.effects.Halo;
import com.ekdorn.pixel610.pixeldungeon.effects.particles.FlameParticle;
import com.ekdorn.pixel610.pixeldungeon.levels.Room.Type;
import com.ekdorn.pixel610.utils.PointF;
import com.ekdorn.pixel610.utils.Random;

public class PrisonLevel extends RegularLevel {

	{
		color1 = 0x6a723d;
		color2 = 0x88924c;
	}
	
	@Override
	public String tilesTex() {
		return Assets.TILES_PRISON;
	}
	
	@Override
	public String waterTex() {
		return Assets.WATER_PRISON;
	}

	@Override
	public String plantsTex() {
		return Assets.PLANTS_PRISON;
	}
	
	protected boolean[] water() {
		return Patch.generate( feeling == Feeling.WATER ? 0.65f : 0.45f, 4 );
	}
	
	protected boolean[] grass() {
		return Patch.generate( feeling == Feeling.GRASS ? 0.60f : 0.40f, 3 );
	}
	
	@Override
	protected void assignRoomType() {
		super.assignRoomType();
		
		for (Room r : rooms) {
			if (r.type == Type.TUNNEL) {
				r.type = Type.PASSAGE;
			}
		}
	}
	
	@Override
	protected void createMobs() {
		super.createMobs();
		
		Wandmaker.Quest.spawn( this, roomEntrance );
	}
	
	@Override
	protected void decorate() {
		
		for (int i=WIDTH + 1; i < LENGTH - WIDTH - 1; i++) {
			if (map[i] == Terrain.EMPTY) { 
				
				float c = 0.05f;
				if (map[i + 1] == Terrain.WALL && map[i + WIDTH] == Terrain.WALL) {
					c += 0.2f;
				}
				if (map[i - 1] == Terrain.WALL && map[i + WIDTH] == Terrain.WALL) {
					c += 0.2f;
				}
				if (map[i + 1] == Terrain.WALL && map[i - WIDTH] == Terrain.WALL) {
					c += 0.2f;
				}
				if (map[i - 1] == Terrain.WALL && map[i - WIDTH] == Terrain.WALL) {
					c += 0.2f;
				}
				
				if (Random.Float() < c) {
					map[i] = Terrain.EMPTY_DECO;
				}
			}
		}
		
		for (int i=0; i < WIDTH; i++) {
			if (map[i] == Terrain.WALL &&  
				(map[i + WIDTH] == Terrain.EMPTY || map[i + WIDTH] == Terrain.EMPTY_SP) &&
				Random.Int( 6 ) == 0) {
				
				map[i] = Terrain.WALL_DECO;
			}
		}
		
		for (int i=WIDTH; i < LENGTH - WIDTH; i++) {
			if (map[i] == Terrain.WALL && 
				map[i - WIDTH] == Terrain.WALL && 
				(map[i + WIDTH] == Terrain.EMPTY || map[i + WIDTH] == Terrain.EMPTY_SP) &&
				Random.Int( 3 ) == 0) {
				
				map[i] = Terrain.WALL_DECO;
			}
		}
		
		while (true) {
			int pos = roomEntrance.random();
			if (pos != entrance) {
				map[pos] = Terrain.SIGN;
				break;
			}
		}
	}
	
	@Override
	public String tileName( int tile ) {
		switch (tile) {
			case Terrain.EMPTY:
			case Terrain.EMPTY_SP:
			case Terrain.EMPTY_DECO:
			case Terrain.SECRET_TOXIC_TRAP:
			case Terrain.SECRET_FIRE_TRAP:
			case Terrain.SECRET_PARALYTIC_TRAP:
			case Terrain.SECRET_POISON_TRAP:
			case Terrain.SECRET_ALARM_TRAP:
			case Terrain.SECRET_LIGHTNING_TRAP:
				return Babylon.get().getFromResources("levels_prison_floor");
			case Terrain.WATER:
				return Babylon.get().getFromResources("levels_prison_water");
			case Terrain.GRASS:
				return Babylon.get().getFromResources("levels_prison_grass");
			case Terrain.WALL:
			case Terrain.WALL_DECO:
			case Terrain.SECRET_DOOR:
				return Babylon.get().getFromResources("levels_prison_wall");
			case Terrain.EMBERS:
				return Babylon.get().getFromResources("levels_prison_embers");
			case Terrain.PEDESTAL:
				return Babylon.get().getFromResources("levels_prison_pedestal");
			case Terrain.BARRICADE:
				return Babylon.get().getFromResources("levels_prison_barricade");
			case Terrain.HIGH_GRASS:
				return Babylon.get().getFromResources("levels_prison_highgrass");
			case Terrain.SIGN:
				return Babylon.get().getFromResources("levels_prison_sign");
			case Terrain.WELL:
				return Babylon.get().getFromResources("levels_prison_well");
			case Terrain.EMPTY_WELL:
				return Babylon.get().getFromResources("levels_prison_emptywell");
			case Terrain.STATUE:
				return Babylon.get().getFromResources("levels_prison_statue");
			case Terrain.STATUE_SP:
				return Babylon.get().getFromResources("levels_prison_statue_sp");
			case Terrain.ALARM_TRAP:
				return Babylon.get().getFromResources("levels_prison_alarmtrap");
			case Terrain.LIGHTNING_TRAP:
				return Babylon.get().getFromResources("levels_prison_lightningtrap");
			case Terrain.GRIPPING_TRAP:
				return Babylon.get().getFromResources("levels_prison_grippingtrap");
			default:
				return super.tileName( tile );
		}
	}
	
	@Override
	public String tileDesc(int tile) {
		switch (tile) {
			case Terrain.BOOKSHELF:
				return Babylon.get().getFromResources("levels_prison_books_desc");
			case Terrain.EMPTY_DECO:
			 	return Babylon.get().getFromResources("levels_prison_empty_desc");
			case Terrain.EMBERS:
				return Babylon.get().getFromResources("levels_prison_embers_desc");
			case Terrain.HIGH_GRASS:
				return Babylon.get().getFromResources("levels_prison_highgrass_desc");
			case Terrain.BARRICADE:
				return Babylon.get().getFromResources("levels_prison_barricade_desc");
			case Terrain.SIGN:
				return Babylon.get().getFromResources("levels_prison_sign_desc");
			case Terrain.STATUE:
				return Babylon.get().getFromResources("levels_prison_statue_desc");
			case Terrain.STATUE_SP:
				return Babylon.get().getFromResources("levels_prison_statue_sp_desc");
			case Terrain.EMPTY_WELL:
				return Babylon.get().getFromResources("levels_prison_emptywell_desc");
			case Terrain.PEDESTAL:
				return Babylon.get().getFromResources("levels_prison_pedestal_desc");
			default:
				return super.tileDesc( tile );
		}
	}
	
	@Override
	public void addVisuals( Scene scene ) {
		super.addVisuals( scene );
		addVisuals( this, scene );
	}
	
	public static void addVisuals( Level level, Scene scene ) {
		for (int i=0; i < LENGTH; i++) {
			if (level.map[i] == Terrain.WALL_DECO) {
				scene.add( new Torch( i ) );
			}
		}
	}
	
	private static class Torch extends Emitter {
		
		private int pos;
		
		public Torch( int pos ) {
			super();
			
			this.pos = pos;
			
			PointF p = DungeonTilemap.tileCenterToWorld( pos );
			pos( p.x - 1, p.y + 3, 2, 0 );
			
			pour( FlameParticle.FACTORY, 0.15f );
			
			add( new Halo( 16, 0xFFFFCC, 0.2f ).point( p.x, p.y ) );
		}
		
		@Override
		public void update() {
			if (visible = Dungeon.visible[pos]) {
				super.update();
			}
		}
	}
}