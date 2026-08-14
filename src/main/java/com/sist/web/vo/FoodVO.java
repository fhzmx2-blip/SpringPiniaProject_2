package com.sist.web.vo;



import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;


@Data
public class FoodVO {
	private int no;

	private int likecount, jjimcount, hit, replycount;
	private String name, address, phone, parking, poster, time, content, price, theme, type;
	private double score;
}