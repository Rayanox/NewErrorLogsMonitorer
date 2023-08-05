//package com.rb.monitoring.newerrorlogmonitoring.domain.common.exceptions;
//
//import com.rb.monitoring.newerrorlogmonitoring.domain.common.utils.ExceptionUtil;
//import lombok.AllArgsConstructor;
//import org.apache.commons.lang3.exception.ExceptionUtils;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//
//@AllArgsConstructor
//@Service
//public class ExceptionService {
//
//    private final ExceptionRepository exceptionRepository;
//
//
//    public void insertException(Exception e) {
//        ExceptionEntity exceptionEntity = ExceptionEntity.builder()
//                .exceptionClassName(e.getClass().getSimpleName())
//                .message(ExceptionUtil.getMessageRecursively(e))
//                .stackTrace(ExceptionUtils.getStackTrace(e))
//                .dateTime(LocalDateTime.now())
//                .build();
//
//        exceptionRepository.saveAndFlush(exceptionEntity);
//    }
//
//}
