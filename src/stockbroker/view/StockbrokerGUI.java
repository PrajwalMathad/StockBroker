package stockbroker.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import stockbroker.controller.IStockbrokerGUIController;

/**
 * View implementation for the stockbroker GUI application. It contains methods which displays UI
 * components to the user to interact with. View has method used to plot the performance graph of
 * the portfolio.
 */
public class StockbrokerGUI extends JFrame implements IStockbrokerGUI {

  private JLabel investAmountLabel;
  private JLabel dateLabel;
  private JLabel commissionLabel;
  private JPanel containerPanel;
  private JPanel mainMenuPanel;
  private JPanel createPortfolioPanel;
  private JPanel transactionPanel;
  private JPanel viewPanel;
  private JPanel commonValuePanel;
  private JPanel listContainer;

  private JPanel investmentPanel;
  private JButton mainMenuButton = new JButton("Main Menu");
  private JButton createPortfolioButton;
  private JButton viewPortfolioButton;
  private JButton transactionButton;
  private JButton calculateCostBasisButton;
  private JButton calculatePortfolioValueButton;
  private JButton loadPortfolioButton;
  private JButton investInPortfolioButton;
  private JButton dollarCostAvgButton;
  private JButton performanceChartButton;
  private JButton validateNameButton = new JButton("Create");
  private JButton addStockToFile = new JButton("Add stock");
  private JButton getCostBasisButton = new JButton("Get cost basis");
  private JButton getPortfolioValueButton = new JButton("Get value");
  private JButton getPortfolioCompositionButton = new JButton("Get composition");
  private JButton addSharesButton = new JButton("Specify split");
  private JButton addStocksToDCAButton = new JButton("Add stocks");

  private JButton finalInvestButton = new JButton("Invest");
  private JButton addMoreStock = new JButton("Add new stock");
  private JButton plotChartButton = new JButton("Plot");
  JButton load = new JButton("Load");
  JButton cancel = new JButton("Cancel");
  private JTextField portFolioNameInput;
  private JTextField stockInput;
  private JTextField stockQuantity;
  private JTextField stockDate;
  private JTextField investEndDate;
  private JTextField commissionFee;
  private JTextField investAmount;
  private JTextField investFrequency;
  private JRadioButton transactionTypeSell;
  private JList portfolioList;
  private JFileChooser chooser;
  private Map<JLabel, JTextField> stockGroup;
  JLabel message3 = new JLabel("Click on 'Add new stock' button to add stocks");
  JLabel message4 = new JLabel("");
  private boolean isDCA = true;

  @Override
  public String getPortfolioName() {
    return portFolioNameInput.getText();
  }

  @Override
  public String getStockName() {
    return stockInput.getText();
  }

  @Override
  public String getStockQuantity() {
    return stockQuantity.getText();
  }

  @Override
  public String getTransactionDate() {
    return stockDate.getText();
  }

  @Override
  public String getCommissionFee() {
    return commissionFee.getText();
  }

  @Override
  public String getTransactionType() {
    if (transactionTypeSell.isSelected()) {
      return "sell";
    }
    return "buy";
  }

  @Override
  public void showMainMenu(Boolean show) {
    if (show) {
      if (containerPanel.isAncestorOf(createPortfolioPanel)) {
        this.containerPanel.remove(createPortfolioPanel);
      }
      if (containerPanel.isAncestorOf(transactionPanel)) {
        this.containerPanel.remove(transactionPanel);
      }
      if (containerPanel.isAncestorOf(commonValuePanel)) {
        this.containerPanel.remove(commonValuePanel);
      }
      if (containerPanel.isAncestorOf(viewPanel)) {
        this.containerPanel.remove(viewPanel);
      }
      if (containerPanel.isAncestorOf(listContainer)) {
        this.containerPanel.remove(listContainer);
      }
      if (containerPanel.isAncestorOf(investmentPanel)) {
        this.containerPanel.remove(investmentPanel);
      }
      this.revalidate();
      this.repaint();
      this.mainMenuPanel.setVisible(true);
    }
  }


