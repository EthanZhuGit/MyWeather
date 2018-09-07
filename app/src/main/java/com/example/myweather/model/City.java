package com.example.myweather.model;

/**
 * Created by zyx10 on 2016/12/25 0025.
 */

public class City{
    private String cityId;
    private String cityName;
    private boolean isDefault;
    public static int number;

    public City(String cityId, String cityName,boolean isDefault) {
        this.cityId=cityId;
        this.cityName=cityName;
        this.isDefault=isDefault;
    }

    public String getCityId() {
        return cityId;
    }

    public String getCityName() {
        return cityName;
    }

    public boolean getIsDefault(){
        return isDefault;
    }

    public void setDefault(boolean isDefault) {
        this.isDefault=isDefault;

    }

    public boolean equals(Object obj) {
        if(this ==obj){//如果是引用同一个实例
            return true;
        } if (obj!=null && obj instanceof City) {
            City u = (City) obj;
            return this.getCityId().equals(u.getCityId())&&this.getCityName().equals(u.getCityName());
        }else{
            return false;
        }
    }
}
