/*
 * Herkunft/Origin: http://javacrypto.bplaced.net/
 * Programmierer/Programmer: Michael Fehr
 * Copyright/Copyright: Michael Fehr
 * Lizenttext/Licence: verschiedene Lizenzen / several licenses
 * getestet mit/tested with: Java Runtime Environment 11.0.5 x64
 * verwendete IDE/used IDE: intelliJ IDEA 2019.3.1
 * Datum/Date (dd.mm.jjjj): 15.03.2020
 * Funktion: 05 wandelt das Wallet in ein Watching Wallet um
 * Function: 05 converts a wallet to a watching wallet
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
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.kits.WalletAppKit;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.wallet.UnreadableWalletException;
import org.bitcoinj.wallet.Wallet;

import java.io.File;
import java.io.IOException;

public class BitcoinJ05ConvertWatchingWalletEn {

    static WalletAppKit kit;

    public static void main(String[] args) throws UnreadableWalletException, IOException {
        System.out.println("BitcoinJ 05 Create a watching wallet from a regular one");
        String filenameFullWallet = "bitcoinj02createwallet.wallet";
        String filenameWatchingWallet = "bitcoinj02createwalletW.wallet";
        // check for existing filenameWatchingWallet files
        // files are .wallet and .spvchain
        boolean foundOldWatchingFiles = false;
        if (new File(filenameWatchingWallet).exists()) {
            System.out.println("Existing file needs to get deleted manually: " + filenameWatchingWallet);
            foundOldWatchingFiles = true;
        }
        if (new File(filenameWatchingWallet.replace(".wallet", ".spvchain")).exists()) {
            System.out.println("Existing file needs to get deleted manually: " + filenameWatchingWallet.replace(".wallet", ".spvchain"));
            foundOldWatchingFiles = true;
        }
        if (foundOldWatchingFiles == true) {
            System.out.println("Please delete the files above manually and restart the program. The program ends now.");
            System.exit(0);
        }
        System.out.println("\nWe are using the Bitcoin Testnet and not the Mainnet");
        NetworkParameters netParams = TestNet3Params.get();
        //NetworkParameters netParams = MainNetParams.get();
        System.out.println("\nLoad the Full wallet: " + filenameFullWallet);
        final File fullWalletFile = new File(filenameFullWallet);
        Wallet fullWallet = null;
        if (fullWalletFile.exists()) {
            System.out.println("Loading successfull : " + filenameFullWallet);
            fullWallet = Wallet.loadFromFile(fullWalletFile);
        } else {
            System.out.println("File not found: " + filenameFullWallet);
            System.exit(0);
        }
        System.out.println("**********************************************************************************");
        System.out.println("Dump the full wallet: " + filenameFullWallet);
        System.out.println(fullWallet.toString());
        // create the watching wallet
        System.out.println("We created the watching wallet: " + filenameWatchingWallet);
        DeterministicKey fullWatchingKey = fullWallet.getWatchingKey();
        System.out.println("Determistic watchingKey serializePubB58 (full)    : " + fullWatchingKey.serializePubB58(netParams));
        long fullKeyBirthday = fullWatchingKey.getCreationTimeSeconds();
        DeterministicKey watchingWatchingKey = DeterministicKey.deserializeB58(fullWatchingKey.serializePubB58(netParams), netParams);
        System.out.println("Determistic watchingKey serializePubB58 (watching): " + watchingWatchingKey.serializePubB58(netParams));
        Wallet watchingWallet = Wallet.fromWatchingKeyB58(netParams, watchingWatchingKey.serializePubB58(netParams), fullKeyBirthday);
        System.out.println("**********************************************************************************");
        System.out.println("Dump the watching wallet before reorganisation " + filenameWatchingWallet);
        System.out.println(watchingWallet.toString());
        watchingWallet.saveToFile(new File(filenameWatchingWallet));
        watchingWallet.reset(); // force to reimport the spv-file
        // need an online access to reorg the spv-file and re-import the transactions
        System.out.println("Load the wallet in WalletAppKit: " + filenameWatchingWallet);
        System.out.println("I'm going online to reimport the transactions, please be patient (maybe 1 to 2 minutes)...");
        kit = new WalletAppKit(netParams, new File("."), filenameWatchingWallet.replace(".wallet", "")) {
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
        System.out.println("Dump the watching wallet after reorganiation: " + filenameWatchingWallet);
        System.out.println(kit.wallet().toString());
        System.out.println("Converting was successfull");
    }
}
