package com.sist.web.vo;

import java.util.Date;

import lombok.Data;

@Data
public class BootCommentVO {
	private int no, board_no, group_id, page, group_step, group_tab, root, depth;
	private String id, name, msg, dbday;
	private Date regdate;
}