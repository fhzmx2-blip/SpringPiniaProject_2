package com.sist.web.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Persistent;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name="bootboard")
@Data
public class BootBoard {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int no;
	private String name;
	private String subject;
	private String content;
	@Column(insertable = true, updatable = false)
	private String pwd;
	private int hit;
	@Column(insertable = true, updatable = false,name="regdate")
	private LocalDateTime regdate;
	
	@PrePersist
	public void perSist() {
		regdate=LocalDateTime.now();
	}
}
