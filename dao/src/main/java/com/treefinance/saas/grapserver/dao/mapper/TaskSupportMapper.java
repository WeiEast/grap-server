package com.treefinance.saas.grapserver.dao.mapper;

import com.treefinance.saas.grapserver.dao.entity.TaskSupport;
import com.treefinance.saas.grapserver.dao.entity.TaskSupportCriteria;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface TaskSupportMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_support
     *
     * @mbg.generated Mon Mar 05 16:33:15 CST 2018
     */
    long countByExample(TaskSupportCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_support
     *
     * @mbg.generated Mon Mar 05 16:33:15 CST 2018
     */
    int deleteByExample(TaskSupportCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_support
     *
     * @mbg.generated Mon Mar 05 16:33:15 CST 2018
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_support
     *
     * @mbg.generated Mon Mar 05 16:33:15 CST 2018
     */
    int insert(TaskSupport record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_support
     *
     * @mbg.generated Mon Mar 05 16:33:15 CST 2018
     */
    int insertSelective(TaskSupport record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_support
     *
     * @mbg.generated Mon Mar 05 16:33:15 CST 2018
     */
    List<TaskSupport> selectByExampleWithRowbounds(TaskSupportCriteria example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_support
     *
     * @mbg.generated Mon Mar 05 16:33:15 CST 2018
     */
    List<TaskSupport> selectByExample(TaskSupportCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_support
     *
     * @mbg.generated Mon Mar 05 16:33:15 CST 2018
     */
    TaskSupport selectByPrimaryKey(Integer id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_support
     *
     * @mbg.generated Mon Mar 05 16:33:15 CST 2018
     */
    int updateByExampleSelective(@Param("record") TaskSupport record, @Param("example") TaskSupportCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_support
     *
     * @mbg.generated Mon Mar 05 16:33:15 CST 2018
     */
    int updateByExample(@Param("record") TaskSupport record, @Param("example") TaskSupportCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_support
     *
     * @mbg.generated Mon Mar 05 16:33:15 CST 2018
     */
    int updateByPrimaryKeySelective(TaskSupport record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_support
     *
     * @mbg.generated Mon Mar 05 16:33:15 CST 2018
     */
    int updateByPrimaryKey(TaskSupport record);
}