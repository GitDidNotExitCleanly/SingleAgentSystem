package uk.ac.nott.cs.g54dia.library;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Beliefs {

	private Set<Point> stations;
	private Set<Point> wells;
	
	private boolean isExplorationFinished;
	private ArrayList<Point> exploringPoints;
	private ArrayList<Point> optimalExploringPoints;
	
	private Task task;
	
	private int fuel;
	
	private int water;
	
	private Point currentPosition;
	private Cell currentCell;
	
	public Beliefs(int environmentSize) {
		
		// initialize stations 
		this.stations = new HashSet<Point>();
		// initial wells
		this.wells = new HashSet<Point>();
				
		// initialize task
		this.task = null;
		
		// initialize fuel
		this.fuel = Tanker.MAX_FUEL;
		
		// initialize water
		this.water = 0;
		
		// initialize last position
		this.currentPosition = Tanker.FUEL_PUMP_LOCATION;
		this.currentCell = new FuelPump(this.currentPosition);
		
		// calculate all points needed to explore
		this.isExplorationFinished = false;
		this.exploringPoints = new ArrayList<Point>();
		this.generateExplorePoints(environmentSize);
		this.optimalExploringPoints = new ArrayList<Point>();
	}
	
	private void generateExplorePoints(int environmentSize) {
		
		int valueSize = (environmentSize-Tanker.VIEW_RANGE)/(Tanker.VIEW_RANGE*2);
		int remaining = (environmentSize-Tanker.VIEW_RANGE)%(Tanker.VIEW_RANGE*2);
		int[] value;
		if (remaining != 0) {
			value = new int[(valueSize+1)*2+1];
		}
		else {
			value = new int[valueSize*2+1];
		}
		
		value[(value.length-1)/2] = 0;
		for (int i=1;i<value.length;i++) {
			if (i >= value.length-2 && remaining != 0) {
				if (i%2 != 0) {
					value[((value.length-1)/2)+((i+1)/2)] = value[((value.length-1)/2)+((i+1)/2)-1]+remaining;
				}
				else {
					value[((value.length-1)/2)-(i/2)] = value[((value.length-1)/2)-(i/2)+1]-remaining;
				}
			}
			else {
				if (i%2 != 0) {
					value[((value.length-1)/2)+((i+1)/2)] = ((i+1)/2)*Tanker.VIEW_RANGE*2;
				}
				else {
					value[((value.length-1)/2)-(i/2)] = -(i/2)*Tanker.VIEW_RANGE*2;
				}
			}
		}
		
		int numOfPoints = value.length*value.length;
		int lastXIndex = (value.length-1)/2;
		int lastYIndex = (value.length-1)/2;
		int lastDir = 0;
		int[] lastLength = {0,0};
		int count = 0;
		while (count < numOfPoints-1) {
			if (lastLength[0] == lastLength[1]) {
				lastLength[0] = lastLength[1];
				lastLength[1]++;
			}
			else {
				lastLength[0] = lastLength[1];
			}
			for (int i=0; i < lastLength[1] && count < numOfPoints;i++) {
				switch (lastDir) {
				case 0:
					lastYIndex++;
					break;
				case 1:
					lastXIndex--;
					break;
				case 2:
					lastYIndex--;
					break;
				case 3:
					lastXIndex++;
					break;
				}
				this.exploringPoints.add(new Point(value[lastXIndex],value[lastYIndex]));
				count++;
				if (count >= numOfPoints-1) {
					break;
				}
			}
			lastDir = (lastDir+1)%4 ;
		}
	}
	
	private void geneateOptimalExplorePoints() {
		
		////////////////////
		System.out.println("Generate Optimal Route !");
		
		
		
	}
	
	
	// functions for field ArrayList series
	public Set<Point> getStations() {
		return this.stations;
	}
	
	public Set<Point> getWells() {
		return this.wells;
	}
	
	public boolean isExplorationFinished() {
		return this.isExplorationFinished;
	}
	
	public ArrayList<Point> getExploringPoints() {
		return this.exploringPoints;
	}
	
	public ArrayList<Point> getOptimalExploringPoints() {
		return this.optimalExploringPoints;
	}
	
	
	// functions for field 'TASK'
	public Task getTask() {
		return this.task;
	}
	
	
	// functions for field 'FUEL'
	public boolean isFuelEnoughForReturn() {
		int dx = this.currentPosition.x - Tanker.FUEL_PUMP_LOCATION.x;
		int dy = this.currentPosition.y - Tanker.FUEL_PUMP_LOCATION.y;
		
		if (this.fuel > Math.max(Math.abs(dx), Math.abs(dy))) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public int getFuel() {
		return this.fuel;
	}
	
	
	// functions for field 'WATER'
	public int getWater() {
		return this.water;
	}
	
	
	// functions for field 'CURRENTPOSITION'
	public Point getCurrentPosition() {
		return this.currentPosition;
	}
	
	
	// functions for field 'CURRENTCELL'
	public Cell getCurrentCell() {
		return this.currentCell;
	}
	
	
	// update beliefs
	private final int
	    NORTH       =   0,
	    SOUTH       =   1,
	    EAST        =   2,
	    WEST        =   3,
	    NORTHEAST   =   4,
	    NORTHWEST   =   5,
	    SOUTHEAST   =   6,
	    SOUTHWEST   =   7,
		STILL		= 	8;
	public void updateBeliefs(Cell[][] view,int fuel,int water, Point currentPosition, Cell currentCell) {
		int direction;
		int dx = currentPosition.x - this.currentPosition.x;
		int dy = currentPosition.y - this.currentPosition.y;
		if (dx == 0 && dy == 0) {
			direction = this.STILL;
		}
		else if ( dx == 0 & dy < 0) {
			direction = this.SOUTH;
		}
		else if ( dx == 0 && dy > 0) {
			direction = this.NORTH;
		}
		else if ( dx > 0 && dy > 0) {
			direction = this.NORTHEAST;
		}
		else if ( dx > 0 && dy == 0 ) {
			direction = this.EAST;
		}
		else if ( dx > 0 && dy < 0) {
			direction = this.SOUTHEAST;
		}
		else if ( dx < 0 && dy > 0 ) {
			direction = this.NORTHWEST;
		}
		else if ( dx < 0 && dy == 0) {
			direction = this.WEST;
		}
		else {
			direction = this.SOUTHWEST;
		}
		
		switch (direction) {
			case STILL:
				for (int i=0;i<view.length;i++) {
					for (int j=0;j<view[i].length;j++) {
						
						// is this cell a well ?
						if (view[i][j] instanceof Well) {
							this.wells.add(view[i][j].getPoint());
						}						
						// is this cell a station ? if so, is there any task ?
						else if (view[i][j] instanceof Station) {
							this.stations.add(view[i][j].getPoint());
							if (((Station)view[i][j]).getTask() != null) {
								this.task = ((Station)view[i][j]).getTask();
							}
						}
					}
				}
				break;
			case SOUTH:
				for (int i=0;i<view.length;i++) {
					for (int j=0;j<view[i].length;j++) {
						if (i != view.length-1) {
							break;
						}
						else {
							// is this cell a well ?
							if (view[i][j] instanceof Well) {
								this.wells.add(view[i][j].getPoint());
							}						
							// is this cell a station ? if so, is there any task ?
							else if (view[i][j] instanceof Station) {
								this.stations.add(view[i][j].getPoint());
								if (((Station)view[i][j]).getTask() != null) {
									this.task = ((Station)view[i][j]).getTask();
								}
							}
						}
					}
				}
				break;
			case NORTH:
				for (int i=0;i<view.length;i++) {
					for (int j=0;j<view[i].length;j++) {
						if (i != 0) {
							break;
						}
						else {
							// is this cell a well ?
							if (view[i][j] instanceof Well) {
								this.wells.add(view[i][j].getPoint());
							}						
							// is this cell a station ? if so, is there any task ?
							else if (view[i][j] instanceof Station) {
								this.stations.add(view[i][j].getPoint());
								if (((Station)view[i][j]).getTask() != null) {
									this.task = ((Station)view[i][j]).getTask();
								}
							}
						}
					}
				}
				break;
			case NORTHEAST:
				for (int i=0;i<view.length;i++) {
					for (int j=0;j<view[i].length;j++) {
						if (i != 0 && j != view[i].length-1) {
							continue;
						}
						else {
							// is this cell a well ?
							if (view[i][j] instanceof Well) {
								this.wells.add(view[i][j].getPoint());
							}						
							// is this cell a station ? if so, is there any task ?
							else if (view[i][j] instanceof Station) {
								this.stations.add(view[i][j].getPoint());
								if (((Station)view[i][j]).getTask() != null) {
									this.task = ((Station)view[i][j]).getTask();
								}
							}
						}
					}
				}
				break;
			case EAST:
				for (int i=0;i<view.length;i++) {
					for (int j=0;j<view[i].length;j++) {
						if (j != view.length-1) {
							continue;
						}
						else {
							// is this cell a well ?
							if (view[i][j] instanceof Well) {
								this.wells.add(view[i][j].getPoint());
							}						
							// is this cell a station ? if so, is there any task ?
							else if (view[i][j] instanceof Station) {
								this.stations.add(view[i][j].getPoint());
								if (((Station)view[i][j]).getTask() != null) {
									this.task = ((Station)view[i][j]).getTask();
								}
							}
						}
					}
				}
				break;
			case SOUTHEAST:
				for (int i=0;i<view.length;i++) {
					for (int j=0;j<view[i].length;j++) {
						if (i != view.length-1 && j != view[i].length-1) {
							continue;
						}
						else {
							// is this cell a well ?
							if (view[i][j] instanceof Well) {
								this.wells.add(view[i][j].getPoint());
							}						
							// is this cell a station ? if so, is there any task ?
							else if (view[i][j] instanceof Station) {
								this.stations.add(view[i][j].getPoint());
								if (((Station)view[i][j]).getTask() != null) {
									this.task = ((Station)view[i][j]).getTask();
								}
							}
						}
					}
				}
				break;
			case NORTHWEST:
				for (int i=0;i<view.length;i++) {
					for (int j=0;j<view[i].length;j++) {
						if (i != 0 && j != 0) {
							continue;
						}
						else {
							// is this cell a well ?
							if (view[i][j] instanceof Well) {
								this.wells.add(view[i][j].getPoint());
							}						
							// is this cell a station ? if so, is there any task ?
							else if (view[i][j] instanceof Station) {
								this.stations.add(view[i][j].getPoint());
								if (((Station)view[i][j]).getTask() != null) {
									this.task = ((Station)view[i][j]).getTask();
								}
							}
						}
					}
				}
				break;
			case WEST:
				for (int i=0;i<view.length;i++) {
					for (int j=0;j<view[i].length;j++) {
						if (j != 0) {
							continue;
						}
						else {
							// is this cell a well ?
							if (view[i][j] instanceof Well) {
								this.wells.add(view[i][j].getPoint());
							}						
							// is this cell a station ? if so, is there any task ?
							else if (view[i][j] instanceof Station) {
								this.stations.add(view[i][j].getPoint());
								if (((Station)view[i][j]).getTask() != null) {
									this.task = ((Station)view[i][j]).getTask();
								}
							}
						}
					}
				}
				break;
			case SOUTHWEST:
				for (int i=0;i<view.length;i++) {
					for (int j=0;j<view[i].length;j++) {
						if (i != view.length-1 && j != 0) {
							continue;
						}
						else {
							// is this cell a well ?
							if (view[i][j] instanceof Well) {
								this.wells.add(view[i][j].getPoint());
							}						
							// is this cell a station ? if so, is there any task ?
							else if (view[i][j] instanceof Station) {
								this.stations.add(view[i][j].getPoint());
								if (((Station)view[i][j]).getTask() != null) {
									this.task = ((Station)view[i][j]).getTask();
								}
							}
						}
					}
				}
				break;
		}
		
		// if exploration is NOT finished, 
		// is current position one of exploring position ?
		if (!this.isExplorationFinished) {
			if (this.exploringPoints.remove(currentPosition)) {
				if (this.exploringPoints.size() == 0) {
					this.isExplorationFinished = true;
					this.geneateOptimalExplorePoints();
				}
			}
			
			
			///////////////
			System.out.println("remain: "+this.exploringPoints.size());
			
		}
		
		// update task
		if (this.task != null) {
			if (this.task.isComplete()) {
				this.task = null;
			}
		}
		
		// update fuel
		this.fuel = fuel;
		
		// update water
		this.water = water;
		
		// update current position
		this.currentPosition = currentPosition;
		
		// update current cell
		this.currentCell = currentCell;
	}
	
}