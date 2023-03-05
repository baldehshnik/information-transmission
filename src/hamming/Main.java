package hamming;

import java.util.Scanner;

/**
 * Класс инициализирующий работу с программой.
 * */
public class Main {

    /**
     * Точка входа в программу.
     * Считывает данные с консоли.
     * Запускает алгоритм Хэмминга.
     * */
    public static void main(String[] args) {
        // Создаем объект для считывания данных с консоли
        Scanner scanner = new Scanner(System.in);

        // Выводим подсказку о вводимых данных
        System.out.print("Введите строку для обработки: ");

        // Считываем данные
        String string = scanner.nextLine();

        // Создаем объект для работы с алгоритмом Хэмминга
        Hamming hamming = new Hamming(string);

        // Запускаем алгоритм Хэмминга
        hamming.run();

        // Закрываем поток считывания данных
        scanner.close();
    }
}