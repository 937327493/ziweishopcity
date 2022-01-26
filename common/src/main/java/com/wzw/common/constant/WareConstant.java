package com.wzw.common.constant;


public class WareConstant {
    public enum PurchaseConstant {
        CREATED(0,"新建"),ASSIGNED(1,"已分配"),
        RECEIVED(2,"已领取"),FINISH(3,"已完成"),HASERROT(4,"已错误");
        private PurchaseConstant(Integer code,String msg){
            this.code = code;
            this.message = msg;
        }
        private Integer code;
        private String message;

        public Integer getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }


    public enum PurchaseDetailConstant {
        CREATED(0,"新建"),ASSIGNED(1,"已分配"),
        BUYING(2,"正在采购"),FINISH(3,"已完成"),HASERROT(4,"采购失败");
        private PurchaseDetailConstant(Integer code,String msg){
            this.code = code;
            this.message = msg;
        }
        private Integer code;
        private String message;

        public Integer getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }
}

