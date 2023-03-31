package lzw;

public class Main {
    public static void main(String[] args) {
        LZW lzw = new LZW();
        lzw.compress();
        lzw.decompress();
    }
}
