package stockbroker.model;

import java.io.IOException;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The interface for model of FlexStockbroker class. It contains all the functions to support the
 * flexible portfolios, buy stocks and view the created portfolio on a date, get cost basis, get
 * performance of a portfolio.
 */
public interface IFlexStockbroker extends IStockbroker {

  /**
   * Get all the details of the stock for provided qty, transaction date and commission fee.
   *
   * @param quantity      quantity of stocks to buy or sell.
   * @param date          date of transaction.
   * @param commissionFee commission fee for the transaction.
   * @return a String containing all the details of the stock.
   * @throws IllegalArgumentException if invalid qty or transaction date.
   * @throws IOException              on file handling errors.
   */
  String getStockDetailsWithTotalPrice(double quantity, String date, double commissionFee)
      throws IllegalArgumentException, IOException;

  /**
   * Adds a transaction to the portfolio.
   *
   * @param fileIndex       index of the portfolio file from the list.
   * @param transactionType either buy or sell.
   * @throws IOException on file handling errors.
   */
  void addToPortfolio(String fileIndex, String transactionType) throws IOException;

  /**
   * Validates if a sell transaction is possible for the given details.
   *
   * @param fileIndex     index of the portfolio file from the list.
   * @param quantity      of stocks to sell.
   * @param date          date of sell transaction.
   * @param commissionFee commission fee for the sell transaction.
   * @throws IllegalArgumentException if the transaction cannot be performed.
   * @throws IOException              on file handling errors.
   */
  void validateSellDetails(String fileIndex, double quantity, String date, double commissionFee)
      throws IllegalArgumentException, IOException;

  /**
   * View the composition of a portfolio on a date.
   *
   * @param fileIndex index of the portfolio file from the list.
   * @param date      date on when the composition of the portfolio has to be fetched.
   * @return the compostion in a formatted string.
   * @throws IOException on file handling errors.
   */
  String viewPortfolio(String fileIndex, String date) throws IOException;

  /**
   * Get the value of a portfolio on a given date.
   *
   * @param fileIndex index of the portfolio file from the list.
   * @param date      date at which the value to be shown.
   * @return the value of a portfolio.
   * @throws IOException on file handling errors.
   */
  double getPortfolioValue(String fileIndex, String date) throws IOException;

  /**
   * Get the cost basis of a portfolio on a given date.
   *
   * @param fileIndex index of the portfolio file from the list.
   * @param date      date at which the value to be shown.
   * @return the cost basis value of a portfolio.
   * @throws IOException on file handling errors.
   */
  double getCostBasisOnADate(String fileIndex, String date) throws IOException;

  /**
   * Provide the portfolio performance over a period of time.
   *
   * @param fileIndex index of the portfolio file from the list.
   * @param startDate start date for performance evaluation.
   * @param endDate   end date for performance evaluation.
   * @return a Map containing dates and the number of points to show on bar graph.
   * @throws IOException    on file handling errors.
   * @throws ParseException if invalid dates.
   */
  Map<String, Integer> getPortfolioPerformance(String fileIndex, String startDate, String endDate)
      throws IOException, ParseException;

  /**
   * Invest a fixed amount in a portfolio into different stocks by specifying weights.
   *
   * @param fileIndex     index of the portfolio file from the list.
   * @param stockWeights  the map containing symbol and percentage of amount to invest in each
   *                      stock.
   * @param amount        the amount in dollars to be invested.
   * @param commissionFee commission fee charged per transaction.
   * @param date          date on which the amount has to be invested.
   * @throws IllegalArgumentException when data is not found or weights sum doesn't add to 100.
   * @throws IOException              on file handling errors.
   */
  void investFixedAmountInPortfolio(String fileIndex, Map<String, Double> stockWeights,
      double amount, double commissionFee, String date)
      throws IllegalArgumentException, IOException;

  /**
   * Create a dollar cost averaging strategy by investing a fixed amount in a portfolio into
   * different stocks by specifying weights at specified frequency.
   *
   * @param name          of the strategy to be created. There can be multiple dollar cost
   *                      strategies.
   * @param stockWeights  the map containing symbol and percentage of amount to invest in each
   *                      stock.
   * @param amount        the amount in dollars to be invested.
   * @param commissionFee commission fee charged per transaction.
   * @param startDate     date when the strategy has to be created.
   * @param endDate       end date of the strategy, can be null. Null represents no end date.
   * @param frequency     The frequency at which the amount has to be invested.
   * @throws IllegalArgumentException when data is not found.
   * @throws IOException              on file handling errors.
   */
  void createDollarCostStrategyPF(String name, LinkedHashMap<String, Double> stockWeights,
      double amount, double commissionFee, String startDate, String endDate, int frequency)
      throws IllegalArgumentException, IOException;

  /**
   * Get method for stock and quantity in a portfolio. Called after viewPortfolio is called.
   *
   * @return map object of stock and quantity present in a portfolio.
   */
  Map<String, Double> getStockQtyMap();

}
