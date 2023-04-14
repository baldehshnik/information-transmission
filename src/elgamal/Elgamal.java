package elgamal;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

/**
 * Класс с логикой алгоритма Эль-Гамаля.
 * */
public class Elgamal {

    /**
     * Битовый размер для определения переменной "p".
     * */
    private static final Integer bitLength = 128;

    /**
     * Большое простое число. Определяет размер множества.
     * */
    private BigInteger p;

    /**
     * Случайное число, удовлетворяющее условию - "g > 1".
     * */
    private BigInteger g;

    /**
     * Открытый ключ.
     * */
    private BigInteger y;

    /**
     * Секретный ключ.
     * */
    private BigInteger secretKey;


    /**
     * Число, необходимое для вычисления второй части зашифрованного сообщения.
     * */
    private BigInteger r;

    /**
     * Первая часть зашифрованного сообщения.
     * */
    private BigInteger valuePart1;

    /**
     * Рандомное число, необходимое для вычисления размера множества (переменной "p").
     * */
    private final Random randomNumberOfFirstPerson;

    /**
     * Конструктор класса.
     * */
    public Elgamal() {
        randomNumberOfFirstPerson = new SecureRandom();
    }

    /**
     * Точка входа в алгоритм Аль-Гамаля.
     * */
    public void run(BigInteger secretKeyValue) {
        secretKey = secretKeyValue;

        System.out.println("\nСекретный ключ: " + secretKey);
        System.out.println("Длина бит для создания ключей: " + bitLength + "\n");

        System.out.println("Происходит вычисление значений для шифрования:");
        calculatePublicKey(secretKey);
    }

    /**
     * Функция для вычисления публичного ключи для шифрования данных.
     * */
    public void calculatePublicKey(BigInteger secretKey) {
        p = BigInteger.probablePrime(bitLength, randomNumberOfFirstPerson);
        g = new BigInteger(String.valueOf((new Random()).nextInt(98) + 2));
        y = g.modPow(secretKey, p);

        System.out.println("\tp = " + p);
        System.out.println("\tg = " + g);
        System.out.println("\ty = " + y);
    }

    /**
     * Функция для шифрования переданного сообщения.
     * */
    public void encrypt(BigInteger message) {
        BigInteger k = new BigInteger(bitLength, randomNumberOfFirstPerson);
        r = g.modPow(k, p);
        valuePart1 = message.multiply(y.modPow(k, p));

        System.out.println("\nСообщение для шифрования: " + message);
        System.out.println("Рандомное число, удовлетворяющее условию \"1 < k < (p − 1)\": " + k);
        System.out.println("Следующая пара чисел является шифротекстом: (" + r + "; " + valuePart1 + ")");
    }

    /**
     * Функция для расшифровки ранее зашифрованного значения.
     * */
    public BigInteger decrypt() {
        BigInteger valuePart2 = r.modPow(secretKey, p);
        BigInteger decryptedMessage = valuePart2.modInverse(p).multiply(valuePart1).mod(p);

        System.out.println("Обе части зашифрованного сообщения: (" + valuePart1 + "; " + valuePart2 + ")");
        System.out.println("\nРасшифрованное сообщение: " + decryptedMessage);

        return decryptedMessage;
    }
}