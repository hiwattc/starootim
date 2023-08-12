package com.staroot.im.controller;

import com.staroot.im.entity.User;
import com.staroot.im.repository.SysItemRepository;
import com.staroot.im.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("system")
public class SysItemController {
    private static final Logger logger = LoggerFactory.getLogger(SysItemController.class);

    private final SysItemRepository sysItemRepository;

    public SysItemController(SysItemRepository sysItemRepository) {
        this.sysItemRepository = sysItemRepository;
    }

    @GetMapping("/list")
    private List<Map<String, Object>> getSysItemWithSeq() {
        List<Object[]> resultList = sysItemRepository.findAllWithSequence();
        List<Map<String, Object>> resultMapList = new ArrayList<>();

        for (Object[] result : resultList) {
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("code", result[0]);
            resultMap.put("name", result[1]);
            resultMapList.add(resultMap);
        }

        logger.debug(resultMapList.toString());

        return resultMapList;
    }
}
