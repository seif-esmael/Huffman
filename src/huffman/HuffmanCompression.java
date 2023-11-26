/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package huffman;

import java.util.HashMap;
import java.util.PriorityQueue;

/**
 *
 * @author Seif
 */
public class HuffmanCompression {
    static class node {
        char character;
        int prob;
        node left,right;

        public node(char character, int prob) {
            this.character = character;
            this.prob = prob;
        }
    }

    public static void compress(String input) {
        
        HashMap<Character, Integer> probs_mp = new HashMap<>();
        for (char c : input.toCharArray()) {
            probs_mp.put(c, probs_mp.getOrDefault(c, 0) + 1); 
        }
        //-------
        PriorityQueue<node> pq = new PriorityQueue<>((n1, n2) -> Integer.compare(n1.prob, n2.prob)); // pririty for low (-1)
        for (char c : probs_mp.keySet()) {            
            pq.add(new node(c, probs_mp.get(c)));            
        }
        //-------        
        while (pq.size() > 1) {
            node left = pq.poll();
            node right = pq.poll();
            node sumNode = new node(' ', left.prob + right.prob);
            sumNode.left = left;
            sumNode.right = right;
            pq.add(sumNode);
        }                      
        //-------
        node root = pq.poll();         
        HashMap<Character, String> codes = new HashMap<>();        
        generateCodes(root, "", codes);        
        System.out.println("Symbol\t" + "Code");
        for (char c : codes.keySet()) {
            if (c != '\n') {                
                System.out.println(c + "\t" + codes.get(c));
            }
        }
        System.out.println("----------------------------------------");
        System.out.println("Compressed Data Stream:");
        StringBuilder compressedData = new StringBuilder();
        for (char c : input.toCharArray()) {
            compressedData.append(codes.get(c));
        }
        System.out.println(compressedData.toString());
    }
    //___________________________________________________________________________________________________
    private static void generateCodes(node node, String code, HashMap<Character, String> codes) {
        if (node != null) {            
            if (node.character != ' ') {
                codes.put(node.character, code);
            }
            if(node.left != null || node.right != null)
            {
                generateCodes(node.left, code + "0", codes);
                generateCodes(node.right, code + "1", codes);
            }
                
        }
    }
     
}
