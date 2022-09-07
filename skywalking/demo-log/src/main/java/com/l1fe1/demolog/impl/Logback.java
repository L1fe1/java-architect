package com.l1fe1.demolog.impl;

import com.l1fe1.demolog.Log;

public class Logback implements Log {
    @Override
    public void log(String info) {
        System.out.println("Logback:" + info);
    }
}
