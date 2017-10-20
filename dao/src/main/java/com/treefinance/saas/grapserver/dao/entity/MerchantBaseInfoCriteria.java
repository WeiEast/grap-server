package com.treefinance.saas.grapserver.dao.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MerchantBaseInfoCriteria {
    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table merchant_base
     *
     * @mbggenerated Fri Oct 20 10:37:23 CST 2017
     */
    protected String orderByClause;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table merchant_base
     *
     * @mbggenerated Fri Oct 20 10:37:23 CST 2017
     */
    protected boolean distinct;

    /**
     * This field was generated by MyBatis Generator.
     * This field corresponds to the database table merchant_base
     *
     * @mbggenerated Fri Oct 20 10:37:23 CST 2017
     */
    protected List<Criteria> oredCriteria;

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table merchant_base
     *
     * @mbggenerated Fri Oct 20 10:37:23 CST 2017
     */
    public MerchantBaseInfoCriteria() {
        oredCriteria = new ArrayList<Criteria>();
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table merchant_base
     *
     * @mbggenerated Fri Oct 20 10:37:23 CST 2017
     */
    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table merchant_base
     *
     * @mbggenerated Fri Oct 20 10:37:23 CST 2017
     */
    public String getOrderByClause() {
        return orderByClause;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table merchant_base
     *
     * @mbggenerated Fri Oct 20 10:37:23 CST 2017
     */
    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table merchant_base
     *
     * @mbggenerated Fri Oct 20 10:37:23 CST 2017
     */
    public boolean isDistinct() {
        return distinct;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table merchant_base
     *
     * @mbggenerated Fri Oct 20 10:37:23 CST 2017
     */
    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table merchant_base
     *
     * @mbggenerated Fri Oct 20 10:37:23 CST 2017
     */
    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table merchant_base
     *
     * @mbggenerated Fri Oct 20 10:37:23 CST 2017
     */
    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table merchant_base
     *
     * @mbggenerated Fri Oct 20 10:37:23 CST 2017
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
     * This method corresponds to the database table merchant_base
     *
     * @mbggenerated Fri Oct 20 10:37:23 CST 2017
     */
    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table merchant_base
     *
     * @mbggenerated Fri Oct 20 10:37:23 CST 2017
     */
    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table merchant_base
     *
     * @mbggenerated Fri Oct 20 10:37:23 CST 2017
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

        public Criteria andAppNameIsNull() {
            addCriterion("AppName is null");
            return (Criteria) this;
        }

        public Criteria andAppNameIsNotNull() {
            addCriterion("AppName is not null");
            return (Criteria) this;
        }

        public Criteria andAppNameEqualTo(String value) {
            addCriterion("AppName =", value, "appName");
            return (Criteria) this;
        }

        public Criteria andAppNameNotEqualTo(String value) {
            addCriterion("AppName <>", value, "appName");
            return (Criteria) this;
        }

        public Criteria andAppNameGreaterThan(String value) {
            addCriterion("AppName >", value, "appName");
            return (Criteria) this;
        }

        public Criteria andAppNameGreaterThanOrEqualTo(String value) {
            addCriterion("AppName >=", value, "appName");
            return (Criteria) this;
        }

        public Criteria andAppNameLessThan(String value) {
            addCriterion("AppName <", value, "appName");
            return (Criteria) this;
        }

        public Criteria andAppNameLessThanOrEqualTo(String value) {
            addCriterion("AppName <=", value, "appName");
            return (Criteria) this;
        }

        public Criteria andAppNameLike(String value) {
            addCriterion("AppName like", value, "appName");
            return (Criteria) this;
        }

        public Criteria andAppNameNotLike(String value) {
            addCriterion("AppName not like", value, "appName");
            return (Criteria) this;
        }

        public Criteria andAppNameIn(List<String> values) {
            addCriterion("AppName in", values, "appName");
            return (Criteria) this;
        }

        public Criteria andAppNameNotIn(List<String> values) {
            addCriterion("AppName not in", values, "appName");
            return (Criteria) this;
        }

        public Criteria andAppNameBetween(String value1, String value2) {
            addCriterion("AppName between", value1, value2, "appName");
            return (Criteria) this;
        }

        public Criteria andAppNameNotBetween(String value1, String value2) {
            addCriterion("AppName not between", value1, value2, "appName");
            return (Criteria) this;
        }

        public Criteria andChNameIsNull() {
            addCriterion("ChName is null");
            return (Criteria) this;
        }

        public Criteria andChNameIsNotNull() {
            addCriterion("ChName is not null");
            return (Criteria) this;
        }

        public Criteria andChNameEqualTo(String value) {
            addCriterion("ChName =", value, "chName");
            return (Criteria) this;
        }

        public Criteria andChNameNotEqualTo(String value) {
            addCriterion("ChName <>", value, "chName");
            return (Criteria) this;
        }

        public Criteria andChNameGreaterThan(String value) {
            addCriterion("ChName >", value, "chName");
            return (Criteria) this;
        }

        public Criteria andChNameGreaterThanOrEqualTo(String value) {
            addCriterion("ChName >=", value, "chName");
            return (Criteria) this;
        }

        public Criteria andChNameLessThan(String value) {
            addCriterion("ChName <", value, "chName");
            return (Criteria) this;
        }

        public Criteria andChNameLessThanOrEqualTo(String value) {
            addCriterion("ChName <=", value, "chName");
            return (Criteria) this;
        }

        public Criteria andChNameLike(String value) {
            addCriterion("ChName like", value, "chName");
            return (Criteria) this;
        }

        public Criteria andChNameNotLike(String value) {
            addCriterion("ChName not like", value, "chName");
            return (Criteria) this;
        }

        public Criteria andChNameIn(List<String> values) {
            addCriterion("ChName in", values, "chName");
            return (Criteria) this;
        }

        public Criteria andChNameNotIn(List<String> values) {
            addCriterion("ChName not in", values, "chName");
            return (Criteria) this;
        }

        public Criteria andChNameBetween(String value1, String value2) {
            addCriterion("ChName between", value1, value2, "chName");
            return (Criteria) this;
        }

        public Criteria andChNameNotBetween(String value1, String value2) {
            addCriterion("ChName not between", value1, value2, "chName");
            return (Criteria) this;
        }

        public Criteria andCompanyIsNull() {
            addCriterion("Company is null");
            return (Criteria) this;
        }

        public Criteria andCompanyIsNotNull() {
            addCriterion("Company is not null");
            return (Criteria) this;
        }

        public Criteria andCompanyEqualTo(String value) {
            addCriterion("Company =", value, "company");
            return (Criteria) this;
        }

        public Criteria andCompanyNotEqualTo(String value) {
            addCriterion("Company <>", value, "company");
            return (Criteria) this;
        }

        public Criteria andCompanyGreaterThan(String value) {
            addCriterion("Company >", value, "company");
            return (Criteria) this;
        }

        public Criteria andCompanyGreaterThanOrEqualTo(String value) {
            addCriterion("Company >=", value, "company");
            return (Criteria) this;
        }

        public Criteria andCompanyLessThan(String value) {
            addCriterion("Company <", value, "company");
            return (Criteria) this;
        }

        public Criteria andCompanyLessThanOrEqualTo(String value) {
            addCriterion("Company <=", value, "company");
            return (Criteria) this;
        }

        public Criteria andCompanyLike(String value) {
            addCriterion("Company like", value, "company");
            return (Criteria) this;
        }

        public Criteria andCompanyNotLike(String value) {
            addCriterion("Company not like", value, "company");
            return (Criteria) this;
        }

        public Criteria andCompanyIn(List<String> values) {
            addCriterion("Company in", values, "company");
            return (Criteria) this;
        }

        public Criteria andCompanyNotIn(List<String> values) {
            addCriterion("Company not in", values, "company");
            return (Criteria) this;
        }

        public Criteria andCompanyBetween(String value1, String value2) {
            addCriterion("Company between", value1, value2, "company");
            return (Criteria) this;
        }

        public Criteria andCompanyNotBetween(String value1, String value2) {
            addCriterion("Company not between", value1, value2, "company");
            return (Criteria) this;
        }

        public Criteria andBussinessIsNull() {
            addCriterion("Bussiness is null");
            return (Criteria) this;
        }

        public Criteria andBussinessIsNotNull() {
            addCriterion("Bussiness is not null");
            return (Criteria) this;
        }

        public Criteria andBussinessEqualTo(String value) {
            addCriterion("Bussiness =", value, "bussiness");
            return (Criteria) this;
        }

        public Criteria andBussinessNotEqualTo(String value) {
            addCriterion("Bussiness <>", value, "bussiness");
            return (Criteria) this;
        }

        public Criteria andBussinessGreaterThan(String value) {
            addCriterion("Bussiness >", value, "bussiness");
            return (Criteria) this;
        }

        public Criteria andBussinessGreaterThanOrEqualTo(String value) {
            addCriterion("Bussiness >=", value, "bussiness");
            return (Criteria) this;
        }

        public Criteria andBussinessLessThan(String value) {
            addCriterion("Bussiness <", value, "bussiness");
            return (Criteria) this;
        }

        public Criteria andBussinessLessThanOrEqualTo(String value) {
            addCriterion("Bussiness <=", value, "bussiness");
            return (Criteria) this;
        }

        public Criteria andBussinessLike(String value) {
            addCriterion("Bussiness like", value, "bussiness");
            return (Criteria) this;
        }

        public Criteria andBussinessNotLike(String value) {
            addCriterion("Bussiness not like", value, "bussiness");
            return (Criteria) this;
        }

        public Criteria andBussinessIn(List<String> values) {
            addCriterion("Bussiness in", values, "bussiness");
            return (Criteria) this;
        }

        public Criteria andBussinessNotIn(List<String> values) {
            addCriterion("Bussiness not in", values, "bussiness");
            return (Criteria) this;
        }

        public Criteria andBussinessBetween(String value1, String value2) {
            addCriterion("Bussiness between", value1, value2, "bussiness");
            return (Criteria) this;
        }

        public Criteria andBussinessNotBetween(String value1, String value2) {
            addCriterion("Bussiness not between", value1, value2, "bussiness");
            return (Criteria) this;
        }

        public Criteria andBussiness2IsNull() {
            addCriterion("Bussiness2 is null");
            return (Criteria) this;
        }

        public Criteria andBussiness2IsNotNull() {
            addCriterion("Bussiness2 is not null");
            return (Criteria) this;
        }

        public Criteria andBussiness2EqualTo(String value) {
            addCriterion("Bussiness2 =", value, "bussiness2");
            return (Criteria) this;
        }

        public Criteria andBussiness2NotEqualTo(String value) {
            addCriterion("Bussiness2 <>", value, "bussiness2");
            return (Criteria) this;
        }

        public Criteria andBussiness2GreaterThan(String value) {
            addCriterion("Bussiness2 >", value, "bussiness2");
            return (Criteria) this;
        }

        public Criteria andBussiness2GreaterThanOrEqualTo(String value) {
            addCriterion("Bussiness2 >=", value, "bussiness2");
            return (Criteria) this;
        }

        public Criteria andBussiness2LessThan(String value) {
            addCriterion("Bussiness2 <", value, "bussiness2");
            return (Criteria) this;
        }

        public Criteria andBussiness2LessThanOrEqualTo(String value) {
            addCriterion("Bussiness2 <=", value, "bussiness2");
            return (Criteria) this;
        }

        public Criteria andBussiness2Like(String value) {
            addCriterion("Bussiness2 like", value, "bussiness2");
            return (Criteria) this;
        }

        public Criteria andBussiness2NotLike(String value) {
            addCriterion("Bussiness2 not like", value, "bussiness2");
            return (Criteria) this;
        }

        public Criteria andBussiness2In(List<String> values) {
            addCriterion("Bussiness2 in", values, "bussiness2");
            return (Criteria) this;
        }

        public Criteria andBussiness2NotIn(List<String> values) {
            addCriterion("Bussiness2 not in", values, "bussiness2");
            return (Criteria) this;
        }

        public Criteria andBussiness2Between(String value1, String value2) {
            addCriterion("Bussiness2 between", value1, value2, "bussiness2");
            return (Criteria) this;
        }

        public Criteria andBussiness2NotBetween(String value1, String value2) {
            addCriterion("Bussiness2 not between", value1, value2, "bussiness2");
            return (Criteria) this;
        }

        public Criteria andAddressIsNull() {
            addCriterion("Address is null");
            return (Criteria) this;
        }

        public Criteria andAddressIsNotNull() {
            addCriterion("Address is not null");
            return (Criteria) this;
        }

        public Criteria andAddressEqualTo(String value) {
            addCriterion("Address =", value, "address");
            return (Criteria) this;
        }

        public Criteria andAddressNotEqualTo(String value) {
            addCriterion("Address <>", value, "address");
            return (Criteria) this;
        }

        public Criteria andAddressGreaterThan(String value) {
            addCriterion("Address >", value, "address");
            return (Criteria) this;
        }

        public Criteria andAddressGreaterThanOrEqualTo(String value) {
            addCriterion("Address >=", value, "address");
            return (Criteria) this;
        }

        public Criteria andAddressLessThan(String value) {
            addCriterion("Address <", value, "address");
            return (Criteria) this;
        }

        public Criteria andAddressLessThanOrEqualTo(String value) {
            addCriterion("Address <=", value, "address");
            return (Criteria) this;
        }

        public Criteria andAddressLike(String value) {
            addCriterion("Address like", value, "address");
            return (Criteria) this;
        }

        public Criteria andAddressNotLike(String value) {
            addCriterion("Address not like", value, "address");
            return (Criteria) this;
        }

        public Criteria andAddressIn(List<String> values) {
            addCriterion("Address in", values, "address");
            return (Criteria) this;
        }

        public Criteria andAddressNotIn(List<String> values) {
            addCriterion("Address not in", values, "address");
            return (Criteria) this;
        }

        public Criteria andAddressBetween(String value1, String value2) {
            addCriterion("Address between", value1, value2, "address");
            return (Criteria) this;
        }

        public Criteria andAddressNotBetween(String value1, String value2) {
            addCriterion("Address not between", value1, value2, "address");
            return (Criteria) this;
        }

        public Criteria andContactPersonIsNull() {
            addCriterion("ContactPerson is null");
            return (Criteria) this;
        }

        public Criteria andContactPersonIsNotNull() {
            addCriterion("ContactPerson is not null");
            return (Criteria) this;
        }

        public Criteria andContactPersonEqualTo(String value) {
            addCriterion("ContactPerson =", value, "contactPerson");
            return (Criteria) this;
        }

        public Criteria andContactPersonNotEqualTo(String value) {
            addCriterion("ContactPerson <>", value, "contactPerson");
            return (Criteria) this;
        }

        public Criteria andContactPersonGreaterThan(String value) {
            addCriterion("ContactPerson >", value, "contactPerson");
            return (Criteria) this;
        }

        public Criteria andContactPersonGreaterThanOrEqualTo(String value) {
            addCriterion("ContactPerson >=", value, "contactPerson");
            return (Criteria) this;
        }

        public Criteria andContactPersonLessThan(String value) {
            addCriterion("ContactPerson <", value, "contactPerson");
            return (Criteria) this;
        }

        public Criteria andContactPersonLessThanOrEqualTo(String value) {
            addCriterion("ContactPerson <=", value, "contactPerson");
            return (Criteria) this;
        }

        public Criteria andContactPersonLike(String value) {
            addCriterion("ContactPerson like", value, "contactPerson");
            return (Criteria) this;
        }

        public Criteria andContactPersonNotLike(String value) {
            addCriterion("ContactPerson not like", value, "contactPerson");
            return (Criteria) this;
        }

        public Criteria andContactPersonIn(List<String> values) {
            addCriterion("ContactPerson in", values, "contactPerson");
            return (Criteria) this;
        }

        public Criteria andContactPersonNotIn(List<String> values) {
            addCriterion("ContactPerson not in", values, "contactPerson");
            return (Criteria) this;
        }

        public Criteria andContactPersonBetween(String value1, String value2) {
            addCriterion("ContactPerson between", value1, value2, "contactPerson");
            return (Criteria) this;
        }

        public Criteria andContactPersonNotBetween(String value1, String value2) {
            addCriterion("ContactPerson not between", value1, value2, "contactPerson");
            return (Criteria) this;
        }

        public Criteria andContactValueIsNull() {
            addCriterion("ContactValue is null");
            return (Criteria) this;
        }

        public Criteria andContactValueIsNotNull() {
            addCriterion("ContactValue is not null");
            return (Criteria) this;
        }

        public Criteria andContactValueEqualTo(String value) {
            addCriterion("ContactValue =", value, "contactValue");
            return (Criteria) this;
        }

        public Criteria andContactValueNotEqualTo(String value) {
            addCriterion("ContactValue <>", value, "contactValue");
            return (Criteria) this;
        }

        public Criteria andContactValueGreaterThan(String value) {
            addCriterion("ContactValue >", value, "contactValue");
            return (Criteria) this;
        }

        public Criteria andContactValueGreaterThanOrEqualTo(String value) {
            addCriterion("ContactValue >=", value, "contactValue");
            return (Criteria) this;
        }

        public Criteria andContactValueLessThan(String value) {
            addCriterion("ContactValue <", value, "contactValue");
            return (Criteria) this;
        }

        public Criteria andContactValueLessThanOrEqualTo(String value) {
            addCriterion("ContactValue <=", value, "contactValue");
            return (Criteria) this;
        }

        public Criteria andContactValueLike(String value) {
            addCriterion("ContactValue like", value, "contactValue");
            return (Criteria) this;
        }

        public Criteria andContactValueNotLike(String value) {
            addCriterion("ContactValue not like", value, "contactValue");
            return (Criteria) this;
        }

        public Criteria andContactValueIn(List<String> values) {
            addCriterion("ContactValue in", values, "contactValue");
            return (Criteria) this;
        }

        public Criteria andContactValueNotIn(List<String> values) {
            addCriterion("ContactValue not in", values, "contactValue");
            return (Criteria) this;
        }

        public Criteria andContactValueBetween(String value1, String value2) {
            addCriterion("ContactValue between", value1, value2, "contactValue");
            return (Criteria) this;
        }

        public Criteria andContactValueNotBetween(String value1, String value2) {
            addCriterion("ContactValue not between", value1, value2, "contactValue");
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
     * This class corresponds to the database table merchant_base
     *
     * @mbggenerated do_not_delete_during_merge Fri Oct 20 10:37:23 CST 2017
     */
    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    /**
     * This class was generated by MyBatis Generator.
     * This class corresponds to the database table merchant_base
     *
     * @mbggenerated Fri Oct 20 10:37:23 CST 2017
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