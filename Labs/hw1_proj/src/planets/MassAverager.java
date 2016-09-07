package planets;

public class MassAverager extends Planet
{
	public MassAverager() {
		super("Earth", 5.97f * 1.0e24f);
	}

	public float getMeanPlanetaryMass()
	{
		float average = 0;

    // TODO: This can be done cleaner with Array.reduce
		for (Planet element : this.getAll()) {
		  average += element.getMass();
		}

		return average / this.getAll().length;
	}

	public static void main(String[] args)
	{
		MassAverager averager = new MassAverager();
		System.out.println(averager.getMeanPlanetaryMass());
	}
}
