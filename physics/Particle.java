package physics;

import java.io.Serializable;

public class Particle implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2060523505546950935L;
	private Vector r; // position
	private Vector v; // velocity
	private Vector a; // acceleration 
	
	public Particle () {
		r = new Vector(0,0);
		v = new Vector(0,0);
		a = new Vector(0,0);
	}
	
	public Particle(Vector position, Vector velocity, Vector acceleration) {
		r = new Vector(position);
		v = new Vector(velocity);
		a = new Vector(acceleration);
	}
	
	public Particle(Particle other) {
		r = new Vector(other.r);
		v = new Vector(other.v);
		a = new Vector(other.a);
	}

	public Vector getPosition() {
		return new Vector(r);
	}
	
	public Vector getVelocity() {
		return new Vector(v);
	}
	
	public Vector getAcceleration(){
		return new Vector(a);
	}
	
	public void setPosition(Vector position) {
		r.setXY(position);
	}
	
	public void setVelocity(Vector velocity) {
		v.setXY(velocity);
	}
	
	public void setAcceleration(Vector acceleration) {
		a.setXY(acceleration);
	}
	
	public void setPosition(double x, double y) {
		r.setXY(x,y);
	}
	
	public void setVelocity(double x, double y) {
		v.setXY(x,y);
	}
	
	public void setAcceleration(double x, double y) {
		a.setXY(x,y);
	}
}