package com.sist.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sist.web.entity.BootBoard;

public interface BootBoardRepository extends JpaRepository<BootBoard, Integer> {
	public BootBoard findByNo(int no);
}