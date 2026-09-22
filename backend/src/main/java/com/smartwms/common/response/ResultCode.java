package com.smartwms.common.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    FAIL(500, "操作失败"),

    UNAUTHORIZED(401, "未登录或token已过期"),
    FORBIDDEN(403, "权限不足"),
    NOT_FOUND(404, "资源不存在"),
    BAD_REQUEST(400, "请求参数错误"),
    CONFLICT(409, "数据冲突"),

    USER_NOT_FOUND(1001, "用户不存在"),
    USER_PASSWORD_ERROR(1002, "密码错误"),
    USER_DISABLED(1003, "用户已禁用"),
    USER_EXISTS(1004, "用户已存在"),

    INVENTORY_NOT_ENOUGH(2001, "库存不足"),
    INVENTORY_LOCKED(2002, "库存已锁定"),
    INVENTORY_RELEASE_FAILED(2003, "库存释放失败"),

    LOCATION_CAPACITY_FULL(3001, "库位已满"),
    LOCATION_WEIGHT_EXCEEDED(3002, "库位超重"),
    LOCATION_DISABLED(3003, "库位已禁用"),

    ORDER_STATUS_ERROR(4001, "订单状态错误"),
    ORDER_NOT_FOUND(4002, "订单不存在"),
    ORDER_ALREADY_COMPLETED(4003, "订单已完成"),
    ORDER_ALREADY_CANCELLED(4004, "订单已取消"),

    DUPLICATE_OPERATION(5001, "重复操作"),
    DATA_NOT_FOUND(5002, "数据不存在"),
    DATA_VERSION_CONFLICT(5003, "数据版本冲突");

    private final int code;
    private final String message;
}
