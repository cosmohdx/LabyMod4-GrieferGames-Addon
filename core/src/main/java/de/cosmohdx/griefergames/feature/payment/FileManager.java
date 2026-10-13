package de.cosmohdx.griefergames.feature.payment;

import de.cosmohdx.griefergames.GrieferGames;
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

  private final GrieferGames griefergames;

  public FileManager(GrieferGames griefergames) {
    this.griefergames = griefergames;
    try {
      dataDirectory = new File(Constants.Files.CONFIGS.toString(), griefergames.addonInfo().getNamespace());

      transactionsLogFile = new File(dataDirectory, "transactions.log");
      if(!transactionsLogFile.exists()) {
        transactionsLogFile.createNewFile();
      }

      transactionsLogWriter = new BufferedWriter(new FileWriter(transactionsLogFile, true));
    } catch (IOException e) {
      griefergames.logger().error(GrieferGames.LOG_PREFIX + "Failed to open transactions log", e);
    }
  }

  public void logTransaction(String player, double amount, TransactionType type) {
    if (transactionsLogWriter == null) {
      return;
    }
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
      this.griefergames.logger().error(GrieferGames.LOG_PREFIX + "Failed to write transaction", e);
    }
  }

  public void openTransactionsFile() {
    OperatingSystem.getPlatform().openFile(transactionsLogFile);
  }

  public void open() {
    if (transactionsLogWriter != null) {
      return;
    }
    try {
      transactionsLogWriter = new BufferedWriter(new FileWriter(transactionsLogFile, true));
    } catch (IOException e) {
      this.griefergames.logger().error(GrieferGames.LOG_PREFIX + "Failed to reopen transactions log", e);
    }
  }

  public void close() {
    if (transactionsLogWriter == null) {
      return;
    }
    try {
      transactionsLogWriter.close();
    } catch (IOException e) {
      this.griefergames.logger().error(GrieferGames.LOG_PREFIX + "Failed to close transactions log", e);
    } finally {
      transactionsLogWriter = null;
    }
  }
}
