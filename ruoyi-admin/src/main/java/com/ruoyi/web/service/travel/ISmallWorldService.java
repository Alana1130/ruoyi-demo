package com.ruoyi.web.service.travel;

import java.util.List;
import java.util.Map;

/**
 * 小世界：地区解锁宠物/经营权、星星交易、饲养宠物
 */
public interface ISmallWorldService {

    /** 首次点亮某目的地时发放该地区随机宠物或经营权（二选一） */
    void grantRewardForDestination(Long userId, Long destinationId);

    /** 小世界概览：宠物数、经营权数、星星数 */
    Map<String, Object> getWorldOverview(Long userId);

    /** 我的宠物列表（含模板信息） */
    List<Map<String, Object>> listMyPets(Long userId);

    /** 我的美食经营权列表（含模板信息） */
    List<Map<String, Object>> listMyLicenses(Long userId);

    /** 喂食宠物（有冷却，增加饱食度与经验） */
    Map<String, Object> feedPet(Long userId, Long userPetId);

    /** 互动宠物（有冷却，增加心情与经验） */
    Map<String, Object> playPet(Long userId, Long userPetId);

    /** 修改宠物昵称 */
    boolean updatePetNickname(Long userId, Long userPetId, String nickname);

    /** 交易市场挂单列表（excludeSellerId 可选，排除某用户的挂单，便于不显示自己的） */
    List<Map<String, Object>> listTradeListings(String itemType, Integer limit, Long excludeSellerId);

    /** 我的挂单列表（用于下架） */
    List<Map<String, Object>> listMyListings(Long userId);

    /** 上架（挂单） */
    Map<String, Object> createListing(Long userId, String itemType, Long itemId, Integer starsAmount);

    /** 购买（摘单） */
    Map<String, Object> buyListing(Long buyerId, Long tradeId);

    /** 取消挂单 */
    boolean cancelListing(Long userId, Long tradeId);

    /** 领取演示数据：为当前用户随机发放若干宠物与经营权（便于无数据时体验） */
    Map<String, Object> seedDemoData(Long userId);
}
