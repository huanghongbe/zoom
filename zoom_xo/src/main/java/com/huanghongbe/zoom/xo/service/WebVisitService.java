package com.huanghongbe.zoom.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.huanghongbe.zoom.base.service.SuperService;
import com.huanghongbe.zoom.commons.entity.WebVisit;
import com.huanghongbe.zoom.xo.vo.WebVisitVO;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-02-12 2:09
 */
public interface WebVisitService extends SuperService<WebVisit> {

    /**
     * 增加访问记录（异步接口）
     *
     * @param userUid
     * @param request
     * @param behavior
     * @param moduleUid
     * @param otherData
     */
    void addWebVisit(String userUid, HttpServletRequest request, String behavior, String moduleUid, String otherData);

    /**
     * 获取今日网站访问人数
     *
     * @return
     */
    int getWebVisitCount();

    /**
     * 获取近七天的访问量
     *
     * @return {
     * date: ["2019-6-20","2019-6-21","2019-6-22","2019-6-23","2019-6-24",,"2019-6-25","2019-6-26"]
     * pv: [10,5,6,7,5,3,2]
     * uv: [5,3,4,4,5,2,1]
     * }
     * 注：PV表示访问量   UV表示独立用户数
     */
    Map<String, Object> getVisitByWeek();

    /**
     * 获取访问列表
     *
     * @param webVisitVO
     * @return
     */
    IPage<WebVisit> getPageList(WebVisitVO webVisitVO);
}
