package com.treefinance.saas.grapserver.dao.mapper;

import com.treefinance.saas.grapserver.dao.entity.TaskNextDirective;
import com.treefinance.saas.grapserver.dao.entity.TaskNextDirectiveCriteria;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface TaskNextDirectiveMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_next_directive
     *
     * @mbggenerated Wed Apr 26 19:21:20 CST 2017
     */
    int countByExample(TaskNextDirectiveCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_next_directive
     *
     * @mbggenerated Wed Apr 26 19:21:20 CST 2017
     */
    int deleteByExample(TaskNextDirectiveCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_next_directive
     *
     * @mbggenerated Wed Apr 26 19:21:20 CST 2017
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_next_directive
     *
     * @mbggenerated Wed Apr 26 19:21:20 CST 2017
     */
    int insert(TaskNextDirective record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_next_directive
     *
     * @mbggenerated Wed Apr 26 19:21:20 CST 2017
     */
    int insertSelective(TaskNextDirective record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_next_directive
     *
     * @mbggenerated Wed Apr 26 19:21:20 CST 2017
     */
    List<TaskNextDirective> selectByExampleWithRowbounds(TaskNextDirectiveCriteria example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_next_directive
     *
     * @mbggenerated Wed Apr 26 19:21:20 CST 2017
     */
    List<TaskNextDirective> selectByExample(TaskNextDirectiveCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_next_directive
     *
     * @mbggenerated Wed Apr 26 19:21:20 CST 2017
     */
    TaskNextDirective selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_next_directive
     *
     * @mbggenerated Wed Apr 26 19:21:20 CST 2017
     */
    int updateByExampleSelective(@Param("record") TaskNextDirective record, @Param("example") TaskNextDirectiveCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_next_directive
     *
     * @mbggenerated Wed Apr 26 19:21:20 CST 2017
     */
    int updateByExample(@Param("record") TaskNextDirective record, @Param("example") TaskNextDirectiveCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_next_directive
     *
     * @mbggenerated Wed Apr 26 19:21:20 CST 2017
     */
    int updateByPrimaryKeySelective(TaskNextDirective record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_next_directive
     *
     * @mbggenerated Wed Apr 26 19:21:20 CST 2017
     */
    int updateByPrimaryKey(TaskNextDirective record);
}