package com.ycbjie.ycshopcat.bean;

/**
 * 店铺信息
 */

public class StoreInfo {

    private String id;
    private String name;
    private boolean isChoosed;
    private boolean isEditor;           //自己对该组的编辑状态
    private boolean ActionBarEditor;    //全局对该组的编辑状态
    private boolean isLose;             //是否失效

    public StoreInfo(String id, String name,boolean isLose) {
        this.id = id;
        this.name = name;
        this.isLose = isLose;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChoosed() {
        return isChoosed;
    }

    public void setChoosed(boolean choosed) {
        isChoosed = choosed;
    }

    public boolean isEditor() {
        return isEditor;
    }

    public void setEditor(boolean editor) {
        isEditor = editor;
    }

    public boolean isActionBarEditor() {
        return ActionBarEditor;
    }

    public void setActionBarEditor(boolean actionBarEditor) {
        ActionBarEditor = actionBarEditor;
    }

    public boolean isLose() {
        return isLose;
    }

    public void setLose(boolean lose) {
        isLose = lose;
    }
}
