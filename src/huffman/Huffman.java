
package huffman;

/**
 *
 * @author Seif
 * @author Ziad
 */
public class Huffman {
    public static void main(String[] args) {
       //java.awt.EventQueue.invokeLater(() -> new huff().setVisible(true));
        HuffmanCompression x = new HuffmanCompression();
        x.decompress("compressed.bin");

    }
    
}
