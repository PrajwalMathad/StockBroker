package stockbroker.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The implementation for model of FlexStockbroker class. It contains all the functions to support
 * the flexible portfolios, buy stocks and view the created portfolio on a date, get cost basis, get
 * performance of a portfolio.
 */
public class FlexStockbroker extends StockBroker implements IFlexStockbroker {

  private double commissionFee;
  private Map<String, Double> stockQtyMap;


  private void setCommissionFee(double commissionFee) {
    this.commissionFee = commissionFee;
  }

  private double getCommissionFee() {
    return this.commissionFee;
  }

  public Map<String, Double> getStockQtyMap() {
    return stockQtyMap;
  }

  private boolean checkCacheExists(String symbol) {
    return Files.exists(Path.of("misc/" + symbol + ".csv"));
  }

  private boolean checkPortfolioExists(String name) {
    return Files.exists(Path.of("flexPortfolios/" + name + ".csv"));
  }

  private boolean checkDollarCostStrategyExists(String name) {
    return Files.exists(Path.of("dollarCostStrategy/" + name));
  }


  private void updateCache(String symbol) throws IOException {
    this.cacheStockDetails(symbol);
  }

  private void cacheStockDetails(String symbol) throws IOException {
    URL url = null;
    InputStream in = null;
    StringBuilder output = new StringBuilder();
    try {
      url = new URL(
          "https://www.alphavantage" + ".co/query?function=TIME_SERIES_DAILY" + "&outputsize=full"
              + "&symbol=" + symbol + "&datatype=csv" + apiKey);

      in = url.openStream();
      int b;
      while ((b = in.read()) != -1) {
        output.append((char) b);
      }

    } catch (MalformedURLException e) {
      throw new IOException("the alphavantage API has either changed or no longer works. " + e);
    } catch (IOException e) {
      throw new IOException("Error reading data from the alphavantage API. " + e);
    }

    FileWriter fw = new FileWriter("misc/" + symbol + ".csv", false);
    fw.write(output.toString());
    fw.close();


  }

  private double fetchPriceDataFromCache(String date) throws IllegalArgumentException, IOException {
    BufferedReader br = new BufferedReader(new FileReader("misc/" + getStockSymbol() + ".csv"));
    String row;
    //skip header
    br.readLine();
    while ((row = br.readLine()) != null) {
      String[] values = row.split(",");
      if (values[0].equals(date)) {
        return Double.parseDouble(values[4]);
      }
    }
    throw new IllegalArgumentException(
        "Price data for symbol: " + getStockSymbol() + " not found on " + date);
  }


  @Override
  public String getStockDetailsWithTotalPrice(double quantity, String date, double commissionFee)
      throws IllegalArgumentException, IOException {
    if (quantity <= 0) {
      throw new IllegalArgumentException("Quantity should be greater than 0");
    }

    if (commissionFee < 0) {
      throw new IllegalArgumentException("Commission should be greater than or equal to 0");
    }

    setQuantity(quantity);

    //if cache doesn't exist create a cache
    if (!checkCacheExists(getStockSymbol())) {
      cacheStockDetails(getStockSymbol());
    }

    try {
      setStockPrice(fetchPriceDataFromCache(date));

    } catch (IllegalArgumentException e) {
      // if price data not found in cache then update cache & search again
      updateCache(getStockSymbol());

      // throws IllegalArgumentException if data not found
      setStockPrice(fetchPriceDataFromCache(date));
    }
    setStockBuyDate(date);
    setCommissionFee(commissionFee);
    return "Stock Details:\nStock Name: " + getStockSymbol() + ", " + getStockName()
        + "\nStock Exchange: " + getStockExchange() + "\nStock value: $" + getStockPrice()
        + "\nQuantity: " + getStockQuantity() + "\nTotal value: $" + (getStockQuantity()
        * getStockPrice());
  }

  @Override
  public List<String> listPortfolio() {
    List<String> fileList = new ArrayList<String>();
    File[] files = new File("flexPortfolios").listFiles();
    for (File file : files) {
      if (file.isFile()) {
        fileList.add(file.getName());
      }
    }
    return fileList;
  }


