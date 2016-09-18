package weather;

import java.util.ArrayList;

public class Sky2 extends ArrayList<Cloud> {
  Sky2() {
    super(100);
  }

  public float getMeanHeight() {
    float meanHeight= 0;

    for (Cloud c : this) {
      meanHeight += c.getHeight();
    }

    return meanHeight / this.size();
  }

  public boolean add(Cloud c) {
    this.add(c);
    return true;
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
