package stockbroker.controller;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;
import stockbroker.model.FlexStockbroker;
import stockbroker.model.StockBroker;
import stockbroker.view.IStockbrokerView;
import stockbroker.model.IStockbroker;

/**
 * Controller implementation of the stockbroker application. The controller creates the type of
 * model object based on the user selection. It has private helper functions to control the flow of
 * operations of portfolios depending on its type.
 */
public class StockbrokerController implements IStockbrokerController {

  private Scanner in;
  private IStockbrokerView view;
  private IStockbroker model;
  private boolean quitApp;

  /**
   * Constructor for the controller.
   *
   * @param model model object.
   * @param in    InputStream object to read inputs.
   * @param view  view object.
   */
  public StockbrokerController(IStockbroker model, InputStream in, IStockbrokerView view) {
    this.model = model;
    this.view = view;
    this.in = new Scanner(in);
    this.quitApp = false;
  }

  /**
   * Method to get the portfolio value on a certain date. Works for both flexible and inflexible
   * portfolios.
   *
   * @param pf    portfolio file index.
   * @param model object.
   */
  private void getPortfolioValueControl(String pf, IStockbroker model) {
    if (Integer.parseInt(pf) > model.listPortfolio().toArray().length) {
      view.showInvalidOptionError();
    } else {
      String date = dateInputHandler();
      try {
        double totalValue = model.getPortfolioValue(pf, date);
        view.showTotalPortfolioValue();
        view.showMessage("$" + totalValue + "\n");
      } catch (IOException e) {
        view.showMessage(e.getMessage());
      } catch (IllegalArgumentException e) {
        view.showMessage(e.getMessage());
      }
    }
  }

  /**
   * Method to view the portfolios. Check for type of model object to call the appropriate method
   * from model implementation.
   *
   * @param pf portfolio file index.
   */
  private void viewPortfolioControl(String pf) {
    if (Integer.parseInt(pf) > model.listPortfolio().toArray().length) {
      view.showInvalidOptionError();
    } else {
      try {
        if (model.getClass().getSimpleName().equals("FlexStockbroker")) {
          String date = dateInputHandler();
          FlexStockbroker model = (FlexStockbroker) this.model;
          view.showMessage(model.viewPortfolio(pf, date));
        } else {
          view.showMessage(model.viewPortfolio(pf));
        }
      } catch (IOException e) {
        view.showMessage(e.getMessage());
      } catch (IllegalArgumentException e) {
        view.showMessage(e.getMessage());
      }
    }
  }

  /**
   * Method to create the appropriate file for the portfolio.
   *
   * @param model object.
   * @return the filename.
   */
  private String createPortfolioFileControl(IStockbroker model) {
    boolean runNameLoop = false;
    String fileName = "";
    while (!runNameLoop) {
      view.showEnterPortfolioMessage();
      fileName = in.nextLine();
      if (!fileName.isEmpty()) {
        try {
          model.createPortfolio(fileName);
          runNameLoop = true;
        } catch (Exception e) {
          view.showMessage(e.getMessage());
        }
      } else {
        view.showNoNameMessage();
      }
    }
    return fileName;
  }

  /**
   * Method to handle date input and validation.
   *
   * @return valid date.
   */

  private String dateInputHandler() {
    boolean validDate = false;
    String date = "";
    while (!validDate) {
      view.showEnterDateForPortfolioMessage();
      try {
        date = in.nextLine();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        df.setLenient(false);
        df.parse(date);
        validDate = true;
      } catch (ParseException e) {
        view.invalidDate();
      }
    }
    return date;
  }

  /**
   * Method to handle quantity input and validation.
   *
   * @param transactionType transaction type buy/sell.
   * @return quantity.
   */
  private String quantityInputHandler(String transactionType) {
    boolean validQuantity = false;
    String qty = "";
    while (!validQuantity) {
      view.showEnterQuantityMessage(transactionType);
      try {
        qty = in.nextLine();
        if (Integer.parseInt(qty) < 0) {
          throw new Exception();
        }
        validQuantity = true;
      } catch (Exception e) {
        view.invalidQuantity();
      }
    }
    return qty;
  }

  /**
   * Method to handle commission input and validation.
   *
   * @return valid commission value.
   */
  private double commissionInputHandler() {
    boolean validCommission = false;
    double commission = 0.0;
    while (!validCommission) {
      view.showEnterCommissionMessage();
      try {
        commission = in.nextDouble();
        if (commission < 0) {
          throw new Exception();
        }
        validCommission = true;
      } catch (Exception e) {
        view.invalidQuantity();
      }
    }
    return commission;
  }

