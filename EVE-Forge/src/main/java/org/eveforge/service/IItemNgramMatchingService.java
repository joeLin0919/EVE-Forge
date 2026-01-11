package org.eveforge.service;

import org.eveforge.util.TextMatchingUtil;

import java.util.List;

/**
 * 物品名称N-gram匹配服务接口，专门处理数据库中的模糊查询
 */
public interface IItemNgramMatchingService {

    /**
     * 使用N-gram算法在数据库中查找最匹配的物品名称
     * @param userInput 用户输入的物品名称
     * @param nGramSize N-gram大小
     * @param threshold 相似度阈值
     * @return 匹配结果，包含匹配的物品名称和相似度分数
     */
    TextMatchingUtil.MatchingResult findMatchInDatabase(String userInput, int nGramSize, double threshold);

    /**
     * 使用N-gram算法在数据库中查找所有匹配的物品名称
     * @param userInput 用户输入的物品名称
     * @param nGramSize N-gram大小
     * @param threshold 相似度阈值
     * @return 所有匹配结果列表，按相似度降序排列
     */
    List<TextMatchingUtil.MatchingResult> findAllMatchesInDatabase(String userInput, int nGramSize, double threshold);

    /**
     * 使用默认参数在数据库中查找最匹配的物品名称
     * @param userInput 用户输入的物品名称
     * @return 匹配结果
     */
    TextMatchingUtil.MatchingResult findBestMatch(String userInput);

    /**
     * 使用默认参数在数据库中查找所有匹配的物品名称
     * @param userInput 用户输入的物品名称
     * @return 所有匹配结果列表
     */
    List<TextMatchingUtil.MatchingResult> findAllMatches(String userInput);

    /**
     * 使用unigram算法在数据库中查找最匹配的物品名称
     * @param userInput 用户输入的物品名称
     * @return 匹配结果
     */
    TextMatchingUtil.MatchingResult findBestMatchWithUnigram(String userInput);

    /**
     * 使用unigram算法在数据库中查找所有匹配的物品名称
     * @param userInput 用户输入的物品名称
     * @return 所有匹配结果列表
     */
    List<TextMatchingUtil.MatchingResult> findAllMatchesWithUnigram(String userInput);

    /**
     * 使用unigram算法在数据库中查找最匹配的物品名称，考虑长度因素
     * @param userInput 用户输入的物品名称
     * @return 匹配结果
     */
    TextMatchingUtil.MatchingResult findBestMatchWithUnigramAndLengthPenalty(String userInput);
}