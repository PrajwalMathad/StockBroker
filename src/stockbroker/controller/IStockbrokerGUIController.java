package stockbroker.controller;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import stockbroker.view.IStockbrokerGUI;

/**
 * The interface for controller of stockbroker GUI application. This Controller controls all the
 * operation flows in the graphical user interface
 */
public interface IStockbrokerGUIController {

  /**
   * Method creates the portfolio.
   */
  void createPortfolio();

  /**
   * Method handles the buy/sell of stocks from the portfolio.
   */
  void stockTransactionHandler();

  /**
   * Method handles the common flows such as get cost basis, portfolio value, view portfolio. These
   * operation take in date as input before proceeding. This method helps in selecting the portfolio
   * and setting the portfolio name.
   *
   * @param type of action after the portfolio selection.
   */
  void commonValueFlowHandler(String type);

  /**
   * Method provides the list of available portfolios in the application.
   *
   * @param action type of action to be performed after the portfolio selection.
   */
  void selectPortfolioHandler(String action);

  /**
   * Method handles loading the portfolios from files.
   *
   * @param filePath to the portfolio file to be loaded.
   */
  void loadPortfolioHandler(String filePath);

  /**
   * Method to validate the stock symbol.
   *
   * @param symbol to be validated.
   * @return true/false if the symbol is valid/invalid.
   */
  boolean validateStockSymbol(String symbol);

  /**
   * Method provides the stocks and quantity values of a portfolio in a map. The parameters will be
   * validated before the map is generated.
   *
   * @param date          on which the portfolio values to be selected.
   * @param commissionFee commission fee for transaction.
   * @param amount        to be invested.
   */
  void getStockQuantityMap(String date, String commissionFee, String amount);

  /**
   * Method provides the performance data of the portfolio over a period of time.
   *
   * @param startDate start date of the performance evaluation.
   * @param endDate   end date of the performance evaluation.
   */
  void getPerformanceData(String startDate, String endDate);

  /**
   * Method to control the flow of operations for investing a specified amount, with weightages in a
   * portfolio.
   *
   * @param stockWeights  map of stocks and the corresponding weightage.
   * @param amount        to be invested.
   * @param commissionFee for each transaction.
   * @param date          of investment.
   */
  void investFixedAmountInPortfolio(Map<String, Double> stockWeights, String amount,
      String commissionFee, String date);

  /**
   * Method to control the flow of creation of dollar cost averaging. Creates a file and adds the
   * details.
   *
   * @param name          of the portfolio.
   * @param stockWeights  map of the stocks and the corresponding weight values.
   * @param amount        to be invested.
   * @param commissionFee for each transaction.
   * @param startDate     of investing.
   * @param endDate       of investing.
   * @param frequency     in number of days to invest.
   */
  void createDollarCostStrategyPF(String name, LinkedHashMap<String, Double> stockWeights,
      String amount, String commissionFee, String startDate, String endDate, String frequency);

  /**
   * The start method for the GUI controller.
   *
   * @param view the GUI view object.
   * @throws IOException for file errors.
   */
  void goController(IStockbrokerGUI view) throws IOException;
}
