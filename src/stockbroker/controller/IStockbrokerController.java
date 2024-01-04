package stockbroker.controller;

import java.io.IOException;

/**
 * The interface for controller of stockbroker application. Controller delegates functionalities to
 * model and view.
 */
public interface IStockbrokerController {

  /**
   * The function to start controller.
   *
   * @throws IOException for file errors.
   */
  void goController() throws IOException;
}
