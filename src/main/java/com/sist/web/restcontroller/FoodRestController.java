package com.sist.web.restcontroller;
import java.util.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sist.web.service.*;
import com.sist.web.vo.*;

import lombok.RequiredArgsConstructor;
@RestController
@RequiredArgsConstructor

/*
 *   RestFul : 다른 프로그램과 연동 
 *        => JavaScript / Kotlin 
 *        => CRUD 
 *        => Get / Post / Put / Delete
 *                              | Delete
 *                        | Update
 *            |     |Insert
 *           Select
 *        
 *   클라이언트   |    서버
 *                  SpringFramwork 
 *                  Spring-Boot 
 *   Vue            NodeJS 
 *   React          Django / fastApi 
 *                  ASP.NET  
 *   => PathValiable
 *      /food/detail/1  
 *      
 *   {
 *      name:'',
 *      subject:'',    ==> @RequestBody BoardVO vo
 *      content:'',
 *      pwd:''
 *   }
 */
public class FoodRestController {
   private final FoodService fService;
   
   // 전체 목록 출력 
   @GetMapping("/food/list_vue")
   public ResponseEntity<Map> food_list(
      @RequestParam("page") int page
   )
   {
	   Map map=new HashMap();
	   try
	   {
		   // 목록 
		   List<FoodVO> list=fService.foodListData(page);
		   int[] pages=fService.foodPages(page);
		   
		   map.put("list", list);
		   map.put("curpage", pages[0]);
		   map.put("totalpage", pages[1]);
		   map.put("startPage", pages[2]);
		   map.put("endPage", pages[3]);
		   /*
		    *   response.data => map 
		    *   {
		    *     list:[],
		    *     curpage:1,
		    *     ..
		    *     ..
		    *   }
		    */
	   }catch(Exception ex)
	   {
		   ex.printStackTrace();
		   return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	   }
	   return ResponseEntity.ok(map); // 200 
	   // HttpStatus.OK => 200
   }
}