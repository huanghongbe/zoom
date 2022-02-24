package com.huanghongbe.zoom.search.restapi;

import com.huanghongbe.zoom.commons.entity.Blog;
import com.huanghongbe.zoom.commons.feign.WebFeignClient;
import com.huanghongbe.zoom.search.enums.MessageConf;
import com.huanghongbe.zoom.search.enums.SysConf;
import com.huanghongbe.zoom.search.pojo.ESBlogIndex;
import com.huanghongbe.zoom.search.repository.BlogRepository;
import com.huanghongbe.zoom.search.service.ElasticSearchService;
import com.huanghongbe.zoom.utils.ResultUtil;
import com.huanghongbe.zoom.utils.StringUtils;
import com.huanghongbe.zoom.utils.WebUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ElasticSearch RestAPI
 *
 */
@RequestMapping("/search")
@RestController
public class ElasticSearchRestApi {

    @Autowired
    ElasticsearchTemplate elasticsearchTemplate;
    @Autowired
    private ElasticSearchService searchService;
    @Autowired
    private BlogRepository blogRepository;
    @Resource
    private WebFeignClient webFeignClient;


    @GetMapping("/elasticSearchBlog")
    public String searchBlog(HttpServletRequest request,
                             @RequestParam(required = false) String keywords,
                             @RequestParam(name = "currentPage", required = false, defaultValue = "1") Integer
                                     currentPage,
                             @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer
                                     pageSize) {

        if (StringUtils.isEmpty(keywords)) {
            return ResultUtil.result(SysConf.ERROR, MessageConf.KEYWORD_IS_NOT_EMPTY);
        }
        return ResultUtil.result(SysConf.SUCCESS, searchService.search(keywords, currentPage, pageSize));
    }

    @PostMapping("/deleteElasticSearchByUids")
    public String deleteElasticSearchByUids(@RequestParam(required = true, value = "uid") String uids) {

        List<String> uidList = StringUtils.changeStringToString(uids, SysConf.FILE_SEGMENTATION);

        for (String uid : uidList) {
            blogRepository.deleteById(uid);
        }

        return ResultUtil.result(SysConf.SUCCESS, MessageConf.DELETE_SUCCESS);
    }

    @PostMapping("/deleteElasticSearchByUid")
    public String deleteElasticSearchByUid(@RequestParam(required = true) String uid) {
        blogRepository.deleteById(uid);
        return ResultUtil.result(SysConf.SUCCESS, MessageConf.DELETE_SUCCESS);
    }

    @PostMapping("/addElasticSearchIndexByUid")
    public String addElasticSearchIndexByUid(@RequestParam(required = true) String uid) {

        String result = webFeignClient.getBlogByUid(uid);

        Blog eblog = WebUtils.getData(result, Blog.class);
        if (eblog == null) {
            return ResultUtil.result(SysConf.ERROR, MessageConf.INSERT_FAIL);
        }
        ESBlogIndex blog = searchService.buidBlog(eblog);
        blogRepository.save(blog);
        return ResultUtil.result(SysConf.SUCCESS, MessageConf.INSERT_SUCCESS);
    }

    @PostMapping("/initElasticSearchIndex")
    public String initElasticSearchIndex() throws ParseException {
        elasticsearchTemplate.deleteIndex(ESBlogIndex.class);
        elasticsearchTemplate.createIndex(ESBlogIndex.class);
        elasticsearchTemplate.putMapping(ESBlogIndex.class);

        Long page = 1L;
        Long row = 10L;
        Integer size = 0;

        do {
            // 查询blog信息
            String result = webFeignClient.getBlogBySearch(page, row);

            //构建blog
            List<Blog> blogList = WebUtils.getList(result, Blog.class);
            size = blogList.size();

            List<ESBlogIndex> esBlogIndexList = blogList.stream()
                    .map(searchService::buidBlog).collect(Collectors.toList());

            //存入索引库
            blogRepository.saveAll(esBlogIndexList);
            // 翻页
            page++;
        } while (size == 15);

        return ResultUtil.result(SysConf.SUCCESS, MessageConf.OPERATION_SUCCESS);
    }
}
