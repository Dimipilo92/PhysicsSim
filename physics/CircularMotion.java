package physics;

import java.io.Serializable;

public class CircularMotion extends Motion implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3350637360105986885L;
	Vector C;
	double vMag; // magnitude of velocity
	double R; // Radius
	double theta; // angular position on the circle in degrees
	double T; // period
	
	public CircularMotion(){
		super();
		C = new Vector();
		R = 1;
		vMag = 0;
		T = 1;
		theta = 0;
	}
	
	/**
	 * 
	 * @param center
	 * @param radius
	 * @param velocity
	 * @param position
	 */
	
	public CircularMotion(Vector center, double radius, double velocity, double position) throws ZeroRadiusException{
		// r = C + <Rcos(theta), Rsin(theta)>
		C = center;
		R = radius;
		theta = position * (Math.PI/180);
		vMag = velocity;
		// T = 2(pi)r/v
		if (R == 0)
			throw new ZeroRadiusException();
		if (vMag == 0)
			T = 1;
		else
			T = (2*Math.PI*R)/vMag;
		
		p = new Particle(toCartesian(theta));
	}
	
	/**
	 * 
	 * @param position
	 * @return
	 */
	private Particle toCartesian(double position) {
		
		// r = C + <Rcos(theta),Rsin(theta)>
		Vector r = new Vector(C);
		double x = R * Math.cos(position);
		double y = R * Math.sin(position);
		r.setXY(r.add(x,y));
		// v = <-vsin(theta),vcos(theta)
		Vector v = new Vector();
		v.setX(-vMag*Math.sin(position));
		v.setY(vMag*Math.cos(position));
		// |a| = v^2/r
		// a = <-acos(theta),-asin(theta)>
		Vector a = new Vector();
		double aMag = Math.pow(vMag, 2)/R;
		a.setX(-aMag* Math.cos(position));
		a.setY(-aMag* Math.sin(position));
		
		return new Particle(r, v, a);
	}
	
	@Override
	public Particle predict(double t) throws NegativeTimeException{
		if (t < 0)
			throw new NegativeTimeException();
		double newTheta = (((t/T)*360)+theta);
		return toCartesian(newTheta);
	}
	
	@Override
	public void move(double t) throws NegativeTimeException{
		if (t < 0)
			throw new NegativeTimeException();
		if (vMag == 0)
			return;
		
		theta = (((t/T)*360)+theta);
		super.move(t);
	}
	
	public Vector getCenter() {
		return new Vector(C);
	}
	public double getRadius() {
		return R;
	}
	public double getVelocity() {
		return vMag;
	}
	public double getPosition() {
		return theta;
	}
}