  private void dollarCostLazyExecHelper(String fileIndex, String date) throws IOException {
    String filename = listPortfolio().get(Integer.parseInt(fileIndex) - 1);
    String row;
    String[] values;
    Map<String, Double> stockWeightMap = new HashMap<>();
    LocalDate endDate;
    LocalDate lastProcessed;
    LocalDate givenDate;
    long daysToProcess = 0;
    int frequency;
    double amountToInvest;
    double commissionFee;
    String oldLine;
    BufferedReader br = new BufferedReader(new FileReader("dollarCostStrategy/" + filename));

    //skip header
    br.readLine();
    //read next line containing all details
    row = br.readLine();
    //store it, so that it can be used later to replace lastProcessedDate
    oldLine = row;

    values = row.split(",");

    //put 1st entry in map and read rest of the details
    stockWeightMap.put(values[0], Double.parseDouble(values[1]));
    try {
      if (values[5].equals("null")) {
        //if there is no end date then assign a very large number
        endDate = LocalDate.MAX;
      } else {
        endDate = LocalDate.parse(values[5]);
      }
      lastProcessed = LocalDate.parse(values[7]);
      givenDate = LocalDate.parse(date);
    } catch (DateTimeParseException e) {
      throw new IOException("error parsing date");
    }

    frequency = Integer.parseInt(values[6]);
    amountToInvest = Double.parseDouble(values[2]);
    commissionFee = Double.parseDouble(values[3]);

    //put the rest of stocks and weights in map
    while ((row = br.readLine()) != null) {
      values = row.split(",");
      stockWeightMap.put(values[0], Double.parseDouble(values[1]));
    }

    //check if the last processed date and given date difference is greater than or equal to
    // frequency

    if (givenDate.compareTo(endDate) > 0) {
      //System.out.println("entered");
      daysToProcess = ChronoUnit.DAYS.between(lastProcessed, endDate);
    } else {
      daysToProcess = ChronoUnit.DAYS.between(lastProcessed, givenDate);
    }

    //System.out.println("days to process: " + daysToProcess);

    if (daysToProcess >= frequency) {
      //do batch processing
      int counter = (int) (daysToProcess / frequency);
      LocalDate tmp = lastProcessed;

      while (counter != 0) {
        tmp = tmp.plusDays(frequency);

        //break if tmp date created is future date since no data is available
        if (tmp.isAfter(LocalDate.now())) {
          break;
        }

        int tryCount = 0;
        int maxTries = 3;
        while (true) {
          try {
            //System.out.println("counter is: " + counter + " and Processing date: " + tmp);
            // break out of loop on success
            investFixedAmountInPortfolio(fileIndex, stockWeightMap, amountToInvest, commissionFee,
                tmp.toString());
            break;
          } catch (IllegalArgumentException e) {
            //System.out.println(e);
            // handle exception
            //check data on next date
            tmp = tmp.plusDays(1);
            if (++tryCount == maxTries) {
              throw e;
            }
          }
        }
        counter--;
      }

      //lazy execution done
      //update last processed date in file
      String[] valueToReplace = oldLine.split(",");
      valueToReplace[valueToReplace.length - 1] = tmp.toString();
      StringBuilder newLine = new StringBuilder();
      for (String str : valueToReplace) {
        newLine.append(str);
        newLine.append(",");
      }
      //delete extra comma
      newLine.deleteCharAt(newLine.length() - 1);
      updateLineInAFile(Path.of("dollarCostStrategy/" + filename), oldLine, newLine.toString());
    }

  }

  private void updateLineInAFile(Path path, String oldLine, String newLine) throws IOException {
    List<String> fileContent = new ArrayList<>(Files.readAllLines(path, StandardCharsets.UTF_8));

    for (int i = 0; i < fileContent.size(); i++) {
      if (fileContent.get(i).equals(oldLine)) {
        fileContent.set(i, newLine);
        break;
      }
    }

    Files.write(path, fileContent, StandardCharsets.UTF_8);
  }


