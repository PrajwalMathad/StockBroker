package stockbroker.controller;

import java.io.IOException;

/**
 * Initial controller creates the controller and view pair based on the user selection. User can
 * choose text based interface or graphical user interface.
 */
public interface InitialController {

  /**
   * This method initialises the controller and view pair based on the user selection.
   *
   * @throws IOException for file handling errors.
   */
  void goController() throws IOException;
}
