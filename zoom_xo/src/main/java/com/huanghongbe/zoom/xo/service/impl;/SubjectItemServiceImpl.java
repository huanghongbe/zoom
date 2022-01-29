import com.baomidou.mybatisplus.core.metadata.IPage;
import com.huanghongbe.zoom.base.service.impl.SuperServiceImpl;
import com.huanghongbe.zoom.commons.entity.SubjectItem;
import com.huanghongbe.zoom.xo.mapper.SubjectItemMapper;
import com.huanghongbe.zoom.xo.service.SubjectItemService;
import com.huanghongbe.zoom.xo.vo.SubjectItemVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-01-30 3:57
 */
@Service
public class SubjectItemServiceImpl extends SuperServiceImpl<SubjectItemMapper, SubjectItem> implements SubjectItemService {


    @Override
    public IPage<SubjectItem> getPageList(SubjectItemVO subjectItemVO) {
        return null;
    }

    @Override
    public String addSubjectItemList(List<SubjectItemVO> subjectItemVOList) {
        return null;
    }

    @Override
    public String editSubjectItemList(List<SubjectItemVO> subjectItemVOList) {
        return null;
    }

    @Override
    public String deleteBatchSubjectItem(List<SubjectItemVO> subjectItemVOList) {
        return null;
    }

    @Override
    public String deleteBatchSubjectItemByBlogUid(List<String> blogUid) {
        return null;
    }

    @Override
    public String sortByCreateTime(String subjectUid, Boolean isDesc) {
        return null;
    }
}
