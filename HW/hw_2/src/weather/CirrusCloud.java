package weather;

public class CirrusCloud extends Cloud {
  CirrusCloud(float a, float b) {
    super(a, b);
  }

  @Override
  public String rain() {
    return "I cannot make rain";
  }
}
