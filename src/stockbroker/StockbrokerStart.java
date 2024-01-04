package stockbroker;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import stockbroker.controller.InitialController;
import stockbroker.controller.InitialControllerImpl;
import stockbroker.view.InitialView;
import stockbroker.view.InitialViewImpl;

/**
 * Start of the Stock broker application. Contains the main method.
 */
public class StockbrokerStart {

  /**
   * Main method of the application.
   *
   * @param args command line arguments. Not supported.
   * @throws IOException on file errors.
   */
  public static void main(String[] args) throws IOException {
    InitialView view = new InitialViewImpl(System.out);
    String currentDir = System.getProperty("user.dir");
    Path actualPath = Paths.get(currentDir);
    String dirToCreate = actualPath + File.separator + "portfolios";
    String dirToCreate2 = actualPath + File.separator + "flexPortfolios";
    String dirToCreate3 = actualPath + File.separator + "dollarCostStrategy";
    File dir = new File(dirToCreate);//The name of the directory to create
    File dir2 = new File(dirToCreate2);
    File dir3 = new File(dirToCreate3);
    if (!dir.exists()) {
      dir.mkdir();
    }
    if (!dir2.exists()) {
      dir2.mkdir();
    }
    if (!dir3.exists()) {
      dir3.mkdir();
    }
    InitialController controller = new InitialControllerImpl(System.in, view);
    controller.goController();
  }
}
