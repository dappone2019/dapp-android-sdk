package com.blockchain.dappbirds.opensdk.view.dlpopwindow;

/**
 * 选项的属性
 */
public class DLPopItem {
    /**
     * 文字
     */
    private String text;
    /**
     * 文字、图标 颜色
     */
    private int color = 0x888888;

    public DLPopItem() {

    }

    public DLPopItem(String t, int c) {
        text = t;
        color = c;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
