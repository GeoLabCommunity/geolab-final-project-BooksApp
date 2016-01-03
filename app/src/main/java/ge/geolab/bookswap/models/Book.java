package ge.geolab.bookswap.models;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by dalkh on 28-Dec-15.
 */

public class Book implements Serializable{
    private String id,title,author,description,adType,category,condition,location,exchangeItem,mobileNum,eMail,frontImageUrl;
    private ArrayList<String> pictures;
    public Book(){}

    public String getTitle() {
        return title;
    }

    public String getAdType() {
        return adType;
    }

    public void setAdType(String adType) {
        this.adType = adType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getExchangeItem() {
        return exchangeItem;
    }

    public void setExchangeItem(String exchangeItem) {
        this.exchangeItem = exchangeItem;
    }

    public String getMobileNum() {
        return mobileNum;
    }

    public void setMobileNum(String mobileNum) {
        this.mobileNum = mobileNum;
    }

    public String geteMail() {
        return eMail;
    }

    public void seteMail(String eMail) {
        this.eMail = eMail;
    }

    public ArrayList<String> getPictures() {
        return pictures;
    }

    public void setPictures(ArrayList<String> pictures) {
        this.pictures = pictures;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFrontImageUrl() {
        return frontImageUrl;
    }

    public void setFrontImageUrl(String frontImageUrl) {
        this.frontImageUrl = frontImageUrl;
    }

    public Book(String id,
                String title,
                String author,
                String description,
                String adType,
                String category,
                String condition,
                String location,
                String exchangeItem,
                String mobileNum,
                String eMail,
                String frontImageUrl,
                ArrayList<String> pictureArray){
        this.id=id;
        this.author=author;
        this.title=title;
        this.description=description;
        this.adType=adType;
        this.category=category;
        this.condition=condition;
        this.location=location;
        this.exchangeItem=exchangeItem;
        this.mobileNum=mobileNum;
        this.eMail=eMail;

        this.pictures=pictureArray;
        this.frontImageUrl=frontImageUrl;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
