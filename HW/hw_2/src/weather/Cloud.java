package weather;

public class Cloud {

  private float bottom;
  private float top;

  Cloud(float bottom, float top) {
    this.top = top;
    this.bottom = bottom;
  }

  public float getHeight() {
    return this.top - this.bottom;
  }

  public String rain() {
    return "It is raining";
  }
}
