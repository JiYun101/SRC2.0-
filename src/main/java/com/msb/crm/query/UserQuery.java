package com.msb.crm.query;

import com.msb.crm.base.BaseQuery;
import lombok.Data;

@Data
public class UserQuery extends BaseQuery {
    private String userName;
    private String email;
    private String phone;
}
