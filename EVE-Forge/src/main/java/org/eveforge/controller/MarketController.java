package org.eveforge.controller;


import org.eveforge.repository.vo.CommReqVo;
import org.eveforge.repository.vo.ProductPriceVo;
import org.eveforge.service.IItemNgramMatchingService;
import org.eveforge.service.IItemService;
import org.eveforge.service.IMarketService;
import org.eveforge.util.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/market")
public class MarketController {

    @Autowired
    private IItemNgramMatchingService itemNgramMatchingService;

    @Autowired
    private IMarketService marketService;

    @Autowired
    private IItemService itemService;



    @PostMapping("/getItemPrice")
    public ApiResponse getItemPrice(@RequestBody CommReqVo req){
        if (req.getItemName()==null){
            return ApiResponse.error("请输入物品名称");
        }
        String itemName = itemNgramMatchingService.findBestMatch(req.getItemName()).getMatchedText();
        Integer itemId = itemService.getItemIdByName(itemName).getFirst();
        ProductPriceVo productPriceVo = marketService.getProductPrice(10000002, 30000142,itemId);
        return ApiResponse.success(productPriceVo);
    }
}
