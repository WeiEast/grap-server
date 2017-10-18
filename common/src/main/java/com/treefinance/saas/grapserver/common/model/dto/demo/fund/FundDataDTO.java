package com.treefinance.saas.grapserver.common.model.dto.demo.fund;

import java.io.Serializable;
import java.util.List;

/**
 * Created by haojiahong on 2017/10/17.
 */
public class FundDataDTO implements Serializable {
    private static final long serialVersionUID = -3265819176978912680L;

    private List<FundBillRecordDTO> billRecordList;
    private List<FundLoanInfoDTO> loanInfoList;
    private List<FundLoanRepayRecordDTO> loanRepayRecordList;
    private FundUserInfoDTO userInfo;

    public List<FundBillRecordDTO> getBillRecordList() {
        return billRecordList;
    }

    public void setBillRecordList(List<FundBillRecordDTO> billRecordList) {
        this.billRecordList = billRecordList;
    }

    public List<FundLoanInfoDTO> getLoanInfoList() {
        return loanInfoList;
    }

    public void setLoanInfoList(List<FundLoanInfoDTO> loanInfoList) {
        this.loanInfoList = loanInfoList;
    }

    public List<FundLoanRepayRecordDTO> getLoanRepayRecordList() {
        return loanRepayRecordList;
    }

    public void setLoanRepayRecordList(List<FundLoanRepayRecordDTO> loanRepayRecordList) {
        this.loanRepayRecordList = loanRepayRecordList;
    }

    public FundUserInfoDTO getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(FundUserInfoDTO userInfo) {
        this.userInfo = userInfo;
    }
}
