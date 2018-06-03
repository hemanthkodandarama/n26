package com.bobby.n26.v1.web;

import com.bobby.n26.v1.config.TransactionProperties;
import com.bobby.n26.v1.service.TransactionService;
import com.bobby.n26.v1.web.dto.ConfigData;
import com.bobby.n26.v1.web.mapper.CommonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author Babak Eghbali (Bob)
 * @since 2018/06/02
 */
@RestController("/config")
public class ConfigController {
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    @Autowired TransactionProperties prop;
    @Autowired CommonMapper mapper;
    @Autowired TransactionService service;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<ConfigData> getConfig(){
        return ResponseEntity.ok().body(mapper.toData(prop));
    }

    @RequestMapping(method = RequestMethod.PUT)
    public void saveConfig(@RequestBody ConfigData data){
        if (data == null){
            return;
        }
        log.info("Updating config");
        prop.setAgeLimitForSaveByMillis(data.getAgeLimitForSaveByMillis());
        prop.setAgeLimitForStatsByMillis(data.getAgeLimitForStatsByMillis());
        prop.setTimeDiscountForSaveByMillis(data.getTimeDiscountForSaveByMillis());
        log.info("Config data after update: {}",prop);
    }

}
