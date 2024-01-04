import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


import java.io.File;

import java.io.IOException;

import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


import stockbroker.model.IStockbroker;
import stockbroker.model.StockBroker;

/**
 * Test the StockBroker Model Implementation.
 */
public class StockBrokerTest {

  private IStockbroker sb;

  @Before
  public void setup() {

    sb = new StockBroker();
    String currentdir = System.getProperty("user.dir");
    Path actualPath = Paths.get(currentdir);
    String dirToCreate = actualPath + File.separator + "portfolios";
    File dir = new File(dirToCreate);//The name of the directory to create

    if (!dir.exists()) {
      dir.mkdir();
    }
  }

  @Test
  public void testValidateSymbolValidSymbolInput() throws IOException {
    Assert.assertEquals(true, sb.validateSymbol("GOOG"));
  }

  @Test
  public void testValidateSymbolInvalidSymbolInput() throws IOException {
    Assert.assertEquals(false, sb.validateSymbol("GOOGLE"));
  }

  @Test
  public void testGetStockDetails() throws IOException {
    String valueToAssert =
        "Stock Name: GOOG, Alphabet Inc - Class C" + "\n" + "Stock Exchange: NASDAQ";
    sb.validateSymbol("GOOG");
    Assert.assertEquals(true, sb.getStockDetails().contains(valueToAssert));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetStockValueOnAnInvalidDate() throws IOException {
    sb.validateSymbol("GOOG");
    sb.getStockValueOnADate("2022-10-30");
  }

  @Test
  public void testGetStockValueOnAnValidDate() throws IOException {
    sb.validateSymbol("GOOG");
    Assert.assertEquals(83.49, sb.getStockValueOnADate("2022-11-03"), 0.00);
  }

  @Test
  public void testCreatePortfolio() {
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
    LocalDateTime now = LocalDateTime.now();

    String filename = now.toString().replace(" ", "").replace("/", "-").replace(":", "-");

    try {
      sb.createPortfolio(filename);
    } catch (IOException e) {
      Assert.fail();
    }
  }


  @Test(expected = IOException.class)
  public void testCreatePortfolioFailsOnPortfolioExist() throws IOException {
    String filename = "a";
    sb.createPortfolio(filename);
    sb.createPortfolio(filename);
  }

  @Test
  public void testGetStockDetailsWithTotalPrice() throws IOException {

    sb.validateSymbol("GOOG");
    sb.getStockValueOnADate("2022-11-03");

    String result = sb.getStockDetailsWithTotalPrice(10);

    Assert.assertEquals(true, result.contains("Total value of stocks: $834.9"));

  }

  @Test(expected = IllegalArgumentException.class)
  public void testGetStockDetailsWithTotalPriceInvalidQuantity() throws IOException {
    sb.validateSymbol("GOOG");
    sb.getStockValueOnADate("2022-11-03");
    String result = sb.getStockDetailsWithTotalPrice(-10);
  }


  @Test
  public void testGetStockTotalValue() throws IOException {

    sb.validateSymbol("GOOG");
    sb.getStockValueOnADate("2022-11-03");

    sb.getStockDetailsWithTotalPrice(10);

    Assert.assertEquals(834.9, sb.getStockTotalValue(), 0.0);
  }

  @Test
  public void testAddToPortfolio() throws IOException {
    sb.createPortfolio("testAddToPortfolio");
    sb.validateSymbol("GOOG");
    sb.getStockDetailsWithTotalPrice(10);
    sb.addToPortfolio();
    String text = Files.readString(Paths.get("portfolios/testAddToPortfolio.csv"));
    String result = "GOOG,10.0";

    Assert.assertEquals(true, text.contains(result));

  }

  @Test
  public void testListPortfolio() throws IOException {
    sb.createPortfolio("testList1");
    sb.createPortfolio("testList2");

    Assert.assertEquals(true, sb.listPortfolio().contains("testList1.csv"));
    Assert.assertEquals(true, sb.listPortfolio().contains("testList2.csv"));

  }


  @Test
  public void testViewPortfolio() throws IOException {
    sb.createPortfolio("testViewPortfolio");
    sb.validateSymbol("GOOG");
    sb.getStockDetailsWithTotalPrice(10);
    sb.addToPortfolio();
    String result = "GOOG | 10.0 | 2022-11-03 |";

    int index = sb.getFileIndex("testViewPortfolio");
    Assert.assertEquals(true, sb.viewPortfolio("" + index).contains(result));

  }

  @Test
  public void testGetPortfolioValue() throws IOException {
    sb.createPortfolio("testGetPortfolioValue");
    sb.validateSymbol("GOOG");
    sb.getStockDetailsWithTotalPrice(10);
    sb.addToPortfolio();
    sb.validateSymbol("META");
    sb.getStockDetailsWithTotalPrice(10);
    sb.addToPortfolio();

    int index = sb.getFileIndex("testGetPortfolioValue");

    Assert.assertEquals(1724.0, sb.getPortfolioValue("" + index, "2022-11-03"), 0.00);

  }

  @Test
  public void testLoadPortfolioValid() throws IOException {
    PrintWriter writer = new PrintWriter("test/testLoadPortfolioValid.csv", "UTF-8");
    writer.println("Symbol,Quantity,BuyDate");
    writer.println("GOOG,10.0,2022-11-03");
    writer.close();

    sb.loadPortfolio("test/testLoadPortfolioValid.csv");

    Assert.assertEquals(true, sb.listPortfolio().contains("testLoadPortfolioValid.csv"));

  }

  @Test(expected = IllegalArgumentException.class)
  public void testLoadPortfolioInValid() throws IOException {
    PrintWriter writer = new PrintWriter("test/testLoadPortfolioInValid.csv", "UTF-8");
    writer.println("Symbol,Quantity,BuyDate");
    writer.println("GOOGLE,10.0,2022-11-03");
    writer.close();

    sb.loadPortfolio("test/testLoadPortfolioInValid.csv");
  }


}