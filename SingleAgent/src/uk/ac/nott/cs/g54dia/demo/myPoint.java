package uk.ac.nott.cs.g54dia.demo;

public class myPoint implements Cloneable {

	private volatile int x;
	private volatile int y;
	
	public myPoint(int x,int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return this.x;
	}
	
	public void increaseXbyOne() {
		this.x++;
	}
	
	public void decreaseXbyOne() {
		this.x--;
	}
	
	public int getY() {
		return this.y;
	}
	
	public void increaseYbyOne() {
		this.y++;
	}
	
	public void decreaseYbyOne() {
		this.y--;
	}
	
	@Override
	public boolean equals(Object o) {
		myPoint p = (myPoint)o;
		if (p==null) return false;
		return (p.x == x) && (p.y == y);
	}
 
    /**
     * Override hashCode to make sure identical points produce identical
     * hashes.
     */
    @Override
	public int hashCode() {
    	
        return (((x & 0xff) << 16) + (y & 0xff));
    }
    
    @Override
	public String toString() {
        return "("+ x +","+ y +")";
    }

}
