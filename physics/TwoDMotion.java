package physics;

import java.io.Serializable;

public class TwoDMotion extends Motion implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8766224493680722975L;

	public TwoDMotion() {
		super();
	}

	public TwoDMotion(Particle p){
		super(p);
	}
	
	@Override
	public Particle predict(double t) throws NegativeTimeException{
		if (t < 0)
			throw new NegativeTimeException();
		// r = x0 + v0t + (1/2)at^2
		Vector finalPosition = new Vector(p.getPosition()); // x = x0;
		finalPosition.setXY(finalPosition.add(p.getVelocity().scale(t))); // x = x0 + v0t
		finalPosition.setXY(finalPosition.add(p.getAcceleration().scale(0.5*Math.pow(t,2)))); // x = x0 + v0t + (1/2)at^2
		// v = v0 + at
		Vector finalVelocity = new Vector(p.getVelocity());
		finalVelocity.setXY(finalVelocity.add(p.getAcceleration().scale(t)));

		return new Particle(finalPosition, finalVelocity, p.getAcceleration());
	}
}