package com.l1fe1.demolog.impl;

import com.l1fe1.demolog.Log;

public class Log4j implements Log {
    @Override
    public void log(String info) {
        System.out.println("Log4j:" + info);
    }
}
