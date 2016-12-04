package physics;

import java.io.Serializable;

public abstract class Motion implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4385473681880693566L;
	protected Particle p;
	
	public Motion() {
		p = new Particle();
	}

	public Motion(Particle p){
		this.p = new Particle(p);
	}
	
	public Particle getParticle(){
		return new Particle(p);
	}
	
	public void move(double t) throws NegativeTimeException {
		if (t < 0)
			throw new NegativeTimeException();
		this.p = predict(t);
	}
	
	public abstract Particle predict(double t) throws NegativeTimeException;
}