package stockbroker.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import stockbroker.model.FlexStockbroker;
import stockbroker.model.IFlexStockbroker;
import stockbroker.model.IStockbroker;
import stockbroker.view.IStockbrokerGUI;
import stockbroker.view.IStockbrokerView;
import stockbroker.view.InitialView;
import stockbroker.view.StockbrokerGUI;
import stockbroker.view.StockbrokerView;

/**
 * Initial controller implementation. It decides the type of model-controller-view objects creation
 * based on the user selection. User can select the text based interface or the graphical user
 * interface.
 */
public class InitialControllerImpl implements InitialController {

  private Scanner in;
  private InitialView view;

  /**
   * Constructor for the initial controller implementation.
   *
   * @param in   InputStream object to read inputs.
   * @param view initial view object.
   */
  public InitialControllerImpl(InputStream in, InitialView view) {
    this.view = view;
    this.in = new Scanner(in);
  }

  @Override
  public void goController() throws IOException {
    view.selectView();
    String pfType = in.nextLine();
    if (pfType.equals("1")) {
      IStockbroker model = null;
      IStockbrokerView textView = new StockbrokerView(System.out);
      IStockbrokerController controller = new StockbrokerController(model, System.in, textView);
      controller.goController();
    } else {
      IFlexStockbroker model = new FlexStockbroker();
      IStockbrokerGUI guiView = new StockbrokerGUI("Investify");
      IStockbrokerGUIController controller = new StockbrokerGUIController(model);
      controller.goController(guiView);
    }
  }
}
