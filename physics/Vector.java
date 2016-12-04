package physics;

import java.text.DecimalFormat;
import java.io.Serializable;

public class Vector implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2448681912009058534L;
	private double x;
	private double y;
	
	public Vector(){
		x = 0;
		y = 0;
	}
	
	public Vector(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector(Vector other) {
		this.x = other.x;
		this.y = other.y;
	}
	
	public void setXY(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public void setXY(Vector other) {
		this.x = other.x;
		this.y = other.y;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public Vector add(Vector other) {
		return new Vector(this.x + other.x, this.y + other.y);
	}
	
	public Vector add(double x, double y){
		return new Vector(this.x + x, this.y + y);
	}
	
	public Vector scale(double n) {
		return new Vector(n*this.x, + n*this.y);
	}
	
	public Vector negate() {
		return new Vector (-this.x, -this.y);
	}
	
	public double magnitude() {
		return Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
	}
	
	public double angle() {
		return Math.atan(y/x);
	}
	
	public String toString(){
		DecimalFormat d = new DecimalFormat("#0.00");
		return "<"+d.format(x)+","+d.format(y)+">";
	}
	
	/*
	public static void main(String[] args) {
		
		TwoDMotion t = new TwoDMotion(new Particle(new Vector(0,0), new Vector(0,0), new Vector(1,1)));
		t.move(5.0);
		System.out.println(t.getParticle().getPosition().toString());
		CircularMotion c = new CircularMotion(new Vector(0,0), 1, 2*Math.PI,0);
		c.move(1);
		System.out.println(c.getParticle().getPosition().toString());
	}
	*/
}