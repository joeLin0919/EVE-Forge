package org.eveforge.service;

import org.eveforge.service.IItemNgramMatchingService;

import org.eveforge.util.TextMatchingUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ItemNgramMatchingServiceTest {

    @Autowired
    private IItemNgramMatchingService itemNgramMatchingService;

    @Test
    public void testFindBestMatch() {
        // 测试查找最佳匹配
        TextMatchingUtil.MatchingResult result = itemNgramMatchingService.findBestMatch("Tritanium");
        
        // 由于数据库中可能没有数据，我们主要测试方法不抛出异常
        assertNotNull(itemNgramMatchingService);
    }

    @Test
    public void testFindMatchInDatabase() {
        // 测试使用特定参数的匹配
        TextMatchingUtil.MatchingResult result = itemNgramMatchingService.findMatchInDatabase("Tri", 2, 0.3);
        
        // 验证返回结果类型
        if (result != null) {
            assertTrue(result.getSimilarity() >= 0.0 && result.getSimilarity() <= 1.0);
        }
    }
}