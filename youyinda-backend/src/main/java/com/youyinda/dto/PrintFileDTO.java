package com.youyinda.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 打印文件DTO
 */
@Data
public class PrintFileDTO {

    /**
     * 文件URL
     */
    @NotBlank(message = "文件URL不能为空")
    private String fileUrl;

    /**
     * 文件名称
     */
    @NotBlank(message = "文件名称不能为空")
    private String fileName;

    /**
     * 文件类型
     */
    @NotBlank(message = "文件类型不能为空")
    private String fileType;

    /**
     * 文件大小（字节）
     */
    @NotNull(message = "文件大小不能为空")
    private Long fileSize;

    /**
     * 打印页数
     */
    @NotNull(message = "打印页数不能为空")
    private Integer pages;

    /**
     * 打印份数
     */
    @NotNull(message = "打印份数不能为空")
    private Integer copies;
}
