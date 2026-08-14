package com.sist.web.service;
import java.util.*;

import org.springframework.stereotype.Service;

import com.sist.web.vo.*;

import lombok.RequiredArgsConstructor;

import com.sist.web.mapper.*;
@Service
@RequiredArgsConstructor
public class FoodServiceImpl implements FoodService{
  private final FoodMapper fMapper;

  @Override
  public List<FoodVO> foodListData(int page) {
	// TODO Auto-generated method stub
	int start=(page*12)-12;
	return fMapper.foodListData(start);
  }

  @Override
  public int foodTotalPage() {
	// TODO Auto-generated method stub
	return fMapper.foodTotalPage();
  }

  @Override
  public FoodVO foodDetailData(int no) {
	// TODO Auto-generated method stub
	fMapper.foodHitIncrement(no);
	return fMapper.foodDetailData(no);
  }
  /*
   *   1. Mapper : 재료(데이터베이스만 연동)
   *        => JDBC / MyBatis / JPA
   *   2. Controller / RestController 서빙 (브라우저로 전송) 
   *         |                  |
   *        화면 변경        Vue/React => 값 전송 
   *   3. Service : 쉐프 : 전체 요청 처리 
   *       | = DB + OpenAPI
   */
  @Override
  public int[] foodPages(int page) {
	// TODO Auto-generated method stub
	int totalpage=fMapper.foodTotalPage();
	final int BLOCK=10;
	int startPage=((page-1)/BLOCK*BLOCK)+1;
	int endPage=((page-1)/BLOCK*BLOCK)+BLOCK;
	int[] pages={page,totalpage,startPage,endPage};
	return pages;
  }
  
  
}