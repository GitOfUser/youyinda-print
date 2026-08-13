package com.youyinda.service;

import com.youyinda.common.BusinessException;
import com.youyinda.dto.PrintFileDTO;
import com.youyinda.entity.PrintBasePrice;
import com.youyinda.enums.PrintScene;
import com.youyinda.service.impl.PrintSceneServiceImpl;
import com.youyinda.vo.PrintSceneVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * 打印场景 Service 单元测试
 * 覆盖预设场景列举、应用场景参数填充、自定义场景
 */
@ExtendWith(MockitoExtension.class)
class PrintSceneServiceTest {

    @Mock
    private PrintBasePriceService printBasePriceService;

    @InjectMocks
    private PrintSceneServiceImpl printSceneService;

    private List<PrintFileDTO> files;

    @BeforeEach
    void setUp() {
        files = new ArrayList<>();
        PrintFileDTO f = new PrintFileDTO();
        f.setFileName("test.pdf");
        f.setFileUrl("https://example.com/test.pdf");
        f.setPages(10);
        f.setCopies(1);
        files.add(f);
    }

    private PrintBasePrice buildBasePrice(double base, double min) {
        PrintBasePrice p = new PrintBasePrice();
        p.setBasePrice(base);
        p.setMinPrice(min);
        p.setProfitRatio(30.0);
        p.setProviderCode("liuliuyin");
        return p;
    }

    @Test
    @DisplayName("列出所有6个预设场景")
    void testListScenes() {
        List<PrintSceneVO> scenes = printSceneService.listScenes();

        assertNotNull(scenes);
        assertEquals(6, scenes.size());
        // 校验包含全部预设场景编码
        List<String> codes = scenes.stream().map(PrintSceneVO::getSceneCode).toList();
        for (PrintScene scene : PrintScene.values()) {
            assertTrue(codes.contains(scene.getCode()), "缺少场景: " + scene.getCode());
        }
    }

    @Test
    @DisplayName("应用考试试卷场景-参数正确填充")
    void testApplyExamPaperScene() {
        when(printBasePriceService.getBySpec("liuliuyin", "A4", "黑白", "double"))
                .thenReturn(buildBasePrice(0.10, 0.18));

        PrintSceneVO vo = printSceneService.applyScene("exam_paper", files);

        assertNotNull(vo);
        assertEquals("exam_paper", vo.getSceneCode());
        assertEquals("A4", vo.getPaperType());
        assertEquals("黑白", vo.getColorType());
        assertEquals("双面", vo.getPrintSide());
        assertEquals("无装订", vo.getBinding());
        assertNotNull(vo.getEstimatedTotal());
        // 单份价MAX(0.10*1.3,0.18)=0.18；双面10页按奇数进一=5张；0.18*5=0.90 + 快递5.00 = 5.90
        assertEquals(0, new BigDecimal("5.90").setScale(2, java.math.RoundingMode.HALF_UP)
                .compareTo(vo.getEstimatedTotal().setScale(2, java.math.RoundingMode.HALF_UP)));
    }

    @Test
    @DisplayName("应用毕业论文场景-参数正确填充")
    void testApplyThesisScene() {
        when(printBasePriceService.getBySpec("liuliuyin", "A4", "黑白", "double"))
                .thenReturn(buildBasePrice(0.10, 0.18));

        PrintSceneVO vo = printSceneService.applyScene("thesis", files);

        assertNotNull(vo);
        assertEquals("thesis", vo.getSceneCode());
        assertEquals("A4", vo.getPaperType());
        assertEquals("黑白", vo.getColorType());
        assertEquals("双面", vo.getPrintSide());
        assertEquals("胶装", vo.getBinding());
        // 双面10页按奇数进一=5张；打印费0.18*5=0.90 + 胶装5.00 + 快递5.00 = 10.90
        assertEquals(0, new BigDecimal("10.90").setScale(2, java.math.RoundingMode.HALF_UP)
                .compareTo(vo.getEstimatedTotal().setScale(2, java.math.RoundingMode.HALF_UP)));
    }

    @Test
    @DisplayName("自定义场景-按传入参数计算预估价格")
    void testCustomScene() {
        when(printBasePriceService.getBySpec("liuliuyin", "照片纸", "彩色", "single"))
                .thenReturn(buildBasePrice(0.80, 1.50));

        PrintSceneVO vo = printSceneService.customScene("照片纸", "彩色", "单面", "无装订", 1, files);

        assertNotNull(vo);
        assertEquals("照片纸", vo.getPaperType());
        assertEquals("彩色", vo.getColorType());
        assertEquals("单面", vo.getPrintSide());
        assertEquals("无装订", vo.getBinding());
        // 单份价MAX(0.80*1.3,1.50)=1.50；10页*1份=15.00 + 快递5.00 = 20.00
        assertEquals(0, new BigDecimal("20.00").compareTo(vo.getEstimatedTotal()));
    }
}
