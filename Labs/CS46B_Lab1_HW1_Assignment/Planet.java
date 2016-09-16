package planets;

public class Planet 
{
	// NASA publishes masses of planets at http://nssdc.gsfc.nasa.gov/planetary/factsheet/.
	// Mass units are 10^24 kg.
	private final static float			MASS_UNIT	= 1.0e24f;
	
	private final static Planet[]		THE_PLANETS =
	{
		new Planet("Mercury", 0.33f * MASS_UNIT),
		new Planet("Venus", 4.87f * MASS_UNIT),
		new Planet("Earth", 5.97f * MASS_UNIT),
		new Planet("Mars", 0.642f * MASS_UNIT),
		new Planet("Jupiter", 1898 * MASS_UNIT),
		new Planet("Saturn", 568 * MASS_UNIT),
		new Planet("Uranus", 86.8f * MASS_UNIT),
		new Planet("Neptune", 102f * MASS_UNIT)
		// No more Pluto :-(
	};
	
	
	private String		name;
	private float		massKg;
	
	
	public Planet(String name, float massKg)
	{
		this.name = name;
		this.massKg = massKg;
	}
	
	
	public String getName()
	{
		return name;
	}
	
	
	public float getMass()
	{
		return massKg;
	}
	
	
	public static Planet[] getAll()
	{
		return THE_PLANETS;
	}
}