  @Override
  public String viewPortfolio(String fileIndex, String date) throws IOException {
    String filename = listPortfolio().get(Integer.parseInt(fileIndex) - 1);

    //check if the given portfolio has a dollar cost strategy, if yes perform lazy execution
    if (checkDollarCostStrategyExists(filename)) {
      dollarCostLazyExecHelper(fileIndex, date);
    }

    List<String> fileList = listPortfolio();
    BufferedReader br = new BufferedReader(
        new FileReader("flexPortfolios/" + fileList.get(Integer.parseInt(fileIndex) - 1)));
    String row;
    stockQtyMap = new HashMap<>();
    LocalDate dateToConsider = LocalDate.parse(date);

    //skip header
    br.readLine();

    while ((row = br.readLine()) != null) {
      String[] values = row.split(",");
      LocalDate transactionDate = LocalDate.parse(values[2]);

      //if map contains symbol
      if (stockQtyMap.containsKey(values[0])) {

        //check if transaction is buy && transaction date is before or equals date to consider
        if (values[3].equals("buy") && (transactionDate.isBefore(dateToConsider)
            || transactionDate.equals(dateToConsider))) {
          stockQtyMap.replace(values[0],
              stockQtyMap.get(values[0]) + Double.parseDouble(values[1]));
        }
        //check if transaction is sell && transaction date is before or equals date to consider
        if (values[3].equals("sell") && (transactionDate.isBefore(dateToConsider)
            || transactionDate.equals(dateToConsider))) {
          stockQtyMap.replace(values[0],
              stockQtyMap.get(values[0]) - Double.parseDouble(values[1]));
        }

      } else {

        //check if transaction is buy && transaction date is before or equals date to sell
        if (values[3].equals("buy") && (transactionDate.isBefore(dateToConsider)
            || transactionDate.equals(dateToConsider))) {
          stockQtyMap.put(values[0], Double.parseDouble(values[1]));
        }
        //check if transaction is sell && transaction date is before or equals date to sell
        if (values[3].equals("sell") && (transactionDate.isBefore(dateToConsider)
            || transactionDate.equals(dateToConsider))) {
          stockQtyMap.put(values[0], -Double.parseDouble(values[1]));
        }

      }

    }

    br.close();

    StringBuilder result = new StringBuilder();

    result.append("Symbol" + "|" + "Qty\n");

    stockQtyMap.entrySet().stream()
        .forEach(pair -> result.append(pair.getKey() + " | " + pair.getValue() + "\n"));

    return result.toString();

  }

  @Override
  public double getCostBasisOnADate(String fileIndex, String date) throws IOException {

    String filename = listPortfolio().get(Integer.parseInt(fileIndex) - 1);

    //check if the given portfolio has a dollar cost strategy, if yes perform lazy execution
    if (checkDollarCostStrategyExists(filename)) {
      dollarCostLazyExecHelper(fileIndex, date);
    }

    List<String> fileList = listPortfolio();
    BufferedReader br = new BufferedReader(
        new FileReader("flexPortfolios/" + fileList.get(Integer.parseInt(fileIndex) - 1)));
    String row;
    double costBasisSum = 0;
    LocalDate dateToConsider = LocalDate.parse(date);

    //skip header
    br.readLine();

    while ((row = br.readLine()) != null) {
      String[] values = row.split(",");
      LocalDate transactionDate = LocalDate.parse(values[2]);

      //check if transaction is buy && transaction date is before or equals date to consider
      if (values[3].equals("buy") && (transactionDate.isBefore(dateToConsider)
          || transactionDate.equals(dateToConsider))) {

        //set stock symbol
        setStockSymbol(values[0]);
        //fetch price of that symbol on the buy date
        double stockPriceTmp = fetchPriceDataFromCache(values[2]);
        //price * qty + commission fee
        costBasisSum =
            costBasisSum + (stockPriceTmp * Double.parseDouble(values[1])) + Double.parseDouble(
                values[4]);

      }

      if (values[3].equals("sell") && (transactionDate.isBefore(dateToConsider)
          || transactionDate.equals(dateToConsider))) {

        //Add commission fee for sell transactions
        costBasisSum = costBasisSum + Double.parseDouble(values[4]);

      }


    }

    br.close();

    return costBasisSum;
  }


