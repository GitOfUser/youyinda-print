package com.youyinda.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.youyinda.entity.FileInfo;
import com.youyinda.mapper.FileInfoMapper;
import com.youyinda.service.FileInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FileInfoServiceImpl extends ServiceImpl<FileInfoMapper, FileInfo> implements FileInfoService {
}