  private void mainMenu() {
    mainMenuPanel = new JPanel();
    mainMenuPanel.setLayout(new GridLayout(0, 1, 20, 25));
    JLabel display = new JLabel("Main Menu");
    mainMenuPanel.add(display);

    createPortfolioButton = new JButton("Create portfolio");
    createPortfolioButton.setPreferredSize(new Dimension(150, 30));
    createPortfolioButton.setActionCommand("Create portfolio");
    viewPortfolioButton = new JButton("View portfolio");
    viewPortfolioButton.setActionCommand("View portfolio");
    transactionButton = new JButton("Buy/Sell stocks");
    transactionButton.setActionCommand("transaction");
    calculateCostBasisButton = new JButton("Calculate cost basis");
    calculateCostBasisButton.setActionCommand("cost basis");
    calculatePortfolioValueButton = new JButton("Calculate Portfolio value");
    calculatePortfolioValueButton.setActionCommand("portfolio value");
    loadPortfolioButton = new JButton("Load portfolio");
    investInPortfolioButton = new JButton("Invest in portfolio");
    dollarCostAvgButton = new JButton("Dollar cost Average");
    performanceChartButton = new JButton("Plot performance chart");
    mainMenuPanel.add(createPortfolioButton);
    mainMenuPanel.add(viewPortfolioButton);
    mainMenuPanel.add(transactionButton);
    mainMenuPanel.add(calculateCostBasisButton);
    mainMenuPanel.add(calculatePortfolioValueButton);
    mainMenuPanel.add(loadPortfolioButton);
    mainMenuPanel.add(investInPortfolioButton);
    mainMenuPanel.add(dollarCostAvgButton);
    mainMenuPanel.add(performanceChartButton);
    containerPanel.add(mainMenuPanel);
    this.revalidate();
    this.repaint();

  }

  private JTable portfolioTable(Map<String, Double> stockMap) {
    String[][] tableData = new String[stockMap.size()][2];
    int i = 0;
    for (Map.Entry<String, Double> entry : stockMap.entrySet()) {
      tableData[i][0] = entry.getKey();
      tableData[i][1] = String.valueOf(entry.getValue());
      i++;
    }
    String[] columnNames = {"Stock", "Quantity"};
    return new JTable(tableData, columnNames);
  }

  @Override
  public void showPortfolioComposition(Map<String, Double> stockMap) {
    refreshGUI(commonValuePanel);
    viewPanel = new JPanel();
    viewPanel.setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    JLabel listInfo = new JLabel("Composition of Portfolio");
    JTable compositionTable = portfolioTable(stockMap);
    JScrollPane tableContainer = new JScrollPane(compositionTable);
    tableContainer.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    tableContainer.setSize(new Dimension(80, 150));
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.gridwidth = 3;
    gbc.insets = new Insets(10, 0, 20, 0);
    viewPanel.add(listInfo, gbc);
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.ipadx = 30;
    gbc.gridwidth = 2;
    viewPanel.add(tableContainer, gbc);
    gbc.gridx = 1;
    gbc.gridy = 2;
    gbc.ipadx = 0;
    gbc.insets = new Insets(20, 0, 0, 0);
    gbc.gridwidth = 1;
    viewPanel.add(mainMenuButton, gbc);
    containerPanel.add(viewPanel);
    this.revalidate();
    this.repaint();
  }


  @Override
  public void selectPortfolio(List<String> portfolios, String action) {
    this.mainMenuPanel.setVisible(false);
    listContainer = new JPanel();
    listContainer.setLayout(new GridBagLayout());
    GridBagConstraints gbc = new GridBagConstraints();
    JLabel portfolioListLabel = new JLabel("Select the portfolio from the below list:");
    DefaultListModel<String> list = new DefaultListModel<>();
    for (String portfolio : portfolios) {
      String[] split = portfolio.split("\\.");
      list.addElement(split[0]);
    }
    portfolioList = new JList<>(list);
    JScrollPane listPanel = new JScrollPane(portfolioList);
    listPanel.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.gridx = 1;
    gbc.gridy = 0;
    gbc.gridwidth = 3;
    gbc.insets = new Insets(10, 0, 20, 0);
    listContainer.add(portfolioListLabel, gbc);
    gbc.gridx = 1;
    gbc.gridy = 1;
    gbc.ipadx = 150;
    gbc.fill = GridBagConstraints.VERTICAL;
    listContainer.add(listPanel, gbc);
    gbc.fill = GridBagConstraints.NONE;
    gbc.gridx = 1;
    gbc.gridy = 2;
    gbc.insets = new Insets(20, 0, 0, 0);
    gbc.weighty = 0.8;
    gbc.gridwidth = 1;
    listContainer.add(mainMenuButton, gbc);
    containerPanel.add(listContainer);
    this.revalidate();
    this.repaint();
    portFolioNameInput = new JTextField(10);
    portfolioList.addListSelectionListener(evt -> {
      if (!evt.getValueIsAdjusting()) {
        portFolioNameInput.setText(portfolioList.getSelectedValue().toString());
        handleActions(action);
      }
    });
  }

