package com.youyinda.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileInfoVO {

    private Long id;
    private String fileName;
    private String originalName;
    private String fileUrl;
    private String previewUrl;
    private Long fileSize;
    private String fileType;
    private String fileExt;
    private Integer pageCount;
    private Date createTime;
}