  @Override
  public double getPortfolioValue(String fileIndex, String date)
      throws IllegalArgumentException, IOException {

    String filename = listPortfolio().get(Integer.parseInt(fileIndex) - 1);

    //check if the given portfolio has a dollar cost strategy, if yes perform lazy execution
    if (checkDollarCostStrategyExists(filename)) {
      dollarCostLazyExecHelper(fileIndex, date);
    }

    double result = 0;
    //get the portfolio contents on a date by setting values in map
    viewPortfolio(fileIndex, date);

    for (String stock : stockQtyMap.keySet()) {
      setStockSymbol(stock);

      //this will check in cache and set the price
      getStockDetailsWithTotalPrice(stockQtyMap.get(stock), date, 0.0);

      result = result + (stockQtyMap.get(stock) * getStockPrice());

    }

    return result;
  }

  @Override
  public void createPortfolio(String name) throws IOException {
    List<String> existingFiles = listPortfolio();
    if (!existingFiles.contains(name + ".csv")) {
      setPortfolioName(name);
      File portfolio = new File("flexPortfolios/" + name + ".csv");
      portfolio.createNewFile();
      FileWriter fw = new FileWriter("flexPortfolios/" + name + ".csv");
      fw.append("Symbol,Quantity,Date,Transaction-Type,Commission-Fee");
      fw.close();
    } else {
      throw new IOException(
          "Portfolio with the same name already exists. Please choose a different name!");
    }
  }

  @Override
  public void addToPortfolio(String fileIndex, String transactionType) throws IOException {
    List<String> fileList = listPortfolio();
    FileWriter fw = new FileWriter(
        "flexPortfolios/" + fileList.get(Integer.parseInt(fileIndex) - 1), true);
    fw.append("\n" + getStockSymbol() + "," + getStockQuantity() + "," + getStockBuyDate() + ","
        + transactionType + "," + getCommissionFee());
    fw.close();
  }

  private void validateSellHelper(BufferedReader br, double quantity, String date,
      double commissionFee) throws IllegalArgumentException, IOException {
    String row;

    double quantityAvailable = 0;
    LocalDate dateToSell = LocalDate.parse(date);

    //skip header
    br.readLine();

    while ((row = br.readLine()) != null) {
      String[] values = row.split(",");
      LocalDate transactionDate = LocalDate.parse(values[2]);

      if (values[0].equals(getStockSymbol())) {
        //check if transaction is buy && transaction date is before or equals date to sell
        if (values[3].equals("buy") && (dateToSell.isAfter(transactionDate)
            || transactionDate.equals(dateToSell))) {
          quantityAvailable += Double.parseDouble(values[1]);
        }
        //check if transaction is Sell && transaction date is before or equals date to sell
        if (values[3].equals("sell") && (dateToSell.isAfter(transactionDate)
            || transactionDate.equals(dateToSell))) {
          quantityAvailable -= Double.parseDouble(values[1]);
        }

        //check if transaction is Sell && A sell has already happened in the future.
        if (values[3].equals("sell") && dateToSell.isBefore(transactionDate)) {
          throw new IllegalArgumentException(
              "Invalid transaction." + "A sell has already happened in the future date:"
                  + transactionDate);
        }

      }
    }
    br.close();

    if (quantityAvailable < quantity) {
      throw new IllegalArgumentException(
          "Not enough quantity available to sell." + "Quantity Available to sell is: "
              + quantityAvailable);
    }


  }

  @Override
  public void validateSellDetails(String fileIndex, double quantity, String date,
      double commissionFee) throws IllegalArgumentException, IOException {
    String filename = listPortfolio().get(Integer.parseInt(fileIndex) - 1);

    //check if the given portfolio has a dollar cost strategy, if yes perform lazy execution
    if (checkDollarCostStrategyExists(filename)) {
      dollarCostLazyExecHelper(fileIndex, date);
    }

    List<String> fileList = listPortfolio();
    BufferedReader br = new BufferedReader(
        new FileReader("flexPortfolios/" + fileList.get(Integer.parseInt(fileIndex) - 1)));

    validateSellHelper(br, quantity, date, commissionFee);


  }


