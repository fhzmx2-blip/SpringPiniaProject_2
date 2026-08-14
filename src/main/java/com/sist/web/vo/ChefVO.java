package com.sist.web.vo;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;


@Data
public class ChefVO {
   private String chef;
   private String poster;
   private String mem_cont1,mem_cont3,mem_cont7,mem_cont2;
}
