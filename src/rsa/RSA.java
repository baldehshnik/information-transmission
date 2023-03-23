package rsa;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RSA {

    private final static int alphabetSize = 33;
    private final static List<Character> alphabet = new ArrayList<>(alphabetSize);

    private final static int error = -1;

    private final int secondNumberOfKeys;
    private final int eulerFunction;
    private final char[] message;

    private int firstNumberOfPublicKey;
    private int firstNumberOfPrivateKey;

    private String encryptedMessage;
    private String decryptedMessage;

    static {
        for (char character = 'Я'; character <= 'я'; character++) alphabet.add(character);
    }

    public RSA (final int firstNumber, final int secondNumber, final String message) {
        this.message = message.replaceAll(" ", "Я").toCharArray();
        secondNumberOfKeys = firstNumber * secondNumber;
        eulerFunction = (firstNumber - 1) * (secondNumber - 1);
    }

    public void run() {
        firstNumberOfPublicKey = getFirstNumberOfPublicKey();
        if (firstNumberOfPublicKey == error) {
            System.out.println("First number not found!");
            return;
        }

        firstNumberOfPrivateKey = getFirstNumberOfPrivateKey();
        if (firstNumberOfPrivateKey == error) {
            System.out.println("Second number not found!");
            return;
        }

        System.out.println("Message - " + Arrays.toString(message).replaceAll("Я", " "));

        encryptedMessage = encrypt();
        System.out.println("Encrypted message - " + encryptedMessage);

        String decryptedNumbers = decrypt(encryptedMessage);
        setDecryptedMessage(decryptedNumbers);

        System.out.println("Decrypted message - " + decryptedMessage.replaceAll("Я", " "));
    }

    private void setDecryptedMessage(String decryptedNumbers) {
        StringBuilder decryptedBuilder = new StringBuilder();
        for (String n : decryptedNumbers.split(" ")) {
            int number = Integer.parseInt(n);
            decryptedBuilder.append(alphabet.get(number - 1));
        }

        decryptedMessage = decryptedBuilder.toString();
    }

    private String decrypt(String encryptedMessage) {
        StringBuilder decrypted = new StringBuilder();
        for (String currentString : encryptedMessage.split(" ")) {
            BigInteger bigValue = BigInteger.valueOf(Integer.parseInt(currentString));
            BigInteger result = bigValue.pow(firstNumberOfPrivateKey).mod(BigInteger.valueOf(secondNumberOfKeys));

            decrypted.append(result);
            decrypted.append(" ");
        }

        return decrypted.deleteCharAt(decrypted.length() - 1).toString();
    }

    private String encrypt() {
        StringBuilder encryptedMessage = new StringBuilder();
        for (char currentChar : message) {
            int toEncrypt = alphabet.indexOf(currentChar) + 1;

            BigInteger bigValue = BigInteger.valueOf(toEncrypt);
            BigInteger result = bigValue.pow(firstNumberOfPublicKey).mod(BigInteger.valueOf(secondNumberOfKeys));

            encryptedMessage.append(result);
            encryptedMessage.append(" ");
        }

        return encryptedMessage.deleteCharAt(encryptedMessage.length() - 1).toString();
    }

    private int getFirstNumberOfPrivateKey() {
        for (int i = 1; i < Integer.MAX_VALUE; i++) {
            int checkValue = (firstNumberOfPublicKey * i) % eulerFunction;
            if (checkValue == 1 && i != firstNumberOfPublicKey) return i;
        }

        return error;
    }

    private int getFirstNumberOfPublicKey() {
        List<Integer> naturalNumbers = new ArrayList<>();
        for (int i = 3; i < eulerFunction; i += 2) {
            double s = Math.sqrt(i);
            boolean result = true;
            for (int j = 2; j <= s; j++) {
                if (i % j == 0) {
                    result = false;
                    break;
                }
            }

            if (result && (eulerFunction % i != 0)) naturalNumbers.add(i);
        }

        if (naturalNumbers.size() == 0) return error;
        return naturalNumbers.get(0);
    }
}
