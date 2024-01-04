package stockbroker.model;

import java.io.IOException;
import java.util.List;

/**
 * The interface for model of stockbroker application. It contains all the functions to create a
 * portfolio, buy stocks and view the created portfolio.
 */
public interface IStockbroker {

  /**
   * Validate the input stock name(symbol).
   *
   * @param symbol symbol of stock.
   * @return true/false if symbol is valid/invalid.
   * @throws IOException on file errors.
   */
  boolean validateSymbol(String symbol) throws IOException;

  /**
   * Get the stock details such as stock name, current price etc.
   *
   * @return formatted string of stock details.
   */
  String getStockDetails();

  /**
   * Get the total value of stock based on quantity.
   *
   * @return formatted string of stock details.
   */
  double getStockTotalValue();

  /**
   * Get the current value of the stock.
   *
   * @param date on which the value to be fetched.
   * @return current value of stock as double.
   * @throws IllegalArgumentException for wrong date entry.
   */
  double getStockValueOnADate(String date) throws IllegalArgumentException;

  /**
   * Creates a csv file to store the portfolio details {name}.csv
   *
   * @param name name of the portfolio.
   * @throws IOException on file errors.
   */
  void createPortfolio(String name) throws IOException;

  /**
   * Adds a new stock to the portfolio.
   *
   * @throws IOException on file errors.
   */
  void addToPortfolio() throws IOException;

  /**
   * Lists all the saved portfolios.
   *
   * @return list of portfolio names.
   */
  List<String> listPortfolio();

  /**
   * Shows the portfolio details.
   *
   * @param fileIndex index of the file to fetch the details.
   * @return a formatted string containing the portfolio details.
   * @throws IOException on file errors.
   */
  String viewPortfolio(String fileIndex) throws IOException;

  /**
   * Fetch the stock listing data and save it in a csv file.
   */
  void fetchStockListing() throws IOException;

  /**
   * Get the stock details along with the total value of stocks.
   *
   * @param quantity Number of stocks to add to portfolio.
   * @return stock details as formatted string.
   * @throws IllegalArgumentException for error in quantity value.
   */
  String getStockDetailsWithTotalPrice(double quantity) throws IllegalArgumentException;

  /**
   * Get the portfolio value on a specific date.
   *
   * @param fileIndex index of the portfolio file from the list.
   * @param date      date at which the value to be shown.
   * @return value of the portfolio.
   * @throws IOException for file errors.
   */
  double getPortfolioValue(String fileIndex, String date) throws IOException;

  /**
   * Get the file index from the list of portfolios.
   *
   * @param fileName name of the portfolio file.
   * @return the index of the file from the list.
   */
  int getFileIndex(String fileName);

  /**
   * Load a portfolio from providing an external file.
   *
   * @param path path to the file.
   * @throws IllegalArgumentException for wrong path.
   * @throws IOException              for file errors.
   */
  void loadPortfolio(String path) throws IllegalArgumentException, IOException;

}
