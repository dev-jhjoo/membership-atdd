package com.jhjoo.atdd.membership.service;

import com.jhjoo.atdd.membership.enums.PointSaveType;
import com.jhjoo.atdd.membership.exception.PointErrorResult;
import com.jhjoo.atdd.membership.exception.PointException;
import org.springframework.stereotype.Component;

@Component
public class PointServiceFactory {
    public PointService getPointService(PointSaveType saveType){
        switch (saveType){
            case PERCENT:
                return new PercentPointService();
            case AMOUNT:
                return new AmountPointService();
            default:
                throw new PointException(PointErrorResult.UNKNOWN_SERVICE_TYPE);
        }
    }
}
