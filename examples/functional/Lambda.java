package examples.functional;

import java.util.Arrays;
import java.util.ArrayList;

class Lambda {

  public List<String> mapOverElements() {
    List<String> items = new ArrayList<>(Arrays.asList("A", "B", "C", "D"));

    return items.stream()
      .map(u -> u.toLowerCase())
      .collect(Collectors.toList());
  }

  public static void main(String[] args) {
    Lambda example = new Lambda();

    // Prints a [a, b, c, d]
    System.out.print(example.mapOverElements());
  }
}
