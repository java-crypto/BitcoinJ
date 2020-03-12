/*
 * Herkunft/Origin: http://javacrypto.bplaced.net/
 * Programmierer/Programmer: Michael Fehr
 * Copyright/Copyright: Michael Fehr
 * Lizenttext/Licence: verschiedene Lizenzen / several licenses
 * https://github.com/java-crypto/Bitcoin/blob/master/Bitcoin%20Wallet%20Software%20Electrum%20Verification/LICENCE
 * getestet mit/tested with: Java Runtime Environment 11.0.5 x64
 * verwendete IDE/used IDE: intelliJ IDEA 2019.3.1
 * Datum/Date (dd.mm.jjjj): 12.03.2020
 * Funktion: 04 verbindet das Wallet mit der Online-Blockchain
 * Function: 04 connects the wallet with the online blockchain
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

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.wallet.UnreadableWalletException;

import java.io.File;
import java.io.IOException;

public class BitcoinJ04WalletOnline {

    static WalletAppKit kit;

    public static void main(String[] args) throws UnreadableWalletException, IOException {
        System.out.println("BitcoinJ 04 Verbindung des Wallets mit der Online-Blockchain");
        NetworkParameters netParams = TestNet3Params.get();
        //NetworkParameters netParams = MainNetParams.get();
        String filenameWallet = "bitcoinj02createwalletW.wallet";
        final File walletFile = new File(filenameWallet);
        if (walletFile.exists()) {
            System.out.println("Laden einer bestehenden Walletdatei in das WalletAppKit - die Datei " + filenameWallet + " wird geladen");
        } else {
            System.out.println("Die Datei " + filenameWallet + " ist nicht vorhanden und das Programm wird beendet");
            System.exit(0);
        }
        kit = new WalletAppKit(netParams, new File("."), filenameWallet.replace(".wallet", "")) {
            @Override
            protected void onSetupCompleted() {
                if (wallet().getKeyChainGroupSize() < 1) {
                    wallet().importKey(new ECKey());
                }
            }
        };
        kit.startAsync(); // start the communication
        kit.awaitRunning(); // wait for completion
        System.out.println("**********************************************************************************");
        System.out.println("Adresse für Zahlungseingänge: " + kit.wallet().currentReceiveAddress() + "\n");
        System.out.println("Ausgabe des kompletten Wallets " + filenameWallet + "\n");
        System.out.println(kit.wallet().toString());
        kit.stopAsync(); // wait for completion
    }
}
