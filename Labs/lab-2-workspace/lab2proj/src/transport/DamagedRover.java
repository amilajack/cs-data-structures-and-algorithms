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


    //

        metersTraveled = 0;

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
        }


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