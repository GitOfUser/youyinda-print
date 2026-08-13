package com.youyinda.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.youyinda.entity.UserAddress;
import com.youyinda.mapper.UserAddressMapper;
import com.youyinda.service.impl.UserAddressServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * 用户地址 Service 单元测试
 * 覆盖地址列表查询、默认地址获取、设置默认地址
 */
@ExtendWith(MockitoExtension.class)
class UserAddressServiceImplTest {

    @Mock
    private UserAddressMapper userAddressMapper;

    @InjectMocks
    private UserAddressServiceImpl userAddressService;

    private UserAddress addr1;
    private UserAddress addr2;
    private UserAddress addr3;

    @BeforeEach
    void setUp() {
        addr1 = new UserAddress();
        addr1.setId(1L);
        addr1.setUserId(100L);
        addr1.setName("张三");
        addr1.setPhone("13800000001");
        addr1.setProvince("广东");
        addr1.setCity("深圳");
        addr1.setDistrict("南山区");
        addr1.setDetailAddress("科技园1号");
        addr1.setIsDefault(1);

        addr2 = new UserAddress();
        addr2.setId(2L);
        addr2.setUserId(100L);
        addr2.setName("张三");
        addr2.setPhone("13800000002");
        addr2.setProvince("广东");
        addr2.setCity("广州");
        addr2.setDistrict("天河区");
        addr2.setDetailAddress("天河路2号");
        addr2.setIsDefault(0);

        addr3 = new UserAddress();
        addr3.setId(3L);
        addr3.setUserId(100L);
        addr3.setName("张三");
        addr3.setPhone("13800000003");
        addr3.setProvince("北京");
        addr3.setCity("北京");
        addr3.setDistrict("朝阳区");
        addr3.setDetailAddress("建国路3号");
        addr3.setIsDefault(0);
    }

    @Test
    @DisplayName("根据用户ID获取地址列表-按默认和时间倒序")
    void testGetByUserId() {
        when(userAddressMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Arrays.asList(addr1, addr2, addr3));

        List<UserAddress> result = userAddressService.getByUserId(100L);

        assertNotNull(result);
        assertEquals(3, result.size());
        verify(userAddressMapper).selectList(any(QueryWrapper.class));
    }

    @Test
    @DisplayName("根据用户ID获取地址列表-空列表")
    void testGetByUserId_Empty() {
        when(userAddressMapper.selectList(any(QueryWrapper.class)))
                .thenReturn(Collections.emptyList());

        List<UserAddress> result = userAddressService.getByUserId(999L);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("获取默认地址-存在默认地址")
    void testGetDefaultByUserId_Exists() {
        when(userAddressMapper.selectOne(any(QueryWrapper.class))).thenReturn(addr1);

        UserAddress result = userAddressService.getDefaultByUserId(100L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1, result.getIsDefault());
    }

    @Test
    @DisplayName("获取默认地址-无默认地址返回null")
    void testGetDefaultByUserId_NotExists() {
        when(userAddressMapper.selectOne(any(QueryWrapper.class))).thenReturn(null);

        UserAddress result = userAddressService.getDefaultByUserId(100L);

        assertNull(result);
    }

    @Test
    @DisplayName("设置默认地址-先将所有地址设为非默认，再设指定为默认")
    void testSetDefaultAddress() {
        // 调用设置默认地址
        userAddressService.setDefaultAddress(100L, 2L);

        // 验证第一步：先将用户所有地址设为非默认
        ArgumentCaptor<UserAddress> updateCaptor = ArgumentCaptor.forClass(UserAddress.class);
        ArgumentCaptor<QueryWrapper> wrapperCaptor = ArgumentCaptor.forClass(QueryWrapper.class);
        verify(userAddressMapper).update(updateCaptor.capture(), wrapperCaptor.capture());
        assertEquals(0, updateCaptor.getValue().getIsDefault());

        // 验证第二步：将指定地址设为默认
        ArgumentCaptor<UserAddress> defaultCaptor = ArgumentCaptor.forClass(UserAddress.class);
        verify(userAddressMapper).updateById(defaultCaptor.capture());
        assertEquals(2L, defaultCaptor.getValue().getId());
        assertEquals(1, defaultCaptor.getValue().getIsDefault());
    }
}
