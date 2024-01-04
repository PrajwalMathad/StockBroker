package stockbroker.view;

import java.util.List;
import java.util.Map;

/**
 * The interface for view of stockbroker application. Provides all the methods to display messages
 * in the application.
 */
public interface IStockbrokerView {

  /**
   * Displays message as is.
   *
   * @param generic message to be displayed.
   */
  void showMessage(String generic);

  /**
   * Shows initial application header and message.
   */
  void showAppIntro();

  /**
   * Shows invalid option selected message.
   */
  void showInvalidOptionError();

  /**
   * Shows application main menu.
   */
  void showMainMenu(String type);

  /**
   * Shows salutation and exit message.
   */
  void showExitMessage();

  /**
   * Shows message to enter the portfolio name.
   */
  void showEnterPortfolioMessage();

  void invalidQuantity();

  /**
   * Shows message for invalid name.
   */
  void showNoNameMessage();

  /**
   * Shows menu for create portfolio options.
   */
  void showSecondMenu();

  /**
   * Shows message to enter stock symbol.
   */
  void showEnterStockNameMessage(String transactionType);

  /**
   * Shows message for invalid symbol.
   */
  void showStockNameError();

  /**
   * Shows message to enter quantity of stocks to add.
   */
  void showEnterQuantityMessage(String transactionType);

  /**
   * Shows message to confirm adding stock to portfolio.
   */
  void showBuySellConfirmMessage(String transactionType);

  /**
   * Shows success message after stocks are added.
   */
  void showAddedStockMessage();

  /**
   * Shows list of portfolios and asks to select a portfolio from the list.
   *
   * @param portfolios list of available portfolios.
   */
  void selectPortfolioMessage(List<String> portfolios);

  /**
   * Shows message to enter date.
   */
  void showEnterDateForPortfolioMessage();

  /**
   * Shows message to enter file path to load the portfolio.
   */
  void showEnterFilePathMessageForLoad();

  /**
   * Shows total portfolio value.
   */
  void showTotalPortfolioValue();

  /**
   * Shows message after successful load of portfolio.
   */
  void showSuccessfulLoadMessage();

  /**
   * Shows message to select type of portfolio.
   */
  void showSelectPortfolioType();

  /**
   * Shows message to enter the commission fee.
   */
  void showEnterCommissionMessage();

  /**
   * Shows message for invalid date.
   */
  void invalidDate();

  /**
   * Show cost basis for a flexible portfolio.
   *
   * @param cost cost basis result.
   */
  void showCostBasis(double cost);

  /**
   * Shows enter start date message.
   */
  void showStartDateMessage();

  /**
   * Shows enter end date message.
   */
  void showEndDateMessage();

  /**
   * Plots the performance chart based on the results.
   *
   * @param result        set of the bar chart.
   * @param portfolioName name of portfolio.
   * @param startDate     Start date of performance data.
   * @param endDate       End date of performance data.
   */
  void showPerformanceChart(Map<String, Integer> result, String portfolioName, String startDate,
      String endDate);

  /**
   * Shows error message if time range is invalid.
   */
  void showTimRangeError();
}