  @Override
  public Map<String, Integer> getPortfolioPerformance(String fileIndex, String startDate,
      String endDate) throws IOException, ParseException {

    String filename = listPortfolio().get(Integer.parseInt(fileIndex) - 1);

    //check if the given portfolio has a dollar cost strategy, if yes perform lazy execution
    if (checkDollarCostStrategyExists(filename)) {
      dollarCostLazyExecHelper(fileIndex, startDate);
      dollarCostLazyExecHelper(fileIndex, endDate);
    }

    Map<String, Double> datePFValueMap = new LinkedHashMap<>();
    Map<String, Integer> dateStarsMap = new LinkedHashMap<>();
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    int frequency = 5;
    Date start = (Date) dateFormat.parse(startDate);
    Date end = (Date) dateFormat.parse(endDate);
    Long intervalSize = (end.getTime() - start.getTime()) / frequency;

    for (int i = 0; i <= frequency && intervalSize > 0; i++) {
      Date date = new Date(start.getTime() + intervalSize * i);

      LocalDate tmp = LocalDate.parse(dateFormat.format(date));
      int tryCount = 0;
      int maxTries = 3;
      double pfValue;
      while (true) {
        try {
          // break out of loop on success
          pfValue = getPortfolioValue(fileIndex, tmp.toString());
          break;
        } catch (IllegalArgumentException e) {
          //System.out.println(e);
          // handle exception
          //check data on next date
          tmp = tmp.plusDays(1);
          if (++tryCount == maxTries) {
            throw e;
          }
        }
      }

      datePFValueMap.put(dateFormat.format(date), pfValue);
    }

    double minVal = Collections.min(datePFValueMap.values());
    double maxVal = Collections.max(datePFValueMap.values());

    double delta = (maxVal - minVal) / 10;

    datePFValueMap.entrySet().stream().forEach(
        dateValEntry -> dateStarsMap.put(dateValEntry.getKey(),
            (int) (dateValEntry.getValue() / delta)));

    dateStarsMap.put("scale", (int) delta);
    return dateStarsMap;
  }

  @Override
  public void investFixedAmountInPortfolio(String fileIndex, Map<String, Double> stockWeights,
      double amount, double commissionFee, String date)
      throws IllegalArgumentException, IOException {

    double sumOfPercentages = 0;
    for (double weight : stockWeights.values()) {
      sumOfPercentages += weight;
    }

    if (sumOfPercentages != 100.00) {
      throw new IllegalArgumentException("Sum of weights does not equals to 100");
    }

    //subtract the commission fee from total amount
    amount = amount - (stockWeights.size() * commissionFee);

    //System.out.println("amount:" + amount);
    for (String symbol : stockWeights.keySet()) {
      if (validateSymbol(symbol)) {
        //get the weight percentage
        double weight = stockWeights.get(symbol);
        //divide the amount for each stock based on percentage
        double amtToInvestInThisStock = amount * (weight / 100);

        //System.out.println("stock: " + symbol + " amt: " + amtToInvestInThisStock );

        //get the price of stock by setting dummy qty
        getStockDetailsWithTotalPrice(1, date, commissionFee);
        double qty = amtToInvestInThisStock / getStockPrice();
        setQuantity(qty);
        addToPortfolio(fileIndex, "buy");
      } else {
        throw new IllegalArgumentException("Invalid Symbol");
      }
    }

  }