  /**
   * Method to update the portfolio. For inflexible portfolio the transaction type will always be
   * 'buy'. For Flexible portfolio, all the buy/sell transactions will be updated in the portfolio
   * file.
   *
   * @param fileIndex       file index of the portfolio.
   * @param transactionType buy/sell.
   * @return true if transaction completes.
   * @throws IOException for file handling errors.
   */
  private boolean stockTransactionHandler(String fileIndex, String transactionType)
      throws IOException {
    view.showEnterStockNameMessage(transactionType);
    String stock = in.nextLine();
    if (model.validateSymbol(stock)) {
      String qty = quantityInputHandler(transactionType);
      double commission = 0.0;
      try {
        if (model.getClass().getSimpleName().equals("FlexStockbroker")) {
          String date = dateInputHandler();
          commission = commissionInputHandler();
          in.nextLine();
          FlexStockbroker model = (FlexStockbroker) this.model;
          view.showMessage(
              model.getStockDetailsWithTotalPrice(Integer.parseInt(qty), date, commission));
          if (transactionType.equals("sell")) {
            try {
              model.validateSellDetails(fileIndex, Integer.parseInt(qty), date, commission);
            } catch (IllegalArgumentException e) {
              view.showMessage(e.getMessage());
              return true;
            } catch (IOException e) {
              view.showMessage(e.getMessage());
              return true;
            }
          }
        } else {
          view.showMessage(model.getStockDetailsWithTotalPrice(Integer.parseInt(qty)));
        }
      } catch (IllegalArgumentException e) {
        // handle invalid buy dates.
        view.showMessage(e.getMessage());
      }
      view.showBuySellConfirmMessage(transactionType);
      String confirm = in.nextLine();
      if (confirm.equals("yes")) {
        try {
          if (model.getClass().getSimpleName().equals("FlexStockbroker")) {
            FlexStockbroker model = (FlexStockbroker) this.model;
            model.addToPortfolio(fileIndex, transactionType);
          } else {
            model.addToPortfolio();
          }
          view.showAddedStockMessage();
          return true;
        } catch (IOException e) {
          view.showMessage(e.getMessage());
          return true;
        }
      } else {
        return true;
      }
    } else {
      view.showStockNameError();
      return true;
    }
  }

  /**
   * Method to handle adding stocks to portfolio after it is created.
   *
   * @param fileName index of the portfolio file.
   * @param model    object.
   * @throws IOException for file handling errors.
   */
  private void createPortfolioController(String fileName, IStockbroker model) throws IOException {
    boolean runMainLoop = false;
    while (!runMainLoop) {
      view.showSecondMenu();
      String option2 = in.nextLine();
      switch (option2) {
        case "1":
          if (model.getClass().getSimpleName().equals("FlexStockbroker")) {
            int fileIndex = model.getFileIndex(fileName);
            if (stockTransactionHandler(String.valueOf(fileIndex), "buy")) {
              break;
            }
          } else {
            if (stockTransactionHandler("0", "buy")) {
              break;
            }
          }
          break;
        case "2":
          int fileIndex = model.getFileIndex(fileName);
          viewPortfolioControl(String.valueOf(fileIndex));
          break;
        case "3":
          runMainLoop = true;
          break;
        default:
          break;
      }
    }
  }

  /**
   * Method to handle load portfolio.
   *
   * @param model object.
   */
  private void loadPortfolioController(IStockbroker model) {
    view.showEnterFilePathMessageForLoad();
    String filePath = in.nextLine();
    try {
      model.loadPortfolio(filePath);
      view.showSuccessfulLoadMessage();
    } catch (IllegalArgumentException e) {
      view.showMessage(e.getMessage());
    } catch (IOException e) {
      view.showMessage(e.getMessage());
    }
  }

  private String selectPortfolio() {
    view.selectPortfolioMessage(model.listPortfolio());
    String pf = in.nextLine();
    return pf;
  }

  /**
   * Method to handle cost basis calculation of the portfolio.
   *
   * @param pf file index of the portfolio.
   */
  private void costBasisController(String pf) {
    if (Integer.parseInt(pf) > model.listPortfolio().toArray().length) {
      view.showInvalidOptionError();
    } else {
      try {
        String date = dateInputHandler();
        FlexStockbroker model = (FlexStockbroker) this.model;
        view.showCostBasis(model.getCostBasisOnADate(pf, date));
      } catch (IOException e) {
        view.showMessage(e.getMessage());
      } catch (IllegalArgumentException e) {
        view.showMessage(e.getMessage());
      }
    }
  }

