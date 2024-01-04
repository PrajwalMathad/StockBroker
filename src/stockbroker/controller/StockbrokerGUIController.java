package stockbroker.controller;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;
import stockbroker.model.IFlexStockbroker;
import stockbroker.view.IStockbrokerGUI;

/**
 * Controller implementation of the stockbroker GUI application. The controller handles the flow of
 * operations in the GUI. Supports the GUI changes based on the user interaction with the
 * application.
 */
public class StockbrokerGUIController implements IStockbrokerGUIController {

  private IFlexStockbroker model;
  private IStockbrokerGUI view;

  /**
   * Constructor of the GUI controller.
   *
   * @param model flexible portfolio model object.
   */
  public StockbrokerGUIController(IFlexStockbroker model) {
    this.model = model;
  }

  private boolean validateQuantity(String qty) {
    try {
      if (Integer.parseInt(qty) < 0) {
        throw new Exception();
      }
      return true;
    } catch (Exception e) {
      view.showErrorDialog("Invalid quantity value. Please re enter.");
      return false;
    }
  }

  private boolean validateDate(String date) {
    try {
      DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
      df.setLenient(false);
      df.parse(date);
      return true;
    } catch (ParseException e) {
      view.showErrorDialog("Invalid date value. Please enter in the format yyyy-mm-dd.");
      return false;
    }
  }

  private boolean validateCommissionFee(String commission) {
    try {
      Double.parseDouble(commission);
      if (Double.parseDouble(commission) < 0) {
        throw new NumberFormatException();
      }
      return true;
    } catch (NumberFormatException e) {
      view.showErrorDialog("Invalid commission fee");
      return false;
    }
  }

  private boolean validateAmount(String commission) {
    try {
      Double.parseDouble(commission);
      if (Double.parseDouble(commission) < 0) {
        throw new NumberFormatException();
      }
      return true;
    } catch (NumberFormatException e) {
      view.showErrorDialog("Invalid amount");
      return false;
    }
  }

