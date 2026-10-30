package cn.qihangerp.api.domain.bo;

import lombok.Data;

@Data
public class ShopTokenBo {
    private Long shopId;
    private String token;
    private String refreshToken;
    private int expiresIn;
    private int expiresAt;

}
