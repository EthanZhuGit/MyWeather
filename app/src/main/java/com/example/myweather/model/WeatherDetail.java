package com.example.myweather.model;

import java.util.List;

/**
 * Created by zyx10 on 2016/11/27 0027.
 */

/**示例
 {
 "HeWeather5": [
 {
    "basic":
    { //基本信息
        "city": "北京",  //城市名称
        "cnty": "中国",   //国家
        "id": "CN101010100",  //城市ID
        "lat": "39.904000", //城市维度
        "lon": "116.391000", //城市经度
        "prov": "北京"  //城市所属省份（仅限国内城市）
        "update":
        {  //更新时间
            "loc": "2016-08-31 11:52",  //当地时间
            "utc": "2016-08-31 03:52" //UTC时间
        }
    },
 "now":
    {  //实况天气
    "cond":
        {  //天气状况
            "code": "104",  //天气状况代码
            "txt": "阴"  //天气状况描述
        },
        "fl": "11",  //体感温度
        "hum": "31",  //相对湿度（%）
        "pcpn": "0",  //降水量（mm）
        "pres": "1025",  //气压
        "tmp": "13",  //温度
        "vis": "10",  //能见度（km）
        "wind":
            {//风力风向
                "deg": "40",  //风向（360度）
                "dir": "东北风",  //风向
                "sc": "4-5",  //风力
                "spd": "24"  //风速（kmph）
            }
    },
    "status": "ok"  //接口状态
 }
 ]
 }
 */

public class WeatherDetail {

    private List<HeWeather5Bean> HeWeather5;

    public List<HeWeather5Bean> getHeWeather5() {
        return HeWeather5;
    }

    public void setHeWeather5(List<HeWeather5Bean> HeWeather5) {
        this.HeWeather5 = HeWeather5;
    }

    public static class HeWeather5Bean {
        /**
         * basic : {"city":"徐州","cnty":"中国","id":"CN101190801","lat":"34.263000","lon":"117.188000","update":{"loc":"2016-11-27 09:51","utc":"2016-11-27 01:51"}}
         * now : {"cond":{"code":"100","txt":"晴"},"fl":"4","hum":"96","pcpn":"0","pres":"1025","tmp":"5","vis":"10","wind":{"deg":"271","dir":"北风","sc":"微风","spd":"1"}}
         * status : ok
         */

        private BasicBean basic;
        private NowBean now;
        private String status;

        public BasicBean getBasic() {
            return basic;
        }

        public void setBasic(BasicBean basic) {
            this.basic = basic;
        }

        public NowBean getNow() {
            return now;
        }

        public void setNow(NowBean now) {
            this.now = now;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public static class BasicBean {
            /**
             * city : 徐州
             * cnty : 中国
             * id : CN101190801
             * lat : 34.263000
             * lon : 117.188000
             * update : {"loc":"2016-11-27 09:51","utc":"2016-11-27 01:51"}
             */

            private String city;
            private String cnty;
            private String id;
            private String lat;
            private String lon;
            private UpdateBean update;

            public String getCity() {
                return city;
            }

            public void setCity(String city) {
                this.city = city;
            }

            public String getCnty() {
                return cnty;
            }

            public void setCnty(String cnty) {
                this.cnty = cnty;
            }

            public String getId() {
                return id;
            }

            public void setId(String id) {
                this.id = id;
            }

            public String getLat() {
                return lat;
            }

            public void setLat(String lat) {
                this.lat = lat;
            }

            public String getLon() {
                return lon;
            }

            public void setLon(String lon) {
                this.lon = lon;
            }

            public UpdateBean getUpdate() {
                return update;
            }

            public void setUpdate(UpdateBean update) {
                this.update = update;
            }

            public static class UpdateBean {
                /**
                 * loc : 2016-11-27 09:51
                 * utc : 2016-11-27 01:51
                 */

                private String loc;
                private String utc;

                public String getLoc() {
                    return loc;
                }

                public void setLoc(String loc) {
                    this.loc = loc;
                }

                public String getUtc() {
                    return utc;
                }

                public void setUtc(String utc) {
                    this.utc = utc;
                }
            }
        }

        public static class NowBean {
            /**
             * cond : {"code":"100","txt":"晴"}
             * fl : 4
             * hum : 96
             * pcpn : 0
             * pres : 1025
             * tmp : 5
             * vis : 10
             * wind : {"deg":"271","dir":"北风","sc":"微风","spd":"1"}
             */

            private CondBean cond;
            private String fl;
            private String hum;
            private String pcpn;
            private String pres;
            private String tmp;
            private String vis;
            private WindBean wind;

            public CondBean getCond() {
                return cond;
            }

            public void setCond(CondBean cond) {
                this.cond = cond;
            }

            public String getFl() {
                return fl;
            }

            public void setFl(String fl) {
                this.fl = fl;
            }

            public String getHum() {
                return hum;
            }

            public void setHum(String hum) {
                this.hum = hum;
            }

            public String getPcpn() {
                return pcpn;
            }

            public void setPcpn(String pcpn) {
                this.pcpn = pcpn;
            }

            public String getPres() {
                return pres;
            }

            public void setPres(String pres) {
                this.pres = pres;
            }

            public String getTmp() {
                return tmp;
            }

            public void setTmp(String tmp) {
                this.tmp = tmp;
            }

            public String getVis() {
                return vis;
            }

            public void setVis(String vis) {
                this.vis = vis;
            }

            public WindBean getWind() {
                return wind;
            }

            public void setWind(WindBean wind) {
                this.wind = wind;
            }

            public static class CondBean {
                /**
                 * code : 100
                 * txt : 晴
                 */

                private String code;
                private String txt;

                public String getCode() {
                    return code;
                }

                public void setCode(String code) {
                    this.code = code;
                }

                public String getTxt() {
                    return txt;
                }

                public void setTxt(String txt) {
                    this.txt = txt;
                }
            }

            public static class WindBean {
                /**
                 * deg : 271
                 * dir : 北风
                 * sc : 微风
                 * spd : 1
                 */

                private String deg;
                private String dir;
                private String sc;
                private String spd;

                public String getDeg() {
                    return deg;
                }

                public void setDeg(String deg) {
                    this.deg = deg;
                }

                public String getDir() {
                    return dir;
                }

                public void setDir(String dir) {
                    this.dir = dir;
                }

                public String getSc() {
                    return sc;
                }

                public void setSc(String sc) {
                    this.sc = sc;
                }

                public String getSpd() {
                    return spd;
                }

                public void setSpd(String spd) {
                    this.spd = spd;
                }
            }
        }
    }
}