  private boolean validateSymbol(String symbol) {
    if (symbol.equals("")) {
      view.showErrorDialog("Please enter a stock symbol");
      return false;
    }
    try {
      if (model.validateSymbol(symbol)) {
        return true;
      } else {
        view.showErrorDialog("Invalid stock symbol.");
        return false;
      }
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  public void createPortfolio() {
    String fileName = view.getPortfolioName();
    if (!fileName.equals("")) {
      try {
        model.createPortfolio(fileName);
        view.showMessageDialog("Successfully created portfolio.");
        view.showMainMenu(true);
      } catch (Exception e) {
        view.showErrorDialog(e.getMessage());
      }
    } else {
      view.showErrorDialog("Please enter a name");
    }
  }

  @Override
  public void selectPortfolioHandler(String action) {
    view.selectPortfolio(model.listPortfolio(), action);
  }

  @Override
  public void getStockQuantityMap(String date, String commissionFee, String amount) {
    String fileIndex = String.valueOf(model.getFileIndex(view.getPortfolioName()));
    try {
      if (validateDate(date) && validateCommissionFee(commissionFee) && validateAmount(amount)) {
        model.viewPortfolio(fileIndex, date);
        Map<String, Double> stockMap = model.getStockQtyMap();
        view.weightageSplitHandler(stockMap, false);
      }
    } catch (Exception e) {
      view.showErrorDialog(e.getMessage());
    }
  }

  @Override
  public boolean validateStockSymbol(String symbol) {
    try {
      return model.validateSymbol(symbol);
    } catch (Exception e) {
      return false;
    }
  }

  @Override
  public void stockTransactionHandler() {
    String fileIndex = String.valueOf(model.getFileIndex(view.getPortfolioName()));
    String symbol = view.getStockName();
    String qty = view.getStockQuantity();
    String date = view.getTransactionDate();
    String commission = view.getCommissionFee();
    String transactionType = view.getTransactionType();
    try {
      if (validateSymbol(symbol) && validateQuantity(qty) && validateDate(date)
          && validateCommissionFee(commission)) {
        model.validateSymbol(symbol);
        model.getStockDetailsWithTotalPrice(Integer.parseInt(qty), date,
            Double.parseDouble(commission));
        if (transactionType.equals("sell")) {
          try {
            model.validateSellDetails(fileIndex, Integer.parseInt(qty), date,
                Double.parseDouble(commission));
          } catch (Exception e) {
            view.showErrorDialog(e.getMessage());
            return;
          }
        }
        model.addToPortfolio(fileIndex, transactionType);
        view.showMessageDialog("Successfully added stock to portfolio.");
        view.showMainMenu(true);
      }

    } catch (Exception e) {
      view.showErrorDialog(e.getMessage());
    }
  }

  @Override
  public void commonValueFlowHandler(String type) {
    String date = view.getTransactionDate();
    String fileIndex = String.valueOf(model.getFileIndex(view.getPortfolioName()));
    try {
      if (validateDate(date)) {
        switch (type) {
          case "viewPortfolio":
            model.viewPortfolio(fileIndex, date);
            Map<String, Double> stockMap = model.getStockQtyMap();
            view.showPortfolioComposition(stockMap);
            break;
          case "costBasis":
            Double costBasis = model.getCostBasisOnADate(fileIndex, date);
            view.showMessageDialog("Cost basis of the portfolio: $" + costBasis);
            view.showMainMenu(true);
            break;
          case "portfolioValue":
            Double totalValue = model.getPortfolioValue(fileIndex, date);
            view.showMessageDialog("Value of the portfolio: $" + totalValue);
            view.showMainMenu(true);
            break;
          default:
            break;
        }
      }
    } catch (Exception e) {
      view.showErrorDialog(e.getMessage());
    }
  }

  @Override
  public void loadPortfolioHandler(String filePath) {
    try {
      model.loadPortfolio(filePath);
      view.showMessageDialog("Portfolio was successfully loaded");
    } catch (IllegalArgumentException e) {
      view.showErrorDialog("Error loading file");
    } catch (IOException e) {
      view.showErrorDialog("Error loading file");
    }
  }

  @Override
  public void investFixedAmountInPortfolio(Map<String, Double> stockWeights, String amount,
      String commissionFee, String date) {
    String fileIndex = String.valueOf(model.getFileIndex(view.getPortfolioName()));
    try {
      if (validateDate(date) && validateCommissionFee(commissionFee) && validateAmount(amount)) {
        model.investFixedAmountInPortfolio(fileIndex, stockWeights, Double.parseDouble(amount),
            Double.parseDouble(commissionFee), date);
        view.showMessageDialog("Successfully invested.");
        view.showMainMenu(true);
      }
    } catch (Exception e) {
      view.showErrorDialog(e.getMessage());
    }
  }

  @Override
  public void createDollarCostStrategyPF(String name, LinkedHashMap<String, Double> stockWeights,
      String amount, String commissionFee, String startDate, String endDate, String frequency) {
    try {
      if (validateDate(startDate) && (endDate.equals("") || validateDate(endDate))
          && validateCommissionFee(commissionFee) && validateAmount(amount)) {
        if (endDate.equals("")) {
          endDate = null;
        }
        model.createDollarCostStrategyPF(name, stockWeights, Double.parseDouble(amount),
            Double.parseDouble(commissionFee), startDate, endDate, Integer.parseInt(frequency));
        view.showMessageDialog("Successfully created.");
        view.showMainMenu(true);
      }
    } catch (Exception e) {
      view.showErrorDialog(e.getMessage());
    }
  }

  @Override
  public void getPerformanceData(String startDate, String endDate) {
    String fileIndex = String.valueOf(model.getFileIndex(view.getPortfolioName()));
    if (validateDate(startDate) && validateDate(endDate)) {
      try {
        Map<String, Integer> performanceData = model.getPortfolioPerformance(fileIndex, startDate,
            endDate);
        performanceData.remove("scale");
        view.plotPerformanceChart(performanceData);
      } catch (Exception e) {
        view.showErrorDialog(e.getMessage());
      }
    }

  }

  @Override
  public void goController(IStockbrokerGUI view) throws IOException {
    this.view = view;
    //provide view with all the callbacks
    view.listenerStore(this);
  }
}
