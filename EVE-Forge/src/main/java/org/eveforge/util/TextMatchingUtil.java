package org.eveforge.util;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 文本匹配工具类，使用N-gram算法进行字符串相似度匹配
 */
public class TextMatchingUtil {
    
    /**
     * 计算两个字符串的N-gram相似度
     * @param str1 字符串1
     * @param str2 字符串2
     * @param n N-gram的N值
     * @return 相似度分数 (0.0 - 1.0)
     */
    public static double calculateNGramSimilarity(String str1, String str2, int n) {
        if (str1 == null || str2 == null) {
            return 0.0;
        }
        
        if (str1.equals(str2)) {
            return 1.0;
        }
        
        // 预处理字符串，转换为小写并去除多余空格
        str1 = preprocessText(str1);
        str2 = preprocessText(str2);
        
        if (str1.length() < n || str2.length() < n) {
            // 如果字符串长度小于n，则使用较小的n值或字符级别比较
            return calculateCharacterSimilarity(str1, str2);
        }
        
        Set<String> ngrams1 = generateNGrams(str1, n);
        Set<String> ngrams2 = generateNGrams(str2, n);
        
        // 计算交集和并集
        Set<String> intersection = new HashSet<>(ngrams1);
        intersection.retainAll(ngrams2);
        
        Set<String> union = new HashSet<>(ngrams1);
        union.addAll(ngrams2);
        
        if (union.isEmpty()) {
            return 0.0;
        }
        
        // 计算基础Jaccard相似度
        double baseSimilarity = (double) intersection.size() / union.size();
        
        // 添加长度惩罚，避免短字符串匹配长字符串时出现过高相似度
        double lengthPenalty = calculateLengthPenalty(str1, str2);
        
        return baseSimilarity * lengthPenalty;
    }
    
    /**
     * 计算字符级别相似度（当字符串长度小于N时使用）
     */
    private static double calculateCharacterSimilarity(String str1, String str2) {
        Set<Character> chars1 = str1.chars().mapToObj(c -> (char) c).collect(Collectors.toSet());
        Set<Character> chars2 = str2.chars().mapToObj(c -> (char) c).collect(Collectors.toSet());
        
        Set<Character> intersection = new HashSet<>(chars1);
        intersection.retainAll(chars2);
        
        Set<Character> union = new HashSet<>(chars1);
        union.addAll(chars2);
        
        if (union.isEmpty()) {
            return 0.0;
        }
        
        // 计算基础相似度
        double baseSimilarity = (double) intersection.size() / union.size();
        
        // 应用长度惩罚
        double lengthPenalty = calculateLengthPenalty(str1, str2);
        
        return baseSimilarity * lengthPenalty;
    }
    
    /**
     * 计算长度惩罚因子，避免短字符串匹配长字符串时出现过高相似度
     * @param str1 字符串1
     * @param str2 字符串2
     * @return 长度惩罚因子 (0.0 - 1.0)
     */
    private static double calculateLengthPenalty(String str1, String str2) {
        // 基础长度比率
        if (str1.isEmpty() || str2.isEmpty()) {
            return 0.0;
        }
        
        // 返回1.0表示无长度惩罚，保持原始N-gram相似度计算
        return 1.0;
    }
    
    /**
     * 生成字符串的N-gram集合
     */
    public static Set<String> generateNGrams(String text, int n) {
        Set<String> ngrams = new HashSet<>();
        
        if (text == null || text.length() < n) {
            return ngrams;
        }
        
        for (int i = 0; i <= text.length() - n; i++) {
            ngrams.add(text.substring(i, i + n));
        }
        
        return ngrams;
    }
    
    /**
     * 预处理文本：转换为小写，去除多余空格
     */
    private static String preprocessText(String text) {
        if (text == null) {
            return "";
        }
        return text.toLowerCase().trim().replaceAll("\\s+", "");
    }
    
    /**
     * 在候选列表中找到与目标字符串最匹配的项
     * @param target 目标字符串
     * @param candidates 候选字符串列表
     * @param n N-gram的N值
     * @param threshold 相似度阈值
     * @return 匹配结果，包含匹配的字符串和相似度分数
     */
    public static MatchingResult findBestMatch(String target, List<String> candidates, int n, double threshold) {
        if (target == null || candidates == null || candidates.isEmpty()) {
            return null;
        }
        
        MatchingResult bestMatch = null;
        double maxSimilarity = 0.0;
        
        for (String candidate : candidates) {
            double similarity = calculateNGramSimilarity(target, candidate, n);
            if (similarity > maxSimilarity && similarity >= threshold) {
                maxSimilarity = similarity;
                bestMatch = new MatchingResult(candidate, similarity);
            }
        }
        
        return bestMatch;
    }
    
    /**
     * 在候选列表中找到所有匹配项（超过阈值的）
     * @param target 目标字符串
     * @param candidates 候选字符串列表
     * @param n N-gram的N值
     * @param threshold 相似度阈值
     * @return 所有匹配结果列表，按相似度降序排列
     */
    public static List<MatchingResult> findAllMatches(String target, List<String> candidates, int n, double threshold) {
        if (target == null || candidates == null || candidates.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<MatchingResult> matches = new ArrayList<>();
        
        for (String candidate : candidates) {
            double similarity = calculateNGramSimilarity(target, candidate, n);
            if (similarity >= threshold) {
                matches.add(new MatchingResult(candidate, similarity));
            }
        }
        
        // 按相似度降序排列
        matches.sort((a, b) -> Double.compare(b.getSimilarity(), a.getSimilarity()));
        
        return matches;
    }
    
    /**
     * 匹配结果类
     */
    public static class MatchingResult {
        private final String matchedText;
        private final double similarity;
        
        public MatchingResult(String matchedText, double similarity) {
            this.matchedText = matchedText;
            this.similarity = similarity;
        }
        
        public String getMatchedText() {
            return matchedText;
        }
        
        public double getSimilarity() {
            return similarity;
        }
        
        @Override
        public String toString() {
            return String.format("MatchingResult{text='%s', similarity=%.4f}", matchedText, similarity);
        }
    }
}