package physics;

public class ZeroRadiusException extends CircularMotionException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 761248169271365691L;
	public String getMessage(){
		return "ZeroRadiusException: Cannot have a radius of zero!";
	}
}
