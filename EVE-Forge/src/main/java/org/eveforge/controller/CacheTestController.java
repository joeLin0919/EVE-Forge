package org.eveforge.controller;

import org.eveforge.repository.vo.ProductPriceVo;
import org.eveforge.service.IMarketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/test")
public class CacheTestController {

    @Autowired
    private IMarketService marketService;

    @GetMapping("/price")
    public List<ProductPriceVo> testCache(@RequestParam String itemName) {
        return marketService.getProductPricesByName(itemName);
    }
}