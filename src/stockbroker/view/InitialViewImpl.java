package stockbroker.view;

import java.io.PrintStream;

/**
 * Initial view implementation. Helps in selecting the type of the UI user would want to proceed
 * with.
 */
public class InitialViewImpl implements InitialView {

  private PrintStream out;

  /**
   * Constructor for the view.
   *
   * @param out PrintStream object to print the output to the screen.
   */
  public InitialViewImpl(PrintStream out) {
    this.out = out;
  }

  @Override
  public void selectView() {
    out.println("--------------------------------------------");
    out.println("Select the view type:");
    out.println("--------------------------------------------");
    out.println("1: Text based interface");
    out.println("2: Graphical User Interface(GUI)");
  }
}
