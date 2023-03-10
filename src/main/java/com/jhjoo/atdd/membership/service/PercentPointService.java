package com.jhjoo.atdd.membership.service;

public class PercentPointService implements PointService {

    final int percent = 1;
    @Override
    public int calculateAmount(int price) {
        return (price * percent)/100;
    }
}
