package weather;

import java.lang.reflect.Field;
import java.util.Set;
import java.util.TreeSet;

public class WeatherTester {

  private static void testBasicClassesExist()
  {
    // Collect names of missing classes.
    String[] basicNames = { "Cloud", "CirrusCloud", "CumulusCloud", "StratusCloud", "Sky" };
    Set<String> missing = new TreeSet<>();
    for (String name: basicNames)
    {
      String fullName = "weather." + name;
      try
      {
        Class.forName(fullName);
      }
      catch (LinkageError x)
      {
        System.out.println("Phil says this should never happen.");
      }
      catch (ClassNotFoundException x)
      {
        missing.add(name);
      }
    }
    // Nothing missing.
    if (missing.isEmpty())
    System.out.println("All the basic class are present");
    // 1 or more basic (i.e. not Sky2) classes are missing.
    else{
      System.out.println("The following class(es) are missing:");
      for (String m: missing)
      System.out.println( m + " ");
    }
  }
  public static void testSky2Exists()
  {
    try
    {
      Class.forName("weather.Sky2");
    }
    catch (LinkageError x)
    {
      System.out.println("This should never happen.");
    }
    catch (ClassNotFoundException x)
    {
      System.out.println("No class Sky2");
    }
    System.out.println("class Sky2 present");
  }
  public static void testSkyStructure()
  {
    try
    {
      Class<?> clazz = Class.forName("weather.Sky");
      Field cloudsField = clazz.getDeclaredField("clouds");
      if (cloudsField.getType() != java.util.ArrayList.class)
      System.out.println("Unexpected type for clouds in Sky: " + cloudsField.getType() +
      " should be ArrayList");
    }
    catch (NoSuchFieldException x)
    {
      System.out.println("No field clouds in class Sky");
    }
    catch (ClassNotFoundException x)
    {
      System.out.println("No class Sky2");        // should be caught in testSky2Exists
    }
    System.out.println("Sky2 structure is correct");
  }
  public static void testSkyBehavior()
  {
    StratusCloud strat = new StratusCloud(100, 1000);
    if (!strat.rain().startsWith("It is raining"))
    System.out.println("Bad StratusCloud::rain");
    CumulusCloud cumu = new CumulusCloud(200, 2000);
    if (!cumu.rain().startsWith("It is raining"))
    System.out.println("Bad CumulusCloud::rain");
    CirrusCloud cirr = new CirrusCloud(300, 3000);
    if (!cirr.rain().startsWith("I cannot make"))
    System.out.println("Bad CirrusCloud::rain");
    Sky sky = new Sky();
    sky.add(strat);
    sky.add(cumu);
    sky.add(cirr);
    float mean = sky.getMeanHeight();
    if (mean < 1799  ||  mean > 1801)
    System.out.println("Bad mean height for 3 clouds "
    + "with bottom:top = { 100:1000, 200:2000, 300:3000 }");
    else
    System.out.println("Sky2 gives correct mean");
  }
  public static void testSky2Structure()
  {
    try
    {
      Class.forName("weather.Sky2").getDeclaredField("clouds");        // should throw because clouds should not exist.
      System.out.println("Sky2 should not contain field clouds");
    }
    catch (NoSuchFieldException|ClassNotFoundException x) { }

    try
    {
      Class<?> superclass = Class.forName("weather.Sky2").getSuperclass();
      if (superclass != java.util.ArrayList.class)
      System.out.println("Sky2 does not extend ArrayList");
    }
    catch (ClassNotFoundException x) { }
    System.out.println("Sky2 structure is coreect ");
  }
  public static void testSky2Behavior()
  {

    testSkyBehavior();
  }
  public static void testCloudSubclassing()
  {
    String[] subClouds = { "CirrusCloud", "CumulusCloud", "StratusCloud" };
    String message = "";
    for (String subCloud: subClouds)
    {
      try
      {
        Class<?> superclass = Class.forName("weather." + subCloud).getSuperclass();
        if (superclass != Class.forName("weather.Cloud"))
        message += subCloud + " does not extend Cloud. ";
      }
      catch (ClassNotFoundException x) { }
    }
    message = message.trim();
    if(!message.isEmpty())
    System.out.println(message);
    else
    System.out.println("subclassing for all the cloud classes are correct");
  }


  public static void testCloudCtors()
  {
    String[] allClouds = { "Cloud", "CirrusCloud", "CumulusCloud", "StratusCloud" };
    String message = "";
    Class<?>[] expectedArgTypes = { float.class, float.class };
    for (String cloud: allClouds)
    {
      try
      {
        Class.forName("weather." + cloud).getDeclaredConstructor(expectedArgTypes);
      }
      catch (ClassNotFoundException x) { }
      catch (NoSuchMethodException x)
      {
        message += cloud + " does not have a (float, float) ctor. ";
      }
    }
    message = message.trim();
    if(!message.isEmpty())
    System.out.println(message);
    else
    System.out.println("Ctors for all the cloud classes are correct");
  }


  public static void testSkyAdd()
  {
    try
    {
      Class<?>[] expectedTypes = { Class.forName("weather.Cloud") };
      Class.forName("weather.Sky").getMethod("add", expectedTypes);
    }
    catch (ClassNotFoundException x) { }
    catch (NoSuchMethodException x)
    {
      System.out.println("No add(Cloud) method in Sky");
    }
  }
  public static void main(String[] args)
  {
    testBasicClassesExist();
    testSky2Exists();
    testSkyStructure();
    testSkyBehavior();
    testSky2Structure();
    testSky2Behavior();
    testCloudSubclassing();
    testCloudCtors();
    testSkyAdd();
  }
}
