package com.vidshort.lyrical.video.status.myapp.model;

public class Apps {
    String appName;
    String appImage;
    String appLink;

    public Apps(String appName,String appImage,String appLink){
        this.appName = appName;
        this.appImage = appImage;
        this.appLink =appLink;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppImage() {
        return appImage;
    }

    public void setAppImage(String appImage) {
        this.appImage = appImage;
    }

    public String getAppLink() {
        return appLink;
    }

    public void setAppILink(String appLink) {
        this.appLink = appLink;
    }
}
