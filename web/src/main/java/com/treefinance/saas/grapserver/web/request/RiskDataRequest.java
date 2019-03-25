package com.treefinance.saas.grapserver.web.request;

import lombok.Data;

/**
 * @author:guoguoyun
 * @date:Created in 2019/3/22下午3:27
 */
@Data
public class RiskDataRequest {
    private String appid;

    private Params params;

    private Bill data;
}
