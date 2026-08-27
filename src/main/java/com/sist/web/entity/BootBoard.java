package com.sist.web.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.annotation.Persistent;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;

/*
NO      NOT NULL NUMBER         
NAME    NOT NULL VARCHAR2(51)   
SUBJECT NOT NULL VARCHAR2(4000) 
CONTENT NOT NULL CLOB           
PWD     NOT NULL VARCHAR2(10)   
REGDATE          DATE           
HIT              NUMBER
 */
@Entity
@Table(name="bootboard")
@DynamicUpdate
@Data
// => SQL 자동처리 => save() => INSERT...
/*
 *   JPA (Java Persistence API) 
 *   1) Java ORM(Object Relation Mapping)의 표준
 *                      --------- 관계형 데이터베이스
 *                      | => MyBatis / Hibernate(JPA)
 *   2) 자바 객체와 데이터베이스 컬럼 매핑 
 *          ---            ---
 *           |              |
 *           ---------------- 동일 
 *           => INSERT / UPDATE / DELETE 
 *       => Entity : 반드시 컬럼과 일치 
 *          save() / delete() 
 *            |- insert/update 
 *   3) 자동으로  SQL문장을 만든다 (ORM) => 사용빈도 : (8:2)
 *      => 검색 : findBy
 *               ------ WHERE 컬럼 연산자 값
 *                            no   = 1
 *               findByNo(int no)
 *      => native: 실제 SQL문장을 제작 / JPQL / QueryDSL 
 *   4) 장단점 
 *      1. SQL 의존도 감소 (객체 중심 개발)
 *      2. 개발이 빠르다 (CURD중심) 
 *      3. 캐시메모리 / 지연로딩 => 성능 최적화 
 *         => Redis 
 *      4. 복잡한 객체관계가 있는 경우에 이해가 어렵다 
 *         ----------- JOIN 
 *      5. Subquery를 지원하지 않는다 
 *      6. JOIN => 잘못설정하면 성능 저하 발생 
 *         N:1 , N:N 
 *   5) 생명주기 
 *      JPA에서 연결 => 메소드 호출 => SQL제작 => DB연동 
 *   6) 주로 사용 
 *      : 간단한 CRUD / 대용량 (MyBatis)
 *      
 *   = MyBatis / JPA 장단점 
 *    
 *   
 */
public class BootBoard {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  // 자동 증가 번호 설정 
  // @GeneratedValue(strategy = GenerationType.SEQUENCE,generator="seq명" )
  private int no;
  private String name;
  private String subject;
  private String content;
  @Column(insertable = true,updatable = false)
  private String pwd;
  private int hit;
  @Column(insertable = true,updatable = false,name="regdate")
  private LocalDateTime regdate;
  
  @PrePersist
  public void perSist()
  {
	  regdate=LocalDateTime.now();
  }
}