package com.smartwms.outbound.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartwms.outbound.entity.OutboundOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OutboundOrderMapper extends BaseMapper<OutboundOrder> {
}
