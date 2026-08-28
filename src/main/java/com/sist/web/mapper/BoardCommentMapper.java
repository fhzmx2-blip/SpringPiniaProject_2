package com.sist.web.mapper;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;
import java.util.*;
import com.sist.web.vo.*;
@Mapper
@Repository
public interface BoardCommentMapper {
  
	public List<BootCommentVO> boardCommentListData(Map map);
	public int boardCommentCount(int board_no);
	public void boardCommentInsert(BootCommentVO vo);
	
	@Select("SELECT id,group_id,group_step,group_tab "
		   +"FROM bootComment "
		   +"WHERE no=#{no}")
	public BootCommentVO boardParentInfoData(int no);
	
	@Update("UPDATE bootComment SET "
		   +"group_step=group_step+1 "
		   +"WHERE group_id=#{group_id} AND group_step>#{group_step}")
	public void boardGroupStepIncrement(
	   @Param("group_id") int group_id,
	   @Param("group_step") int group_step
	);
	
	@SelectKey(keyProperty = "no" , resultType = int.class , before = true,
			statement = "SELECT NVL(MAX(no)+1,1) as no FROM bootComment")
	@Insert("INSERT INTO bootComment VALUES("
			+"#{no},#{board_no},#{id},#{name},#{msg},"
			+"SYSDATE,#{group_id},#{group_step},"
			+"#{group_tab},#{root},0"
		   +")")
	public void boardCommentReReply(BootCommentVO vo);
	
	@Update("UPDATE bootComment SET "
		   +"depth=depth+1 "
		   +"WHERE no=#{no}")
	public void boardDepthIncrement(int no);
	
	
}