  /**
   * After the selection of portfolio, execute the flow based on the user action.
   *
   * @param action to decide the operation flow.
   */
  private void handleActions(String action) {
    switch (action) {
      case "viewHandler":
        commonValueFlow("viewPortfolio");
        break;
      case "transactionHandler":
        transactionFlow();
        break;
      case "costBasisHandler":
        commonValueFlow("costBasis");
        break;
      case "portfolioValueHandler":
        commonValueFlow("portfolioValue");
        break;
      case "investInPortfolioHandler":
        investInPortfolioFlow();
        break;
      case "performanceChartHandler":
        plotPerformanceChartFlow();
        break;
      default:
        break;
    }
  }

  private void createPortfolioFlow() {
    this.mainMenuPanel.setVisible(false);
    createPortfolioPanel = new JPanel();
    createPortfolioPanel.setLayout(new GridLayout(0, 2, 20, 25));
    JLabel enterName = new JLabel("Enter portfolio name");
    createPortfolioPanel.add(enterName);
    portFolioNameInput = new JTextField(10);
    createPortfolioPanel.add(portFolioNameInput);
    createPortfolioPanel.add(validateNameButton);
    createPortfolioPanel.add(mainMenuButton);
    containerPanel.add(createPortfolioPanel);
    this.revalidate();
    this.repaint();
  }

  private void stockDetailsInput() {
    JLabel transactionTypeLabel = new JLabel("Select the transaction type");
    JRadioButton transactionTypeBuy = new JRadioButton("Buy", true);
    transactionTypeSell = new JRadioButton("Sell");
    ButtonGroup bg = new ButtonGroup();
    bg.add(transactionTypeBuy);
    bg.add(transactionTypeSell);
    transactionPanel.add(transactionTypeBuy);
    transactionPanel.add(transactionTypeSell);
    JLabel stockLabel = new JLabel("Enter Stock name");
    stockInput = new JTextField(10);
    transactionPanel.add(stockLabel);
    transactionPanel.add(stockInput);
    JLabel quantityLabel = new JLabel("Enter Stock quantity");
    stockQuantity = new JTextField(5);
    transactionPanel.add(quantityLabel);
    transactionPanel.add(stockQuantity);
    dateLabel = new JLabel("Enter date of transaction (yyyy-mm-dd)");
    stockDate = new JTextField(10);
    transactionPanel.add(dateLabel);
    transactionPanel.add(stockDate);
    commissionLabel = new JLabel("Enter commission fee: $");
    commissionFee = new JTextField(5);
    transactionPanel.add(commissionLabel);
    transactionPanel.add(commissionFee);
    transactionPanel.add(addStockToFile);
    transactionPanel.add(mainMenuButton);
  }

  private void transactionFlow() {
    refreshGUI(listContainer);
    transactionPanel = new JPanel();
    transactionPanel.setLayout(new GridLayout(0, 2, 20, 20));
    stockDetailsInput();
    transactionPanel.add(mainMenuButton);
    containerPanel.add(transactionPanel);
    this.revalidate();
    this.repaint();
  }

