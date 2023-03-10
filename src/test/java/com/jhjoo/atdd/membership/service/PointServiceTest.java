package com.jhjoo.atdd.membership.service;

import com.jhjoo.atdd.membership.enums.PointSaveType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


@ExtendWith(MockitoExtension.class)
class PointServiceTest {

    @InjectMocks
    private PointServiceFactory serviceFactory;

    @Test
    public void 정량포인트서비스클래스생성(){
        // given
        final PointSaveType saveType = PointSaveType.AMOUNT;

        // when
        final PointService pointService = serviceFactory.getPointService(saveType);

        // then
        assertThat(pointService.getClass()).isEqualTo(AmountPointService.class);
    }

    @Test
    public void 비율포인트서비스클래스생성(){
        // given
        final PointSaveType saveType = PointSaveType.PERCENT;

        // when
        final PointService pointService = serviceFactory.getPointService(saveType);

        // then
        assertThat(pointService.getClass()).isEqualTo(PercentPointService.class);
    }


    @Test
    public void _10000원정량포인트적립(){
        // given
        final PointService pointService = serviceFactory.getPointService(PointSaveType.AMOUNT);

        // when
        final int point = pointService.calculateAmount(10000);

        // then
        assertThat(point).isEqualTo(100);
    }


    @Test
    public void _10000원비율포인트적립(){
        // given
        final PointService pointService = serviceFactory.getPointService(PointSaveType.PERCENT);

        // when
        final int point = pointService.calculateAmount(10000);

        // then
        assertThat(point).isEqualTo(100);
    }

    @Test
    public void _12450원비율포인트적립(){
        // given
        final PointService pointService = serviceFactory.getPointService(PointSaveType.PERCENT);

        // when
        final int point = pointService.calculateAmount(12450);

        // then
        assertThat(point).isEqualTo(124);
    }

}