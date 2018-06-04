package com.bobby.n26.v1.web;

import com.bobby.n26.v1.common.OutDatedTransactionException;
import com.bobby.n26.v1.service.TransactionService;
import com.bobby.n26.v1.web.dto.StatsData;
import com.bobby.n26.v1.web.dto.TransactionData;
import com.bobby.n26.v1.web.mapper.CommonMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Babak Eghbali (Bob)
 * @since 2018/06/02
 */
@RestController
public class TransactionController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired CommonMapper mapper;
    @Autowired TransactionService service;

    @RequestMapping(path = "/transactions", method = RequestMethod.POST)
    public ResponseEntity saveTransaction(@RequestBody TransactionData data){

        if (data == null || data.getTimestamp() == 0){
            log.error("Invalid transaction data");
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        log.info("Input transaction data: {}",data);
        try {
            service.save(mapper.toModel(data));
            return new ResponseEntity(HttpStatus.CREATED);
        } catch (OutDatedTransactionException outDatedTransactionException) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
    }

    @RequestMapping(path = "/statistics", method = RequestMethod.GET)
    public ResponseEntity<StatsData> getStatistics(){
        StatsData stats = mapper.toData(service.getStatistics());
        log.info("Returning statistics to web client: {}",stats);
        return ResponseEntity.ok().body(stats);
    }

    @RequestMapping(path = "/transactions", method = RequestMethod.DELETE)
    public void clearAllTransactions(){
        log.info("Delete all transactions has been requested by user");
        service.reset();
    }


}
