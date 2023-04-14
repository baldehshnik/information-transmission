package elgamal;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Введите сообщение для зашифровки: ");
        char[] charArray = scanner.nextLine().toCharArray();

        System.out.print("Введите секретный ключ: ");
        BigInteger key = scanner.nextBigInteger();

        System.out.print("\n\nПроизвести шифрование сообщения? (y | n) ");
        String answer = scanner.next();
        if (answer.equals("n")) return;

        List<Elgamal> elgamalList = encrypt(charArray, key);
        System.out.println("\n\nСообщение успешно зашифровано!\n\n");

        System.out.print("Произвести расшифровку сообщения? (y | n) ");
        answer = scanner.next();
        if (answer.equals("n")) return;

        List<String> messageNumbersStrings = decrypt(charArray.length, elgamalList);

        System.out.print("\n\nРасшифрованное сообщение: \"");
        for (String s : messageNumbersStrings) System.out.print((char) Integer.parseInt(s));
        System.out.println("\"");

        scanner.close();
    }

    private static List<Elgamal> encrypt(char[] message, BigInteger secretKey) {
        List<Elgamal> elgamalList = new ArrayList<>(message.length);
        for (char c : message) {
            Elgamal elgamal = new Elgamal();
            elgamal.run(secretKey);
            elgamal.encrypt(BigInteger.valueOf(c));
            elgamalList.add(elgamal);
        }

        return elgamalList;
    }

    private static List<String> decrypt(Integer length, List<Elgamal> elgamalList) {
        List<String> messageNumbersStrings = new ArrayList<>(length);
        for (Elgamal elgamal : elgamalList) messageNumbersStrings.add(elgamal.decrypt().toString(10));

        return messageNumbersStrings;
    }
}
