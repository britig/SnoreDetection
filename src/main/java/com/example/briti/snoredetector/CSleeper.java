package com.example.briti.snoredetector;

/**
 * Created by Briti on 27-Aug-17.
 */

import java.io.PrintStream;

public class CSleeper implements Runnable{
    private Boolean done = Boolean.valueOf(false);
    private SnoreDetector m_ma;
    private CSampler m_sampler;

    public CSleeper(SnoreDetector paramMainActivity, CSampler paramCSampler)
    {
        m_ma = paramMainActivity;
        m_sampler = paramCSampler;
    }

    public void run()
    {
        try {
            m_sampler.Init();
        } catch (Exception e) {
            e.printStackTrace();
        }
        while (true)
            try
            {
                Thread.sleep(1000L);
                System.out.println("Tick");
                continue;
            }
            catch (InterruptedException localInterruptedException)
            {
                localInterruptedException.printStackTrace();
            }
    }
}

