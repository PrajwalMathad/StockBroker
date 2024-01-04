package stockbroker.model;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Model for the stockbroker application. Implements all the required functionalities like create,
 * load and view portfolios. Validate stock symbols, fetch stock values on a given date, calculate
 * portfolio value etc.
 */
public class StockBroker implements IStockbroker {

  protected String stockSymbol;
  protected String stockName;
  protected String stockExchange;

  protected double stockQuantity;
  protected double stockPrice;
  protected final String apiKey = "&apikey=JTQFLMKHAVWUSUC4";
  protected String portfolio;

  protected String stockBuyDate;

  protected void setStockSymbol(String stockSymbol) {
    this.stockSymbol = stockSymbol;
  }

  protected void setStockName(String stockName) {
    this.stockName = stockName;
  }


  protected void setStockPrice(double stockPrice) {
    this.stockPrice = stockPrice;
  }

  protected void setStockExchange(String stockExchange) {
    this.stockExchange = stockExchange;
  }

  protected void setQuantity(double stockQuantity) {
    this.stockQuantity = stockQuantity;
  }

  protected void setStockBuyDate(String stockBuyDate) {
    this.stockBuyDate = stockBuyDate;
  }

  protected String getStockSymbol() {
    return this.stockSymbol;
  }

  protected String getStockBuyDate() {
    return this.stockBuyDate;
  }

  protected String getStockName() {
    return this.stockName;
  }

  protected double getStockPrice() {
    return this.stockPrice;
  }

  protected double getStockQuantity() {
    return this.stockQuantity;
  }

  protected String getStockExchange() {
    return this.stockExchange;
  }

  protected void setPortfolioName(String name) {
    this.portfolio = name;
  }

  protected String getPortfolioName() {
    return this.portfolio;
  }


