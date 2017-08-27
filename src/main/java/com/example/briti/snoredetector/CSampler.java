package com.example.briti.snoredetector;

import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.util.Log;

import com.musicg.wave.WaveHeader;

import java.io.PrintStream;

public class CSampler
{
    private static final int SAMPPERSEC = 44100;
    private static short[] buffer;
    private AudioRecord ar;
    private int audioEncoding = 2;
    private int buffersizebytes;
    private int buflen;
    private int channelConfiguration = 16;
    private int mSamplesRead;
    private Boolean m_bDead = Boolean.valueOf(false);
    private Boolean m_bDead2 = Boolean.valueOf(true);
    private Boolean m_bRun;
    private Boolean m_bSleep = Boolean.valueOf(false);
    private SnoreDetector m_ma;
    private Thread recordingThread;
    byte[] bufferSample;
    public SnoringApi snoringApi;

    public CSampler(SnoreDetector paramMainActivity)
    {
        m_ma = paramMainActivity;
        m_bRun = Boolean.valueOf(false);
    }

    public Boolean GetDead2()
    {
        return m_bDead2;
    }

    public Boolean GetSleep()
    {
        return m_bSleep;
    }

    /**
     * Prepares to collect audiodata.
     * @throws Exception
     */
    public void Init() throws Exception
    {
        try {
            if (!m_bRun)
            {
                ar = new AudioRecord(1, 44100, channelConfiguration, audioEncoding, AudioRecord.getMinBufferSize(44100, channelConfiguration, audioEncoding));
                if (ar.getState() != 1)
                    return;
                System.out.println("State initialized");
            }
        } catch (Exception e) {
            Log.d("TE", e.getMessage());
            throw new Exception();
        }
        while (true)
        {
            buffersizebytes = AudioRecord.getMinBufferSize(44100, channelConfiguration, audioEncoding);
            buffer = new short[buffersizebytes];
            m_bRun = Boolean.valueOf(true);
            System.out.println("State unitialized!!!");
            return;
        }
    }

    /**
     * Restarts the thread
     */
    public void Restart()
    {
        while (true)
        {
            if (m_bDead2.booleanValue())
            {
                m_bDead2 = Boolean.valueOf(false);
                if (m_bDead.booleanValue())
                {
                    m_bDead = Boolean.valueOf(false);
                    ar.stop();
                    ar.release();
                    try {
                        Init();
                    } catch (Exception e) {
                        return;
                    }
                    StartRecording();
                    StartSampling();
                }
                return;
            }
            try
            {
                Thread.sleep(1000L);
            }
            catch (InterruptedException localInterruptedException)
            {
                localInterruptedException.printStackTrace();
            }
        }
    }

    /**
     * Reads the data-bufferts
     */
    public void Sample()
    {
        mSamplesRead = ar.read(buffer, 0, buffersizebytes);
    }


    public void SetRun(Boolean paramBoolean)
    {
        m_bRun = paramBoolean;
        if (m_bRun.booleanValue())
            StartRecording();
        while (true)
        {

            StopRecording();
            return;
        }
    }

    public void SetSleeping(Boolean paramBoolean)
    {
        m_bSleep = paramBoolean;
    }


    public void StartRecording()
    {
        if (ar == null) {
            try {
                Init();
            } catch (Exception e) {
                e.printStackTrace();
            }
            StartRecording();
        } else {

            ar.startRecording();

        }

    }

    /**
     * Collects audiodata and sends it back to the main activity
     */
    public void StartSampling()
    {
        recordingThread = new Thread()
        {
            public void run()
            {
                while (true)
                {
                    if (!m_bRun.booleanValue())
                    {
                        m_bDead = Boolean.valueOf(true);
                        m_bDead2 = Boolean.valueOf(true);
                        return;
                    }
                    Sample();
                    m_ma.setBuffer(CSampler.buffer);
                }
            }
        };
        recordingThread.start();
    }

    public void StopRecording()
    {
        ar.stop();
    }

    public void getBuffer()
    {
        int i;
        for(i=0;i<buffer.length;i++){
            bufferSample[i] =   Byte.parseByte(String.valueOf(buffer[i]));
        }
    }

    public int detectSnoring(){
        // TODO: added detection init
        WaveHeader waveHeader = new WaveHeader();
        waveHeader.setChannels(0);
        waveHeader.setBitsPerSample(0);
        waveHeader.setSampleRate(ar.getSampleRate());
        snoringApi = new SnoringApi(waveHeader);
        getBuffer();
        if(snoringApi.isSnoring(bufferSample)==1){
            return 1;
        }
        return 0;
    }


}
