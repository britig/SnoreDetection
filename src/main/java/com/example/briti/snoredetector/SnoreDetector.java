package com.example.briti.snoredetector;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;

import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;

public class SnoreDetector extends AppCompatActivity {
    private CDrawer.CDrawThread mDrawThread;
    private CDrawer mdrawer;
    private Boolean m_bStart = Boolean.valueOf(false);
    private Boolean recording;
    private CSampler sampler;
    public static int DETECT_SNORE = 0;
    TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_snore_detector);
        mdrawer = (CDrawer) findViewById(R.id.drawer);
        m_bStart = Boolean.valueOf(false);
        result = (TextView)findViewById(R.id.result);
        while (true)
        {
            recording = Boolean.valueOf(false);
            run();
            System.out.println("mDrawThread NOT NULL");
            System.out.println("recorder NOT NULL");
            return;
        }
    }

    @Override
    protected void onPause() {
        System.out.println("onpause");
        sampler.SetRun(Boolean.valueOf(false));
        mDrawThread.setRun(Boolean.valueOf(false));
        sampler.SetSleeping(Boolean.valueOf(true));
        mDrawThread.SetSleeping(Boolean.valueOf(true));
        Boolean.valueOf(false);
        super.onPause();
    }
    @Override
    protected void onRestart()
    {
        m_bStart = Boolean.valueOf(true);
        System.out.println("onRestart");
        super.onRestart();
    }

    public void onStartClick(View view) throws IOException{
        int i = 0;
        while (true)
        {
            if ((sampler.GetDead2().booleanValue()) && (mdrawer.GetDead2().booleanValue()))
            {
                System.out.println(sampler.GetDead2() + ", " + mdrawer.GetDead2());
                sampler.Restart();
                if (!m_bStart.booleanValue())
                    mdrawer.Restart(Boolean.valueOf(true));
                sampler.SetSleeping(Boolean.valueOf(false));
                mDrawThread.SetSleeping(Boolean.valueOf(false));
                m_bStart = Boolean.valueOf(false);
                return;
            }
            try
            {
                Thread.sleep(500L);
                System.out.println("Hang on..");
                i++;
                if (!sampler.GetDead2().booleanValue())
                    System.out.println("sampler not DEAD!!!");
                if (!mdrawer.GetDead2().booleanValue())
                {
                    System.out.println("mDrawer not DeAD!!");
                    mdrawer.SetRun(Boolean.valueOf(false));
                }
                if (i <= 4)
                    continue;
                mDrawThread.SetDead2(Boolean.valueOf(true));
            }
            catch (InterruptedException localInterruptedException)
            {
                localInterruptedException.printStackTrace();
            }
        }
    }

    public void onStopClick(View view) throws IOException {
        sampler.SetRun(Boolean.valueOf(false));
        mDrawThread.setRun(Boolean.valueOf(false));
        sampler.SetSleeping(Boolean.valueOf(true));
        mDrawThread.SetSleeping(Boolean.valueOf(true));
        Boolean.valueOf(false);
    }

    /**
     * Recives the buffert from the sampler
     * @param buffert
     */
    public void setBuffer(short[] paramArrayOfShort)
    {
        mDrawThread = mdrawer.getThread();
        mDrawThread.setBuffer(paramArrayOfShort);
    }

    /**
     * Called by OnCreate to get everything up and running
     */
    public void run()
    {
        try
        {
            if (mDrawThread == null)
            {
                mDrawThread = mdrawer.getThread();
            }
            if (sampler == null)
                sampler = new CSampler(this);
            Context localContext = getApplicationContext();
            Display localDisplay = getWindowManager().getDefaultDisplay();
            Toast localToast = Toast.makeText(localContext, "Recording Started..", Toast.LENGTH_LONG);
            localToast.setGravity(48, 0, localDisplay.getHeight() / 8);
            localToast.show();
            if (sampler != null){
                sampler.Init();
                sampler.StartRecording();
                sampler.StartSampling();
                DETECT_SNORE = sampler.detectSnoring();
                if(DETECT_SNORE==1){
                    result.setText("SNORING");
                    result.setTextColor(RED);
                }
                else{
                    result.setText("NOT SNORING");
                    result.setTextColor(GREEN);
                }
            }
        } catch (NullPointerException e) {
            Log.e("Main_Run", "NullPointer: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
