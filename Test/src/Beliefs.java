import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

/**
 * A 2D position in the Environment.
 * 
 * @author Neil Madden
 */

/*
 * Copyright (c) 2005 Neil Madden.
 * Copyright (c) 2010 University of Nottingham.
 * 
 * See the file "license.terms" for information on usage and redistribution
 * of this file, and for a DISCLAIMER OF ALL WARRANTIES.
 */

class Point implements Cloneable {
    volatile int x, y;

    Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public boolean equals(Object o) {
        Point p = (Point)o;
        if (p==null) return false;
        return (p.x == x) && (p.y == y);
    }

    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public Object clone() {
        return new Point(x,y);
    }

    /**
     * Override hashCode to make sure identical points produce identical
     * hashes.
     */
    public int hashCode() {
        return (((x & 0xff) << 16) + (y & 0xff));
    }
}



public class Beliefs {

	//private ArrayList<Point> stations;
	//private ArrayList<Point> wells;
	
	//private Task task;
	
	//private int fuel;
	
	private Queue<Point> exploringOrder;
	
	public Beliefs(int environmentSize) {
		
		// initialize stations 
	//	this.stations = new ArrayList<Point>();
		// initial wells
	//	this.wells = new ArrayList<Point>();
				
		// initialize task
	//	this.task = null;
		
		// initialize fuel
	//	this.fuel = Tanker.MAX_FUEL;
		
		// calculate all points needed to explore
		this.exploringOrder = new LinkedList<Point>();
		this.generateExploreOrder(environmentSize);
	}
	
	public void printPoint() {
		
		for (int i=0;i<this.exploringOrder.size();i++) {
			System.out.println(this.exploringOrder.poll().toString());
		}
		
		
	}
	
	private void generateExploreOrder(int environmentSize) {
		
		int valueSize = (45-12)/(12*2);
		int remaining = (45-12)%(12*2);
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
					value[((value.length-1)/2)+((i+1)/2)] = ((i+1)/2)*12*2;
				}
				else {
					value[((value.length-1)/2)-(i/2)] = -(i/2)*12*2;
				}
			}
		}
		
		int numOfPoints = value.length*value.length;
		this.exploringOrder.offer(new Point(0,0));
		//System.out.println(new Point(0,0).toString());
		//System.out.println(numOfPoints);
		
		int lastXIndex = (value.length-1)/2;
		int lastYIndex = (value.length-1)/2;
		int lastDir = 0;
		int[] lastLength = {0,0};
		int count = 0;
		while (count < numOfPoints-1) {
			////
			//System.out.println("count: "+count);
			
			if (lastLength[0] == lastLength[1]) {
				lastLength[0] = lastLength[1];
				lastLength[1]++;
			}
			else {
				lastLength[0] = lastLength[1];
			}
			
			/////
			
			
			for (int i=0;i<lastLength[1];i++) {
				
					
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
				this.exploringOrder.offer(new Point(value[lastXIndex],value[lastYIndex]));
				//System.out.println("xIndex: "+lastXIndex+" yIndex: "+lastYIndex);
				
				
				count++;
				//System.out.println(count+" in the loop");
				if (count >= numOfPoints-1) {
					break;
				}
			}
			lastDir = (lastDir+1)%4 ;
		}
	}
	
	// functions for field 'MAP'
	
	
}