  @Override
  public void createDollarCostStrategyPF(String name, LinkedHashMap<String, Double> stockWeights,
      double amount, double commissionFee, String startDate, String endDate, int frequency)
      throws IllegalArgumentException, IOException {

    double sumOfPercentages = 0;
    for (double weight : stockWeights.values()) {
      sumOfPercentages += weight;
    }

    if (sumOfPercentages != 100.00) {
      throw new IllegalArgumentException("Sum of weights does not equals to 100");
    }

    //create a portfolio with same name as strategy
    if (!checkPortfolioExists(name)) {
      createPortfolio(name);
    }

    //create a file in dollarCost folder
    if (!checkDollarCostStrategyExists(name + ".csv")) {
      File strategy = new File("dollarCostStrategy/" + name + ".csv");
      strategy.createNewFile();
      FileWriter fw = new FileWriter("dollarCostStrategy/" + name + ".csv");
      fw.append("Symbol,Weight,Amount,Commission-Fee,StartDate,EndDate,Frequency(in Days),"
          + "LastProcessedDate");
      //get 1st entry in map and write other details
      Optional<String> firstKey = stockWeights.keySet().stream().findFirst();

      //this is to replace last processed date if data not found
      String oldLine = firstKey.get() + "," + stockWeights.get(firstKey.get()) + "," + amount + ","
          + commissionFee + "," + startDate + "," + endDate + "," + frequency + "," + startDate;

      fw.append("\n" + firstKey.get() + "," + stockWeights.get(firstKey.get()) + "," + amount + ","
          + commissionFee + "," + startDate + "," + endDate + "," + frequency + "," + startDate);

      //for rest of the stocks append the details to file
      for (String symbol : stockWeights.keySet()) {
        if ((!symbol.equals(firstKey.get()))) {
          fw.append("\n" + symbol + "," + stockWeights.get(symbol));
        }
      }
      fw.close();

      //buy stocks on start date
      LocalDate tmp = LocalDate.parse(startDate);
      int tryCount = 0;
      int maxTries = 3;
      while (true) {
        try {
          //System.out.println("Processing date: " + tmp);
          // break out of loop on success
          investFixedAmountInPortfolio(getFileIndex(name) + "", stockWeights, amount, commissionFee,
              tmp.toString());
          break;
        } catch (IllegalArgumentException e) {
          //System.out.println(e);
          // handle exception
          //check data on next date
          tmp = tmp.plusDays(1);
          if (++tryCount == maxTries) {
            throw e;
          }
        }
      }

      //update last processed in the file
      String[] valueToReplace = oldLine.split(",");
      valueToReplace[valueToReplace.length - 1] = tmp.toString();
      StringBuilder newLine = new StringBuilder();
      for (String str : valueToReplace) {
        newLine.append(str);
        newLine.append(",");
      }
      //delete extra comma
      newLine.deleteCharAt(newLine.length() - 1);
      //System.out.println(newLine.toString());
      updateLineInAFile(Path.of("dollarCostStrategy/" + name + ".csv"), oldLine,
          newLine.toString());


    } else {
      throw new IllegalArgumentException("dollar-cost avg strategy with same name already exists");
    }


  }

  @Override
  public void loadPortfolio(String path) throws IllegalArgumentException, IOException {

    Path pathSrc = Path.of(path);

    File file = new File(pathSrc.toString());
    String fileName = file.getName();

    BufferedReader br = new BufferedReader(new FileReader(pathSrc.toString()));
    String row;
    StringBuilder tmpData = new StringBuilder();
    tmpData.append("header");

    //skip first line as it contains header
    br.readLine();
    while ((row = br.readLine()) != null) {

      String[] values = row.split(",");
      //validate symbol
      if (!validateSymbol(values[0])) {
        throw new IllegalArgumentException(
            "Invalid symbol entered:" + values[0] + " in row:" + row);
      }
      //Check quantity valid
      if (Double.parseDouble(values[1]) <= 0) {
        throw new IllegalArgumentException("Invalid quantity:" + values[1] + " in row:" + row);
      }
      //check commission fee < 0
      if (Double.parseDouble(values[4]) <= 0) {
        throw new IllegalArgumentException(
            "Invalid commission fee:" + values[4] + " in row:" + row);
      }

      //Check transaction date has a data point
      setStockSymbol(values[0]);
      setQuantity(Double.parseDouble(values[1]));
      setCommissionFee(Double.parseDouble(values[4]));
      try {
        getStockDetailsWithTotalPrice(getStockQuantity(), values[2], getCommissionFee());
      } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException(e + "Invalid date:" + values[2] + " in row:" + row);
      }

      //if transaction is sell check whether it was a valid transaction
      if (values[3].equals("sell")) {

        //create a tmp file & write tmpdata
        File tmpfile = File.createTempFile("abc", ".tmp");
        BufferedWriter bw = new BufferedWriter(new FileWriter(tmpfile));
        bw.write(tmpData.toString());
        bw.close();
        try {
          validateSellHelper(new BufferedReader(new FileReader(tmpfile)), getStockQuantity(),
              values[2], getCommissionFee());
        } catch (IllegalArgumentException e) {
          throw new IllegalArgumentException(e + "\nInvalid entry in file in row: " + row);
        }

      }

      tmpData.append("\n" + row);

    }
    //all rows processed and valid
    //copy it to local portfolio folder
    Path pathDest = Path.of("flexPortfolios" + File.separator + fileName);
    Files.copy(pathSrc, pathDest);

    br.close();


  }
}
