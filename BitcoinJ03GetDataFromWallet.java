/*
 * Herkunft/Origin: http://javacrypto.bplaced.net/
 * Programmierer/Programmer: Michael Fehr
 * Copyright/Copyright: Michael Fehr
 * Lizenttext/Licence: verschiedene Lizenzen / several licenses
 * getestet mit/tested with: Java Runtime Environment 11.0.5 x64
 * verwendete IDE/used IDE: intelliJ IDEA 2019.3.1
 * Datum/Date (dd.mm.jjjj): 12.03.2020
 * Funktion: 03 Liest Daten eines Wallets aus
 * Function: 03 Get data from a wallet
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
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.bitcoinj.wallet.Wallet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class BitcoinJ03GetDataFromWallet {

    public static void main(String[] args) throws IOException, UnreadableWalletException {
        System.out.println("BitcoinJ 03 Daten eines Wallets");
        System.out.println("Wir benutzen für unsere Versuche das Bitcoin Testnetz und nicht das Mainnetz");
        NetworkParameters netParams = TestNet3Params.get();
        //NetworkParameters netParams = MainNetParams.get();
        String filenameWallet = "bitcoinj02createwalletW.wallet";
        final File walletFile = new File(filenameWallet);
        Wallet wallet = null;
        if (walletFile.exists()) {
            System.out.println("Laden einer bestehenden Walletdatei - die Datei " + filenameWallet + " wird geladen");
            wallet = Wallet.loadFromFile(walletFile);
        } else {
            System.out.println("Die Datei " + filenameWallet + " ist nicht vorhanden und das Programm wird beendet");
            System.exit(0);
        }
        System.out.println("**********************************************************************************");
        System.out.println("Ausgabe von Daten des Wallets");
        System.out.println("\nAdresse für Zahlungseingänge: " + wallet.currentReceiveAddress());
        System.out.println("\nBalances (Guthaben)");
        System.out.println("Balance bei Abfrage getBalance                                        : " + wallet.getBalance());
        System.out.println("Balance bei Abfrage getBalance().toFriendlyString()                   : " + wallet.getBalance().toFriendlyString());
        // verschiedene BalanceTypes:
        System.out.println("Balance bei Abfrage getBalance(Wallet.BalanceType.AVAILABLE)          : " + wallet.getBalance(Wallet.BalanceType.AVAILABLE));
        System.out.println("Balance bei Abfrage getBalance(Wallet.BalanceType.AVAILABLE_SPENDABLE): " + wallet.getBalance(Wallet.BalanceType.AVAILABLE_SPENDABLE));
        System.out.println("Balance bei Abfrage getBalance(Wallet.BalanceType.ESTIMATED)          : " + wallet.getBalance(Wallet.BalanceType.ESTIMATED));
        System.out.println("Balance bei Abfrage getBalance(Wallet.BalanceType.ESTIMATED_SPENDABLE): " + wallet.getBalance(Wallet.BalanceType.ESTIMATED_SPENDABLE));
        // transaktionen
        System.out.println("\nTransaktionen im Wallet");
        ArrayList<Transaction> txList = new ArrayList(wallet.getTransactions(true));
        int txListSize = txList.size();
        System.out.println("Es befinden sich " + txListSize + " Transaktionen im Wallet");
        for (int txIterator = 0; txIterator < txListSize; txIterator++) {
            Transaction tx = txList.get(txIterator);
            System.out.println("---------------------------------------------------------------------------------");
            String txDirection = "";
            if (tx.getValue(wallet).isNegative()) txDirection = " Zahlungsausgang";
            else txDirection = " Zahlungseingang";
            System.out.println("Transaktion Nummer " + (txIterator + 1) + txDirection);
            System.out.println("Transaktions-Id: " + tx.getTxId());
            System.out.println("Transaktionsdatum                        : " + tx.getUpdateTime());
            System.out.println("Transaktionsbetrag (getValue)            : " + tx.getValue(wallet).toFriendlyString());
            try {
                System.out.println("Transaktionsgebuehr (getFee)             : -" + tx.getFee().toFriendlyString());
                System.out.println("Transaktionsbetrag  (getValueSendFromMe) : -" + tx.getValueSentFromMe(wallet).toFriendlyString());
                System.out.println("Transaktionsbetrag  (getValueSendToMe)   :  " + tx.getValueSentToMe(wallet).toFriendlyString());
                // berechnung der netto-zahlung an den empfänger
                Coin txGetValue = tx.getValue(wallet); // ist negativ bei ausgehender zahlung
                Coin txGetFee = tx.getFee(); // ist positiv !
                Coin txEmpfaenger = txGetValue.add(txGetFee);
                System.out.println("Transaktionsbetrag an Empfaenger         : " + txEmpfaenger.toFriendlyString());
            } catch (NullPointerException e) {
                System.out.println("Transaktionsgebuehr (fee)                : " + "wegen Zahlungseingang keine Fee");
            }
            System.out.println("Grund der Transaktion (purpose)          : " + tx.getPurpose());
            System.out.println("Confidence (Bestaetigungen)              : " + tx.getConfidence());
            System.out.println("Confidence (Bestaetigungen) (Depth/Tiefe): " + tx.getConfidence().getDepthInBlocks());
            System.out.println("Confidence (Bestaetigungen) (Type)       : " + tx.getConfidence().getConfidenceType());
            try {
                System.out.println("Confidence (Bestaetigungen) (ChainHeight): " + tx.getConfidence().getAppearedAtChainHeight());
            } catch (IllegalStateException e) {
                System.out.println("Confidence (Bestaetigungen) (ChainHeight): " + "keine Bestaetigung da die Transaktion nicht im BUILDING-Typ ist");
            }
        }
        System.out.println("**********************************************************************************");
    }
}