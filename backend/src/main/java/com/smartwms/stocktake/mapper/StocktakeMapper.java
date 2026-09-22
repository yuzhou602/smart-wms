package com.smartwms.stocktake.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartwms.stocktake.entity.Stocktake;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface StocktakeMapper extends BaseMapper<Stocktake> {
}
