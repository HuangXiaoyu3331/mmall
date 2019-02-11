package com.huang.mmall.serviceimpl;

import com.huang.mmall.bean.pojo.PayInfo;
import com.huang.mmall.dao.PayInfoMapper;
import com.huang.mmall.service.PayInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * PayInfoServiceImpl class
 *
 * @author hxy
 * @date 2019/1/30
 */
@Service
public class PayInfoServiceImpl implements PayInfoService {

    @Autowired
    private PayInfoMapper payInfoMapper;

    @Override
    public int save(PayInfo payInfo) {
        return payInfoMapper.insert(payInfo);
    }
}
