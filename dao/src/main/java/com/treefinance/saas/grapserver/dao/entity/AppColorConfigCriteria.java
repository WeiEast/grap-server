package com.treefinance.saas.grapserver.dao.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AppColorConfigCriteria {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table app_color_config
     *
     * @mbggenerated Mon Dec 25 14:33:20 CST 2017
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table app_color_config
     *
     * @mbggenerated Mon Dec 25 14:33:20 CST 2017
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table app_color_config
     *
     * @mbggenerated Mon Dec 25 14:33:20 CST 2017
     */
    protected List<Criteria> oredCriteria;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table app_color_config
     *
     * @mbggenerated Mon Dec 25 14:33:20 CST 2017
     */
    public AppColorConfigCriteria() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table app_color_config
     *
     * @mbggenerated Mon Dec 25 14:33:20 CST 2017
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table app_color_config
     *
     * @mbggenerated Mon Dec 25 14:33:20 CST 2017
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table app_color_config
     *
     * @mbggenerated Mon Dec 25 14:33:20 CST 2017
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table app_color_config
     *
     * @mbggenerated Mon Dec 25 14:33:20 CST 2017
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table app_color_config
     *
     * @mbggenerated Mon Dec 25 14:33:20 CST 2017
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table app_color_config
     *
     * @mbggenerated Mon Dec 25 14:33:20 CST 2017
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table app_color_config
     *
     * @mbggenerated Mon Dec 25 14:33:20 CST 2017
     */
    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table app_color_config
     *
     * @mbggenerated Mon Dec 25 14:33:20 CST 2017
     */
    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table app_color_config
     *
     * @mbggenerated Mon Dec 25 14:33:20 CST 2017
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table app_color_config
     *
     * @mbggenerated Mon Dec 25 14:33:20 CST 2017
     */
    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table app_color_config
     *
     * @mbggenerated Mon Dec 25 14:33:20 CST 2017
     */
    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andIdIsNull() {
            addCriterion("Id is null");
            return (Criteria) this;
        }

        public Criteria andIdIsNotNull() {
            addCriterion("Id is not null");
            return (Criteria) this;
        }

        public Criteria andIdEqualTo(Long value) {
            addCriterion("Id =", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotEqualTo(Long value) {
            addCriterion("Id <>", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThan(Long value) {
            addCriterion("Id >", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdGreaterThanOrEqualTo(Long value) {
            addCriterion("Id >=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThan(Long value) {
            addCriterion("Id <", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdLessThanOrEqualTo(Long value) {
            addCriterion("Id <=", value, "id");
            return (Criteria) this;
        }

        public Criteria andIdIn(List<Long> values) {
            addCriterion("Id in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotIn(List<Long> values) {
            addCriterion("Id not in", values, "id");
            return (Criteria) this;
        }

        public Criteria andIdBetween(Long value1, Long value2) {
            addCriterion("Id between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andIdNotBetween(Long value1, Long value2) {
            addCriterion("Id not between", value1, value2, "id");
            return (Criteria) this;
        }

        public Criteria andAppIdIsNull() {
            addCriterion("AppId is null");
            return (Criteria) this;
        }

        public Criteria andAppIdIsNotNull() {
            addCriterion("AppId is not null");
            return (Criteria) this;
        }

        public Criteria andAppIdEqualTo(String value) {
            addCriterion("AppId =", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdNotEqualTo(String value) {
            addCriterion("AppId <>", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdGreaterThan(String value) {
            addCriterion("AppId >", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdGreaterThanOrEqualTo(String value) {
            addCriterion("AppId >=", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdLessThan(String value) {
            addCriterion("AppId <", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdLessThanOrEqualTo(String value) {
            addCriterion("AppId <=", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdLike(String value) {
            addCriterion("AppId like", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdNotLike(String value) {
            addCriterion("AppId not like", value, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdIn(List<String> values) {
            addCriterion("AppId in", values, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdNotIn(List<String> values) {
            addCriterion("AppId not in", values, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdBetween(String value1, String value2) {
            addCriterion("AppId between", value1, value2, "appId");
            return (Criteria) this;
        }

        public Criteria andAppIdNotBetween(String value1, String value2) {
            addCriterion("AppId not between", value1, value2, "appId");
            return (Criteria) this;
        }

        public Criteria andStyleIsNull() {
            addCriterion("Style is null");
            return (Criteria) this;
        }

        public Criteria andStyleIsNotNull() {
            addCriterion("Style is not null");
            return (Criteria) this;
        }

        public Criteria andStyleEqualTo(String value) {
            addCriterion("Style =", value, "style");
            return (Criteria) this;
        }

        public Criteria andStyleNotEqualTo(String value) {
            addCriterion("Style <>", value, "style");
            return (Criteria) this;
        }

        public Criteria andStyleGreaterThan(String value) {
            addCriterion("Style >", value, "style");
            return (Criteria) this;
        }

        public Criteria andStyleGreaterThanOrEqualTo(String value) {
            addCriterion("Style >=", value, "style");
            return (Criteria) this;
        }

        public Criteria andStyleLessThan(String value) {
            addCriterion("Style <", value, "style");
            return (Criteria) this;
        }

        public Criteria andStyleLessThanOrEqualTo(String value) {
            addCriterion("Style <=", value, "style");
            return (Criteria) this;
        }

        public Criteria andStyleLike(String value) {
            addCriterion("Style like", value, "style");
            return (Criteria) this;
        }

        public Criteria andStyleNotLike(String value) {
            addCriterion("Style not like", value, "style");
            return (Criteria) this;
        }

        public Criteria andStyleIn(List<String> values) {
            addCriterion("Style in", values, "style");
            return (Criteria) this;
        }

        public Criteria andStyleNotIn(List<String> values) {
            addCriterion("Style not in", values, "style");
            return (Criteria) this;
        }

        public Criteria andStyleBetween(String value1, String value2) {
            addCriterion("Style between", value1, value2, "style");
            return (Criteria) this;
        }

        public Criteria andStyleNotBetween(String value1, String value2) {
            addCriterion("Style not between", value1, value2, "style");
            return (Criteria) this;
        }

        public Criteria andMainIsNull() {
            addCriterion("Main is null");
            return (Criteria) this;
        }

        public Criteria andMainIsNotNull() {
            addCriterion("Main is not null");
            return (Criteria) this;
        }

        public Criteria andMainEqualTo(String value) {
            addCriterion("Main =", value, "main");
            return (Criteria) this;
        }

        public Criteria andMainNotEqualTo(String value) {
            addCriterion("Main <>", value, "main");
            return (Criteria) this;
        }

        public Criteria andMainGreaterThan(String value) {
            addCriterion("Main >", value, "main");
            return (Criteria) this;
        }

        public Criteria andMainGreaterThanOrEqualTo(String value) {
            addCriterion("Main >=", value, "main");
            return (Criteria) this;
        }

        public Criteria andMainLessThan(String value) {
            addCriterion("Main <", value, "main");
            return (Criteria) this;
        }

        public Criteria andMainLessThanOrEqualTo(String value) {
            addCriterion("Main <=", value, "main");
            return (Criteria) this;
        }

        public Criteria andMainLike(String value) {
            addCriterion("Main like", value, "main");
            return (Criteria) this;
        }

        public Criteria andMainNotLike(String value) {
            addCriterion("Main not like", value, "main");
            return (Criteria) this;
        }

        public Criteria andMainIn(List<String> values) {
            addCriterion("Main in", values, "main");
            return (Criteria) this;
        }

        public Criteria andMainNotIn(List<String> values) {
            addCriterion("Main not in", values, "main");
            return (Criteria) this;
        }

        public Criteria andMainBetween(String value1, String value2) {
            addCriterion("Main between", value1, value2, "main");
            return (Criteria) this;
        }

        public Criteria andMainNotBetween(String value1, String value2) {
            addCriterion("Main not between", value1, value2, "main");
            return (Criteria) this;
        }

        public Criteria andAssistIsNull() {
            addCriterion("Assist is null");
            return (Criteria) this;
        }

        public Criteria andAssistIsNotNull() {
            addCriterion("Assist is not null");
            return (Criteria) this;
        }

        public Criteria andAssistEqualTo(String value) {
            addCriterion("Assist =", value, "assist");
            return (Criteria) this;
        }

        public Criteria andAssistNotEqualTo(String value) {
            addCriterion("Assist <>", value, "assist");
            return (Criteria) this;
        }

        public Criteria andAssistGreaterThan(String value) {
            addCriterion("Assist >", value, "assist");
            return (Criteria) this;
        }

        public Criteria andAssistGreaterThanOrEqualTo(String value) {
            addCriterion("Assist >=", value, "assist");
            return (Criteria) this;
        }

        public Criteria andAssistLessThan(String value) {
            addCriterion("Assist <", value, "assist");
            return (Criteria) this;
        }

        public Criteria andAssistLessThanOrEqualTo(String value) {
            addCriterion("Assist <=", value, "assist");
            return (Criteria) this;
        }

        public Criteria andAssistLike(String value) {
            addCriterion("Assist like", value, "assist");
            return (Criteria) this;
        }

        public Criteria andAssistNotLike(String value) {
            addCriterion("Assist not like", value, "assist");
            return (Criteria) this;
        }

        public Criteria andAssistIn(List<String> values) {
            addCriterion("Assist in", values, "assist");
            return (Criteria) this;
        }

        public Criteria andAssistNotIn(List<String> values) {
            addCriterion("Assist not in", values, "assist");
            return (Criteria) this;
        }

        public Criteria andAssistBetween(String value1, String value2) {
            addCriterion("Assist between", value1, value2, "assist");
            return (Criteria) this;
        }

        public Criteria andAssistNotBetween(String value1, String value2) {
            addCriterion("Assist not between", value1, value2, "assist");
            return (Criteria) this;
        }

        public Criteria andAssistErrorIsNull() {
            addCriterion("AssistError is null");
            return (Criteria) this;
        }

        public Criteria andAssistErrorIsNotNull() {
            addCriterion("AssistError is not null");
            return (Criteria) this;
        }

        public Criteria andAssistErrorEqualTo(String value) {
            addCriterion("AssistError =", value, "assistError");
            return (Criteria) this;
        }

        public Criteria andAssistErrorNotEqualTo(String value) {
            addCriterion("AssistError <>", value, "assistError");
            return (Criteria) this;
        }

        public Criteria andAssistErrorGreaterThan(String value) {
            addCriterion("AssistError >", value, "assistError");
            return (Criteria) this;
        }

        public Criteria andAssistErrorGreaterThanOrEqualTo(String value) {
            addCriterion("AssistError >=", value, "assistError");
            return (Criteria) this;
        }

        public Criteria andAssistErrorLessThan(String value) {
            addCriterion("AssistError <", value, "assistError");
            return (Criteria) this;
        }

        public Criteria andAssistErrorLessThanOrEqualTo(String value) {
            addCriterion("AssistError <=", value, "assistError");
            return (Criteria) this;
        }

        public Criteria andAssistErrorLike(String value) {
            addCriterion("AssistError like", value, "assistError");
            return (Criteria) this;
        }

        public Criteria andAssistErrorNotLike(String value) {
            addCriterion("AssistError not like", value, "assistError");
            return (Criteria) this;
        }

        public Criteria andAssistErrorIn(List<String> values) {
            addCriterion("AssistError in", values, "assistError");
            return (Criteria) this;
        }

        public Criteria andAssistErrorNotIn(List<String> values) {
            addCriterion("AssistError not in", values, "assistError");
            return (Criteria) this;
        }

        public Criteria andAssistErrorBetween(String value1, String value2) {
            addCriterion("AssistError between", value1, value2, "assistError");
            return (Criteria) this;
        }

        public Criteria andAssistErrorNotBetween(String value1, String value2) {
            addCriterion("AssistError not between", value1, value2, "assistError");
            return (Criteria) this;
        }

        public Criteria andBackBtnAndFontColorIsNull() {
            addCriterion("BackBtnAndFontColor is null");
            return (Criteria) this;
        }

        public Criteria andBackBtnAndFontColorIsNotNull() {
            addCriterion("BackBtnAndFontColor is not null");
            return (Criteria) this;
        }

        public Criteria andBackBtnAndFontColorEqualTo(String value) {
            addCriterion("BackBtnAndFontColor =", value, "backBtnAndFontColor");
            return (Criteria) this;
        }

        public Criteria andBackBtnAndFontColorNotEqualTo(String value) {
            addCriterion("BackBtnAndFontColor <>", value, "backBtnAndFontColor");
            return (Criteria) this;
        }

        public Criteria andBackBtnAndFontColorGreaterThan(String value) {
            addCriterion("BackBtnAndFontColor >", value, "backBtnAndFontColor");
            return (Criteria) this;
        }

        public Criteria andBackBtnAndFontColorGreaterThanOrEqualTo(String value) {
            addCriterion("BackBtnAndFontColor >=", value, "backBtnAndFontColor");
            return (Criteria) this;
        }

        public Criteria andBackBtnAndFontColorLessThan(String value) {
            addCriterion("BackBtnAndFontColor <", value, "backBtnAndFontColor");
            return (Criteria) this;
        }

        public Criteria andBackBtnAndFontColorLessThanOrEqualTo(String value) {
            addCriterion("BackBtnAndFontColor <=", value, "backBtnAndFontColor");
            return (Criteria) this;
        }

        public Criteria andBackBtnAndFontColorLike(String value) {
            addCriterion("BackBtnAndFontColor like", value, "backBtnAndFontColor");
            return (Criteria) this;
        }

        public Criteria andBackBtnAndFontColorNotLike(String value) {
            addCriterion("BackBtnAndFontColor not like", value, "backBtnAndFontColor");
            return (Criteria) this;
        }

        public Criteria andBackBtnAndFontColorIn(List<String> values) {
            addCriterion("BackBtnAndFontColor in", values, "backBtnAndFontColor");
            return (Criteria) this;
        }

        public Criteria andBackBtnAndFontColorNotIn(List<String> values) {
            addCriterion("BackBtnAndFontColor not in", values, "backBtnAndFontColor");
            return (Criteria) this;
        }

        public Criteria andBackBtnAndFontColorBetween(String value1, String value2) {
            addCriterion("BackBtnAndFontColor between", value1, value2, "backBtnAndFontColor");
            return (Criteria) this;
        }

        public Criteria andBackBtnAndFontColorNotBetween(String value1, String value2) {
            addCriterion("BackBtnAndFontColor not between", value1, value2, "backBtnAndFontColor");
            return (Criteria) this;
        }

        public Criteria andBackgroundIsNull() {
            addCriterion("Background is null");
            return (Criteria) this;
        }

        public Criteria andBackgroundIsNotNull() {
            addCriterion("Background is not null");
            return (Criteria) this;
        }

        public Criteria andBackgroundEqualTo(String value) {
            addCriterion("Background =", value, "background");
            return (Criteria) this;
        }

        public Criteria andBackgroundNotEqualTo(String value) {
            addCriterion("Background <>", value, "background");
            return (Criteria) this;
        }

        public Criteria andBackgroundGreaterThan(String value) {
            addCriterion("Background >", value, "background");
            return (Criteria) this;
        }

        public Criteria andBackgroundGreaterThanOrEqualTo(String value) {
            addCriterion("Background >=", value, "background");
            return (Criteria) this;
        }

        public Criteria andBackgroundLessThan(String value) {
            addCriterion("Background <", value, "background");
            return (Criteria) this;
        }

        public Criteria andBackgroundLessThanOrEqualTo(String value) {
            addCriterion("Background <=", value, "background");
            return (Criteria) this;
        }

        public Criteria andBackgroundLike(String value) {
            addCriterion("Background like", value, "background");
            return (Criteria) this;
        }

        public Criteria andBackgroundNotLike(String value) {
            addCriterion("Background not like", value, "background");
            return (Criteria) this;
        }

        public Criteria andBackgroundIn(List<String> values) {
            addCriterion("Background in", values, "background");
            return (Criteria) this;
        }

        public Criteria andBackgroundNotIn(List<String> values) {
            addCriterion("Background not in", values, "background");
            return (Criteria) this;
        }

        public Criteria andBackgroundBetween(String value1, String value2) {
            addCriterion("Background between", value1, value2, "background");
            return (Criteria) this;
        }

        public Criteria andBackgroundNotBetween(String value1, String value2) {
            addCriterion("Background not between", value1, value2, "background");
            return (Criteria) this;
        }

        public Criteria andBtnDisabledIsNull() {
            addCriterion("BtnDisabled is null");
            return (Criteria) this;
        }

        public Criteria andBtnDisabledIsNotNull() {
            addCriterion("BtnDisabled is not null");
            return (Criteria) this;
        }

        public Criteria andBtnDisabledEqualTo(String value) {
            addCriterion("BtnDisabled =", value, "btnDisabled");
            return (Criteria) this;
        }

        public Criteria andBtnDisabledNotEqualTo(String value) {
            addCriterion("BtnDisabled <>", value, "btnDisabled");
            return (Criteria) this;
        }

        public Criteria andBtnDisabledGreaterThan(String value) {
            addCriterion("BtnDisabled >", value, "btnDisabled");
            return (Criteria) this;
        }

        public Criteria andBtnDisabledGreaterThanOrEqualTo(String value) {
            addCriterion("BtnDisabled >=", value, "btnDisabled");
            return (Criteria) this;
        }

        public Criteria andBtnDisabledLessThan(String value) {
            addCriterion("BtnDisabled <", value, "btnDisabled");
            return (Criteria) this;
        }

        public Criteria andBtnDisabledLessThanOrEqualTo(String value) {
            addCriterion("BtnDisabled <=", value, "btnDisabled");
            return (Criteria) this;
        }

        public Criteria andBtnDisabledLike(String value) {
            addCriterion("BtnDisabled like", value, "btnDisabled");
            return (Criteria) this;
        }

        public Criteria andBtnDisabledNotLike(String value) {
            addCriterion("BtnDisabled not like", value, "btnDisabled");
            return (Criteria) this;
        }

        public Criteria andBtnDisabledIn(List<String> values) {
            addCriterion("BtnDisabled in", values, "btnDisabled");
            return (Criteria) this;
        }

        public Criteria andBtnDisabledNotIn(List<String> values) {
            addCriterion("BtnDisabled not in", values, "btnDisabled");
            return (Criteria) this;
        }

        public Criteria andBtnDisabledBetween(String value1, String value2) {
            addCriterion("BtnDisabled between", value1, value2, "btnDisabled");
            return (Criteria) this;
        }

        public Criteria andBtnDisabledNotBetween(String value1, String value2) {
            addCriterion("BtnDisabled not between", value1, value2, "btnDisabled");
            return (Criteria) this;
        }

        public Criteria andScheduleErrorIsNull() {
            addCriterion("ScheduleError is null");
            return (Criteria) this;
        }

        public Criteria andScheduleErrorIsNotNull() {
            addCriterion("ScheduleError is not null");
            return (Criteria) this;
        }

        public Criteria andScheduleErrorEqualTo(String value) {
            addCriterion("ScheduleError =", value, "scheduleError");
            return (Criteria) this;
        }

        public Criteria andScheduleErrorNotEqualTo(String value) {
            addCriterion("ScheduleError <>", value, "scheduleError");
            return (Criteria) this;
        }

        public Criteria andScheduleErrorGreaterThan(String value) {
            addCriterion("ScheduleError >", value, "scheduleError");
            return (Criteria) this;
        }

        public Criteria andScheduleErrorGreaterThanOrEqualTo(String value) {
            addCriterion("ScheduleError >=", value, "scheduleError");
            return (Criteria) this;
        }

        public Criteria andScheduleErrorLessThan(String value) {
            addCriterion("ScheduleError <", value, "scheduleError");
            return (Criteria) this;
        }

        public Criteria andScheduleErrorLessThanOrEqualTo(String value) {
            addCriterion("ScheduleError <=", value, "scheduleError");
            return (Criteria) this;
        }

        public Criteria andScheduleErrorLike(String value) {
            addCriterion("ScheduleError like", value, "scheduleError");
            return (Criteria) this;
        }

        public Criteria andScheduleErrorNotLike(String value) {
            addCriterion("ScheduleError not like", value, "scheduleError");
            return (Criteria) this;
        }

        public Criteria andScheduleErrorIn(List<String> values) {
            addCriterion("ScheduleError in", values, "scheduleError");
            return (Criteria) this;
        }

        public Criteria andScheduleErrorNotIn(List<String> values) {
            addCriterion("ScheduleError not in", values, "scheduleError");
            return (Criteria) this;
        }

        public Criteria andScheduleErrorBetween(String value1, String value2) {
            addCriterion("ScheduleError between", value1, value2, "scheduleError");
            return (Criteria) this;
        }

        public Criteria andScheduleErrorNotBetween(String value1, String value2) {
            addCriterion("ScheduleError not between", value1, value2, "scheduleError");
            return (Criteria) this;
        }

        public Criteria andStatusIsNull() {
            addCriterion("Status is null");
            return (Criteria) this;
        }

        public Criteria andStatusIsNotNull() {
            addCriterion("Status is not null");
            return (Criteria) this;
        }

        public Criteria andStatusEqualTo(Byte value) {
            addCriterion("Status =", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotEqualTo(Byte value) {
            addCriterion("Status <>", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThan(Byte value) {
            addCriterion("Status >", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusGreaterThanOrEqualTo(Byte value) {
            addCriterion("Status >=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThan(Byte value) {
            addCriterion("Status <", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusLessThanOrEqualTo(Byte value) {
            addCriterion("Status <=", value, "status");
            return (Criteria) this;
        }

        public Criteria andStatusIn(List<Byte> values) {
            addCriterion("Status in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotIn(List<Byte> values) {
            addCriterion("Status not in", values, "status");
            return (Criteria) this;
        }

        public Criteria andStatusBetween(Byte value1, Byte value2) {
            addCriterion("Status between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andStatusNotBetween(Byte value1, Byte value2) {
            addCriterion("Status not between", value1, value2, "status");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNull() {
            addCriterion("CreateTime is null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIsNotNull() {
            addCriterion("CreateTime is not null");
            return (Criteria) this;
        }

        public Criteria andCreateTimeEqualTo(Date value) {
            addCriterion("CreateTime =", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotEqualTo(Date value) {
            addCriterion("CreateTime <>", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThan(Date value) {
            addCriterion("CreateTime >", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("CreateTime >=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThan(Date value) {
            addCriterion("CreateTime <", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeLessThanOrEqualTo(Date value) {
            addCriterion("CreateTime <=", value, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeIn(List<Date> values) {
            addCriterion("CreateTime in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotIn(List<Date> values) {
            addCriterion("CreateTime not in", values, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeBetween(Date value1, Date value2) {
            addCriterion("CreateTime between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andCreateTimeNotBetween(Date value1, Date value2) {
            addCriterion("CreateTime not between", value1, value2, "createTime");
            return (Criteria) this;
        }

        public Criteria andLastUpdateTimeIsNull() {
            addCriterion("LastUpdateTime is null");
            return (Criteria) this;
        }

        public Criteria andLastUpdateTimeIsNotNull() {
            addCriterion("LastUpdateTime is not null");
            return (Criteria) this;
        }

        public Criteria andLastUpdateTimeEqualTo(Date value) {
            addCriterion("LastUpdateTime =", value, "lastUpdateTime");
            return (Criteria) this;
        }

        public Criteria andLastUpdateTimeNotEqualTo(Date value) {
            addCriterion("LastUpdateTime <>", value, "lastUpdateTime");
            return (Criteria) this;
        }

        public Criteria andLastUpdateTimeGreaterThan(Date value) {
            addCriterion("LastUpdateTime >", value, "lastUpdateTime");
            return (Criteria) this;
        }

        public Criteria andLastUpdateTimeGreaterThanOrEqualTo(Date value) {
            addCriterion("LastUpdateTime >=", value, "lastUpdateTime");
            return (Criteria) this;
        }

        public Criteria andLastUpdateTimeLessThan(Date value) {
            addCriterion("LastUpdateTime <", value, "lastUpdateTime");
            return (Criteria) this;
        }

        public Criteria andLastUpdateTimeLessThanOrEqualTo(Date value) {
            addCriterion("LastUpdateTime <=", value, "lastUpdateTime");
            return (Criteria) this;
        }

        public Criteria andLastUpdateTimeIn(List<Date> values) {
            addCriterion("LastUpdateTime in", values, "lastUpdateTime");
            return (Criteria) this;
        }

        public Criteria andLastUpdateTimeNotIn(List<Date> values) {
            addCriterion("LastUpdateTime not in", values, "lastUpdateTime");
            return (Criteria) this;
        }

        public Criteria andLastUpdateTimeBetween(Date value1, Date value2) {
            addCriterion("LastUpdateTime between", value1, value2, "lastUpdateTime");
            return (Criteria) this;
        }

        public Criteria andLastUpdateTimeNotBetween(Date value1, Date value2) {
            addCriterion("LastUpdateTime not between", value1, value2, "lastUpdateTime");
            return (Criteria) this;
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table app_color_config
     *
     * @mbggenerated do_not_delete_during_merge Mon Dec 25 14:33:20 CST 2017
     */
    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table app_color_config
     *
     * @mbggenerated Mon Dec 25 14:33:20 CST 2017
     */
    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}