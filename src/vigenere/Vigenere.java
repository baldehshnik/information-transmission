package vigenere;

import java.util.*;

public class Vigenere {

    private final int keyLength;
    private final char[] encryptedMessage;

    private final static int alphabetSize = 33;
    private final static List<Character> alphabet = new ArrayList<>(alphabetSize);

    public Vigenere(int keyLength, String encryptedMessage) {
        this.keyLength = keyLength;
        this.encryptedMessage = encryptedMessage.replaceAll("_", "а").toCharArray();
    }

    static {
        for (char character = 'А'; character <= 'а'; character++) alphabet.add(character);
    }

    public void run() {
        List<Map<Character, Integer>> mapList = new ArrayList<>(keyLength);
        for (int i = 0; i < keyLength; i++) {
            mapList.add(new HashMap<>(alphabetSize));
            Map<Character, Integer> currentMap = mapList.get(i);

            fillMapWithSymbols(currentMap);
            fillMapWithKeySymbols(currentMap, i);
        }

        char[] maxSymbols = getTheMostOccurringSymbols(mapList);
        String[] keys = showKeyVariables(maxSymbols);

        System.out.print("""
                \t\tВыберите пункт для продолжения
                1. Завершить программу (введите "end")
                2. Вывести расшифрованный текст по введенному ключу (введите ключ)
                3. Вывести все расшифрованные тексты по найденным ключам (введите "all")
                Выберите пункт для работы:\s""");

        Scanner scanner = new Scanner(System.in);
        String choice = scanner.nextLine();
        scanner.close();

        if (choice.equals("end")) {
            System.out.println("Завершение работы!");
        } else if (choice.equals("all")) {
            for (String key : keys) showTextByKey(key.replaceAll("_", "а"));
        } else if (choice.length() == keyLength) {
            showTextByKey(choice);
        } else {
            System.out.println("Неизвестный выбор. Завершение работы!");
        }
    }

    private void showTextByKey(String userKey) {
        String key = getFullKey(userKey);
        System.out.println("\n\t\t\t" + userKey);

        String decryptedMessage = changeWrongSymbol(decrypt(key.toCharArray(), encryptedMessage));
        System.out.println(decryptedMessage);
    }

    private String getFullKey(String shortKey) {
        StringBuilder key = new StringBuilder(shortKey);
        while (key.length() < encryptedMessage.length) {
            key.append(key);
        }

        if (key.length() != encryptedMessage.length) {
            while (key.length() != encryptedMessage.length) {
                key = new StringBuilder(key.substring(0, key.length() - 1));
            }
        }

        return key.toString();
    }

    private String changeWrongSymbol(String message) {
        String result = message;
        for (char symbol : message.toCharArray()) {
            if (symbol < 'а' || symbol > 'я') {
                result = message.replaceAll(String.valueOf(symbol), " ");
                break;
            }
        }

        return result;
    }

    private char[][] generateVigenereTable() {
        char[][] table = new char[alphabetSize][alphabetSize];
        for (int i = 0; i < alphabetSize; i++) {
            int symbolNumberToEncrypt = i;
            for (int j = 0; j < alphabetSize; j++) {
                if(symbolNumberToEncrypt == alphabetSize) symbolNumberToEncrypt = 0;

                table[i][j] = (char) (1072 + symbolNumberToEncrypt);
                symbolNumberToEncrypt++;
            }
        }

        return table;
    }

    private String decrypt(char[] key, char[] encryptedMessage) {
        char[] decryptedMessage = new char[encryptedMessage.length];
        int keySymbolToDecrypt, encryptedSymbolToDecrypt;

        char[][] table = generateVigenereTable();
        for (int i = 0; i < encryptedMessage.length; i++) {
            keySymbolToDecrypt = (int) key[i] - 1072;
            encryptedSymbolToDecrypt = (int) encryptedMessage[i] - 1072;
            if (keySymbolToDecrypt > encryptedSymbolToDecrypt) {
                decryptedMessage[i] = table[alphabetSize + (encryptedSymbolToDecrypt - keySymbolToDecrypt)][0];
            } else {
                decryptedMessage[i] = table[encryptedSymbolToDecrypt - keySymbolToDecrypt][0];
            }
        }

        return new String(decryptedMessage);
    }

    private void fillMapWithKeySymbols(Map<Character, Integer> map, int keyPosition) {
        for (int j = 0; j < encryptedMessage.length; j++) {
            if (j == keyPosition + keyLength) {
                map.put(encryptedMessage[j], map.get(encryptedMessage[j]) + 1);
                keyPosition += keyLength;
            }
        }
    }

    private void fillMapWithSymbols(Map<Character, Integer> map) {
        for (char symbol : alphabet) map.put(symbol, 0);
    }

    private char[] getTheMostOccurringSymbols(List<Map<Character, Integer>> mapList) {
        char[] maxSymbols = new char[keyLength];
        for (int i = 0; i < keyLength; i++) {
            maxSymbols[i] = alphabet.get(0);

            int max = 0;
            Map<Character, Integer> currentMap = mapList.get(i);
            for (char symbol : alphabet) {
                if (currentMap.get(symbol) > max) {
                    max = currentMap.get(symbol);
                    maxSymbols[i] = symbol;
                }
            }
        }

        return maxSymbols;
    }

    private String[] showKeyVariables(char[] maxSymbols) {
        String[] keys = new String[alphabetSize];

        System.out.println("Найденные ключи: ");
        for (int i = 0; i < alphabetSize; i++) {
            StringBuilder key = new StringBuilder();
            for (int j = 0; j < keyLength; j++) {
                int symbolNumber = Integer.valueOf(alphabet.get(i)) + maxSymbols[j] - maxSymbols[0];
                char symbol = (char) symbolNumber;
                if (symbol < alphabet.get(0)) {
                    int difference = (int) alphabet.get(0) - symbolNumber;
                    symbol = (char) (alphabet.get(alphabetSize - 1) - difference + 1);
                } else if (symbol > alphabet.get(alphabetSize - 1)) {
                    int difference = symbolNumber - (int) alphabet.get(alphabetSize - 1);
                    symbol = (char) (alphabet.get(0) + difference - 1);
                }

                key.append(symbol);
            }

            key = new StringBuilder(key.toString().replaceAll("а", "_"));
            keys[i] = key.toString();
            System.out.println(i + ". " + key);
        }

        return keys;
    }
}
