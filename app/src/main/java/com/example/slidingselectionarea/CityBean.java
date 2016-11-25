package com.example.slidingselectionarea;

import java.util.List;

/**
 * Created by sk080 on 2016/11/25.
 */

public class CityBean {

    /**
     * name : 广东
     * sub : [{"name":"广州","sub":[{"name":"请选择"},{"name":"越秀区"},{"name":"荔湾区"},{"name":"海珠区"},{"name":"天河区"},{"name":"白云区"},{"name":"黄埔区"},{"name":"番禺区"},{"name":"花都区"},{"name":"南沙区"},{"name":"萝岗区"},{"name":"增城市"},{"name":"从化市"},{"name":"其他"}],"type":0}]
     */

    private String name;
    /**
     * name : 广州
     * sub : [{"name":"请选择"},{"name":"越秀区"},{"name":"荔湾区"},{"name":"海珠区"},{"name":"天河区"},{"name":"白云区"},{"name":"黄埔区"},{"name":"番禺区"},{"name":"花都区"},{"name":"南沙区"},{"name":"萝岗区"},{"name":"增城市"},{"name":"从化市"},{"name":"其他"}]
     * type : 0
     */

    private List<SubBean> sub;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SubBean> getSub() {
        return sub;
    }

    public void setSub(List<SubBean> sub) {
        this.sub = sub;
    }

    public static class SubBean {
        private String name;
        private int type;
        /**
         * name : 请选择
         */

        private List<SubBean> sub;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public List<SubBean> getSub() {
            return sub;
        }

        public void setSub(List<SubBean> sub) {
            this.sub = sub;
        }
    }
}
