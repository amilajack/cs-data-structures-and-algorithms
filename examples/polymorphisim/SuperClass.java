package examples.polymorphisim;

public class SuperClass {

  SuperClass() {
    System.out.print("SuperClass has been constructed \n");
    this.privateMethodThatWillNotBeInherited();
  }

  /**
   * An example of a method that is inherited by the super class
   */
  public void exampleInheritedMethod() {
    System.out.print("Inherited Method \n");
  }

  /**
   * An example of a method that will be overriden by the SubClass
   */
  public void exampleOverridenMethod() {
    System.out.print("This will be overriden and will not be printed \n");
  }

  /**
   * This is a private method and is therefore invisible to the SubClass. It is
   * not inhereited.
   *
   * You may want to do this so you can hide as much functionality as possible
   * from sub classes.
   */
  public void privateMethodThatWillNotBeInherited() {
    System.out.print("This method is invisible to all sub classes");
  }
}
