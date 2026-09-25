package de.cosmohdx.griefergames.feature.payment;

import de.cosmohdx.griefergames.GrieferGames;
import de.cosmohdx.griefergames.feature.payment.TransactionType;
import net.labymod.api.Constants;
import net.labymod.api.models.OperatingSystem;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileManager {
  private File dataDirectory;
  private File transactionsLogFile;
  private BufferedWriter transactionsLogWriter;

  public FileManager(GrieferGames griefergames) {
    try {
      dataDirectory = new File(Constants.Files.CONFIGS.toString(), griefergames.addonInfo().getNamespace());

      transactionsLogFile = new File(dataDirectory, "transactions.log");
      if(!transactionsLogFile.exists()) {
        transactionsLogFile.createNewFile();
      }

      transactionsLogWriter = new BufferedWriter(new FileWriter(transactionsLogFile, true));
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void logTransaction(String player, double amount, TransactionType type) {
    try {
      final String date = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(new Date());
      switch(type) {
        case PAY:
          transactionsLogWriter.write("["+date+"] Payed $"+amount+" to "+player+"\n");
          break;
        case RECEIVE:
          transactionsLogWriter.write("["+date+"] Received $"+amount+" from "+player+"\n");
          break;
        case MONEYDROP:
          transactionsLogWriter.write("["+date+"] Earned $"+amount+" from moneydrop\n");
      }
      transactionsLogWriter.flush();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void openTransactionsFile() {
    OperatingSystem.getPlatform().openFile(transactionsLogFile);
  }

  public void close() {
    if (transactionsLogWriter == null) {
      return;
    }
    try {
      transactionsLogWriter.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
