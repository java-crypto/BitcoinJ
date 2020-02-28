/*
 * Herkunft/Origin: http://javacrypto.bplaced.net/
 * Programmierer/Programmer: Michael Fehr
 * Copyright/Copyright: Michael Fehr & andere/others
 * Lizenttext/Licence: verschiedene Lizenzen / several licenses
 * https://github.com/java-crypto/Bitcoin/blob/master/Bitcoin%20Wallet%20Software%20Electrum%20Verification/LICENCE
 * getestet mit/tested with: Java Runtime Environment 11.0.5 x64
 * verwendete IDE/used IDE: intelliJ IDEA 2019.3.1
 * Datum/Date (dd.mm.jjjj): 28.02.2020
 * Funktion: 01 Erzeugt eine Bitcoin-Adresse aus einem öffentlichen Schlüssel
 * Function: 01 cretaes a bitcoin address out of a public key
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
 * Bouncy Castle: bcprov-jdk15on-164.jar, bcpg-jdk15on-164.jar
 * my Github-Repository: https://github.com/java-crypto/BitcoinJ
 * libs in my Github-Repo: https://github.com/java-crypto/BitcoinJ/tree/master/libs
 *
 */

import org.bitcoinj.core.Address;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.script.Script;

public class BitcoinJ01CreateAddress {

    public static void main(String[] args) throws Exception {
        System.out.println("BitcoinJ 01 Erzeugung einer Bitcoin Adresse aus einem Public Key");

        System.out.println("\nWir benutzen für unsere Versuche das Bitcoin Testnetz und nicht das Mainnetz");
        NetworkParameters netParams = TestNet3Params.get();
        //NetworkParameters netParams = MainNetParams.get();

        System.out.println("\nErzeugung eines Schlüsselpaares mit ECKey von bitcoinj");
        ECKey ecKey = new ECKey();

        System.out.println("Das ist unser neues Schlüsselpaar:");
        System.out.println("Privater Schlüssel WIF:" + ecKey.getPrivateKeyAsWiF(netParams));
        System.out.println("Öffentlicher Schlüssel:" + ecKey);

        System.out.println("\nDer öffentliche Schlüssel im WIF-Format ist die Adresse im Bitcoin (Test-) Netz");// get valid Bitcoin address from public key
        Address addressFromKey = Address.fromKey(netParams, ecKey, Script.ScriptType.P2PKH);
        System.out.println("Die Adresse lautet:" + addressFromKey);
    }
}
