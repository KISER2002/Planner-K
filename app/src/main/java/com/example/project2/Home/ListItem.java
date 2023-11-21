package com.example.project2.Home;

import java.util.List;

public class ListItem {
    private String pk, idx, bno, title;
    // 하위 리사이클러뷰 아이템으로 정의한 subItemList를 전역변수로 선언한다.
    private List<Card> cardList;


    public ListItem(String pk, String idx, String bno, String title, List<Card> cardList) {
        this.pk = pk;
        this.idx = idx;
        this.bno = bno;
        this.title = title;
        // 하위 리사이클러뷰
        this.cardList = cardList;
    }

    public ListItem() {

    }

    public String getPk() {
        return pk;
    }

    public void setPk(String pk) {
        this.pk = pk;
    }

    public String getIdx() {
        return idx;
    }

    public void setIdx(String idx) {
        this.idx = idx;
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

    public List<Card> getCardList() {
        return cardList;
    }

    public void setCardList(List<Card> cardList) {
        this.cardList = cardList;
    }
}
