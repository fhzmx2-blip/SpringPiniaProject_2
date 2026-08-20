package com.sist.web.restcontroller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.sist.web.service.*;
import com.sist.web.vo.*;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

import java.util.*;
@RestController
@RequiredArgsConstructor
public class CommentRestController {
   private final CommentService cService;
   
   public Map commonsData(int page,int fno)
   {
	   Map map=new HashMap();
	   int start=(page*10)-10;
	   List<CommentVO> list=cService.commentListData(start,fno);
	   int count=cService.commentRowCount(fno);
	   int totalpage=(int)(Math.ceil(count/10.0));
	   map.put("rList", list);
	   map.put("count", count);
	   map.put("curpage", page);
	   map.put("totalpage", totalpage);
	   return map;
   }
   @GetMapping("/comment/list_vue")
   public ResponseEntity<Map> comment_list(
      @RequestParam("page") int page,
      @RequestParam("fno") int fno
   )
   {
	   Map map=new HashMap();
	   try
	   {
		   map=commonsData(page, fno);
	   }catch(Exception ex)
	   {
		   ex.printStackTrace();
		   return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	   }
	   return ResponseEntity.ok(map);
   }
   @PostMapping("/comment/insert_vue")
   // {} => vo JSON => 객체로 변경 : @RequestBody
   // 일반 데이터 => @ModelAttribute
   public ResponseEntity<Map> comment_insert(
		@RequestBody CommentVO vo,
		HttpSession session
   )
   {
	   Map map=new HashMap();
	   try
	   {
		   String id=(String)session.getAttribute("userid");
		   String name=(String)session.getAttribute("username");
		   vo.setId(id);
		   vo.setName(name);
		   cService.commentInsert(vo);
		   map=commonsData(vo.getPage(), vo.getFno());
	   }catch(Exception ex)
	   {
		   ex.printStackTrace();
		   return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
	   }
	   return ResponseEntity.ok(map);
   }
}



