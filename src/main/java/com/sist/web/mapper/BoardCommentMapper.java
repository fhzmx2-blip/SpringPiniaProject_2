package com.sist.web.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import java.util.*;
import com.sist.web.vo.*;

@Mapper
@Repository
public interface BoardCommentMapper {

	public List<BootCommentVO> boardCommentListData(Map map);
	public int boardCommentCount(int board_no);
	public void boardCommentInsert(BootCommentVO vo);
}