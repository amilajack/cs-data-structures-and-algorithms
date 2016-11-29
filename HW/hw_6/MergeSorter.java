package mergesort;

public class MergeSorter {

  public static void mergeSort(int[] items) {
    int arrayLength = items.length;

    // Base Case
    if (arrayLength < 2) return;

    int middleIndex = arrayLength / 2;
    int leftSideLength = middleIndex;
    int rightSideLength = arrayLength - middleIndex;

    // Allocate a fixed length integer arrays and allocate to them later
    int[] leftSide = new int[leftSideLength];
    int[] rightSide = new int[rightSideLength];

    for (int i = 0; i < middleIndex; i++) {
      leftSide[i] = items[i];
    }
    for (int i = middleIndex; i < arrayLength; i++) {
      rightSide[i - middleIndex] = items[i];
    }

    // Sort the left and the right sides of the array and merge them together
    mergeSort(leftSide);
    mergeSort(rightSide);
    merge(leftSide, rightSide, items);
  }

  /**
   * Mutate the result reference instead of returning a new array of ints
   * @param  {leftSide}  | The left side of the array previous array
   * @param  {rightSide} | The right side of the array previous array
   * @param  {result}    | The array to merge into
   */
  public static void merge(int[] leftSide, int[] rightSide, int[] result) {
    int leftSideLength = leftSide.length;
    int rightSideLength = rightSide.length;
    int leftSideIndex = 0, rigthSideIndex = 0, resultSideIndex = 0;

    while (leftSideIndex < leftSideLength && rigthSideIndex < rightSideLength) {
      if (leftSide[leftSideIndex] <= rightSide[rigthSideIndex]) {
        result[resultSideIndex++] = leftSide[leftSideIndex++];
      } else {
        result[resultSideIndex++] = rightSide[rigthSideIndex++];
      }
    }
    while (leftSideIndex < leftSideLength) {
      result[resultSideIndex++] = leftSide[leftSideIndex++];
    }
    while (rigthSideIndex < leftSideLength) {
      result[resultSideIndex++] = rightSide[rigthSideIndex++];
    }
  }
}
