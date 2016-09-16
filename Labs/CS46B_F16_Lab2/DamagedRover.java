package transport;

public class DamagedRover extends UnmannedVehicle 
{
	private final static int		MAX_TRAVEL_METERS_BEFORE_EMPTY_BATTERY	= 10000;
	private final static int		METERS_FROM_START_TO_CLIFF				=  1000;
	private final static int		N_SIMULATIONS							=  5000;
	
	private double 					position;
	private double					metersTraveled;
	private boolean					fell;	
	
	
	//
	// Simulates travel under damage conditions. In each turn, travels forward or
	// backward either 1, 2, 3, or 4 meters. Continues until there's no more power
	// in the battery, or we fall off a cliff. Cliffs are at position = 1000 or
	// position = -1000.
	//
	public void simulateStormDamageTravel()
	{
		position = 0;
		double metersTraveled = 0;
		while (metersTraveled < MAX_TRAVEL_METERS_BEFORE_EMPTY_BATTERY)
		{
			double distanceNextTurn = (int)(1 + 4*Math.random());	
			boolean forwardNotBack = (Math.random() > 0.5);
			metersTraveled += distanceNextTurn;
			if (forwardNotBack)
				position += distanceNextTurn;
			else
				position -= distanceNextTurn;
			if (position <= -METERS_FROM_START_TO_CLIFF  ||  position >= METERS_FROM_START_TO_CLIFF)
			{
				fell = true;
				return;
			}
		}
	}
	
	
	public double getMetersTraveled()
	{
		return metersTraveled;
	}
	
	
	public boolean getFell()
	{
		return fell;
	}
	
	
	public static void main(String[] args) 
	{
		System.out.println("START");

		int nFalls = 0;
		double totalMetersBeforeFalling = 0;
		for (int i=0; i<N_SIMULATIONS; i++)
		{
			DamagedRover rover = new DamagedRover();
			rover.simulateStormDamageTravel();
			if (rover.getFell())
			{
				// Fell.
				nFalls++;
				totalMetersBeforeFalling += rover.getMetersTraveled();
			}
		}
		
		double averageMetersBeforeFalling = totalMetersBeforeFalling / nFalls;
		System.out.println("Average distance before falling = " + averageMetersBeforeFalling);
		
	}
}

