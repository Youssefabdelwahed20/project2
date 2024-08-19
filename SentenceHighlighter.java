/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sentencehighlighter;

import java.awt.Color;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

import javax.swing.text.html.*;
import javax.swing.text.html.parser.*;

//import org.apache.commons.text.similarity.CosineSimilarity;
/**
 *
 * @author Format Store
 */
public class SentenceHighlighter {



    public static void main(String[] args) throws IOException {
        // Example usage with file paths
        String[] filePaths = { "file1.txt", "file2.txt" }; // Replace with your text file paths
        String outputPath = "highlighted_output.html";

        List<String> sentences = processFiles(filePaths);
        double[][] similarityMatrix = calculateSimilarityMatrix(sentences);
        String highlightedHtml = highlightSentences(sentences, similarityMatrix);

        saveHtml(highlightedHtml, outputPath);
    }

    // Read text from multiple files
    public static List<String> processFiles(String[] filePaths) throws IOException {
        List<String> sentences = new ArrayList<>();
        for (String filePath : filePaths) {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            sentences.addAll(splitIntoSentences(content));
        }
        return sentences;
    }

    // Split text into sentences
    public static List<String> splitIntoSentences(String text) {
        return Arrays.asList(text.split("(?<!\\w\\.\\w.)(?<![A-Z][a-z]\\.)(?<=\\.|\\?)\\s"));
    }

    // Calculate the similarity matrix using cosine similarity
    public static double[][] calculateSimilarityMatrix(List<String> sentences) {
        int n = sentences.size();
        double[][] similarityMatrix = new double[n][n];
        CosineSimilarity cosineSimilarity = new CosineSimilarity();

        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                double similarity = cosineSimilarity.cosineSimilarity(sentences.get(i), sentences.get(j));
                similarityMatrix[i][j] = similarity;
                similarityMatrix[j][i] = similarity; // Symmetric matrix
            }
        }
        return similarityMatrix;
    }

    // Highlight sentences based on similarity
    public static String highlightSentences(List<String> sentences, double[][] similarityMatrix) {
        int n = sentences.size();
        StringBuilder highlightedText = new StringBuilder();

        for (int i = 0; i < n; i++) {
            float hue = (float) (Math.random()); // Random hue for diversity
            Color color = Color.getHSBColor(hue, 0.5f, 0.9f);

            // Convert to hex color
            String hexColor = String.format("#%02x%02x%02x", color.getRed(), color.getGreen(), color.getBlue());
            highlightedText.append("<span style=\"background-color:").append(hexColor).append("\">")
                    .append(sentences.get(i)).append("</span> ");
        }
        return highlightedText.toString();
    }

    // Save the highlighted text as an HTML file
    public static void saveHtml(String content, String outputPath) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath));
        writer.write("<html><body>");
        writer.write(content);
        writer.write("</body></html>");
        writer.close();
    }

    // Helper method to convert cosine similarity to RGB colors
    private static Color cosineSimilarityToColor(double similarity) {
        float hue = (float) (1.0 - similarity); // More similar => closer to red (0)
        return Color.getHSBColor(hue, 0.6f, 0.9f);
    }
}
