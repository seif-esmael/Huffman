package huffman;

import java.io.*;
import java.math.*;
import java.util.*;
public class HuffmanCompression {
    static class Node {
        char character;
        int prob;
        Node left, right;

        public Node(char character, int prob) {
            this.character = character;
            this.prob = prob;
        }
    }

    public static void compress(String filePath) {
        String input = readFromFile(filePath);
        HashMap<Character, Integer> probabilities = new HashMap<>();
        for (char c : input.toCharArray()) {
            probabilities.put(c, probabilities.getOrDefault(c, 0) + 1);
        }
        PriorityQueue<Node> pq = new PriorityQueue<>(Comparator.comparingInt(n -> n.prob));
        for (char c : probabilities.keySet()) {
            pq.add(new Node(c, probabilities.get(c)));
        }
        while (pq.size() > 1) {
            Node left = pq.poll();
            Node right = pq.poll();
            Node sumNode = new Node(' ', left.prob + right.prob);
            sumNode.left = left;
            sumNode.right = right;
            pq.add(sumNode);
        }
        Node root = pq.poll();
        HashMap<Character, String> codes = new HashMap<>();
        generateCodes(root, "", codes);
        String overHeadTable = toBeWrittenToFile(codes);
        StringBuilder compressedData = new StringBuilder();
        for (char c : input.toCharArray()) {
            compressedData.append(codes.get(c));
        }
        System.out.println(compressedData);
        try (DataOutputStream outputStream = new DataOutputStream(new FileOutputStream("compressed.bin"))) {
            outputStream.writeByte(codes.size());
            for (Map.Entry<Character, String> entry : codes.entrySet()) {
                outputStream.writeByte(entry.getKey());
                byte[] codeBytes = fromIntToByte(Integer.parseInt(entry.getValue(), 2));
                outputStream.writeByte(codeBytes.length);
                for (byte b : codeBytes) outputStream.writeByte(b);
            }
            outputStream.writeShort(compressedData.length());
            outputStream.write(convertToBytes(compressedData.toString()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void generateCodes(Node node, String code, HashMap<Character, String> codes) {
        if (node != null) {
            if (node.character != ' ') {
                codes.put(node.character, code);
            }
            if (node.left != null || node.right != null) {
                generateCodes(node.left, code + "0", codes);
                generateCodes(node.right, code + "1", codes);
            }

        }
    }

    public static String toBeWrittenToFile(HashMap<Character, String> codes) {
        StringBuilder sb = new StringBuilder();
        for (char c : codes.keySet()) {
            if (c != '\n') {
                sb.append(c).append(" ").append(codes.get(c)).append("\n");
            }
        }
        return sb.toString();
    }

    public static void decompress(String compressedFile) {
        try(DataInputStream compressedStreamInBytes = new DataInputStream(new FileInputStream(compressedFile))) {
            int overHeadSize = compressedStreamInBytes.readUnsignedByte();
            HashMap<String, Character> overHeadTable = new HashMap<>();
            for (int i = 0; i < overHeadSize; i++) {
                char character = (char) compressedStreamInBytes.readUnsignedByte();
                int sizeOfCode = compressedStreamInBytes.readUnsignedByte();
                int[] codeBytes = new int[sizeOfCode];
                for (int j = 0; j < sizeOfCode; j++) {
                    codeBytes[j] = compressedStreamInBytes.readUnsignedByte();
                }
                String code = "";
                for (int codeByte : codeBytes) {
                    code += Integer.toBinaryString(codeByte);
                }
                overHeadTable.put(code, character);
            }
            int sizeOfBinary = compressedStreamInBytes.readUnsignedShort();
            byte[] bytes = compressedStreamInBytes.readAllBytes();
            String binary = convertToBinary(bytes, sizeOfBinary);
            String output =decompressHelper(binary, overHeadTable);
            writeToTextFile(output);
        } catch (IOException e){
            e.printStackTrace();
        }

    }
    public static String decompressHelper(String stream, HashMap<String, Character> codeMap) {
        StringBuilder Output = new StringBuilder();
        String code = "";
        for (char c : stream.toCharArray()) {
            code += c;
            if (codeMap.containsKey(code)) {
                Output.append(codeMap.get(code));
                code = "";
            }
        }
        return Output.toString();

    }
    public static String convertToBinary(byte[] bytes, int sizeOfBinary) {
        StringBuilder binary = new StringBuilder();
        for (byte b : bytes) {
            for (int i = 0; i < 8; i++) {
                int bit = (b >> (7 - i)) & 1;
                if (binary.length() == sizeOfBinary) {
                    return binary.toString();
                }
                binary.append(bit);
            }
        }
        return binary.toString();
    }

    public static byte[] fromIntToByte(int num) {
        BigInteger bigInteger = BigInteger.valueOf(num);
        return bigInteger.toByteArray();
    }

    public static byte[] convertToBytes(String binary) {
        int arrayLength = Math.ceilDiv(binary.length(), 8);
        byte[] bytes = new byte[arrayLength];
        int index = 0;
        int remainingBits = binary.length();
        while (remainingBits >= 8) {
            String byteString = binary.substring(index, index + 8);
            bytes[index / 8] = (byte) Integer.parseInt(byteString, 2);
            index += 8;
            remainingBits -= 8;
        }
        if (remainingBits > 0) {
            String byteString = binary.substring(index);
            for (int i = 0; i < 8 - remainingBits; i++) {
                byteString += "0";
            }
            bytes[arrayLength - 1] = (byte) Integer.parseInt(byteString, 2);
        }
        return bytes;
    }
    private static void writeToTextFile(String data) {
        try (FileWriter writer = new FileWriter("decompressed.txt")) {
            writer.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static String readFromFile(String filePath) {
        StringBuilder data = new StringBuilder();
        try (FileReader reader = new FileReader(filePath)) {
            int c;
            while ((c = reader.read()) != -1) {
                data.append((char) c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data.toString();
    }

}
