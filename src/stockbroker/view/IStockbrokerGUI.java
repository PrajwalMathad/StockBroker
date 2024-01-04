package stockbroker.view;

import java.util.List;
import java.util.Map;
import stockbroker.controller.IStockbrokerGUIController;

/**
 * The interface for GUI of stockbroker application. Provides all the methods to display UI
 * components in the application. Handles the user interactions with the application.
 */
public interface IStockbrokerGUI {

  /**
   * Get method for the portfolio name.
   *
   * @return name of the selected/created portfolio.
   */
  String getPortfolioName();

  /**
   * Get method for the stock symbol name.
   *
   * @return name of the selected stock symbol.
   */
  String getStockName();

  /**
   * Get method for the stock quantity.
   *
   * @return entered quantity of the stock.
   */
  String getStockQuantity();

  /**
   * Get method for the transaction date.
   *
   * @return date of transaction of the stock.
   */
  String getTransactionDate();

  /**
   * Get method for the commission fee.
   *
   * @return commission fee for transaction.
   */
  String getCommissionFee();

  /**
   * Get method for the transaction type.
   *
   * @return buy/sell as transaction type.
   */
  String getTransactionType();

  /**
   * Method to show the main menu of options.
   *
   * @param show true/false for visibility.
   */
  void showMainMenu(Boolean show);

  /**
   * Displays the portfolio composition as a table.
   *
   * @param stockMap stock and quantity map in a portfolio.
   */
  void showPortfolioComposition(Map<String, Double> stockMap);

  /**
   * Handles the part to distribute the amount weightage among the stocks for investing and dollar
   * cost averaging.
   *
   * @param stockMap stock and quantity map in a portfolio.
   * @param isDCA    to check if it is for dollar cost averaging or ine time investing.
   */
  void weightageSplitHandler(Map<String, Double> stockMap, boolean isDCA);

  /**
   * Contains all the listeners for the UI components that need input from the controller.
   *
   * @param features controller object for the UI component to interact with in the view.
   */
  void listenerStore(IStockbrokerGUIController features);

  /**
   * Method to display the generic info message dialog box.
   *
   * @param message to be displayed.
   */
  void showMessageDialog(String message);

  /**
   * Method to display the error messages as a dialog box.
   *
   * @param message error message to be displayed.
   */
  void showErrorDialog(String message);

  /**
   * Method displays the list of available portfolios. User can select the required portfolio from
   * the list to proceed.
   *
   * @param portfolios list.
   * @param action     to be performed after the selection.
   */
  void selectPortfolio(List<String> portfolios, String action);

  /**
   * Plots the performance bar chart for the selected portfolio over a period of time entered by the
   * user.
   *
   * @param performanceData map of the dates and the performance value of the portfolio.
   */
  void plotPerformanceChart(Map<String, Integer> performanceData);
}
