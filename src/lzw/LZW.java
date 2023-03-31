package lzw;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class LZW {
    private static final String fileName = "info";
    private static final String textFileFormat = ".txt";

    private final HashMap<String, Short> compressionDictionary = new HashMap<>();
    private final HashMap<Short, String> decompressionDictionary = new HashMap<>();

    short maxCharCode = 0, decompressionMaxCharCode = 0;

    public LZW() {
        initDictionary();
    }

    private void initDictionary() {
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(fileName + textFileFormat), StandardCharsets.UTF_8)) {
            short charCode;
            while ((charCode = (short) reader.read()) != -1) {
                char symbol = (char) charCode;
                if (!compressionDictionary.containsKey(String.valueOf(symbol))) {
                    compressionDictionary.put(String.valueOf(symbol), charCode);
                    decompressionDictionary.put(charCode, String.valueOf(symbol));

                    if (charCode > maxCharCode) {
                        maxCharCode = charCode;
                        decompressionMaxCharCode = charCode;
                    }
                }
            }

            System.out.println("File dictionary: " + compressionDictionary);
        } catch (Exception ex) {
            System.out.println("\n\nDictionary error: " + ex.getMessage());
        }
    }

    public void compress() {
        try (InputStreamReader reader = new InputStreamReader(new FileInputStream(fileName + textFileFormat), StandardCharsets.UTF_8);
             ObjectOutputStream writer = new ObjectOutputStream(new FileOutputStream(fileName + "_compressed.lzw"))) {

            System.out.print("\nCompressed: ");

            short charCode;
            String charSequence = String.valueOf((char) reader.read());
            while ((charCode = (short) reader.read()) != -1) {
                char symbol = (char) charCode;
                if (!compressionDictionary.containsKey(charSequence + symbol)) {
                    writeCompressionSequence(charSequence, writer);
                    compressionDictionary.put(charSequence + symbol, ++maxCharCode);
                    charSequence = String.valueOf(symbol);
                } else {
                    charSequence += symbol;
                }
            }

            writeCompressionSequence(charSequence, writer);
            writer.writeShort(0);

            System.out.println("\n\nCompressed dictionary: " + compressionDictionary);
        } catch (Exception ex) {
            System.out.println("\n\nCompression error: " + ex.getMessage());
        }
    }

    private void writeCompressionSequence(String charSequence, ObjectOutputStream writer) throws IOException {
        short charSequenceCode = Short.parseShort(compressionDictionary.get(charSequence).toString());
        writer.writeShort(charSequenceCode);

        if (charSequence.length() == 1) System.out.print((char) charSequenceCode);
        else System.out.print(charSequenceCode);
    }

    public void decompress() {
        try (ObjectInputStream reader = new ObjectInputStream(new FileInputStream(fileName + "_compressed.lzw"));
             FileWriter writer = new FileWriter(fileName + "_decompressed" + textFileFormat)) {

            System.out.print("\nDecompressed: ");

            short charCode, charSequenceCode;
            String charSequenceFromDictionary, charSequence;

            charCode = reader.readShort();
            writer.write(decompressionDictionary.get(charCode));
            System.out.print(decompressionDictionary.get(charCode));

            while ((charSequenceCode = reader.readShort()) != -1 && charSequenceCode != 0) {
                charSequenceFromDictionary = decompressionDictionary.get(charCode);
                if (decompressionDictionary.containsKey(charSequenceCode)) {
                    charSequence = decompressionDictionary.get(charSequenceCode);
                    writer.write(charSequence);
                    decompressionDictionary.put(++decompressionMaxCharCode, charSequenceFromDictionary + charSequence.charAt(0));

                    System.out.print(charSequence);
                } else {
                    String resultCharSequence = charSequenceFromDictionary + charSequenceFromDictionary.charAt(0);
                    decompressionDictionary.put(++decompressionMaxCharCode, resultCharSequence);
                    writer.write(resultCharSequence);

                    System.out.print(resultCharSequence);
                }

                charCode = charSequenceCode;
            }

            System.out.println("\n\nDecompressed dictionary: " + decompressionDictionary);
        } catch (Exception ex) {
            System.out.println("\n\nDecompression error: " + ex.getMessage());
        }
    }
}