package com.ruoyi.web.mapper.travel;

import java.util.List;
import com.ruoyi.web.domain.travel.XlRouteItem;

/**
 * 线路节点 数据层
 */
public interface XlRouteItemMapper {
    List<XlRouteItem> selectByRouteId(Long routeId);
}
