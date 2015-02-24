package uk.ac.nott.cs.g54dia.library;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class Beliefs {

	private ArrayList<Point> stations;
	private ArrayList<Point> wells;
	private HashMap<Point,Point> bestWellForStation;
	
	private boolean isExplorationFinished;
	private ArrayList<Point> exploringPoints;
	private ArrayList<Point> optimalExploringPoints;
	
	private ArrayList<Task> tasks;
	
	private int fuel;
	
	private int water;
	
	private Point currentPosition;
	private Cell currentCell;
	
	public Beliefs(int environmentSize) {
		
		// initialize stations 
		this.stations = new ArrayList<Point>();
		
		// initial wells
		this.wells = new ArrayList<Point>();
		
		// initialize hash table
		this.bestWellForStation = new HashMap<Point,Point>();
				
		// initialize task
		this.tasks = new ArrayList<Task>();

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
		
		int valueSize = (environmentSize-Tanker.VIEW_RANGE)/(Tanker.VIEW_RANGE*2+1);
		int remaining = (environmentSize-Tanker.VIEW_RANGE)%(Tanker.VIEW_RANGE*2+1);
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
					value[((value.length-1)/2)+((i+1)/2)] = ((i+1)/2)*(Tanker.VIEW_RANGE*2+1);
				}
				else {
					value[((value.length-1)/2)-(i/2)] = -(i/2)*(Tanker.VIEW_RANGE*2+1);
				}
			}
		}
		
		this.exploringPoints.add(Tanker.FUEL_PUMP_LOCATION);
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
	
	private void generateStationWellPair() {
		
		for (int i=0;i<this.stations.size();i++) {
			
			int minDistance = -1;
			Point closestWell = null;
			for (int j = 0;j<this.wells.size();j++) {
				int distance = Math.max(Math.abs(this.stations.get(i).x-this.wells.get(j).x), Math.abs(this.stations.get(i).y-this.wells.get(j).y));
				if (minDistance == -1) {
					minDistance = distance;
					closestWell = this.wells.get(j);
				}
				else {
					if (distance < minDistance) {
						minDistance = distance;
						closestWell = this.wells.get(j);
					}
				}
			}
			this.bestWellForStation.put(this.stations.get(i), closestWell);
		}
		
	}
	
	private void generateOptimalTaskList() {
		
		// group all task with same well
		ArrayList<ArrayList<Task>> optimalTasksList = new ArrayList<ArrayList<Task>>();
		ArrayList<Task> tasksCopy = new ArrayList<Task>();
		for (int i=0;i<this.tasks.size();i++) {
			tasksCopy.add(this.tasks.get(i));
		}
		while (tasksCopy.size() != 0) {
			Point well = this.bestWellForStation.get(tasksCopy.get(0).getStationPosition());
			ArrayList<Task> temp = new ArrayList<Task>();
			for (int i=0;i<tasksCopy.size();i++) {
				if (this.bestWellForStation.get(tasksCopy.get(i).getStationPosition()).equals(well)) {
					temp.add(tasksCopy.remove(i));
				}
			}
			optimalTasksList.add(temp);
		}
		
		// tensity of tasks with the same nearest well
		optimalTasksList.sort(new Comparator<ArrayList<Task>>() {
			@Override
			public int compare(ArrayList<Task> l1,ArrayList<Task> l2) {
				return l2.size() - l1.size();
			}
		});
		
		for (int i=0;i<optimalTasksList.size();i++) {
			tasksCopy.addAll(optimalTasksList.get(i));
		}
		this.tasks = tasksCopy;
		
	}
	
	private void geneateOptimalExplorePoints(boolean WhetherReplan) {
		
		if (WhetherReplan) {
		
			ArrayList<Point> optimalPoints = new ArrayList<Point>();
			// get max_X and min_X
			int max_X = -1;
			int min_X = -1;
			for (int i=0;i<this.stations.size();i++) {
				int x = this.stations.get(i).x;
				if (i == 0) {
					max_X = x;
					min_X = x;
				}
				else {
					if (x > max_X) {
						max_X = x;
					}
					else if (x < min_X) {
						min_X = x;
					}
				}
			}
			
			// get max_Y and min_Y
			int max_Y = -1;
			int min_Y = -1;
			for (int i=0;i<this.stations.size();i++) {
				int y = this.stations.get(i).y;
				if (i == 0) {
					max_Y = y;
					min_Y = y;
				}
				else {
					if (y > max_Y) {
						max_Y = y;
					}
					else if (y < min_Y) {
						min_Y = y;
					}
				}
			}
			
			int width;
			if ((max_X-min_X+1)%(Tanker.VIEW_RANGE*2+1) != 0) {
				width = (max_X-min_X+1)/(Tanker.VIEW_RANGE*2+1)+1;
			}
			else {
				width = (max_X-min_X+1)/(Tanker.VIEW_RANGE*2+1);
			}
			
			int height;
			if ((max_Y-min_Y+1)%(Tanker.VIEW_RANGE*2+1) != 0) {
				height = (max_Y-min_Y+1)/(Tanker.VIEW_RANGE*2+1)+1;
			}
			else {
				height = (max_Y-min_Y+1)/(Tanker.VIEW_RANGE*2+1);
			}
			
			for (int i=0;i<width;i++) {
				for (int j=0;j<height;j++) {
					for (int index = 0;index < this.stations.size();index++) {
						if (this.stations.get(index).x >= min_X+i*Tanker.VIEW_RANGE*2
						&& this.stations.get(index).x < min_X+(i+1)*Tanker.VIEW_RANGE*2
						&& this.stations.get(index).y < max_Y-j*Tanker.VIEW_RANGE*2
						&& this.stations.get(index).y >= max_Y-(j+1)*Tanker.VIEW_RANGE*2) {
							optimalPoints.add(new Point((min_X+i*Tanker.VIEW_RANGE*2+min_X+(i+1)*Tanker.VIEW_RANGE*2)/2,(max_Y-j*Tanker.VIEW_RANGE*2+max_Y-(j+1)*Tanker.VIEW_RANGE*2)/2));
							break;
						}
						else if (i == 0 && j == height-1) {
							if (this.stations.get(index).x == min_X+(i+1)*Tanker.VIEW_RANGE*2
								|| this.stations.get(index).y == max_Y-j*Tanker.VIEW_RANGE*2) {
								optimalPoints.add(new Point((min_X+i*Tanker.VIEW_RANGE*2+min_X+(i+1)*Tanker.VIEW_RANGE*2)/2,(max_Y-j*Tanker.VIEW_RANGE*2+max_Y-(j+1)*Tanker.VIEW_RANGE*2)/2));
								break;
							}
						}
						else if (i == 0 && this.stations.get(index).y == max_Y-j*Tanker.VIEW_RANGE*2) {
							optimalPoints.add(new Point((min_X+i*Tanker.VIEW_RANGE*2+min_X+(i+1)*Tanker.VIEW_RANGE*2)/2,(max_Y-j*Tanker.VIEW_RANGE*2+max_Y-(j+1)*Tanker.VIEW_RANGE*2)/2));
							break;
						}
						else if (j == height-1 && this.stations.get(index).x == min_X+(i+1)*Tanker.VIEW_RANGE*2) {
							optimalPoints.add(new Point((min_X+i*Tanker.VIEW_RANGE*2+min_X+(i+1)*Tanker.VIEW_RANGE*2)/2,(max_Y-j*Tanker.VIEW_RANGE*2+max_Y-(j+1)*Tanker.VIEW_RANGE*2)/2));
							break;
						}				
					}
				}
			}
	
			ArrayList<Point> exFirst = new ArrayList<Point>();
			ArrayList<Point> exSecond = new ArrayList<Point>();
			ArrayList<Point> exThird = new ArrayList<Point>();
			ArrayList<Point> exFourth = new ArrayList<Point>();
			
			for (int i = 0;i<optimalPoints.size();i++) {
				Point point = optimalPoints.get(i);
				int x = point.x;
				int y = point.y;
				
				if (x > 0 && y > 0) {
					exSecond.add(new Point(x,y));
				}
				else if (x > 0 && y == 0) {
					exSecond.add(new Point(x,y));
				}
				else if (x > 0 && y < 0) {
					exFourth.add(new Point(x,y));
				}
				else if (x == 0 && y > 0) {
					exFirst.add(new Point(x,y));
				}
				else if (x == 0 && y < 0) {
					exSecond.add(new Point(x,y));
				}
				else if (x < 0 && y > 0) {
					exFirst.add(new Point(x,y));
				}
				else if (x < 0 && y == 0) {
					exFirst.add(new Point(x,y));
				}
				else if (x < 0 && y < 0) {
					exThird.add(new Point(x,y));
				}
			}
			
			// point tensity of one area
			ArrayList<ArrayList<Point>> tensity = new ArrayList<ArrayList<Point>>();
			tensity.add(exFirst);
			tensity.add(exSecond);
			tensity.add(exThird);
			tensity.add(exFourth);
			tensity.sort(new Comparator<ArrayList<Point>>() {
				@Override
				public int compare(ArrayList<Point> arr1,ArrayList<Point> arr2) {
					return arr1.size()-arr2.size();
				}
			});
			
			this.optimalExploringPoints.add(Tanker.FUEL_PUMP_LOCATION);
			for (int i=0;i<4;i++) {
				this.optimalExploringPoints.addAll(tensity.get(i));
			}
	
		}
		
		for (int i=0;i<this.optimalExploringPoints.size();i++) {
			this.exploringPoints.add(this.optimalExploringPoints.get(i));
		}

	}
	

	// functions for field ArrayList series
	public ArrayList<Point> getStations() {
		return this.stations;
	}
	
	public ArrayList<Point> getWells() {
		return this.wells;
	}
	
	public HashMap<Point,Point> getStationWellPair() {
		return this.bestWellForStation;
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
	public ArrayList<Task> getTasks() {
		return this.tasks;
	}
	
	
	// functions for field 'FUEL'
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
	public void updateBeliefs(Cell[][] view,int fuel,int water, Point currentPosition, Cell currentCell) {
		
		// update task
		for (int i=0;i<this.tasks.size();i++) {
			if (this.tasks.get(i).isComplete()) {
				this.tasks.remove(i);
			}
		}
		
		// update stations, wells, task
		for (int i=0;i<view.length;i++) {
			for (int j=0;j<view[i].length;j++) {
				
				// is this cell a well ?
				if ((view[i][j]) instanceof Well && this.isExplorationFinished == false) {
					if (!this.wells.contains(view[i][j].getPoint())) {
						this.wells.add(view[i][j].getPoint());
					}
				}						
				// is this cell a station ? if so, is there any task ?
				else if ((view[i][j]) instanceof Station) {
					if (!this.stations.contains(view[i][j].getPoint()) && this.isExplorationFinished == false) {
						this.stations.add(view[i][j].getPoint());
					}
					if (((Station)view[i][j]).getTask() != null) {
						Task task = ((Station)view[i][j]).getTask();
						if (!this.tasks.contains(task)) {
							if (this.isExplorationFinished) {
								if (this.stations.contains(task.getStationPosition())) {
									this.tasks.add(task);

									// replan the task list
									this.generateOptimalTaskList();
								}
							}
							else {
								this.tasks.add(task);
							}
						}
					}
				}
			}
		}
		
		// if exploration is NOT finished, 
		// is current position one of exploring position ?
		if (this.exploringPoints.remove(currentPosition)) {
			if (this.exploringPoints.size() == 0) {
				if (!this.isExplorationFinished) {
					this.isExplorationFinished = true;
					this.generateStationWellPair();
					this.geneateOptimalExplorePoints(true);
				}
				else {
					this.geneateOptimalExplorePoints(false);
				}
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
