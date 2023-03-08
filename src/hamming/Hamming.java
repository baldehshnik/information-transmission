package hamming;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Класс реализует алгоритм Хэмминга.
 * Позволяет:
 *      1. Передать в него текстовое значение
 *      2. Получить переданное значение в виде последовательности бит
 *      3. Установить в эту последовательность бит ошибку
 *      4. Исправить ошибку, если она существует
 * */
class Hamming {

    /**
     * Переменная для назначения ошибки.
     * */
    private final static int error = -1;

    /**
     * Переменная для установления временного значения контрольных бит.
     * */
    private final static int temporaryValue = 2;

    /**
     * Переменная, хранящая строку обработки.
     * */
    private final String line;

    /**
     * Переменная для хранения исходных бит сообщения.
     * */
    private byte[] bits;

    /**
     * Конструктор класса.
     * */
    public Hamming(String line) {
        // Назначаем переменной класса "line" переданное в конструктор значение
        this.line = line;
    }

    /**
     * Функция для запуска работы.
     * Получет последоваетельность битов из введенной строки.
     * Вызывает функцию для генерации последовательности битов с учетом контрольных битов.
     * Вызывает функцию запроса у пользователя ошибки.
     * Вызывает функцию исправления ошибки.
     */
    public void run() {
        // Преобразуем полученную строку в последовательность байт
        byte[] bytes = line.getBytes();

        // Создаем пустой список для хранения битов
        List<Byte> bitsList = new ArrayList<>();

        // В цикле перебираем все байты и преобразем их в биты. После записываем все биты в список "bitsList"
        for (byte item : bytes) {
            // Преобразуем байт в бинарную строку и разбиваем ее на массив, состоящий из одиночных битов
            String[] byteStrings = Integer.toBinaryString(item).split("");

            // Записываем биты из "byteStrings" в "bitsList", преобразовав каждый бит в тип Byte
            for (String value : byteStrings) bitsList.add(Byte.parseByte(value, 2));
        }

        // Создаем пустой массив для хранения битов
        byte[] defaultBitsArray = new byte[bitsList.size()];

        // Записываем в переменную длину массива битов ("defaultBitsArray")
        int defaultBitsArrayLength = defaultBitsArray.length;

        // Заполняем массив "defaultBitsArray" значениями из списка "bitsList"
        for (int i = 0; i < defaultBitsArrayLength; i++) defaultBitsArray[i] = bitsList.get(i);

        // Назначаем переменной класса "bits" массив битов "defaultBitsArray"
        bits = defaultBitsArray;

        // Выводим на экран массив битов "defaultBitsArray"
        showBits("Введенная строка в виде набора бит", defaultBitsArray);

        // Генерируем массив битов со вставленными контрольными битами
        byte[] bitsArrayWithParityBits = generateBitsArrayWithParityBits(defaultBitsArray);

        // Выводим последовательность битов с учетом контрольных битов
        showBits("Массив бит с контрольными битами", bitsArrayWithParityBits);

        // Получаем номер бита, в который нужно занести ошибку (конвертировать бит)
        int errorPosition = getErrorPosition();

        // Получаем число контрольных битов
        int parityBitsCount = bitsArrayWithParityBits.length - defaultBitsArray.length;

        // Проверка не выходит ли номер выбранного бита для ошибки за границы
        if (errorPosition > parityBitsCount + bits.length || errorPosition < 0) {
            // Выводим сообщение об ошибке
            System.out.println("Выбранная позиция не существует");

            // Завершаем работу программы
            return;
        }

        // Если была задана ошибка, то конвертируем выбранный бит
        if (errorPosition != 0) {
            // Конвертируем выбранный бит
            bitsArrayWithParityBits[errorPosition - 1] = getConvertedBit(bitsArrayWithParityBits[errorPosition - 1]);

            // Выводим последовательность битов с ошибочным битом и позицией этой ошибки
            showBits(String.format("Массив бит с ошибочным битом на позиции номер %d", errorPosition), bitsArrayWithParityBits);
        }

        // Исправляем ошибку в последоваетльности битов
        byte[] correctedArray = getCorrectedBitsArray(bitsArrayWithParityBits, parityBitsCount);

        // Проверка: не была ли найдена ошибка
        if (correctedArray.length == 0) {
            // Выводим сообщение об отсутствии ошибки
            System.out.println("Ошибка не найдена!");

            // Выводим на экран введенную строку, так как ошибок нет
            showResultMessage();

            // Завершаем работы программы
            return;
        }

        // Выводим последовательность битов после удаления ошибки
        showBits("Последовательность бит после исправления ошибки с учетом контрольных бит", correctedArray);

        // Преобразуем исправленную последовательность бит в текстовое сообщение
        convertBitsArrayWithParityBitsToMessage(correctedArray, parityBitsCount);
    }

