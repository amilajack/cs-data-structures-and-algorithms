package transport;

public class DamagedRover extends MarsRover
{
	private final static int		MAX_TRAVEL_METERS_BEFORE_EMPTY_BATTERY	= 10000;
	private final static int		METERS_FROM_START_TO_CLIFF				=  1000;
	private final static int		N_SIMULATIONS							= 500;
	// private final static int		N_SIMULATIONS							= 15000;

	private double 					position;
	private double					metersTraveled;
	private boolean					fell                                    = false;


    //    // Simulates travel under damage conditions. In each turn, travels forward or    // backward either 1, 2, 3, or 4 meters. Continues until there's no more power    // in the battery, or we fall off a cliff. Cliffs are at position = 1000 or    // position = -1000.    //    public void simulateStormDamageTravel() {
        position = 0;
        metersTraveled = 0;        if (MAX_TRAVEL_METERS_BEFORE_EMPTY_BATTERY == 0) {            System.out.print("Out of Power");        }        while (metersTraveled < MAX_TRAVEL_METERS_BEFORE_EMPTY_BATTERY) {            double distanceNextTurn = (int)(1 + 4 * Math.random());            boolean forwardNotBack = (Math.random() > 0.5);            // Adjust position and metersTraveled.            // Check for falling off cliff.

            this.metersTraveled += distanceNextTurn;

            if (forwardNotBack == false) {
                position += distanceNextTurn;
            }
            else {
                position -= distanceNextTurn;
            }

            if (position > 1000 || position < -1000) {
                this.fell = true;
            }
        }    }


	public double getMetersTraveled()
	{
		return this.metersTraveled;
	}


	public boolean getFell()
	{
		return this.fell;
	}


	public static void main(String[] args)
	{
		System.out.println("START");

		int nFalls = 0;
		double totalMetersBeforeFalling = 0;

		for (int i = 0; i < 100; i++)
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

        if (nFalls == 0) {
            System.out.println("Average distance before falling = " + totalMetersBeforeFalling);
        }
        else {
            double averageMetersBeforeFalling = totalMetersBeforeFalling / nFalls;
    		System.out.println("Average distance before falling = " + averageMetersBeforeFalling);
        }
	}
}
