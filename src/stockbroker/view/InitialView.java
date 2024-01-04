package stockbroker.view;

/**
 * Interface for the initial view of the application. Helps in selecting the type of the UI user
 * would want to proceed with.
 */
public interface InitialView {

  /**
   * provides the user with the text based interface and the graphical user interface options.
   */
  void selectView();
}
