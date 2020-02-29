package com.yash.networkspeed;

class DataItem{
    private double data;
    private String suffix;
    private String type;
    DataItem(){
        data=0.0;
        suffix="B/s";
        type="B";
    }
    DataItem(double data)
    {
        this.data=data;
        this.suffix="B/s";
        this.type="B";
    }
    DataItem(double data,String suffix){
        this.data=data;
        this.suffix=suffix;
        this.type="B";
    }

    void convertToHighestSuffix(){
        suffix="B/s";
        type="B";
        if(data>1024){
            data/=1024;
            suffix="KB/s";
            type="KB";
            if (data>1024){
                data/=1024;
                suffix="MB/s";
                type="MB";
                if(data>1024){
                    data/=1024;
                    suffix="GB/s";
                    type="GB";
                }
            }
        }
    }
    DataItem convert(){
        this.convertToHighestSuffix();
        return this;
    }

    double getData(){
        return this.data;
    }
    String getSuffix(){
        return this.suffix;
    }
    String getType(){
        return this.type;
    }


}
