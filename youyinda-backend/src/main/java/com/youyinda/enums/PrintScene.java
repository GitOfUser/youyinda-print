package com.youyinda.enums;

import lombok.Getter;

/**
 * 多场景打印预设枚举
 * 封装常见打印场景的默认参数，方便用户一键选择
 */
@Getter
public enum PrintScene {

    EXAM_PAPER("exam_paper", "考试试卷", "A4", "黑白", "双面", "无装订", "适合试卷打印"),
    THESIS("thesis", "毕业论文", "A4", "黑白", "双面", "胶装", "适合论文/报告打印"),
    COLOR_PPT("color_ppt", "彩色PPT", "A4", "彩色", "单面", "骑马钉", "适合演示文稿打印"),
    PHOTO("photo", "照片冲印", "照片纸", "彩色", "单面", "无装订", "适合照片打印"),
    CONTRACT("contract", "合同文件", "A4", "黑白", "单面", "无装订", "适合合同/证件打印"),
    BOOKLET("booklet", "手册画册", "A4", "彩色", "双面", "骑马钉", "适合手册/画册打印");

    private final String code;
    private final String name;
    private final String paperType;
    private final String colorType;
    private final String printSide;
    private final String binding;
    private final String desc;

    PrintScene(String code, String name, String paperType, String colorType,
               String printSide, String binding, String desc) {
        this.code = code;
        this.name = name;
        this.paperType = paperType;
        this.colorType = colorType;
        this.printSide = printSide;
        this.binding = binding;
        this.desc = desc;
    }

    public static PrintScene getByCode(String code) {
        for (PrintScene scene : values()) {
            if (scene.getCode().equalsIgnoreCase(code)) {
                return scene;
            }
        }
        return null;
    }
}
