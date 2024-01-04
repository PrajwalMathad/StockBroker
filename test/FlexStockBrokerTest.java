import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;

import stockbroker.model.FlexStockbroker;
import stockbroker.model.IFlexStockbroker;

/**
 * A class to test the functionalities of Flexible Stock Broker.
 */
public class FlexStockBrokerTest {

  private IFlexStockbroker sb;

  @Before
  public void setup() {

    sb = new FlexStockbroker();
    String currentdir = System.getProperty("user.dir");
    Path actualPath = Paths.get(currentdir);
    String dirToCreate = actualPath + File.separator + "flexPortfolios";
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
  public void testGetStockDetailsWithTotalPrice() throws IOException {
    String valueToAssert =
        "Stock Name: GOOG, Alphabet Inc - Class C" + "\n" + "Stock Exchange: NASDAQ";
    sb.validateSymbol("GOOG");
    Assert.assertEquals(true,
        sb.getStockDetailsWithTotalPrice(10, "2022-11-15", 10).contains(valueToAssert));
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
  public void testGetStockDetailsWithTotalPriceCheckPrice() throws IOException {
    String valueToAssert = "987.2";
    sb.validateSymbol("GOOG");
    Assert.assertEquals(true,
        sb.getStockDetailsWithTotalPrice(10, "2022-11-15", 10).contains(valueToAssert));
  }


  @Test
  public void testAddToPortfolio() throws IOException {

    sb.createPortfolio("testAddToPortfolio");
    sb.validateSymbol("GOOG");
    sb.getStockDetailsWithTotalPrice(10, "2022-11-15", 10);
    sb.addToPortfolio(sb.getFileIndex("testAddToPortfolio") + "", "buy");
    String text = Files.readString(Paths.get("flexPortfolios/testAddToPortfolio.csv"));
    String result = "GOOG,10.0";

    Assert.assertEquals(true, text.contains(result));


  }


  @Test(expected = IllegalArgumentException.class)
  public void testValidSell() throws IOException {
    sb.createPortfolio("testValidateSell");
    sb.validateSymbol("GOOG");
    sb.getStockDetailsWithTotalPrice(10, "2022-11-15", 10);
    sb.addToPortfolio(sb.getFileIndex("testValidateSell") + "", "buy");
    sb.validateSymbol("GOOG");
    sb.validateSellDetails(sb.getFileIndex("testValidateSell") + "", 11, "2022-11-15", 10);


  }

  @Test
  public void testViewPortfolioOnADate() throws IOException {
    sb.createPortfolio("testViewPortfolio");
    sb.validateSymbol("GOOG");
    sb.getStockDetailsWithTotalPrice(10, "2022-11-15", 10);
    sb.addToPortfolio(sb.getFileIndex("testViewPortfolio") + "", "buy");
    String result = "GOOG | 10.0";

    Assert.assertEquals(true,
        sb.viewPortfolio(sb.getFileIndex("testViewPortfolio") + "", "2022-11-15").contains(result));


  }

  @Test
  public void testGetPortfolioValue() throws IOException {
    sb.createPortfolio("testGetPortfolioValue");
    sb.validateSymbol("GOOG");
    sb.getStockDetailsWithTotalPrice(10, "2022-11-15", 10);
    sb.addToPortfolio(sb.getFileIndex("testGetPortfolioValue") + "", "buy");

    Assert.assertEquals(987.2,
        sb.getPortfolioValue(sb.getFileIndex("testGetPortfolioValue") + "", "2022-11-15"), 0.00);
  }


  @Test
  public void testCostBasis() throws IOException {
    sb.createPortfolio("testCostBasis");
    sb.validateSymbol("GOOG");
    sb.getStockDetailsWithTotalPrice(10, "2022-11-15", 10);
    sb.addToPortfolio(sb.getFileIndex("testCostBasis") + "", "buy");
    Assert.assertEquals(997.2,
        sb.getCostBasisOnADate(sb.getFileIndex("testCostBasis") + "", "2022-11-15"), 0.00);
  }

  @Test
  public void testMultipleTransaction() throws IOException {
    sb.createPortfolio("testMulti");
    sb.validateSymbol("GOOG");
    sb.getStockDetailsWithTotalPrice(10, "2022-11-10", 10);
    sb.addToPortfolio(sb.getFileIndex("testMulti") + "", "buy");
    sb.validateSymbol("GOOG");
    sb.getStockDetailsWithTotalPrice(10, "2022-11-11", 10);
    sb.addToPortfolio(sb.getFileIndex("testMulti") + "", "buy");
    sb.validateSymbol("INFY");
    sb.getStockDetailsWithTotalPrice(10, "2022-11-11", 10);
    sb.addToPortfolio(sb.getFileIndex("testMulti") + "", "buy");
    sb.validateSymbol("GOOG");
    sb.validateSellDetails(sb.getFileIndex("testMulti") + "", 10, "2022-11-15", 10);
    sb.addToPortfolio(sb.getFileIndex("testMulti") + "", "sell");
    Assert.assertEquals(1184.8,
        sb.getPortfolioValue(sb.getFileIndex("testMulti") + "", "2022-11-15"), 0.1);
  }

  @Test
  public void testInvestFixedAmount() throws IOException {
    sb.createPortfolio("testInvestFixedAmount");
    LinkedHashMap<String, Double> stockWt = new LinkedHashMap<>();
    stockWt.put("GOOG", 33.33);
    stockWt.put("INFY", 33.33);
    stockWt.put("TTM", 33.34);
    sb.investFixedAmountInPortfolio(sb.getFileIndex("testInvestFixedAmount") + "", stockWt, 1000,
        10, "2022-11-25");
    //assert if it's invested or not by checking value of portfolio
    Assert.assertEquals(970.00,
        sb.getPortfolioValue(sb.getFileIndex("testInvestFixedAmount") + "", "2022-11-25"), 0.1);


  }

  @Test(expected = IllegalArgumentException.class)
  public void testInvestFixedAmountInvalidWeight() throws IOException {
    sb.createPortfolio("testInvestFixedAmountInvalidWeight");
    LinkedHashMap<String, Double> stockWt = new LinkedHashMap<>();
    stockWt.put("GOOG", 33.33);
    stockWt.put("INFY", 33.33);
    stockWt.put("TTM", 30.00);
    sb.investFixedAmountInPortfolio(sb.getFileIndex("testInvestFixedAmountInvalidWeight") + "",
        stockWt, 1000, 10, "2022-11-25");
    //assert if it's invested or not by checking value of portfolio
    Assert.assertEquals(970.00,
        sb.getPortfolioValue(sb.getFileIndex("testInvestFixedAmountInvalidWeight") + "",
            "2022-11-25"), 0.1);

  }

  @Test(expected = IllegalArgumentException.class)
  public void testCreateDollarCostStrategyInvalidWt() throws IOException {
    LinkedHashMap<String, Double> stockWt = new LinkedHashMap<>();
    stockWt.put("GOOG", 33.33);
    stockWt.put("INFY", 33.33);
    stockWt.put("TTM", 30.00);
    sb.createDollarCostStrategyPF("testCreateDollarCostStrategyInvalidWt", stockWt, 1000, 10,
        "2022-06-01", null, 30);
    //assert if it's invested or not by checking value of portfolio
    Assert.assertEquals(970.00,
        sb.getPortfolioValue(sb.getFileIndex("testCreateDollarCostStrategyInvalidWt") + "",
            "2022-11-25"), 0.1);

  }

  @Test
  public void testCreateDollarCostStrategyWithEndDate() throws IOException {
    LinkedHashMap<String, Double> stockWt = new LinkedHashMap<>();
    stockWt.put("GOOG", 33.33);
    stockWt.put("INFY", 33.33);
    stockWt.put("TTM", 33.34);
    sb.createDollarCostStrategyPF("testCreateDollarCostStrategyWithEndDate", stockWt, 1000, 10,
        "2022-06-01", "2022-09-30", 30);
    //assert if it's invested or not by checking cost basis of portfolio
    Assert.assertEquals(5000.00,
        sb.getCostBasisOnADate(sb.getFileIndex("testCreateDollarCostStrategyWithEndDate") + "",
            "2022-09-30"), 0.1);

    //check 1 month after end date
    Assert.assertEquals(5000.00,
        sb.getCostBasisOnADate(sb.getFileIndex("testCreateDollarCostStrategyWithEndDate") + "",
            "2022-10-31"), 0.1);


  }


  @Test
  public void testCreateDollarCostStrategyWithNoEndDate() throws IOException {
    LinkedHashMap<String, Double> stockWt = new LinkedHashMap<>();
    stockWt.put("GOOG", 33.33);
    stockWt.put("INFY", 33.33);
    stockWt.put("TTM", 33.34);
    sb.createDollarCostStrategyPF("testCreateDollarCostStrategyWithNoEndDate", stockWt, 1000, 10,
        "2022-06-01", null, 30);
    //assert if it's invested or not by checking cost basis of portfolio
    Assert.assertEquals(5000.00,
        sb.getCostBasisOnADate(sb.getFileIndex("testCreateDollarCostStrategyWithNoEndDate") + "",
            "2022-09-30"), 0.1);

    //check next month
    Assert.assertEquals(6000.00,
        sb.getCostBasisOnADate(sb.getFileIndex("testCreateDollarCostStrategyWithNoEndDate") + "",
            "2022-10-31"), 0.1);

  }


  @Test
  public void testCreateDollarCostStrategyOnHoliday() throws IOException {
    LinkedHashMap<String, Double> stockWt = new LinkedHashMap<>();
    stockWt.put("GOOG", 33.33);
    stockWt.put("INFY", 33.33);
    stockWt.put("TTM", 33.34);
    sb.createDollarCostStrategyPF("testCreateDollarCostStrategyOnHoliday", stockWt, 1000, 10,
        "2022-06-04", null, 30);
    //assert if it's invested or not by checking cost basis of portfolio
    Assert.assertEquals(1000.00,
        sb.getCostBasisOnADate(sb.getFileIndex("testCreateDollarCostStrategyOnHoliday") + "",
            "2022-06-06"), 0.1);


  }


  @Test
  public void testBuyOnCreateDollarCostStrategy() throws IOException {
    LinkedHashMap<String, Double> stockWt = new LinkedHashMap<>();
    stockWt.put("GOOG", 33.33);
    stockWt.put("INFY", 33.33);
    stockWt.put("TTM", 33.34);
    sb.createDollarCostStrategyPF("testBuyOnCreateDollarCostStrategy", stockWt, 1000, 10,
        "2022-06-01", null, 30);
    //assert if it's invested or not by checking cost basis of portfolio
    Assert.assertEquals(1000.00,
        sb.getCostBasisOnADate(sb.getFileIndex("testBuyOnCreateDollarCostStrategy") + "",
            "2022-06-01"), 0.1);


  }

  @Test
  public void testGetCostBasisAfterFirstFrequencyOfDollarCostStgy() throws IOException {
    LinkedHashMap<String, Double> stockWt = new LinkedHashMap<>();
    stockWt.put("GOOG", 33.33);
    stockWt.put("INFY", 33.33);
    stockWt.put("TTM", 33.34);
    sb.createDollarCostStrategyPF("testGetCostBasisAfterFirstFrequencyOfDollarCostStgy", stockWt,
        1000, 10, "2022-06-01", null, 30);
    //assert if it's invested or not by checking cost basis of portfolio
    Assert.assertEquals(2000.00, sb.getCostBasisOnADate(
            sb.getFileIndex("testGetCostBasisAfterFirstFrequencyOfDollarCostStgy")
                + "", "2022-07-01"),
        0.1);


  }

  @Test
  public void testViewPFOfDollarCostStgy() throws IOException {
    LinkedHashMap<String, Double> stockWt = new LinkedHashMap<>();
    stockWt.put("GOOG", 100.00);

    sb.createDollarCostStrategyPF("testViewPFOfDollarCostStgy", stockWt, 1000, 10, "2022-11-29",
        null, 30);
    //assert if it's invested or not by view portfolio
    String result = "GOOG | 10.37";

    Assert.assertEquals(true,
        sb.viewPortfolio(sb.getFileIndex("testViewPFOfDollarCostStgy") + "", "2022-11-29")
            .contains(result));


  }


  @Test(expected = IllegalArgumentException.class)
  public void testNegativeCommissionWhenBuyOrSell() throws IOException {
    sb.createPortfolio("testNegativeCommisionWhenBuyOrSell");
    sb.validateSymbol("GOOG");
    sb.getStockDetailsWithTotalPrice(10, "2022-11-15", -10);
  }


}



