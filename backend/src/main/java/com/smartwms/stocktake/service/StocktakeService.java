package com.smartwms.stocktake.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.smartwms.common.response.PageResult;
import com.smartwms.stocktake.entity.Stocktake;
import com.smartwms.stocktake.entity.StocktakeItem;
import com.smartwms.stocktake.vo.StocktakeVO;

import java.util.List;

public interface StocktakeService extends IService<Stocktake> {

    PageResult<StocktakeVO> listStocktakes(int page, int pageSize, String keyword, String status);

    StocktakeVO getStocktakeById(Long id);

    List<StocktakeItem> getStocktakeItems(Long stocktakeId);

    StocktakeVO createStocktake(Stocktake stocktake, List<StocktakeItem> items);

    void startStocktake(Long id);

    void countStocktakeItem(Long itemId, Integer actualQty, Long userId);

    void completeStocktake(Long id);
}
