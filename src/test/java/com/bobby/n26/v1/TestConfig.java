package com.bobby.n26.v1;

import com.bobby.n26.v1.service.InMemoryTransactionService;
import com.bobby.n26.v1.service.TransactionService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author Babak Eghbali (Bob)
 * @since 2018/06/02
 */
@TestConfiguration
public class TestConfig {

    @Bean TransactionService transactionService(){
        return new InMemoryTransactionService();
    }

}
