package com.treefinance.saas.grapserver.dao.mapper;

import com.treefinance.saas.grapserver.dao.entity.AppColorConfig;
import com.treefinance.saas.grapserver.dao.entity.AppColorConfigCriteria;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

public interface AppColorConfigMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table app_color_config
     *
     * @mbggenerated Tue Jun 13 15:04:05 CST 2017
     */
    int countByExample(AppColorConfigCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table app_color_config
     *
     * @mbggenerated Tue Jun 13 15:04:05 CST 2017
     */
    int deleteByExample(AppColorConfigCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table app_color_config
     *
     * @mbggenerated Tue Jun 13 15:04:05 CST 2017
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table app_color_config
     *
     * @mbggenerated Tue Jun 13 15:04:05 CST 2017
     */
    int insert(AppColorConfig record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table app_color_config
     *
     * @mbggenerated Tue Jun 13 15:04:05 CST 2017
     */
    int insertSelective(AppColorConfig record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table app_color_config
     *
     * @mbggenerated Tue Jun 13 15:04:05 CST 2017
     */
    List<AppColorConfig> selectByExampleWithRowbounds(AppColorConfigCriteria example, RowBounds rowBounds);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table app_color_config
     *
     * @mbggenerated Tue Jun 13 15:04:05 CST 2017
     */
    List<AppColorConfig> selectByExample(AppColorConfigCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table app_color_config
     *
     * @mbggenerated Tue Jun 13 15:04:05 CST 2017
     */
    AppColorConfig selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table app_color_config
     *
     * @mbggenerated Tue Jun 13 15:04:05 CST 2017
     */
    int updateByExampleSelective(@Param("record") AppColorConfig record, @Param("example") AppColorConfigCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table app_color_config
     *
     * @mbggenerated Tue Jun 13 15:04:05 CST 2017
     */
    int updateByExample(@Param("record") AppColorConfig record, @Param("example") AppColorConfigCriteria example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table app_color_config
     *
     * @mbggenerated Tue Jun 13 15:04:05 CST 2017
     */
    int updateByPrimaryKeySelective(AppColorConfig record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table app_color_config
     *
     * @mbggenerated Tue Jun 13 15:04:05 CST 2017
     */
    int updateByPrimaryKey(AppColorConfig record);
}