package com.jhjoo.atdd.membership.service;

public class AmountPointService implements PointService{

    final int amount = 100;

    @Override
    public int calculateAmount(int price) {
        return amount;
    }
}
