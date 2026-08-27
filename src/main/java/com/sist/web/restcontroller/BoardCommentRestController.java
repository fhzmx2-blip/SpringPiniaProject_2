package com.sist.web.restcontroller;

import java.util.*;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.sist.web.mapper.BoardCommentMapper;
import com.sist.web.vo.*;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class BoardCommentRestController {
	private final BoardCommentMapper bMapper;

	public Map commonsListData(int page, int board_no) {
		Map map = new HashMap();
		int start = (page * 10) - 10;
		map.put("start", start);
		map.put("board_no", board_no);

		List<BootCommentVO> list = bMapper.boardCommentListData(map);
		int count = bMapper.boardCommentCount(board_no);
		int totalpage = (int) (Math.ceil(count / 10.0));

		map = new HashMap();
		map.put("list", list);
		map.put("curpage", page);
		map.put("totalpage", totalpage);
		map.put("count", count);

		return map;
	}

	@Async
	@GetMapping("/board/list_vue")
	public ResponseEntity<Map> board_List(@RequestParam("no") int board_no, @RequestParam("page") int page) {
		Map map = new HashMap();
		try {
			map = commonsListData(page, board_no);
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		return ResponseEntity.ok(map);
	}

	@Async
	@PostMapping("/reply/insert_vue")

	public ResponseEntity<Map> reply_insert(@RequestBody BootCommentVO vo, HttpSession session) {
		Map map = new HashMap();
		try {
			String id = (String) session.getAttribute("userid");
			String name = (String) session.getAttribute("username");
			vo.setId(id);
			vo.setName(name);

			bMapper.boardCommentInsert(vo);

			map = commonsListData(vo.getPage(), vo.getBoard_no());
		} catch (Exception ex) {
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
		}
		return ResponseEntity.ok(map);
	}
}