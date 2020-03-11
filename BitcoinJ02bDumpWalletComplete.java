/*
 * Herkunft/Origin: http://javacrypto.bplaced.net/
 * Programmierer/Programmer: Michael Fehr
 * Copyright/Copyright: Michael Fehr & andere/others
 * Lizenttext/Licence: verschiedene Lizenzen / several licenses
 * https://github.com/java-crypto/Bitcoin/blob/master/Bitcoin%20Wallet%20Software%20Electrum%20Verification/LICENCE
 * getestet mit/tested with: Java Runtime Environment 11.0.5 x64
 * verwendete IDE/used IDE: intelliJ IDEA 2019.3.1
 * Datum/Date (dd.mm.jjjj): 11.03.2020
 * Funktion: 02b Erzeugt den kompletten Dump eines Wallets
 * Function: 02b creates the complete dump of a wallet
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
 * libs in my Github-Repo: https://github.com/java-crypto/BitcoinJ/tree/master/libs
 *
 */

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.bitcoinj.wallet.Wallet;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class BitcoinJ02bDumpWalletComplete {

    public static void main(String[] args) throws IOException, UnreadableWalletException {
        System.out.println("BitcoinJ 02b kompletter DUMP eines Wallets");
        System.out.println("\n* * * Achtung: dieser Dump enthält private Schlüssel und den Seed * * *");

        System.out.println("\nWir benutzen für unsere Versuche das Bitcoin Testnetz und nicht das Mainnetz");
        NetworkParameters netParams = TestNet3Params.get();
        //NetworkParameters netParams = MainNetParams.get();

        System.out.println("\nLaden einer bestehenden Walletdatei");
        String filenameWallet = "bitcoinj02createwallet.wallet";

        final File walletFile = new File(filenameWallet);
        Wallet wallet = null;
        if (walletFile.exists()) {
            System.out.println("Die Datei " + filenameWallet + " wird geladen");
            wallet = Wallet.loadFromFile(walletFile);
        } else {
            System.out.println("Die Datei " + filenameWallet + " ist nicht vorhanden und das Programm wird beendet");
            System.exit(0);
        }

        System.out.println("**********************************************************************************");
        System.out.println("Ausgabe des kompletten Wallets\n");
        System.out.println(wallet.toString());
        //System.out.println(wallet.toString(true,true,null,  true,true,null));
        System.out.println("Ausgabe der importierten Schlüssel:");
        List<ECKey> importedKeys = wallet.getImportedKeys();
        List<ECKey> importedPrivateKeys = wallet.getImportedKeys();
        System.out.println("Es wurden " + importedKeys.size() +" Schlüssel importiert");
        for (int i = 0; i < importedKeys.size(); i++) {
            System.out.println("öffentlicher Schlüssel: " + (i+1) + ": " + importedKeys.get(i));
            System.out.println("privater Schlüssel WIF: " + (i+1) + ": " + importedKeys.get(i).getPrivateKeyAsWiF(netParams));
        }
        System.out.println("\nAdresse für Zahlungseingänge: " + wallet.currentReceiveAddress());
    }
}