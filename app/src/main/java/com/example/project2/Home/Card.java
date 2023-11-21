package com.example.project2.Home;

import java.io.Serializable;

public class Card implements Serializable {
    private int idx;
    private int list_idx;
    private String bno;
    private String title;

    public Card(int idx, int list_idx, String bno, String title) {
        this.idx = idx;
        this.list_idx = list_idx;
        this.bno = bno;
        this.title = title;
    }

    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public int getList_idx() {
        return list_idx;
    }

    public void setList_idx(int list_idx) {
        this.list_idx = list_idx;
    }

    public String getBno() {
        return bno;
    }

    public void setBno(String bno) {
        this.bno = bno;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