    /**
     * Функция выводит введенную на обработку строку.
     */
    private void showResultMessage() {
        // Выводим результирующую строку
        System.out.println("Результирующая строка: \"" + line + "\"!");
    }

    /**
     * Функция для вывода последовательности бит с переданным сообщением.
     */
    private void showBits(String message, byte[] bits) {
        // Вывод переданного сообщения
        System.out.print(message + ": ");

        // Выводим последовательность бит
        for (int i = bits.length - 1; i >= 0; i--) {
            // Выводим текущий бит
            System.out.print(bits[i]);
        }

        // Перевод на новую строку, т. е. следующее сообщение будет писаться на следующей строке
        System.out.println();
    }

    /**
     * Функция запрашивает у пользователя позицию, где создать ошибку.
     */
    private int getErrorPosition() {
        // Создается объект для считывания данных с консоли
        Scanner scanner = new Scanner(System.in);

        //Выводим сообщение запроса ошибки
        System.out.print("\nВведите номер бита, который будет преобразован в ошибочный (если ошибка не нужна, введите \"0\"): ");

        // Получаем ошибку
        int position = scanner.nextInt();

        // Закрываем поток считывания данных, чтобы избежать утечки памяти
        scanner.close();

        // Возвращаем позицию ошибки
        return position;
    }

    /**
     * Функция для конвертации бита.
     */
    private byte getConvertedBit(byte bit) {
        // Конвертируем бит, преобразем его к типу byte и возвращаем значение
        return (byte) ((bit + 1) % 2);
    }

    /**
     * Функция возвращает массив бит с учетом контрольных бит.
     * Вызывает функцию для определения значения контрольного бита.
     */
    private byte[] generateBitsArrayWithParityBits(byte[] defaultBitsArray) {
        // Переменная для подсчета количества контрольных битов
        int parityBitsCount = 0;

        // Счетчики для циклов
        int firstCounter = 0, secondCounter = 0, thirdCounter = 0;

        // Считаем количество контрольных битов
        while (firstCounter < defaultBitsArray.length) {
            // Проверка: входит ли высчитанный контрольный бит в исходную последовательность бит
            if (Math.pow(2, parityBitsCount) == firstCounter + 1 + parityBitsCount) {
                // Увеличиваем счетчик контрольных битов
                parityBitsCount++;
            } else {
                // Увеличиваем счетчик, чтобы двигаться дальше по массиву
                firstCounter++;
            }
        }

        // Массив битов с учетом контрольных битов
        byte[] bitsArrayWithParityBits = new byte[defaultBitsArray.length + parityBitsCount];

        // Заполняем двойками местоположения контрольных битов
        for (firstCounter = 0; firstCounter < bitsArrayWithParityBits.length; firstCounter++) {
            // Проверка: является ли текущее местоположение массива "bitsArrayWithParityBits" местом контрольного бита
            if (Math.pow(2, secondCounter) == firstCounter + 1) {
                // Устанавливаем двойку на место контрольного бита
                bitsArrayWithParityBits[firstCounter] = temporaryValue;

                // Увеличиваем счетчик, чтобы посчитать положение следующего контрольного бита
                secondCounter++;
            } else {
                // Устанавливаем значение бита из исходного массива битов ("defaultBitsArray" - без учета контрольных битов)
                bitsArrayWithParityBits[thirdCounter + secondCounter] = defaultBitsArray[thirdCounter];

                // Увеличиваем счетчик, чтобы двигатся дальше по массивам и установки значений бит
                thirdCounter++;
            }
        }

        // Устанавливает значения контрольных битов на соответствующие места
        for (firstCounter = 0; firstCounter < parityBitsCount; firstCounter++) {
            // Устанавливает значение контрольного бита на место этого контрольного бита
            bitsArrayWithParityBits[((int) Math.pow(2, firstCounter)) - 1] = getParityBitValue(bitsArrayWithParityBits, firstCounter);
        }

        // Возвращаем массив битов с учетом контрольных битов
        return bitsArrayWithParityBits;
    }

