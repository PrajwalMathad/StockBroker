import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import org.junit.Test;
import stockbroker.controller.StockbrokerController;
import stockbroker.model.IFlexStockbroker;
import stockbroker.model.IStockbroker;
import stockbroker.view.IStockbrokerView;
import stockbroker.view.StockbrokerView;

/**
 * Tests for controller.
 */
public class StockbrokerControllerTest {

  private IStockbroker model;
  private IFlexStockbroker flexModel;
  private PrintStream out;

  @Test
  public void testGoController() throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    InputStream in = new ByteArrayInputStream("1\n5".getBytes());
    PrintStream out = new PrintStream(bytes);
    IStockbrokerView view = new StockbrokerView(out);
    StockbrokerController controller = new StockbrokerController(model, in, view);
    controller.goController();
    String init = "Thank you for stopping by!";
    assertEquals(true, new String(bytes.toByteArray()).contains(init));
  }

  @Test
  public void testViewFlow() throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    InputStream in = new ByteArrayInputStream("1\n2\nQ\n5".getBytes());
    PrintStream out = new PrintStream(bytes);
    IStockbrokerView view = new StockbrokerView(out);
    StockbrokerController controller = new StockbrokerController(model, in, view);
    controller.goController();
    String init = "Select the option from the below portfolio list to get the details:";
    assertEquals(true, new String(bytes.toByteArray()).contains(init));
  }

  @Test
  public void testCreatePortfolioFlow() throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    InputStream in = new ByteArrayInputStream("1\n1\nfortest\n1\nINFY\n20\nyes\n3\n5".getBytes());
    PrintStream out = new PrintStream(bytes);
    IStockbrokerView view = new StockbrokerView(out);
    StockbrokerController controller = new StockbrokerController(model, in, view);
    controller.goController();
    String stockDetails = "Stock Name: INFY, Infosys Ltd";
    String confirmMessage = "Confirm buy stock(s). Enter yes/no";
    String successMessage = "Successfully updated the portfolio!";
    assertEquals(true, new String(bytes.toByteArray()).contains(stockDetails));
    assertEquals(true, new String(bytes.toByteArray()).contains(confirmMessage));
    assertEquals(true, new String(bytes.toByteArray()).contains(successMessage));
  }

  @Test
  public void testGetValuePortfolioFlow() throws IOException {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    InputStream in = new ByteArrayInputStream("2\n9".getBytes());//1    2022-11-16
    PrintStream out = new PrintStream(bytes);
    IStockbrokerView view = new StockbrokerView(out);
    StockbrokerController controller = new StockbrokerController(model, in, view);
    controller.goController();
    String stockDetails = "Select the portfolio type.";
    assertEquals(true, new String(bytes.toByteArray()).contains(stockDetails));
  }

}