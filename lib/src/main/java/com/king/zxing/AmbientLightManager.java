package com.king.zxing;

/*
 * Copyright (C) 2012 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import com.king.zxing.camera.CameraManager;

/**
 * Detects ambient light and switches on the front light when very dark, and off again when sufficiently light.
 *
 * @author Sean Owen
 * @author Nikolaus Huber
 */
final class AmbientLightManager implements SensorEventListener {

    private static final int INTERVAL_TIME = 200;

    protected static final float TOO_DARK_LUX = 45.0f;
    protected static final float BRIGHT_ENOUGH_LUX = 100.0f;

    /**
     * 光线太暗时，默认：照度45 lux
     */
    private float tooDarkLux = TOO_DARK_LUX;
    /**
     * 光线足够亮时，默认：照度450 lux
     */
    private float brightEnoughLux = BRIGHT_ENOUGH_LUX;

    private final Context context;
    private CameraManager cameraManager;
    private Sensor lightSensor;

    private long lastTime;

    AmbientLightManager(Context context) {
        this.context = context;
    }

    void start(CameraManager cameraManager) {
        this.cameraManager = cameraManager;
        SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        if (lightSensor != null) {
            sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    void stop() {
        if (lightSensor != null) {
            SensorManager sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
            sensorManager.unregisterListener(this);
            cameraManager = null;
            lightSensor = null;
        }
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        long currentTime = System.currentTimeMillis();
        if(currentTime - lastTime < INTERVAL_TIME){//降低频率
            return;
        }
        lastTime = currentTime;

        float ambientLightLux = sensorEvent.values[0];
        if (cameraManager != null) {
            if (ambientLightLux <= tooDarkLux) {
                cameraManager.sensorChanged(true,ambientLightLux);
            } else if (ambientLightLux >= brightEnoughLux) {
                cameraManager.sensorChanged(false,ambientLightLux);
            }
        }
    }

    public void setTooDarkLux(float tooDarkLux){
        this.tooDarkLux = tooDarkLux;
    }

    public void setBrightEnoughLux(float brightEnoughLux){
        this.brightEnoughLux = brightEnoughLux;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // do nothing
    }

}