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

import android.util.Log;

import java.util.List;

import com.ekdorn.pixel610.noosa.Scene;
import com.ekdorn.pixel610.pixeldungeon.Assets;
import com.ekdorn.pixel610.pixeldungeon.Babylon;
import com.ekdorn.pixel610.pixeldungeon.Bones;
import com.ekdorn.pixel610.pixeldungeon.Dungeon;
import com.ekdorn.pixel610.pixeldungeon.actors.Actor;
import com.ekdorn.pixel610.pixeldungeon.actors.Char;
import com.ekdorn.pixel610.pixeldungeon.actors.mobs.Bestiary;
import com.ekdorn.pixel610.pixeldungeon.actors.mobs.Mob;
import com.ekdorn.pixel610.pixeldungeon.actors.mobs.Tengu;
import com.ekdorn.pixel610.pixeldungeon.items.Heap;
import com.ekdorn.pixel610.pixeldungeon.items.Item;
import com.ekdorn.pixel610.pixeldungeon.items.keys.IronKey;
import com.ekdorn.pixel610.pixeldungeon.items.keys.SkeletonKey;
import com.ekdorn.pixel610.pixeldungeon.levels.Room.Type;
import com.ekdorn.pixel610.pixeldungeon.levels.painters.Painter;
import com.ekdorn.pixel610.pixeldungeon.scenes.GameScene;
import com.ekdorn.pixel610.utils.Bundle;
import com.ekdorn.pixel610.utils.Graph;
import com.ekdorn.pixel610.utils.Point;
import com.ekdorn.pixel610.utils.Random;

public class PrisonBossLevel extends RegularLevel {

	{
		color1 = 0x6a723d;
		color2 = 0x88924c;
	}
	
	private Room anteroom;
	private int arenaDoor;
	
	private boolean enteredArena = false;
	private boolean keyDropped = false;
	
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
	
