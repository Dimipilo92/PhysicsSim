package physics;

public class NegativeTimeException extends PhysicsException {
	/**
	 * 
	 */
	private static final long serialVersionUID = -9072005552604428322L;

	@Override
	public String getMessage(){
		return "NegativeTimeException: Cannot have a negative time!";
	}
}
