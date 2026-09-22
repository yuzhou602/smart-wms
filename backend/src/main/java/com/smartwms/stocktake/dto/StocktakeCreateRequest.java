package com.smartwms.stocktake.dto;

import com.smartwms.stocktake.entity.Stocktake;
import com.smartwms.stocktake.entity.StocktakeItem;
import lombok.Data;

import java.util.List;

@Data
public class StocktakeCreateRequest {

    private Stocktake stocktake;

    private List<StocktakeItem> items;
}