    /**
     * Функция, которая возвращает значение переданного контрольного бита
     */
    private byte getParityBitValue(byte[] bitsArrayWithParityBits, int parityBitNumber) {
        // Переменная для значение переданного контрольного бита
        byte parityBitValue = 0;

        // Подсчитываем значение контрольного бита
        for (int i = 0; i < bitsArrayWithParityBits.length; i++) {
            // Проверка: не является ли текущий элемент контрольным битом
            if (bitsArrayWithParityBits[i] != temporaryValue) {
                // Получаем значение контрольного бита для текущей итерации цикла
                byte bit = calculateParityBitValue(i + 1, parityBitNumber, bitsArrayWithParityBits[i], parityBitValue);

                // Проверка: не является ли полученное значение бита ошибочным
                if (bit != error) {
                    // Устанавливаем значение переданному контрольному биту
                    parityBitValue = bit;
                }
            }
        }

        // Возвращаем значение текущего контрольного бита
        return parityBitValue;
    }

    /**
     * Функция определяет значение контрольного бита (0 или 1).
     * Чтобы функция работала корректно, ее необходимо вызывать в цикле перебора массива бит без контрольных бит.
     * Тогда она будет перебирать каждый бит массива и, если необходимо, будет конвертировать значение контрольного бита.
     * После всех итераций цикла, последнее конвертированное значение и будет значением контрольного бита.
     * <p>
     * Примечание: фактически, функция просто считает количесто единиц среди контролируемых контрольным битом бит
     * и, если оно четное, то значение будет 1, иначе 0.
     */
    private byte calculateParityBitValue(int index, int parityBitNumber, byte currentBit, byte bitToConversion) {
        // Преобразуем индекс текущего бита в бинарную строку
        String s = Integer.toBinaryString(index);

        // Получение бита в позиции 2^("parityBitNumber") двоичного значения индекса "k"
        int bit = ((Integer.parseInt(s)) / ((int) Math.pow(10, parityBitNumber))) % 10;

        // Если текущий бит равен 1 и полученный бит равен 1, то конвертируем переданный бит
        if (bit == 1 && currentBit == 1) {
            // Возвращаем конвертированный бит
            return getConvertedBit(bitToConversion);
        } else {
            // Возвращаем ошибку
            return error;
        }
    }

