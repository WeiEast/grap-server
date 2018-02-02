package com.treefinance.saas.grapserver.dao.mapper;

import com.treefinance.saas.grapserver.dao.entity.TaskCallbackLog;
import com.treefinance.saas.grapserver.dao.entity.TaskCallbackLogCriteria;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

public interface TaskCallbackLogMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_callback_log
     *
     * @mbg.generated Wed Jan 31 16:58:01 CST 2018
     */
    long countByExample(TaskCallbackLogCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_callback_log
     *
     * @mbg.generated Wed Jan 31 16:58:01 CST 2018
     */
    int deleteByExample(TaskCallbackLogCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_callback_log
     *
     * @mbg.generated Wed Jan 31 16:58:01 CST 2018
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_callback_log
     *
     * @mbg.generated Wed Jan 31 16:58:01 CST 2018
     */
    int insert(TaskCallbackLog record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_callback_log
     *
     * @mbg.generated Wed Jan 31 16:58:01 CST 2018
     */
    int insertSelective(TaskCallbackLog record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_callback_log
     *
     * @mbg.generated Wed Jan 31 16:58:01 CST 2018
     */
    List<TaskCallbackLog> selectByExampleWithRowbounds(TaskCallbackLogCriteria example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_callback_log
     *
     * @mbg.generated Wed Jan 31 16:58:01 CST 2018
     */
    List<TaskCallbackLog> selectByExample(TaskCallbackLogCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_callback_log
     *
     * @mbg.generated Wed Jan 31 16:58:01 CST 2018
     */
    List<TaskCallbackLog> selectPaginationByExample(TaskCallbackLogCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_callback_log
     *
     * @mbg.generated Wed Jan 31 16:58:01 CST 2018
     */
    void batchInsert(List<TaskCallbackLog> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_callback_log
     *
     * @mbg.generated Wed Jan 31 16:58:01 CST 2018
     */
    void batchUpdateByPrimaryKey(List<TaskCallbackLog> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_callback_log
     *
     * @mbg.generated Wed Jan 31 16:58:01 CST 2018
     */
    void batchUpdateByPrimaryKeySelective(List<TaskCallbackLog> list);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_callback_log
     *
     * @mbg.generated Wed Jan 31 16:58:01 CST 2018
     */
    TaskCallbackLog selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_callback_log
     *
     * @mbg.generated Wed Jan 31 16:58:01 CST 2018
     */
    int updateByExampleSelective(@Param("record") TaskCallbackLog record, @Param("example") TaskCallbackLogCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_callback_log
     *
     * @mbg.generated Wed Jan 31 16:58:01 CST 2018
     */
    int updateByExample(@Param("record") TaskCallbackLog record, @Param("example") TaskCallbackLogCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_callback_log
     *
     * @mbg.generated Wed Jan 31 16:58:01 CST 2018
     */
    int updateByPrimaryKeySelective(TaskCallbackLog record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_callback_log
     *
     * @mbg.generated Wed Jan 31 16:58:01 CST 2018
     */
    int updateByPrimaryKey(TaskCallbackLog record);
}