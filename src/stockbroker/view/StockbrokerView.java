package stockbroker.view;

import java.io.PrintStream;
import java.util.List;
import java.util.Map;

/**
 * View implementation for the stockbroker application. It contains methods which displays contents/
 * messages or errors to the user. View has method used to plot the performance graph of the
 * portfolio.
 */
public class StockbrokerView implements IStockbrokerView {

  private PrintStream out;

  /**
   * Constructor for the view.
   *
   * @param out PrintStream object to print the output to the screen.
   */
  public StockbrokerView(PrintStream out) {
    this.out = out;
  }


  @Override
  public void showMessage(String generic) {
    out.println(generic);
  }

  @Override
  public void showAppIntro() {
    out.println("\n\n---------------------------------------------");
    out.println("---------------- Investify ------------------");
    out.println("---------------------------------------------\n");
    out.println("Welcome : Choose from the following options:");

  }

  @Override
  public void showInvalidOptionError() {
    out.println("You have entered an invalid option! Please enter correct option from the list");
  }


  @Override
  public void showMainMenu(String type) {
    out.println("--------------------------------------------");
    out.println("Main Menu: ");
    out.println("--------------------------------------------");
    out.println("1: Create portfolio");
    out.println("2: View portfolio");
    out.println("3: Get total value of portfolio on a date");
    out.println("4: Load portfolio");
    if (type.equals("Flexible")) {
      out.println("5: Buy stocks");
      out.println("6: Sell stocks");
      out.println("7: Get cost basis for a portfolio");
      out.println("8: Get portfolio performance");
      out.println("9: Exit application");
    } else {
      out.println("5: Exit application");
    }
    out.println("--------------------------------------------");
    out.print("Enter your choice: ");
  }

  @Override
  public void showSecondMenu() {
    out.println("1: Add stocks to current portfolio");
    out.println("2: View current portfolio");
    out.println("3: Back to main menu");
    out.print("Enter your choice: ");
  }

  @Override
  public void showEnterPortfolioMessage() {
    out.println("Enter portfolio name: ");
  }

  @Override
  public void showNoNameMessage() {
    out.println("Please enter a valid name.");
  }

  @Override
  public void showEnterStockNameMessage(String transactionType) {
    out.println("Enter the symbol of the stock to " + transactionType + " : ");
  }

  @Override
  public void invalidQuantity() {
    out.println("Invalid quantity! Enter a non negative whole number");
  }

  @Override
  public void showStockNameError() {
    out.println("Invalid symbol entered! ");
  }

  @Override
  public void showEnterQuantityMessage(String transactionType) {
    out.println("\nEnter the quantity of stocks to " + transactionType);
  }

  @Override
  public void showBuySellConfirmMessage(String transactionType) {
    out.println("\nConfirm " + transactionType + " stock(s). Enter yes/no");
  }

  @Override
  public void showAddedStockMessage() {
    out.println("Successfully updated the portfolio!\n");
  }

  @Override
  public void selectPortfolioMessage(List<String> portfolios) {
    out.println("Select the option from the below portfolio list to get the details:");
    int i = 1;
    for (String portfolio : portfolios) {
      String[] split = portfolio.split("\\.");
      out.println(i + ": " + split[0]);
      i += 1;
    }
    out.println("Q: Back to main menu");
  }

  @Override
  public void showEnterDateForPortfolioMessage() {
    out.println("Enter the date in format yyyy-mm-dd.");
  }

  @Override
  public void showEnterFilePathMessageForLoad() {
    out.println("Enter the file path to load portfolio file.\nOnly CSV file format is supported.");
  }

  @Override
  public void showSuccessfulLoadMessage() {
    out.println("Portfolio is successfully loaded.");
  }

  @Override
  public void showSelectPortfolioType() {
    out.println("Select the portfolio type.");
    out.println("1: Inflexible portfolio");
    out.println("2: Flexible portfolio");
  }

  @Override
  public void showExitMessage() {
    out.println("Thank you for stopping by!");
  }

  @Override
  public void showTotalPortfolioValue() {
    out.print("Total value of the portfolio: ");
  }

  @Override
  public void showEnterCommissionMessage() {
    out.println("Enter the commission fee:");
  }

  @Override
  public void invalidDate() {
    out.println("Invalid date.");
  }

  @Override
  public void showCostBasis(double cost) {
    out.println("Cost basis of the portfolio: $" + cost);
  }

  @Override
  public void showStartDateMessage() {
    out.print("Enter start date.");
  }

  @Override
  public void showEndDateMessage() {
    out.print("Enter end date.");
  }

  // to create each bar to display.
  private String eachBar(Integer number) {
    StringBuilder bar = new StringBuilder();
    for (int i = 0; i < number; i++) {
      bar.append("*");
    }
    return bar.toString();
  }

  @Override
  public void showPerformanceChart(Map<String, Integer> result, String portfolioName,
      String startDate, String endDate) {
    String[] split = portfolioName.split("\\.");
    out.println("Performance of portfolio " + split[0] + " from " + startDate + " to " + endDate);
    for (Map.Entry<String, Integer> entry : result.entrySet()) {
      if (!entry.getKey().equals("scale")) {
        out.println(entry.getKey() + ": " + eachBar(entry.getValue()));
      } else {
        out.println("\nScale: * = $" + entry.getValue());
      }
    }
  }

  @Override
  public void showTimRangeError() {
    out.println("Time range too short. Provide at least a difference of 5 days.");
  }
}
