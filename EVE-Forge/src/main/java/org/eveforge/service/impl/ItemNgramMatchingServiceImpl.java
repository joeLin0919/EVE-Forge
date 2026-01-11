package org.eveforge.service.impl;

import org.eveforge.mapper.ProductMapper;
import org.eveforge.service.IItemNgramMatchingService;
import org.eveforge.util.TextMatchingUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 物品名称N-gram匹配服务实现，专门处理数据库中的模糊查询
 */
@Service
public class ItemNgramMatchingServiceImpl implements IItemNgramMatchingService {
    
    @Autowired
    private ProductMapper productMapper;

    @Value("${N-gram.size}")
    private Integer size;
    
    /**
     * 使用N-gram算法在数据库中查找最匹配的物品名称
     * @param userInput 用户输入的物品名称
     * @param nGramSize N-gram大小
     * @param threshold 相似度阈值
     * @return 匹配结果，包含匹配的物品名称和相似度分数
     */
    @Override
    public TextMatchingUtil.MatchingResult findMatchInDatabase(String userInput, int nGramSize, double threshold) {
        // 从数据库获取所有物品名称
        List<String> allItemNames = productMapper.getAllProductNames();
        
        // 使用N-gram算法查找最佳匹配
        return TextMatchingUtil.findBestMatch(userInput, allItemNames, nGramSize, threshold);
    }
    
    /**
     * 使用N-gram算法在数据库中查找所有匹配的物品名称
     * @param userInput 用户输入的物品名称
     * @param nGramSize N-gram大小
     * @param threshold 相似度阈值
     * @return 所有匹配结果列表，按相似度降序排列
     */
    @Override
    public List<TextMatchingUtil.MatchingResult> findAllMatchesInDatabase(String userInput, int nGramSize, double threshold) {
        // 从数据库获取所有物品名称
        List<String> allItemNames = productMapper.getAllProductNames();
        
        // 使用N-gram算法查找所有匹配项
        return TextMatchingUtil.findAllMatches(userInput, allItemNames, nGramSize, threshold);
    }
    
    /**
     * 使用默认参数在数据库中查找最匹配的物品名称
     * @param userInput 用户输入的物品名称
     * @return 匹配结果
     */
    @Override
    public TextMatchingUtil.MatchingResult findBestMatch(String userInput) {
        return findMatchInDatabase(userInput, size , 0.3); // 使用unigram和0.3阈值以获得更宽松的匹配
    }
    
    /**
     * 使用默认参数在数据库中查找所有匹配的物品名称
     * @param userInput 用户输入的物品名称
     * @return 所有匹配结果列表
     */
    @Override
    public List<TextMatchingUtil.MatchingResult> findAllMatches(String userInput) {
        return findAllMatchesInDatabase(userInput, size , 0.3); // 使用unigram和0.2阈值以获得更宽松的匹配
    }
    
    /**
     * 使用unigram算法在数据库中查找最匹配的物品名称
     * @param userInput 用户输入的物品名称
     * @return 匹配结果
     */
    @Override
    public TextMatchingUtil.MatchingResult findBestMatchWithUnigram(String userInput) {
        return findMatchInDatabase(userInput, size  , 0.3); // 使用unigram和0.3阈值
    }
    
    /**
     * 使用unigram算法在数据库中查找所有匹配的物品名称
     * @param userInput 用户输入的物品名称
     * @return 所有匹配结果列表
     */
    @Override
    public List<TextMatchingUtil.MatchingResult> findAllMatchesWithUnigram(String userInput) {
        return findAllMatchesInDatabase(userInput, size , 0.2); // 使用unigram和0.2阈值
    }
    
    /**
     * 使用unigram算法在数据库中查找最匹配的物品名称，考虑长度因素
     * @param userInput 用户输入的物品名称
     * @return 匹配结果
     */
    @Override
    public TextMatchingUtil.MatchingResult findBestMatchWithUnigramAndLengthPenalty(String userInput) {
        // 从数据库获取所有物品名称
        List<String> allItemNames = productMapper.getAllProductNames();
        
        // 使用N-gram算法查找最佳匹配，但使用更严格的长度惩罚
        TextMatchingUtil.MatchingResult bestMatch = null;
        double maxSimilarity = 0.0;
        allItemNames.stream()
                .filter(candidate -> candidate.length() >= userInput.length())
                .toList();
        for (String candidate : allItemNames) {
            // 使用unigram计算相似度
            double similarity = TextMatchingUtil.calculateNGramSimilarity(userInput, candidate, 1);
            
            if (similarity > maxSimilarity) {
                maxSimilarity = similarity;
                bestMatch = new TextMatchingUtil.MatchingResult(candidate, similarity);
            }
        }
        
        return bestMatch;
    }
}