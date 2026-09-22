package com.smartwms.analytics.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.smartwms.analytics.mapper.AnalyticsMapper;
import com.smartwms.analytics.service.AnalyticsService;
import com.smartwms.inventory.entity.Inventory;
import com.smartwms.inventory.mapper.InventoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;

@Service @RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {
    private final AnalyticsMapper analyticsMapper;
    private final InventoryMapper inventoryMapper;

    @Override public Map<String,Object> getAnalytics(int requestedDays) {
        int days = Math.max(7, Math.min(90, requestedDays));
        List<Inventory> inventory = inventoryMapper.selectList(new LambdaQueryWrapper<>());
        long currentTotal=inventory.stream().mapToLong(i->value(i.getTotalQty())).sum();
        long currentAvailable=inventory.stream().mapToLong(i->value(i.getAvailableQty())).sum();
        List<Map<String,Object>> changes=analyticsMapper.dailyInventoryChanges(days);
        Map<LocalDate,Long> delta=new HashMap<>();
        changes.forEach(r->delta.put(toDate(r.get("day")),number(r.get("changeQty"))));
        long rangeChange=delta.values().stream().mapToLong(Long::longValue).sum();
        long total=currentTotal-rangeChange, available=currentAvailable-rangeChange;
        List<String> dates=new ArrayList<>(); List<Long> totals=new ArrayList<>(), availables=new ArrayList<>();
        for(int i=days-1;i>=0;i--){LocalDate d=LocalDate.now().minusDays(i);long c=delta.getOrDefault(d,0L);total+=c;available+=c;dates.add(d.toString());totals.add(Math.max(0,total));availables.add(Math.max(0,available));}
        List<Map<String,Object>> sku=analyticsMapper.outboundBySku(); int n=sku.size(),a=(int)Math.ceil(n*.2),b=(int)Math.ceil(n*.5);
        List<Map<String,Object>> abc=List.of(Map.of("name","A类（高频）","value",a),Map.of("name","B类（中频）","value",Math.max(0,b-a)),Map.of("name","C类（低频）","value",Math.max(0,n-b)));
        List<String> months=new ArrayList<>();Map<String,Long> monthly=new HashMap<>();analyticsMapper.monthlyOutbound().forEach(r->monthly.put(String.valueOf(r.get("month")),number(r.get("quantity"))));List<BigDecimal> turnover=new ArrayList<>();
        for(int i=5;i>=0;i--){String m=YearMonth.now().minusMonths(i).toString();months.add(m);turnover.add(currentTotal==0?BigDecimal.ZERO:BigDecimal.valueOf(monthly.getOrDefault(m,0L)).divide(BigDecimal.valueOf(currentTotal),2,RoundingMode.HALF_UP));}
        return Map.of("trend",Map.of("dates",dates,"total",totals,"available",availables),"abc",abc,"turnover",Map.of("months",months,"values",turnover),"topProducts",sku.stream().limit(10).toList());
    }

    @Override public Map<String,Object> getInboundOutboundTrend(){List<String> days=new ArrayList<>();Map<LocalDate,Long> in=dayMap(analyticsMapper.dailyInbound()),out=dayMap(analyticsMapper.dailyOutbound());List<Long> ins=new ArrayList<>(),outs=new ArrayList<>();for(int i=6;i>=0;i--){LocalDate d=LocalDate.now().minusDays(i);days.add(d.toString());ins.add(in.getOrDefault(d,0L));outs.add(out.getOrDefault(d,0L));}return Map.of("days",days,"inbound",ins,"outbound",outs);}
    @Override public Map<String,Object> getWarehouseUtilization(){List<Map<String,Object>> values=analyticsMapper.warehouseUtilization().stream().map(r->{long used=number(r.get("usedCapacity")),total=number(r.get("totalCapacity"));return Map.<String,Object>of("id",r.get("id"),"name",r.get("name"),"usedCapacity",used,"totalCapacity",total,"value",total==0?0:Math.min(100,Math.round(used*100f/total)));}).toList();return Map.of("warehouses",values);}
    private Map<LocalDate,Long> dayMap(List<Map<String,Object>> rows){Map<LocalDate,Long> m=new HashMap<>();rows.forEach(r->m.put(toDate(r.get("day")),number(r.get("quantity"))));return m;}
    private LocalDate toDate(Object value){if(value instanceof java.sql.Date d)return d.toLocalDate();return LocalDate.parse(String.valueOf(value));}
    private long number(Object v){return v instanceof Number n?n.longValue():v==null?0:Long.parseLong(String.valueOf(v));}
    private int value(Integer v){return v==null?0:v;}
}
