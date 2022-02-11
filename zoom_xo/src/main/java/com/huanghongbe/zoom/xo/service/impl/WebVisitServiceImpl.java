package com.huanghongbe.zoom.xo.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.huanghongbe.zoom.base.global.Constants;
import com.huanghongbe.zoom.base.service.impl.SuperServiceImpl;
import com.huanghongbe.zoom.commons.entity.WebVisit;
import com.huanghongbe.zoom.utils.DateUtils;
import com.huanghongbe.zoom.utils.JsonUtils;
import com.huanghongbe.zoom.utils.RedisUtil;
import com.huanghongbe.zoom.utils.StringUtils;
import com.huanghongbe.zoom.xo.enums.RedisConf;
import com.huanghongbe.zoom.xo.mapper.WebVisitMapper;
import com.huanghongbe.zoom.xo.service.WebVisitService;
import com.huanghongbe.zoom.xo.vo.WebVisitVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-02-12 2:10
 */
@Service
public class WebVisitServiceImpl extends SuperServiceImpl<WebVisitMapper, WebVisit> implements WebVisitService {
    @Resource
    private WebVisitMapper webVisitMapper;
    @Autowired
    private RedisUtil redisUtil;
    @Override
    public void addWebVisit(String userUid, HttpServletRequest request, String behavior, String moduleUid, String otherData) {

    }

    @Override
    public int getWebVisitCount() {
        // 获取今日开始和结束时间
        String startTime = DateUtils.getToDayStartTime();
        String endTime = DateUtils.getToDayEndTime();
        return webVisitMapper.getIpCount(startTime, endTime);
    }

    @Override
    public Map<String, Object> getVisitByWeek() {
        // 从Redis中获取一周访问量
        String weekVisitJson = redisUtil.get(RedisConf.DASHBOARD + Constants.SYMBOL_COLON + RedisConf.WEEK_VISIT);
        if (StringUtils.isNotEmpty(weekVisitJson)) {
            Map<String, Object> weekVisitMap = JsonUtils.jsonToMap(weekVisitJson);
            return weekVisitMap;
        }

        // 获取到今天结束的时间
        String todayEndTime = DateUtils.getToDayEndTime();
        //获取最近七天的日期
        Date sevenDaysDate = DateUtils.getDate(todayEndTime, -6);
        String sevenDays = DateUtils.getOneDayStartTime(sevenDaysDate);
        // 获取最近七天的数组列表
        List<String> sevenDaysList = DateUtils.getDaysByN(7, "yyyy-MM-dd");
        // 获得最近七天的访问量
        List<Map<String, Object>> pvMap = webVisitMapper.getPVByWeek(sevenDays, todayEndTime);
        // 获得最近七天的独立用户
        List<Map<String, Object>> uvMap = webVisitMapper.getUVByWeek(sevenDays, todayEndTime);

        Map<String, Object> countPVMap = new HashMap<>();
        Map<String, Object> countUVMap = new HashMap<>();

        pvMap.forEach(item->{
            countPVMap.put(item.get("DATE").toString(), item.get("COUNT"));
        });
        uvMap.forEach(item->{
            countUVMap.put(item.get("DATE").toString(), item.get("COUNT"));
        });
        // 访问量数组
        List<Integer> pvList = new ArrayList<>();
        // 独立用户数组
        List<Integer> uvList = new ArrayList<>();

        sevenDaysList.forEach(day->{
            if (countPVMap.get(day) != null) {
                Number pvNumber = (Number) countPVMap.get(day);
                Number uvNumber = (Number) countUVMap.get(day);
                pvList.add(pvNumber.intValue());
                uvList.add(uvNumber.intValue());
            } else {
                pvList.add(0);
                uvList.add(0);
            }
        });
        Map<String, Object> resultMap = new HashMap<>(Constants.NUM_THREE);
        // 不含年份的数组格式
        List<String> resultSevenDaysList = DateUtils.getDaysByN(7, "MM-dd");
        resultMap.put("date", resultSevenDaysList);
        resultMap.put("pv", pvList);
        resultMap.put("uv", uvList);

        // 可能会存在短期的数据不一致的问题，即零点时不能准时更新，而是要在0:10才会重新刷新纪录。 后期考虑加入定时器处理这个问题
        // 将一周访问量存入Redis中【过期时间10分钟】
        redisUtil.setEx(RedisConf.DASHBOARD + Constants.SYMBOL_COLON + RedisConf.WEEK_VISIT, JsonUtils.objectToJson(resultMap), 10, TimeUnit.MINUTES);
        return resultMap;
    }

    @Override
    public IPage<WebVisit> getPageList(WebVisitVO webVisitVO) {
        return null;
    }
}
