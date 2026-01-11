package org.eveforge.util;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class TextMatchingUtilTest {

    @Test
    public void testNGramSimilarity() {
        // 测试完全相同的字符串
        double similarity1 = TextMatchingUtil.calculateNGramSimilarity("Tritanium", "Tritanium", 2);
        assertEquals(1.0, similarity1, 0.001);

        // 测试完全不同的字符串
        double similarity2 = TextMatchingUtil.calculateNGramSimilarity("Tritanium", "Pyerite", 2);
        assertTrue(similarity2 < 1.0);

        // 测试相似字符串
        double similarity3 = TextMatchingUtil.calculateNGramSimilarity("Tritani", "Tritanium", 2);
        assertTrue(similarity3 > 0.5);
    }

    @Test
    public void testGenerateNGrams() {
        String text = "test";
        int n = 2;
        var ngrams = TextMatchingUtil.generateNGrams(text, n);

        assertEquals(3, ngrams.size()); // "te", "es", "st"
        assertTrue(ngrams.contains("te"));
        assertTrue(ngrams.contains("es"));
        assertTrue(ngrams.contains("st"));
    }

    @Test
    public void testFindBestMatch() {
        List<String> candidates = Arrays.asList("Tritanium", "Pyerite", "Mexallon", "Isogen");
        
        // 测试查找最佳匹配
        var result = TextMatchingUtil.findBestMatch("Tri", candidates, 2, 0.1);
        
        assertNotNull(result);
        assertEquals("Tritanium", result.getMatchedText());
        assertTrue(result.getSimilarity() > 0);
    }

    @Test
    public void testFindAllMatches() {
        List<String> candidates = Arrays.asList("Tritanium", "Pyerite", "Mexallon", "Isogen");
        
        // 测试查找所有匹配项
        var results = TextMatchingUtil.findAllMatches("Tri", candidates, 2, 0.1);
        
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(r -> r.getMatchedText().equals("Tritanium")));
    }

    @Test
    public void testPreprocessing() {
        // 测试预处理功能（大小写、空格）
        double similarity = TextMatchingUtil.calculateNGramSimilarity("Tri tanium", "TRITANIUM", 2);
        assertTrue(similarity > 0.5); // 应该有较高的相似度
    }
}