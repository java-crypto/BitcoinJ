/*
 * Herkunft/Origin: http://javacrypto.bplaced.net/
 * Programmierer/Programmer: Michael Fehr
 * Copyright/Copyright: Michael Fehr
 * Lizenttext/Licence: verschiedene Lizenzen / several licenses
 * getestet mit/tested with: Java Runtime Environment 11.0.5 x64
 * verwendete IDE/used IDE: intelliJ IDEA 2019.3.1
 * Datum/Date (dd.mm.jjjj): 12.03.2020
 * Funktion: 06 Erzeugt eine Transaktion
 * Function: 06 creates an transaction
 *
 * Sicherheitshinweis/Security notice
 * Die Programmroutinen dienen nur der Darstellung und haben keinen Anspruch auf eine korrekte Funktion,
 * insbesondere mit Blick auf die Sicherheit !
 * Prüfen Sie die Sicherheit bevor das Programm in der echten Welt eingesetzt wird.
 * The program routines just show the function but please be aware of the security part -
 * check yourself before using in the real world !
 *
 * Sie benötigen diverse Bibliotheken (alle im Github-Archiv im Unterordner "libs")
 * You need a lot of libraries (see my Github-repository in subfolder "libs")
 * verwendete BitcoinJ-Bibliothek / used BitcoinJ Library: bitcoinj-core-0.15.6.jar
 * my Github-Repository: https://github.com/java-crypto/BitcoinJ
 * libs in my Github-Repo: https://github.com/java-crypto/BitcoinJ_Libraries
 *
 */

import org.bitcoinj.core.*;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.utils.BriefLogFormatter;
import org.bitcoinj.wallet.SendRequest;
import org.bitcoinj.wallet.Wallet;
import org.bitcoinj.wallet.WalletTransaction;

import javax.swing.*;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ExecutionException;

