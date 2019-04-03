package com.treefinance.saas.grapserver.web.request;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author:guoguoyun
 * @date:Created in 2019/3/22下午3:05
 */
@Data
public class Bill implements Serializable {

    private List<Bills> bills;

}
