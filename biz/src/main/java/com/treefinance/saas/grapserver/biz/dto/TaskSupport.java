/*
 * Copyright © 2015 - 2017 杭州大树网络技术有限公司. All Rights Reserved
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.treefinance.saas.grapserver.biz.dto;

import java.io.Serializable;
import java.util.Date;

public class TaskSupport implements Serializable {
    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_support.Id
     *
     * @mbg.generated Wed Mar 07 11:32:02 CST 2018
     */
    private Integer id;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_support.Category
     *
     * @mbg.generated Wed Mar 07 11:32:02 CST 2018
     */
    private String category;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_support.Type
     *
     * @mbg.generated Wed Mar 07 11:32:02 CST 2018
     */
    private String type;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_support.Name
     *
     * @mbg.generated Wed Mar 07 11:32:02 CST 2018
     */
    private String name;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_support.Image
     *
     * @mbg.generated Wed Mar 07 11:32:02 CST 2018
     */
    private String image;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_support.Enable
     *
     * @mbg.generated Wed Mar 07 11:32:02 CST 2018
     */
    private Boolean enable;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_support.IsShow
     *
     * @mbg.generated Wed Mar 07 11:32:02 CST 2018
     */
    private Boolean isShow;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_support.Sort
     *
     * @mbg.generated Wed Mar 07 11:32:02 CST 2018
     */
    private Integer sort;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_support.CreateTime
     *
     * @mbg.generated Wed Mar 07 11:32:02 CST 2018
     */
    private Date createTime;

    /**
     *
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database column task_support.LastUpdateTime
     *
     * @mbg.generated Wed Mar 07 11:32:02 CST 2018
     */
    private Date lastUpdateTime;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table task_support
     *
     * @mbg.generated Wed Mar 07 11:32:02 CST 2018
     */
    private static final long serialVersionUID = 1L;

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_support.Id
     *
     * @return the value of task_support.Id
     *
     * @mbg.generated Wed Mar 07 11:32:02 CST 2018
     */
    public Integer getId() {
        return id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_support.Id
     *
     * @param id the value for task_support.Id
     *
     * @mbg.generated Wed Mar 07 11:32:02 CST 2018
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_support.Category
     *
     * @return the value of task_support.Category
     *
     * @mbg.generated Wed Mar 07 11:32:02 CST 2018
     */
    public String getCategory() {
        return category;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_support.Category
     *
     * @param category the value for task_support.Category
     *
     * @mbg.generated Wed Mar 07 11:32:02 CST 2018
     */
    public void setCategory(String category) {
        this.category = category == null ? null : category.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_support.Type
     *
     * @return the value of task_support.Type
     *
     * @mbg.generated Wed Mar 07 11:32:02 CST 2018
     */
    public String getType() {
        return type;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_support.Type
     *
     * @param type the value for task_support.Type
     *
     * @mbg.generated Wed Mar 07 11:32:02 CST 2018
     */
    public void setType(String type) {
        this.type = type == null ? null : type.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_support.Name
     *
     * @return the value of task_support.Name
     *
     * @mbg.generated Wed Mar 07 11:32:02 CST 2018
     */
    public String getName() {
        return name;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_support.Name
     *
     * @param name the value for task_support.Name
     *
     * @mbg.generated Wed Mar 07 11:32:02 CST 2018
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_support.Image
     *
     * @return the value of task_support.Image
     *
     * @mbg.generated Wed Mar 07 11:32:02 CST 2018
     */
    public String getImage() {
        return image;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_support.Image
     *
     * @param image the value for task_support.Image
     *
     * @mbg.generated Wed Mar 07 11:32:02 CST 2018
     */
    public void setImage(String image) {
        this.image = image == null ? null : image.trim();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_support.Enable
     *
     * @return the value of task_support.Enable
     *
     * @mbg.generated Wed Mar 07 11:32:02 CST 2018
     */
    public Boolean getEnable() {
        return enable;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_support.Enable
     *
     * @param enable the value for task_support.Enable
     *
     * @mbg.generated Wed Mar 07 11:32:02 CST 2018
     */
    public void setEnable(Boolean enable) {
        this.enable = enable;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_support.IsShow
     *
     * @return the value of task_support.IsShow
     *
     * @mbg.generated Wed Mar 07 11:32:02 CST 2018
     */
    public Boolean getIsShow() {
        return isShow;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_support.IsShow
     *
     * @param isShow the value for task_support.IsShow
     *
     * @mbg.generated Wed Mar 07 11:32:02 CST 2018
     */
    public void setIsShow(Boolean isShow) {
        this.isShow = isShow;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_support.Sort
     *
     * @return the value of task_support.Sort
     *
     * @mbg.generated Wed Mar 07 11:32:02 CST 2018
     */
    public Integer getSort() {
        return sort;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_support.Sort
     *
     * @param sort the value for task_support.Sort
     *
     * @mbg.generated Wed Mar 07 11:32:02 CST 2018
     */
    public void setSort(Integer sort) {
        this.sort = sort;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_support.CreateTime
     *
     * @return the value of task_support.CreateTime
     *
     * @mbg.generated Wed Mar 07 11:32:02 CST 2018
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_support.CreateTime
     *
     * @param createTime the value for task_support.CreateTime
     *
     * @mbg.generated Wed Mar 07 11:32:02 CST 2018
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method returns the value of the database column task_support.LastUpdateTime
     *
     * @return the value of task_support.LastUpdateTime
     *
     * @mbg.generated Wed Mar 07 11:32:02 CST 2018
     */
    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method sets the value of the database column task_support.LastUpdateTime
     *
     * @param lastUpdateTime the value for task_support.LastUpdateTime
     *
     * @mbg.generated Wed Mar 07 11:32:02 CST 2018
     */
    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table task_support
     *
     * @mbg.generated Wed Mar 07 11:32:02 CST 2018
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", id=").append(id);
        sb.append(", category=").append(category);
        sb.append(", type=").append(type);
        sb.append(", name=").append(name);
        sb.append(", image=").append(image);
        sb.append(", enable=").append(enable);
        sb.append(", isShow=").append(isShow);
        sb.append(", sort=").append(sort);
        sb.append(", createTime=").append(createTime);
        sb.append(", lastUpdateTime=").append(lastUpdateTime);
        sb.append("]");
        return sb.toString();
    }
}