public class BitcoinJ06CreateTransaction {
    public static void main(String[] args) throws AddressFormatException, ExecutionException, InterruptedException {
        // redirect the console to two windows
        RedirectedFrame outputFrameErrors = new RedirectedFrame("Logfileausgaben Fenster", true, false, true, "BitcoinJ06Transaction_Logfile_" + getActualDateReverse() + ".txt", 700, 600, JFrame.DO_NOTHING_ON_CLOSE);
        RedirectedFrame outputFrameOutput = new RedirectedFrame("Programmausgaben Fenster", false, true, true, "BitcoinJ06Transaction_Output_" + getActualDateReverse() + ".txt", 700, 600, JFrame.DO_NOTHING_ON_CLOSE);
        System.out.println("BitcoinJ 06 Erzeuge eine Transaction (Ueberweisung)");
        NetworkParameters netParams = TestNet3Params.get(); // preset
        String filenameWallet = "bitcoinj02createwallet";
        // choose network type (TEST or REG)
        String networkType = "TEST";
        //String networkType = "REG";
        switch (networkType) {
            case "TEST": {
                netParams = TestNet3Params.get();
                //filenameWallet = filenameWallet + "_Testnet";
                break;
            }
            case "REG": {
                netParams = RegTestParams.get();
                //filenameWallet = filenameWallet + "_Regtest";
                break;
            }
            default: {
                System.out.println("Es ist kein networkType angegeben, das Programm wird nicht gestartet");
                JOptionPane.showMessageDialog(null,
                        "Das Programm wird beendet sobald der OK-Button betaetigt wird",
                        "Programmende",
                        JOptionPane.WARNING_MESSAGE);
                System.exit(0);
            }
        }
        System.out.println("Das Programm arbeitet im Netzwerk: " + netParams.getId());
        // init wallet app kit
        WalletAppKit kit = null;
        BriefLogFormatter.init();
        kit = new WalletAppKit(netParams, new File("."), filenameWallet) {
            @Override
            protected void onSetupCompleted() {
                if (wallet().getKeyChainGroupSize() < 1) {
                    wallet().importKey(new ECKey());
                }
            }
        };
        System.out.println("Startzeit des WalletAppKit  : " + getActualDate());
        System.out.println("Bitte warten, das Wallet App Kit geht online und aktualisiert das Wallet");
        if (netParams == RegTestParams.get()) {
            kit.connectToLocalHost(); // neccessary for regtest-mode
        }
        kit.startAsync();
        kit.awaitRunning();
        // data from wallet
        String currentAddress = kit.wallet().currentReceiveAddress().toString();
        String balanceAvailable = kit.wallet().getBalance(Wallet.BalanceType.AVAILABLE).toFriendlyString();
        System.out.println("\nAktuelle Empfangsadresse    : " + currentAddress);
        System.out.println("Balance/Guthaben verfuegbar : " + balanceAvailable + " (entspricht " + kit.wallet().getBalance(Wallet.BalanceType.AVAILABLE).multiply(1000).toPlainString() + " mBTC)");
        System.out.println("Transactionen Pending       : " + kit.wallet().getTransactionPool(WalletTransaction.Pool.PENDING).size());
        System.out.println("Transactions Unspent        : " + kit.wallet().getTransactionPool(WalletTransaction.Pool.UNSPENT).size());
        System.out.println("Transactions Spent          : " + kit.wallet().getTransactionPool(WalletTransaction.Pool.SPENT).size());
        System.out.println("Transactions Dead           : " + kit.wallet().getTransactionPool(WalletTransaction.Pool.DEAD).size());

        // empfangsadresse = recipient address
        String recipient = "2MyEHka6Whdt87LyK3VuUGGfuWkNTtSD3Hj"; // block.io account
        Address recipientAddress = Address.fromString(netParams, recipient.trim());
        System.out.println("Empfaengeradresse           : " + recipientAddress);
        // tell peer to send amountToSend to recipientAddress
        Coin btcToSend = Coin.parseCoin("2.001");
        System.out.println("Transaktionsbetrag: " + btcToSend.toFriendlyString()
                + " (entspricht " + btcToSend.multiply(1000).toPlainString() + " mBTC)"
                + " an diese Adresse: " + recipient);
        SendRequest request = SendRequest.to(recipientAddress, btcToSend);
        //request.recipientsPayFees = true; // recipient pays the fees means the fee is subtracted from amount that goes to the recipient
        Wallet.SendResult sendresult = null;
        // final confirmation
        String message = "Sollen " + btcToSend.toFriendlyString() + " (entspricht " + btcToSend.multiply(1000).toPlainString() + " mBTC)\n"
                + "an die Adresse " + recipient
                + "\nueberwiesen werden ?";
        if (JOptionPane.showConfirmDialog(null, message, "Transaktionsbestaetigung",
                JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            // yes option
        } else {
            // no option
            kit.stopAsync(); // wait for completion
            System.out.println("\nEndezeit des WalletAppKit   : " + getActualDate());
            JOptionPane.showMessageDialog(null,
                    "Das Programm wird beendet sobald der OK-Button betaetigt wird",
                    "Programmende",
                    JOptionPane.WARNING_MESSAGE);
            outputFrameErrors.dispose();
            outputFrameOutput.dispose();
            return;
        }
        try {
            sendresult = kit.wallet().sendCoins(request);
            System.out.println("\nDie Transaktion wurde ausgefuehrt !");
            System.out.println("Sendedatum: (request update time)    : " + request.tx.getUpdateTime());
            System.out.println("Transaktions-ID (sendresult TxId)    : " + sendresult.tx.getTxId());
            Coin txValue = request.tx.getValue(kit.wallet());
            System.out.println("Transaktionsbetrag                   : " + txValue.toFriendlyString() + " (entspricht " + txValue.multiply(1000).toPlainString() + " mBTC)");
            Coin fee = sendresult.broadcast.broadcast().get().getFee();
            System.out.println("Gebuehr (sendresult broadcast fee)   : " + fee.toFriendlyString() + " (entspricht " + fee.multiply(1000).toPlainString() + " mBTC)");
        } catch (InsufficientMoneyException e) {
            System.out.println("\n* * * Fehler: Ungenuegendes Guthaben im Wallet, die Transaktion wurde nicht ausgefuehrt * * *");
            JOptionPane.showMessageDialog(null,
                    "* * * Fehler: Ungenuegendes Guthaben im Wallet, die Transaktion wurde nicht ausgefuehrt * * *",
                    "Fehler",
                    JOptionPane.WARNING_MESSAGE);
            e.printStackTrace();
        }
        kit.stopAsync(); // wait for completion
        System.out.println("\nEndezeit des WalletAppKit   : " + getActualDate());
        JOptionPane.showMessageDialog(null,
                "Das Programm wird beendet sobald der OK-Button betaetigt wird",
                "Programmende",
                JOptionPane.WARNING_MESSAGE);
        outputFrameErrors.dispose();
        outputFrameOutput.dispose();
    }

    private static String getActualDateReverse() {
        // provides the actual date and time in this format yyyy-MM-dd_HH-mm-ss e.g. 2020-03-16_10-27-15
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss");
        LocalDateTime today = LocalDateTime.now();
        return formatter.format(today);
    }

    private static String getActualDate() {
        // provides the actual date and time in this format dd.MM.yyyy HH:mm:ss e.g. 16.03.2020 10:27:15
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        LocalDateTime today = LocalDateTime.now();
        return formatter.format(today);
    }
}
