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
        if(data>=1073741824.0) {
            data /= 1073741824.0;
            suffix="GB/s";
            type="GB";
        }
        else if(data>=1048576.0){
            data/=1048576.0;
            suffix="MB/s";
            type="MB";
        }
        else if(data>=1024.0) {
            data/=1024.0;
            suffix="KB/s";
            type="KB";
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
