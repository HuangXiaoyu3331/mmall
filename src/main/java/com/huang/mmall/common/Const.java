package com.huang.mmall.common;

import com.google.common.collect.Sets;

import java.util.Set;

/**
 * 常量类
 *
 * @author hxy
 * @date 2019/01/14
 */
public class Const {
    public static final String CURRENT_USER = "currentUser";

    public static final String EMAIL = "email";
    public static final String USERNAME = "username";

    public interface ProductListOrderBy {
        /**
         * set的时间复杂度是O(1)，而List的时间复杂度是O(n)，所以用set性能会好一点
         */
        Set<String> PRICE_ASC_DESC = Sets.newHashSet("price asc", "price desc");
    }

    public interface Cart {
        /**
         * 购物车选中状态
         */
        int CHECKED = 1;
        /**
         * 购物车未选中状态
         */
        int UN_CHECKED = 0;
        /**
         * 限制数量成功
         */
        String LIMIT_NUM_FAIL = "LIMIT_NUM_FAIL";
        /**
         * 限制数量失败
         */
        String LIMIT_NUM_SUCCESS = "LIMIT_NUM_SUCCESS";
    }

    public interface Role {
        /**
         * 普通用户
         */
        int ROLE_CUSTOMER = 0;
        /**
         * 管理员
         */
        int ROLE_ADMIN = 1;
    }

    /**
     * 商品状态枚举类
     */
    public enum ProductStatusEnum {
        /**
         * 商品在线状态
         */
        ON_SALE(1, "在线");
        private int code;
        private String msg;

        ProductStatusEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }

    public enum OrderStatusEnum {
        /**
         * 订单已取消
         */
        CANCELED(0, "已取消"),
        /**
         * 订单未支付
         */
        NO_PAY(10, "未支付"),
        /**
         * 订单已支付
         */
        PAID(20, "已支付"),
        /**
         * 订单已发货
         */
        SHIPPED(40, "已发货"),
        /**
         * 订单完成
         */
        ORDER_SUCCESS(50, "订单完成"),
        /**
         * 订单关闭
         */
        ORDER_CLOSE(60, "订单关闭");
        private int code;
        private String msg;

        OrderStatusEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }

        public static OrderStatusEnum codeOf(int code) {
            for (OrderStatusEnum orderStatusEnum : values()) {
                if (orderStatusEnum.getCode() == code) {
                    return orderStatusEnum;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }
    }

    public interface AlipayCallback {
        String TRADE_STATUS_WAIT_BUYER_PAY = "WAIT_BUYER_PAY";
        String TRADE_STATUS_TRADE_SUCCESS = "TRADE_SUCCESS";

        String RESPONSE_SUCCESS = "success";
        String RESPONSE_FAILED = "failed";
    }

    /**
     * 付款平台枚举
     */
    public enum PayPlatformEnum {
        /**
         * 支付宝
         */
        ALIPAY(1, "支付宝");
        private int code;
        private String msg;

        PayPlatformEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }

    public enum PaymentTypeEnum {
        /**
         * 在线支付
         */
        ONLINE_PAY(1, "在线支付");

        private Integer code;
        private String msg;

        PaymentTypeEnum(Integer code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public Integer getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }

        public static PaymentTypeEnum codeOf(int code) {
            for (PaymentTypeEnum paymentTypeEnum : values()) {
                if (paymentTypeEnum.getCode() == code) {
                    return paymentTypeEnum;
                }
            }
            throw new RuntimeException("没有找到对应的枚举");
        }
    }
}
