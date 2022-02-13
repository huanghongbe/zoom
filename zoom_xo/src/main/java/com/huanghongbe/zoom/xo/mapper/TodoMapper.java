package com.huanghongbe.zoom.xo.mapper;

import com.huanghongbe.zoom.base.enums.EStatus;
import com.huanghongbe.zoom.base.mapper.SuperMapper;
import com.huanghongbe.zoom.commons.entity.Todo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author ：huanghongbe
 * @description：
 * @date ：2022-02-12 2:45
 */
public interface TodoMapper extends SuperMapper<Todo> {
    /**
     * 批量更新未删除的代表事项的状态
     *
     * @param done
     */
    @Select("UPDATE t_todo SET done = #{done} WHERE STATUS = " + EStatus.ENABLE + " AND admin_uid = #{adminUid}")
    public void toggleAll(@Param("done") Integer done, @Param("adminUid") String adminUid);
}