	private static final String ARENA	= "arena";
	private static final String DOOR	= "door";
	private static final String ENTERED	= "entered";
	private static final String DROPPED	= "droppped";
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		super.storeInBundle( bundle );
		bundle.put( ARENA, roomExit );
		bundle.put( DOOR, arenaDoor );
		bundle.put( ENTERED, enteredArena );
		bundle.put( DROPPED, keyDropped );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		super.restoreFromBundle( bundle );
		roomExit = (Room)bundle.get( ARENA );
		arenaDoor = bundle.getInt( DOOR );
		enteredArena = bundle.getBoolean( ENTERED );
		keyDropped = bundle.getBoolean( DROPPED );
	}
	
	@Override
	protected boolean build() {
		
		initRooms();

		int distance;
		int retry = 0;

		do {
			
			if (retry++ > 10) {
				return false;
			}
			
			int innerRetry = 0;
			do {
				if (innerRetry++ > 10) {
					return false;
				}
				roomEntrance = Random.element( rooms );
			} while (roomEntrance.width() < 4 || roomEntrance.height() < 4);
			
			innerRetry = 0;
			do {
				if (innerRetry++ > 10) {
					return false;
				}
				roomExit = Random.element( rooms );

			} while (
				roomExit == roomEntrance || 
				roomExit.width() < 10 ||
				roomExit.height() < 5 ||
				roomExit.top == 0);
	
			Graph.buildDistanceMap( rooms, roomExit );
			distance = Graph.buildPath( rooms, roomEntrance, roomExit ).size();
			
		} while (distance < 3);

		//Log.e("TAG", "build: EXIT CREATED " + roomExit.height());
		roomEntrance.type = Type.ENTRANCE;
		roomExit.type = Type.BOSS_EXIT;
		
		List<Room> path = Graph.buildPath( rooms, roomEntrance, roomExit );	
		Graph.setPrice( path, roomEntrance.distance );
		
		Graph.buildDistanceMap( rooms, roomExit );
		path = Graph.buildPath( rooms, roomEntrance, roomExit );
		
		anteroom = path.get( path.size() - 2 );
		anteroom.type = Type.STANDARD;
		
		Room room = roomEntrance;
		for (Room next : path) {
			room.connect( next );
			room = next;
		}
		
		for (Room r : rooms) {
			//Log.e("TAG", "build: room " + r.height() + " " + r.width());
			if (r.type == Type.NULL && r.connected.size() > 0) {
				r.type = Type.PASSAGE; 
			}
		}

		paint();

		Room r = (Room)roomExit.connected.keySet().toArray()[0];
		if (roomExit.connected.get( r ).y == roomExit.top) {
			return false;
		}

		paintWater();
		paintGrass();

		placeTraps();
		
		return true;
	}
		
	protected boolean[] water() {
		return Patch.generate( 0.45f, 5 );
	}
	
	protected boolean[] grass() {
		return Patch.generate( 0.30f, 4 );
	}
	
	protected void paintDoors( Room r ) {
		for (Room n : r.connected.keySet()) {
			
			if (r.type == Type.NULL) {
				continue;
			}
			
			Point door = r.connected.get( n );
			
			if (r.type == Room.Type.PASSAGE && n.type == Room.Type.PASSAGE) {
				
				Painter.set( this, door, Terrain.EMPTY );
				
			} else {
				
				Painter.set( this, door, Terrain.DOOR );
				
			}
			
		}
	}
	
	@Override
	protected void placeTraps() {

		int nTraps = nTraps();

		for (int i=0; i < nTraps; i++) {

			int trapPos = Random.Int( LENGTH );

			if (map[trapPos] == Terrain.EMPTY) {
				map[trapPos] = Terrain.POISON_TRAP;
			}
		}
	}
	
	@Override
	protected void decorate() {

		for (int i=WIDTH + 1; i < LENGTH - WIDTH - 1; i++) {
			if (map[i] == Terrain.EMPTY) {

				float c = 0.15f;
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
				Random.Int( 4 ) == 0) {
				
				map[i] = Terrain.WALL_DECO;
			}
		}
		
		for (int i=WIDTH; i < LENGTH - WIDTH; i++) {
			if (map[i] == Terrain.WALL && 
				map[i - WIDTH] == Terrain.WALL && 
				(map[i + WIDTH] == Terrain.EMPTY || map[i + WIDTH] == Terrain.EMPTY_SP) &&
				Random.Int( 2 ) == 0) {
				
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
		
		Point door = roomExit.entrance();
		arenaDoor = door.x + door.y * WIDTH;
		Painter.set( this, arenaDoor, Terrain.LOCKED_DOOR );

		Painter.fill(this, roomExit, 1, Terrain.EMPTY_SP);
		Painter.fill(this, roomExit, 2, Terrain.EMPTY);
	}
	
	@Override
	protected void createMobs() {
	}
	
	public Actor respawner() {
		return null;
	}
	
	@Override
	protected void createItems() {
		int keyPos = anteroom.random();
		while (!passable[keyPos]) {
			keyPos = anteroom.random();
		}
		drop( new IronKey(), keyPos ).type = Heap.Type.CHEST;
		
		Item item = Bones.get();
		if (item != null) {
			int pos;
			do {
				pos = roomEntrance.random();
			} while (pos == entrance || map[pos] == Terrain.SIGN);
			drop( item, pos ).type = Heap.Type.SKELETON;
		}
	}

	@Override
	public void press( int cell, Char ch ) {
		
		super.press( cell, ch );
		
		if (ch == Dungeon.hero && !enteredArena && roomExit.inside( cell )) {
			
			enteredArena = true;
		
			int pos;
			do {
				pos = roomExit.random();
			} while (pos == cell || Actor.findChar( pos ) != null);
			
			Mob boss = Bestiary.mob( Dungeon.depth );
			boss.state = ((Tengu) boss).DANCING;
			boss.pos = pos;
			GameScene.add( boss );
			boss.notice();
			
			mobPress( boss );
			
			set( arenaDoor, Terrain.LOCKED_DOOR );
			GameScene.updateMap( arenaDoor );
			Dungeon.observe();
		}
	}
	
	@Override
	public Heap drop( Item item, int cell ) {
		
		if (!keyDropped && item instanceof SkeletonKey) {
			
			keyDropped = true;
			
			set( arenaDoor, Terrain.DOOR );
			GameScene.updateMap( arenaDoor );
			Dungeon.observe();
		}
		
		return super.drop( item, cell );
	}
	
	@Override
	public int randomRespawnCell() {
		return -1;
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
		PrisonLevel.addVisuals( this, scene );
	}
}