    /**
     * Функция удаляет контрольные биты из переданного массива бит.
     * Если удалось испарвить ошибку, выводит сообщение.
     * Если ошибка не была исправлена, выводит сообщение об ошибке.
     */
    private void convertBitsArrayWithParityBitsToMessage(byte[] bitsArrayWithParityBits, int parityBitsCount) {
        // Создаем пустой массив для хранения битов сообщения без контрольных битов
        byte[] defaultBitsArray = new byte[bitsArrayWithParityBits.length - parityBitsCount];

        // Создаем счетчики для проверки и вставки бит
        int parityBit = 0, defaultBitsArrayCounter = 0;

        // Запоняем массив битами за исключением контрольных бит
        for (int i = 0; i < bitsArrayWithParityBits.length; i++) {
            // Проверка: является ли текущий бит контрольным
            if (parityBit < parityBitsCount && Math.pow(2, parityBit) == i + 1) {
                // Увеличиваем счетчик найденных контрольных бит
                parityBit++;
            } else {
                // Записываем текущий бит в битовый массив сообщения
                defaultBitsArray[defaultBitsArrayCounter] = bitsArrayWithParityBits[i];

                // Увеличиваем счетчик найденных бит сообщения
                defaultBitsArrayCounter++;
            }
        }

        // Выводит результирующую последовательность бит
        showBits("Результирующая последовательность бит", defaultBitsArray);

        // Проверка: равен ли исходный массив бит массиву бит после исправления
        if (Arrays.equals(bits, defaultBitsArray)) {
            // Выводим результирующую строку
            System.out.println("Результирующая строка: \"" + line + "\"");

            // Завершаем работу программы
            return;
        }

        // Выводим сообщение об ошибке
        System.out.println("Не удалось исправить ошибку!");

        // Создаем строку для записи бит
        StringBuilder bitesSequence = new StringBuilder();

        // Создаем последовательность бит
        for (byte c : defaultBitsArray) {
            // Добавляем текущий бит исходного массива в строку "bitesSequence"
            bitesSequence.append(c);
        }

        // Разбиваем последовательность бит по 8 штук
        String[] bitesArrayToConvertToBytes =  bitesSequence.toString().split("(?<=\\G.{8})");

        // Создаем массив для записи байтов
        byte[] bytesArray = new byte[bitesArrayToConvertToBytes.length];

        // Преобразуем биты в байты и записываем в массив "bytesArray"
        for (int i = 0; i < bitesArrayToConvertToBytes.length; i++) {
            // Пробуем преобразовать текущую последовательность бит в байт и добавляем в массив
            try {
                // Преобразуем последовательность бит в байт
                bytesArray[i] = Byte.parseByte(bitesArrayToConvertToBytes[i], 2);
            } catch (NumberFormatException exception) {
                // Преобразуем последовательность бит в целое число типа "int" и приводим его к типу "byte"
                bytesArray[i] = (byte) Integer.parseInt(bitesArrayToConvertToBytes[i], 2);
            }
        }

        // Конвертируем последовательность байт в строку
        String resultString = new String(bytesArray, StandardCharsets.UTF_8);

        // Выводим результирующую строку с учетом ошибки
        System.out.println("Результирующая строка с учетом ошибки: " + resultString);
    }

    /**
     * Функция возвращает набор бит после исправления ошибки.
     */
    private byte[] getCorrectedBitsArray(byte[] bitsArrayWithParityBits, int parityBitsCount) {
        // Массив для хранения бит расположения ошибочного бита
        byte[] errorBitLocationArray = new byte[parityBitsCount];

        // Переменная для хранения расположения ошибочного бита
        StringBuilder binaryErrorLocation = new StringBuilder();

        /*
         * Заполняем массив для хранения бит расположения ошибочного бита (проверяем четность такое количество раз,
         * которому равно значение переменно "parityBitsCount").
         */
        for (int parityBitNumber = 0; parityBitNumber < parityBitsCount; parityBitNumber++) {
            // Находим бит значения контрольного бита
            for (int currentBitNumber = 0; currentBitNumber < bitsArrayWithParityBits.length; currentBitNumber++) {
                // Получаем значение текущего бита
                byte currentBit = bitsArrayWithParityBits[currentBitNumber];

                // Получаем значение текущего контрольного бита для текущей итерации цикла с массивом "bitsArrayWithParityBits"
                byte bit = calculateParityBitValue(currentBitNumber + 1, parityBitNumber, currentBit, errorBitLocationArray[parityBitNumber]);

                // Проверка: не является ли полученный бит ошибкой
                if (bit != error) {
                    // Устанавливаем найденный бит в последовательность расположения ошибочного бита
                    errorBitLocationArray[parityBitNumber] = bit;
                }
            }

            // Вставляем бит расположения ошибочного бита
            binaryErrorLocation.insert(0, errorBitLocationArray[parityBitNumber]);
        }

        // Получаем позицию ошибочного бита в виде целого числа
        int errorLocation = Integer.parseInt(binaryErrorLocation.toString(), 2);

        // Проверка: есть ли в последовательности бит ошибка
        if (errorLocation != 0) {
            // Исправляем значение бита с ошибкой при помощи конвертации
            bitsArrayWithParityBits[errorLocation - 1] = getConvertedBit(bitsArrayWithParityBits[errorLocation - 1]);

            // Возвращаем исправленный массив
            return bitsArrayWithParityBits;
        }

        // Возвращаем пустой массив в качестве ошибки
        return new byte[0];
    }
}