  private void dollarCostAvgFlow() {
    this.mainMenuPanel.setVisible(false);
    this.revalidate();
    this.repaint();
    investmentPanel = new JPanel();
    investmentPanel.setLayout(new GridLayout(0, 2, 20, 20));
    JLabel enterName = new JLabel("Enter portfolio name");
    portFolioNameInput = new JTextField(10);
    investmentPanel.add(enterName);
    investmentPanel.add(portFolioNameInput);
    investAmountLabel = new JLabel("Enter amount to invest: $");
    investAmount = new JTextField(5);
    investmentPanel.add(investAmountLabel);
    investmentPanel.add(investAmount);
    dateLabel = new JLabel("Enter Start date of investment (yyyy-mm-dd)");
    stockDate = new JTextField(10);
    investmentPanel.add(dateLabel);
    investmentPanel.add(stockDate);
    JLabel endDateLabel = new JLabel("Enter end date of investment (yyyy-mm-dd)");
    investEndDate = new JTextField(10);
    investmentPanel.add(endDateLabel);
    investmentPanel.add(investEndDate);
    JLabel frequencyLabel = new JLabel("Enter frequency of investment in no of days");
    investFrequency = new JTextField(5);
    investmentPanel.add(frequencyLabel);
    investmentPanel.add(investFrequency);
    commissionLabel = new JLabel("Enter commission fee: $");
    commissionFee = new JTextField(5);
    investmentPanel.add(commissionLabel);
    investmentPanel.add(commissionFee);
    investmentPanel.add(addStocksToDCAButton);
    investmentPanel.add(mainMenuButton);
    containerPanel.add(investmentPanel);
    this.revalidate();
    this.repaint();
  }

  private void investInPortfolioFlow() {
    refreshGUI(listContainer);
    investmentPanel = new JPanel();
    investmentPanel.setLayout(new GridLayout(0, 2, 20, 20));
    investAmountLabel = new JLabel("Enter amount to invest: $");
    investAmount = new JTextField(5);
    investmentPanel.add(investAmountLabel);
    investmentPanel.add(investAmount);
    dateLabel = new JLabel("Enter date of investment (yyyy-mm-dd)");
    stockDate = new JTextField(10);
    investmentPanel.add(dateLabel);
    investmentPanel.add(stockDate);
    commissionLabel = new JLabel("Enter commission fee: $");
    commissionFee = new JTextField(5);
    investmentPanel.add(commissionLabel);
    investmentPanel.add(commissionFee);
    investmentPanel.add(addSharesButton);
    containerPanel.add(investmentPanel);
    this.revalidate();
    this.repaint();
  }

  @Override
  public void weightageSplitHandler(Map<String, Double> stockMap, boolean dca) {
    this.investmentPanel.removeAll();
    this.revalidate();
    this.repaint();
    isDCA = dca;
    stockGroup = new HashMap<JLabel, JTextField>();
    JLabel message1 = new JLabel("Specify weightage for each stock");
    JLabel message2 = new JLabel("");
    investmentPanel.add(message1);
    investmentPanel.add(message2);
    if (stockMap.size() == 0) {
      investmentPanel.add(message3);
      investmentPanel.add(message4);
    }
    for (Map.Entry<String, Double> entry : stockMap.entrySet()) {
      JLabel stockName = new JLabel(entry.getKey());
      JTextField weight = new JTextField();
      stockGroup.put(stockName, weight);
    }
    for (Map.Entry<JLabel, JTextField> entry : stockGroup.entrySet()) {
      investmentPanel.add(entry.getKey());
      investmentPanel.add(entry.getValue());
    }
    investmentPanel.add(addMoreStock);
    investmentPanel.add(finalInvestButton);
    investmentPanel.add(mainMenuButton);
    this.revalidate();
    this.repaint();
  }

