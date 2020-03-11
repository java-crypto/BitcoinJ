/*
 * Herkunft/Origin: http://javacrypto.bplaced.net/
 * Programmierer/Programmer: Michael Fehr
 * Copyright/Copyright: Michael Fehr & andere/others
 * Lizenttext/Licence: verschiedene Lizenzen / several licenses
 * getestet mit/tested with: Java Runtime Environment 11.0.5 x64
 * verwendete IDE/used IDE: intelliJ IDEA 2019.3.1
 * Datum/Date (dd.mm.jjjj): 11.03.2020
 * Funktion: 02 Erzeugt ein Wallet
 * Function: 02 creates a wallet
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
import org.bitcoinj.script.Script;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.bitcoinj.wallet.Wallet;

import java.io.File;
import java.io.IOException;

public class BitcoinJ02CreateWallet {

    public static void main(String[] args) throws IOException, UnreadableWalletException {
        System.out.println("BitcoinJ 02 Erzeugung eines Wallets");

        System.out.println("\nWir benutzen für unsere Versuche das Bitcoin Testnetz und nicht das Mainnetz");
        NetworkParameters netParams = TestNet3Params.get();
        //NetworkParameters netParams = MainNetParams.get();

        System.out.println("\nLaden einer bestehenden Walletdatei bzw. Erstellung einer neuen Walletdatei");
        String filenameWallet = "bitcoinj02createwallet.wallet";

        final File walletFile = new File(filenameWallet);
        Wallet wallet = null;
        if (walletFile.exists()) {
            System.out.println("Die Datei " + filenameWallet + " wird geladen");
            wallet = Wallet.loadFromFile(walletFile);
        } else {
            System.out.println("Die Datei " + filenameWallet + " wird neu erzeugt");
            wallet = Wallet.createDeterministic(netParams, Script.ScriptType.P2PKH);
            System.out.println("\nDas neue Wallet ist ein Hierarchisch deterministisches (HD) Wallet.");
            //wallet = Wallet.createBasic(netParams);
            //System.out.println("\nDas neue Wallet ist ein einfaches Wallet (kein HD Wallet)");
        }

        System.out.println("\nZusätzlich zu den bestehenden Adressen importieren wir 5 zusätzliche Schlüsselpaare");
        ECKey ecKeyFirst = null; // den ersten erzeugten eckey speichern wir zu vergleichszwecken ab
        try {
            // 5 eigene keys
            for (int i = 0; i < 5; i++) {
                ECKey ecKey = new ECKey();
                if (i == 0) { // den ersten erzeugten eckey speichern wir zu vergleichszwecken ab
                    ecKeyFirst = ecKey;
                }
                System.out.println("Neuer ECKey Nummer " + (i + 1) + ": " + ecKey);
                //System.out.println("Neuer ECKey Nummer " + (i+1) + ":" + bytesToHex(eckey.getPubKey())); // added
                wallet.importKey(ecKey); // changed
                // speicherung des wallets
                wallet.saveToFile(walletFile);
            }
        } catch (IOException e) {
            System.out.println("Die Erzeugung der Walletdatei " + filenameWallet + " war nicht möglich:\n" + e);
        }

        System.out.println("\nÜberprüfung das der erste neu erzeugte Schlüssel im Wallet gespeichert ist");
        // fetch the first key in the wallet directly from the keychain ArrayList
        ECKey walletFirstKey = wallet.getImportedKeys().get(0);
        System.out.println("Erster Schlüssel im Wallet   : " + walletFirstKey);
        System.out.println("Schlüssel aus der Generierung: " + ecKeyFirst);
        if (wallet.isPubKeyHashMine(ecKeyFirst.getPubKeyHash(), null)) {
            System.out.println(" * Der Schlüssel ist im Wallet vorhanden *");
        } else {
            System.out.println(" * Der Schlüssel ist NICHT im Wallet vorhanden");
        }
        System.out.println("\n****************************************************************************************************************************");
        System.out.println("Ausgabe des kompletten Wallets ohne private Schlüssel");
        System.out.println(wallet);
    }
}