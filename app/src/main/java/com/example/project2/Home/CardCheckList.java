package com.example.project2.Home;

import java.io.Serializable;

public class CardCheckList implements Serializable {
    private int card_idx;
    private boolean isCheck = false;
    private boolean isEdit = true;
    private String content;

    public CardCheckList(int card_idx, boolean isCheck, boolean isEdit, String content) {
        this.card_idx = card_idx;
        this.isCheck = isCheck;
        this.isEdit = isEdit;
        this.content = content;
    }

    public int getCard_idx() {
        return card_idx;
    }

    public void setCard_idx(int card_idx) {
        this.card_idx = card_idx;
    }

    public boolean getIsCheck() {
        return isCheck;
    }

    public void setIsCheck(boolean isCheck) {
        this.isCheck = isCheck;
    }

    public boolean getIsEdit() {
        return isEdit;
    }

    public void setIsEdit(boolean isEdit) {
        this.isEdit = isEdit;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}