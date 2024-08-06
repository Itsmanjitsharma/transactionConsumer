package com.app.consumer.tranaction_consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class Testcontroller {

    private static final Logger logger = LoggerFactory.getLogger(Testcontroller.class);
    

    @GetMapping("/test")
    public void test(){
        logger.info("information added to consumer");
    }
}
