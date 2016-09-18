package weather;

import java.util.ArrayList;

public class Sky {

  private ArrayList<Cloud> clouds;

  Sky() {
    clouds = new ArrayList<Cloud>(100);
  }

  public boolean add(Cloud c) {
    this.clouds.add(c);
    return true;
  }

  public float getMeanHeight() {
    float meanHeight= 0;

    for (Cloud c : this.clouds) {
      meanHeight += c.getHeight();
    }

    return meanHeight / this.clouds.size();
  }

  public static void main(String[] args) {
    StratusCloud strat = new StratusCloud(100, 1000);

    if (!strat.rain().startsWith("It is raining")) {
      System.out.println("Bad StratusCloud::rain");
    }

    CumulusCloud cumu = new CumulusCloud(200, 2000);

    if (!cumu.rain().startsWith("It is raining")) {
      System.out.println("Bad CumulusCloud::rain");
    }

    CirrusCloud cirr = new CirrusCloud(300, 3000);

    if (!cirr.rain().startsWith("I cannot make")) {
      System.out.println("Bad CirrusCloud::rain");
    }

    Sky sky = new Sky();

    sky.add(strat);
    sky.add(cumu);
    sky.add(cirr);

    float mean = sky.getMeanHeight();

    if (mean < 1799 || mean > 1801) {
      System.out.println("Bad mean height: expected 1800, saw " + mean);
    }

    System.out.println("Everything (else) is ok");
  }
}