  /**
   * Method to handle the visualization of performance of the portfolio.
   *
   * @param pf file index of the portfolio.
   */
  private void portfolioPerformanceController(String pf) {
    if (Integer.parseInt(pf) > model.listPortfolio().toArray().length) {
      view.showInvalidOptionError();
    } else {
      try {
        view.showStartDateMessage();
        String startDate = dateInputHandler();
        view.showEndDateMessage();
        String endDate = dateInputHandler();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        df.setLenient(false);
        Date d1 = df.parse(startDate);
        Date d2 = df.parse(endDate);
        int diffInDays = (int) ((d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24));
        if (diffInDays < 5) {
          view.showTimRangeError();
          return;
        }
        String portfolioName = model.listPortfolio().get(Integer.parseInt(pf) - 1);
        FlexStockbroker model = (FlexStockbroker) this.model;
        view.showPerformanceChart(model.getPortfolioPerformance(pf, startDate, endDate),
            portfolioName, startDate, endDate);
      } catch (Exception e) {
        view.showMessage(e.getMessage());
        return;
      }
    }
  }

  /**
   * Method to handle flow of inflexible portfolio operations.
   *
   * @throws IOException for file handling errors.
   */
  private void inflexiblePortfolioControl() throws IOException {
    this.model = new StockBroker();
    while (!quitApp) {
      view.showMainMenu("Inflexible");
      String option = in.nextLine();
      String fileName = "";
      switch (option) {
        case "1":
          fileName = createPortfolioFileControl(model);
          createPortfolioController(fileName, model);
          break;

        case "2":
          String pf = selectPortfolio();
          if (pf.equals("Q")) {
            break;
          }
          viewPortfolioControl(pf);
          break;

        case "3":
          view.selectPortfolioMessage(model.listPortfolio());
          pf = in.nextLine();
          if (pf.equals("Q")) {
            break;
          }
          getPortfolioValueControl(pf, model);
          break;

        case "4":
          loadPortfolioController(model);
          break;
        case "5":
          view.showExitMessage();
          this.quitApp = true;
          break;
        default:
          view.showInvalidOptionError();
      }
    }
  }

  /**
   * Method to handle flow of flexible portfolio operations.
   *
   * @throws IOException for file handling errors.
   */
  private void flexiblePortfolioControl() throws IOException {
    this.model = new FlexStockbroker();
    while (!quitApp) {
      view.showMainMenu("Flexible");
      String option = in.nextLine();
      String fileName = "";
      switch (option) {
        case "1":
          fileName = createPortfolioFileControl(model);
          createPortfolioController(fileName, model);
          break;

        case "2":
          view.selectPortfolioMessage(model.listPortfolio());
          String pf = in.nextLine();
          if (pf.equals("Q")) {
            break;
          }
          viewPortfolioControl(pf);
          break;

        case "3":
          view.selectPortfolioMessage(model.listPortfolio());
          pf = in.nextLine();
          if (pf.equals("Q")) {
            break;
          }
          getPortfolioValueControl(pf, model);
          break;

        case "4":
          loadPortfolioController(model);
          break;
        case "5":
          //buy stocks
          view.selectPortfolioMessage(model.listPortfolio());
          pf = in.nextLine();
          if (pf.equals("Q")) {
            break;
          }
          stockTransactionHandler(pf, "buy");
          break;
        case "6":
          //sell stocks
          view.selectPortfolioMessage(model.listPortfolio());
          pf = in.nextLine();
          if (pf.equals("Q")) {
            break;
          }
          stockTransactionHandler(pf, "sell");
          break;
        case "7":
          //cost basis
          view.selectPortfolioMessage(model.listPortfolio());
          pf = in.nextLine();
          if (pf.equals("Q")) {
            break;
          }
          costBasisController(pf);
          break;
        case "8":
          //performance
          view.selectPortfolioMessage(model.listPortfolio());
          pf = in.nextLine();
          if (pf.equals("Q")) {
            break;
          }
          portfolioPerformanceController(pf);
          break;
        case "9":
          view.showExitMessage();
          quitApp = true;
          break;
        default:
          view.showInvalidOptionError();
      }
    }
  }

  @Override
  public void goController() throws IOException {

    //model.fetchStockListing();
    view.showAppIntro();
    view.showSelectPortfolioType();
    String pfType = in.nextLine();
    if (pfType.equals("1")) {
      inflexiblePortfolioControl();
    } else {
      flexiblePortfolioControl();
    }
  }
}