  private void loadPortfolioHandler() {
    chooser = new JFileChooser("C:\\Workspace");
    FileNameExtensionFilter filter = new FileNameExtensionFilter("CSV files", "csv");
    chooser.setFileFilter(filter);
    int returnVal = chooser.showOpenDialog(this);
    if (returnVal == JFileChooser.APPROVE_OPTION) {
      Object[] options = {load, cancel};
      JOptionPane.showOptionDialog(this,
          "Do you want to load the file " + chooser.getSelectedFile().getName() + " ?", "Load file",
          JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
    }
  }

  private void commonValueFlow(String type) {
    refreshGUI(listContainer);
    commonValuePanel = new JPanel();
    commonValuePanel.setLayout(new GridLayout(0, 2, 20, 20));
    switch (type) {
      case "viewPortfolio":
        dateLabel = new JLabel("Enter date to get composition on (yyyy-mm-dd)");
        commonValuePanel.add(dateLabel);
        stockDate = new JTextField(10);
        commonValuePanel.add(stockDate);
        commonValuePanel.add(getPortfolioCompositionButton);
        commonValuePanel.add(mainMenuButton);
        break;
      case "costBasis":
        dateLabel = new JLabel("Enter date to get cost basis on (yyyy-mm-dd)");
        commonValuePanel.add(dateLabel);
        stockDate = new JTextField(10);
        commonValuePanel.add(stockDate);
        commonValuePanel.add(getCostBasisButton);
        commonValuePanel.add(mainMenuButton);
        break;
      case "portfolioValue":
        dateLabel = new JLabel("Enter date to get portfolio value on (yyyy-mm-dd)");
        commonValuePanel.add(dateLabel);
        stockDate = new JTextField(10);
        commonValuePanel.add(stockDate);
        commonValuePanel.add(getPortfolioValueButton);
        commonValuePanel.add(mainMenuButton);
        break;
      default:
        break;
    }
    containerPanel.add(commonValuePanel);
    this.revalidate();
    this.repaint();
  }

  private void plotPerformanceChartFlow() {
    refreshGUI(listContainer);
    commonValuePanel = new JPanel();
    commonValuePanel.setLayout(new GridLayout(0, 2, 20, 20));
    dateLabel = new JLabel("Enter Start date (yyyy-mm-dd)");
    stockDate = new JTextField(10);
    commonValuePanel.add(dateLabel);
    commonValuePanel.add(stockDate);
    JLabel endDateLabel = new JLabel("Enter end date (yyyy-mm-dd)");
    investEndDate = new JTextField(10);
    commonValuePanel.add(endDateLabel);
    commonValuePanel.add(investEndDate);
    commonValuePanel.add(plotChartButton);
    containerPanel.add(commonValuePanel);
    this.revalidate();
    this.repaint();
  }

  @Override
  public void plotPerformanceChart(Map<String, Integer> performanceData) {
    JFrame frame = new JFrame();
    frame.setLocation(200, 100);
    frame.setSize(500, 400);

    double[] value = new double[performanceData.size()];
    String[] dates = performanceData.keySet().toArray(new String[0]);

    Integer[] values = performanceData.values().toArray(new Integer[0]);
    Arrays.setAll(value, i -> values[i]);

    frame.getContentPane().add(new PerformanceChart(value, dates,
        "Portfolio Performance from:" + dates[0] + " to: " + dates[dates.length - 1]));
    WindowListener winListener = new WindowAdapter() {
      public void windowClosing(WindowEvent event) {
        frame.setVisible(false);
        refreshGUI(commonValuePanel);
        showMainMenu(true);
      }
    };
    frame.addWindowListener(winListener);
    frame.setVisible(true);
  }

  private void refreshGUI(JPanel comp) {
    comp.removeAll();
    this.remove(comp);
    this.revalidate();
    this.repaint();
  }

  @Override
  public void listenerStore(IStockbrokerGUIController features) {
    mainMenuButton.addActionListener(evt -> showMainMenu(true));
    createPortfolioButton.addActionListener(evt -> createPortfolioFlow());
    viewPortfolioButton.addActionListener(evt -> features.selectPortfolioHandler("viewHandler"));
    transactionButton.addActionListener(
        evt -> features.selectPortfolioHandler("transactionHandler"));
    calculateCostBasisButton.addActionListener(
        evt -> features.selectPortfolioHandler("costBasisHandler"));
    calculatePortfolioValueButton.addActionListener(
        evt -> features.selectPortfolioHandler("portfolioValueHandler"));
    loadPortfolioButton.addActionListener(evt -> loadPortfolioHandler());
    validateNameButton.addActionListener(evt -> features.createPortfolio());
    addStockToFile.addActionListener(evt -> features.stockTransactionHandler());
    getCostBasisButton.addActionListener(evt -> features.commonValueFlowHandler("costBasis"));
    getPortfolioValueButton.addActionListener(
        evt -> features.commonValueFlowHandler("portfolioValue"));
    getPortfolioCompositionButton.addActionListener(
        evt -> features.commonValueFlowHandler("viewPortfolio"));
    performanceChartButton.addActionListener(
        evt -> features.selectPortfolioHandler("performanceChartHandler"));
    addStocksToDCAButton.addActionListener(evt -> {
      Map<String, Double> stockWeights = new HashMap<String, Double>();
      weightageSplitHandler(stockWeights, true);
    });
    load.addActionListener(evt -> {
      Window w = SwingUtilities.getWindowAncestor(load);
      if (w != null) {
        w.setVisible(false);
      }
      features.loadPortfolioHandler(chooser.getSelectedFile().getPath());
    });
    cancel.addActionListener(evt -> {
      Window w = SwingUtilities.getWindowAncestor(cancel);
      if (w != null) {
        w.setVisible(false);
      }
    });
    investInPortfolioButton.addActionListener(
        evt -> features.selectPortfolioHandler("investInPortfolioHandler"));
    dollarCostAvgButton.addActionListener(evt -> dollarCostAvgFlow());
    plotChartButton.addActionListener(
        evt -> features.getPerformanceData(stockDate.getText(), investEndDate.getText()));
    addSharesButton.addActionListener(
        evt -> features.getStockQuantityMap(stockDate.getText(), commissionFee.getText(),
            investAmount.getText()));
    finalInvestButton.addActionListener(evt -> {
      Map<String, Double> stockWeights = new LinkedHashMap<>();
      String amount = investAmount.getText();
      String commission = commissionFee.getText();
      String date = stockDate.getText();
      Double totalWeight = 0.0;
      for (Map.Entry<JLabel, JTextField> entry : stockGroup.entrySet()) {
        try {
          stockWeights.put(entry.getKey().getText(),
              Double.parseDouble(entry.getValue().getText()));
          totalWeight = totalWeight + Double.parseDouble(entry.getValue().getText());
        } catch (Exception e) {
          showErrorDialog("Invalid weightage values.");
          return;
        }
      }
      if (totalWeight == 100) {
        if (isDCA) {
          String endDate = investEndDate.getText();
          String pfName = portFolioNameInput.getText();
          String frequency = investFrequency.getText();
          features.createDollarCostStrategyPF(pfName, (LinkedHashMap<String, Double>) stockWeights,
              amount, commission, date, endDate, frequency);
        } else {
          features.investFixedAmountInPortfolio(stockWeights, amount, commission, date);
        }
      } else {
        showErrorDialog("Total weightage should add up to 100.");
      }
    });
    addMoreStock.addActionListener(evt -> {
      investmentPanel.remove(addMoreStock);
      investmentPanel.remove(finalInvestButton);
      investmentPanel.remove(mainMenuButton);
      this.revalidate();
      this.repaint();
      String inputValue = JOptionPane.showInputDialog(this, "Please enter stock symbol",
          "Input Symbol", JOptionPane.PLAIN_MESSAGE);
      try {
        if (!inputValue.equals("")) {
          if (features.validateStockSymbol(inputValue)) {
            if (investmentPanel.isAncestorOf(message3)) {
              investmentPanel.remove(message3);
              investmentPanel.remove(message4);
            }
            JLabel stockName = new JLabel(inputValue);
            JTextField weight = new JTextField();
            stockGroup.put(stockName, weight);
            investmentPanel.add(stockName);
            investmentPanel.add(weight);
          } else {
            showErrorDialog("Invalid symbol");
          }
        }
      } catch (Exception e) {
        showErrorDialog(e.getMessage());
      }
      investmentPanel.add(addMoreStock);
      investmentPanel.add(finalInvestButton);
      investmentPanel.add(mainMenuButton);
      this.revalidate();
      this.repaint();
    });
  }


  @Override
  public void showErrorDialog(String message) {
    JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
  }

  @Override
  public void showMessageDialog(String message) {
    JOptionPane.showMessageDialog(this, message, "Info", JOptionPane.INFORMATION_MESSAGE);
  }

  /**
   * Constructor for the GUI view. Takes in the title of the frame.
   *
   * @param title of the frame.
   */
  public StockbrokerGUI(String title) {
    super(title);
    setSize(600, 600);
    setLocation(200, 100);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setLayout(new FlowLayout());
    containerPanel = new JPanel();
    add(containerPanel);
    mainMenu();
    setVisible(true);
  }
}
