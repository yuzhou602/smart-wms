package com.smartwms.inbound.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.smartwms.inbound.entity.InboundOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InboundOrderMapper extends BaseMapper<InboundOrder> {
}
