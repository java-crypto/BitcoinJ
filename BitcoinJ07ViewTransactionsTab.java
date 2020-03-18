/*
 * Herkunft/Origin: http://javacrypto.bplaced.net/
 * Programmierer/Programmer: Michael Fehr
 * Copyright/Copyright: Michael Fehr
 * Lizenttext/Licence: verschiedene Lizenzen / several licenses
 * getestet mit/tested with: Java Runtime Environment 11.0.5 x64
 * verwendete IDE/used IDE: intelliJ IDEA 2019.3.1
 * Datum/Date (dd.mm.jjjj): 18.03.2020
 * Funktion: 07 Zeigt die Transaktionen eines Wallets in tabellarischer Form an
 * Function: 07 shows transaction tabbed data from our wallet
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

import org.bitcoinj.core.Coin;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.RegTestParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.utils.BriefLogFormatter;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.bitcoinj.wallet.Wallet;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

public class BitcoinJ07ViewTransactionsTab {

    public static void main(String[] args) throws IOException, UnreadableWalletException {
        // reirect the console to two windows
        RedirectedFrame outputFrameErrors = new RedirectedFrame("Logfileausgaben Fenster", true, false, true, "BitcoinJ07TransactionsTab_Logfile_" + getActualDateReverse() + ".txt", 700, 600, JFrame.DO_NOTHING_ON_CLOSE);
        RedirectedFrame outputFrameOutput = new RedirectedFrame("Programmausgaben Fenster", false, true, true, "BitcoinJ07TransactionsTab_Output_" + getActualDateReverse() + ".txt", 700, 600, JFrame.DO_NOTHING_ON_CLOSE);
        System.out.println("BitcoinJ 07 Zeige die Transaktionen eines Wallets tabellarisch");

        NetworkParameters netParams = TestNet3Params.get(); // preset
        String filenameWallet = "bitcoinj02createwallet";
        // choose network type (TEST or REG)
        String networkType = "TEST";
        //String networkType = "REG";
        switch (networkType) {
            case "TEST": {
                netParams = TestNet3Params.get();
                break;
            }
            case "REG": {
                netParams = RegTestParams.get();
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
        System.out.println("Ausgabe der Transaktionen des Wallets tabellearisch");
        System.out.println("**********************************************************************************************************************************************");
        System.out.println("Nr. Richtung        Datum + Uhrzeit       Tx Betrag in BTC    Fee in BTC Dep Transaction ID");
        Wallet wallet = kit.wallet();
        // transaktionen
        ArrayList<Transaction> txList = new ArrayList(wallet.getTransactions(true));
        txList.sort((tx1, tx2) -> {
            return tx2.getUpdateTime().compareTo(tx1.getUpdateTime());
        });
        int txListSize = txList.size();
        //System.out.println("Es befinden sich " + txListSize + " Transaktionen im Wallet, die aktuellste steht oben");
        String line = "";
        for (int txIterator = 0; txIterator < txListSize; txIterator++) {
            line = "";
            Transaction tx = txList.get(txIterator);
            //System.out.println("---------------------------------------------------------------------------------");
            String txDirection = "";
            if (tx.getValue(wallet).isNegative()) txDirection = " Zahlungsausgang";
            else txDirection = " Zahlungseingang";
            //String.format("%1$" + length + "s", inputString)
            line = line + String.format("%1$" + 3 + "s", (txListSize - (txIterator))) + txDirection + " ";

            line = line + formatDate(tx.getUpdateTime()) + " ";
            // values
            Coin txGetValue = tx.getValue(wallet);
            Coin txGetFee = Coin.valueOf(0);
            Coin txNetto = Coin.valueOf(0);
            //System.out.println("Transaktion Nummer " + (txListSize - (txIterator)) + txDirection);
            //System.out.println("Transaktions-Id: " + tx.getTxId());
            //System.out.println("Transaktionsdatum                        : " + tx.getUpdateTime());
            //System.out.println("Transaktionsbetrag (getValue)            : " + tx.getValue(wallet).toFriendlyString());
            Coin txEmpfaenger = Coin.valueOf(0);
            try {
                //System.out.println("Transaktionsgebuehr (getFee)             : -" + tx.getFee().toFriendlyString());
                //System.out.println("Transaktionsbetrag  (getValueSendFromMe) : -" + tx.getValueSentFromMe(wallet).toFriendlyString());
                //System.out.println("Transaktionsbetrag  (getValueSendToMe)   :  " + tx.getValueSentToMe(wallet).toFriendlyString());
                // berechnung der netto-zahlung an den empfänger
                txGetValue = tx.getValue(wallet); // ist negativ bei ausgehender zahlung
                txGetFee = tx.getFee(); // ist positiv !
                txEmpfaenger = txGetValue.add(txGetFee);
                txNetto = txGetValue.add(txGetFee);
                //System.out.println("Transaktionsbetrag an Empfaenger         : " + txEmpfaenger.toFriendlyString());
            } catch (NullPointerException e) {
                //System.out.println("Transaktionsgebuehr (fee)                : " + "wegen Zahlungseingang keine Fee");
            }
            line = line + String.format("%1$" + 18 + "s", txNetto.toFriendlyString());
            try {
                line = line + String.format("%1$" + 15 + "s", txGetFee.toFriendlyString());} catch (NullPointerException e) {
                line = line + String.format("%1$" + 15 + "s", Coin.ZERO.toFriendlyString());
            }

            //line = line + " " + tx.getTxId();
            //line = line + " " + String.format("%1$" + 14 + "s", txDepth) + " " + tx.getTxId();
            //System.out.println("#" + line);

            int txDepth = 0;
            //System.out.println("Grund der Transaktion (purpose)          : " + tx.getPurpose());
            //System.out.println("Confidence (Bestaetigungen)              : " + tx.getConfidence());
            //System.out.println("Confidence (Bestaetigungen) (Depth/Tiefe): " + tx.getConfidence().getDepthInBlocks());
            //System.out.println("Confidence (Bestaetigungen) (Type)       : " + tx.getConfidence().getConfidenceType());
            try {
                txDepth = tx.getConfidence().getDepthInBlocks();
                //System.out.println("Confidence (Bestaetigungen) (ChainHeight): " + tx.getConfidence().getAppearedAtChainHeight());
            } catch (IllegalStateException e) {
                //System.out.println("Confidence (Bestaetigungen) (ChainHeight): " + "keine Bestaetigung da die Transaktion nicht im BUILDING-Typ ist");
            }
            line = line + String.format("%1$" + 4 + "s", txDepth) + " " + tx.getTxId();
            System.out.println(line);
        }
        System.out.println("**********************************************************************************************************************************************");
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
        // provides the actual date and time in this format dd-MM-yyyy_HH-mm-ss e.g. 16.03.2020 10:27:15
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
        LocalDateTime today = LocalDateTime.now();
        return formatter.format(today);
    }

    private static String formatDate(Date date) {
        // provides the date and time in this format dd-MM-yyyy_HH-mm-ss e.g. 16-03-2020_10-27-15
        String pattern = "dd.MM.yyyy HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        return simpleDateFormat.format(date);
    }
}