  @Override
  public void fetchStockListing() throws IOException {
    URL url = null;
    File stockList = new File("misc/stockList.csv");

    try {
      url = new URL(
          "https://www.alphavantage" + ".co/query?function=LISTING_STATUS&datatype=csv" + apiKey);
    } catch (MalformedURLException e) {
      throw new RuntimeException("the alphavantage API has either changed or " + "no longer works");
    }
    try {
      BufferedInputStream in = new BufferedInputStream(url.openStream());
      FileOutputStream fileOutputStream = new FileOutputStream(stockList);
      byte[] dataBuffer = new byte[1024];
      int bytesRead;
      while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
        fileOutputStream.write(dataBuffer, 0, bytesRead);
      }
    } catch (IOException e) {
      throw new IOException("Error in saving the file!");
    }
  }

  @Override
  public boolean validateSymbol(String symbol) throws IOException {
    boolean validSymbol = false;

    try {
      BufferedReader br = new BufferedReader(new FileReader("misc/stockList.csv"));
      String row;
      while ((row = br.readLine()) != null) {
        String[] values = row.split(",");
        if (values[0].equals(symbol)) {
          validSymbol = true;
          this.setStockSymbol(values[0]);
          this.setStockName(values[1]);
          this.setStockExchange(values[2]);
          break;
        }
      }
      br.close();
    } catch (IOException e) {
      throw new IOException(e);
    }

    return validSymbol;
  }


  @Override
  public double getStockValueOnADate(String date) throws IllegalArgumentException {
    String apiFunction;

    if (date.equals("latest")) {
      apiFunction = "GLOBAL_QUOTE";
    } else {
      apiFunction = "TIME_SERIES_DAILY";
    }

    URL url = null;
    try {
      url = new URL(
          "https://www.alphavantage" + ".co/query?function=" + apiFunction + "&outputsize=full"
              + "&symbol=" + getStockSymbol() + "&datatype=csv" + apiKey);
    } catch (MalformedURLException e) {
      throw new RuntimeException("the alphavantage API has either changed or " + "no longer works");
    }

    InputStream in = null;
    StringBuilder output = new StringBuilder();
    try {
      in = url.openStream();
      int b;

      while ((b = in.read()) != -1) {
        output.append((char) b);
      }

    } catch (IOException e) {
      throw new IllegalArgumentException("No price data found for " + stockSymbol);
    }

    String[] lines = output.toString().split("\n");

    //System.out.println("data received:\n" + output.toString());

    //latest price available
    if (date.equals("latest")) {
      String[] values = lines[1].split(",");
      //set stock buy date & Price to reduce API calls
      setStockBuyDate(values[6]);
      setStockPrice(Double.parseDouble(values[4]));
      return getStockPrice();
    } else {
      //get the closing price on a given date
      for (String line : lines) {
        String[] values = line.split(",");
        if (values[0].equals(date)) {
          //set stock buy date & Price to reduce API calls
          setStockBuyDate(values[0]);
          setStockPrice(Double.parseDouble(values[4]));
          return getStockPrice();
        }
      }

    }

    throw new IllegalArgumentException("data on given date not found");

  }


  @Override
  public String getStockDetails() {
    return "Stock Details:\nStock Name: " + getStockSymbol() + ", " + getStockName()
        + "\nStock Exchange: " + getStockExchange() + "\nStock value: $" + getStockValueOnADate(
        "latest");
  }

  @Override
  public double getStockTotalValue() {
    return getStockQuantity() * getStockPrice();
  }

  @Override
  public String getStockDetailsWithTotalPrice(double quantity) throws IllegalArgumentException {
    if (quantity <= 0) {
      throw new IllegalArgumentException("Quantity should be greater than 0");
    }
    setQuantity(quantity);
    return getStockDetails() + "\nQuantity: " + getStockQuantity() + "\nTotal value of stocks: $"
        + getStockTotalValue();
  }

  @Override
  public int getFileIndex(String fileName) {
    List<String> existingFiles = listPortfolio();
    for (int i = 0; i < existingFiles.size(); i++) {
      if (existingFiles.get(i).equals(fileName + ".csv")) {
        return i + 1;
      }
    }
    return 0;
  }


  @Override
  public void createPortfolio(String name) throws IOException {
    List<String> existingFiles = listPortfolio();
    if (!existingFiles.contains(name + ".csv")) {
      setPortfolioName(name);
      File portfolio = new File("portfolios/" + name + ".csv");
      portfolio.createNewFile();
      FileWriter fw = new FileWriter("portfolios/" + name + ".csv");
      fw.append("Symbol,Quantity,BuyDate");
      fw.close();
    } else {
      throw new IOException(
          "Portfolio with the same name already exists. Please choose a different name!");
    }
  }


  @Override
  public double getPortfolioValue(String fileIndex, String date) throws IOException {
    File[] files = new File("portfolios").listFiles();

    double totalValue = 0;

    try {
      BufferedReader br = new BufferedReader(
          new FileReader(files[Integer.parseInt(fileIndex) - 1]));
      String row;
      //skip first line as it contains header
      br.readLine();
      while ((row = br.readLine()) != null) {
        String[] values = row.split(",");
        setStockSymbol(values[0]);
        setQuantity(Double.parseDouble(values[1]));
        totalValue += getStockQuantity() * getStockValueOnADate(date);

      }
      br.close();
    } catch (IOException e) {
      throw new IOException(e);
    }

    return totalValue;
  }


  @Override
  public void addToPortfolio() throws IOException {
    FileWriter fw = new FileWriter("portfolios/" + getPortfolioName() + ".csv", true);
    fw.append("\n" + getStockSymbol() + "," + getStockQuantity() + "," + getStockBuyDate());
    fw.close();
  }

  @Override
  public List<String> listPortfolio() {
    List<String> fileList = new ArrayList<String>();
    File[] files = new File("portfolios").listFiles();
    for (File file : files) {
      if (file.isFile()) {
        fileList.add(file.getName());
      }
    }
    return fileList;
  }

  @Override
  public String viewPortfolio(String fileIndex) throws IOException {
    File[] files = new File("portfolios").listFiles();
    FileReader fr = new FileReader(files[Integer.parseInt(fileIndex) - 1]);
    BufferedReader br = new BufferedReader(fr);
    String line = "";
    String[] tempArr;
    String output = "\n";
    while ((line = br.readLine()) != null) {
      tempArr = line.split("\n");
      for (String tempStr : tempArr) {
        String[] split = tempStr.split(",");
        output = output + split[0] + " | " + split[1] + " | " + split[2] + " | ";
      }
      output = output + "\n";
    }
    br.close();
    return output;
  }

  @Override
  public void loadPortfolio(String path) throws IllegalArgumentException, IOException {

    Path pathSrc = Path.of(path);

    File file = new File(pathSrc.toString());
    String fileName = file.getName();

    try {
      BufferedReader br = new BufferedReader(new FileReader(pathSrc.toString()));
      String row;
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
        //Check buy date has a data point
        setStockSymbol(values[0]);
        try {
          getStockValueOnADate(values[2]);
        } catch (IllegalArgumentException e) {
          throw new IllegalArgumentException(e + "Invalid date:" + values[2] + " in row:" + row);
        }

      }
      //all rows processed and valid
      //copy it to local portfolio folder
      Path pathDest = Path.of("portfolios" + File.separator + fileName);
      Files.copy(pathSrc, pathDest);

      br.close();
    } catch (IOException e) {
      throw new IOException(e);
    }

  }
}
