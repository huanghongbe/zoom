package com.huanghongbe.zoom.commons.fallback;


import com.huanghongbe.zoom.commons.feign.SearchFeignClient;
import com.huanghongbe.zoom.utils.ResultUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 搜索服务降级兜底方法【当服务不可用时会触发】
 *
 */
@Component
@Slf4j
public class SearchFeignFallback implements SearchFeignClient {

    @Override
    public String deleteElasticSearchByUid(String uid) {
        log.error("搜索服务出现异常, 服务降级返回, 删除ElasticSearch索引失败");
        return ResultUtil.errorWithMessage("搜索服务出现异常, 服务降级返回, 删除ElasticSearch索引失败");
    }

    @Override
    public String deleteElasticSearchByUids(String uids) {
        log.error("搜索服务出现异常, 服务降级返回, 批量删除ElasticSearch索引失败");
        return ResultUtil.errorWithMessage("搜索服务出现异常, 服务降级返回, 批量删除ElasticSearch索引失败");
    }

    @Override
    public String initElasticSearchIndex() {
        log.error("搜索服务出现异常, 服务降级返回, 初始化ElasticSearch索引失败");
        return ResultUtil.errorWithMessage("搜索服务出现异常, 服务降级返回, 初始化ElasticSearch索引失败");
    }

    @Override
    public String addElasticSearchIndexByUid(String uid) {
        log.error("搜索服务出现异常, 服务降级返回, 添加ElasticSearch索引失败");
        return ResultUtil.errorWithMessage("搜索服务出现异常, 服务降级返回, 添加ElasticSearch索引失败");
    }

}
