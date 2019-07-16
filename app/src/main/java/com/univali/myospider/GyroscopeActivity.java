package com.univali.myospider;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

public class GyroscopeActivity extends AppCompatActivity implements SensorEventListener
{
    String SUBSCRIPTION;
    MqttAndroidClient client;

    Button bStart;
    Button bStop;

    EditText etIP2;
    EditText etPort2;
    EditText etTopic2;
    EditText etUsername2;
    EditText etPassword2;

    Boolean sendMessages = false;

    //private static final String TAG = "GyroscopeActivity";
    private SensorManager sensorManager;
    Sensor accelerometer;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gyroscope);

        bStart = findViewById(R.id.bStart);
        bStop = findViewById(R.id.bStop);
        etIP2 = findViewById(R.id.etIP2);
        etPort2 = findViewById(R.id.etPort2);
        etTopic2 = findViewById(R.id.etTopic2);
        etUsername2 = findViewById(R.id.etUsername2);
        etPassword2 = findViewById(R.id.etPassword2);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(GyroscopeActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        bStart.setEnabled(false);
        bStop.setEnabled(false);
    }

    //Verifica se algum campo está vazio e coloca um erro se estiver
    //Recebe o EditText a ser verificado
    //Retorna true caso esteja vazio, e false caso não
    public boolean isEmpty(EditText et)
    {
        String validation = et.getText().toString();
        if(TextUtils.isEmpty(validation))
        {
            et.setError("Can't be empty");
            return true;
        }

        return false;
    }

    public void onClickConnect(View v)
    {
        String ip = etIP2.getText().toString();
        String port = etPort2.getText().toString();
        SUBSCRIPTION = etTopic2.getText().toString();
        String USERNAME = etUsername2.getText().toString();
        String PASSWORD = etPassword2.getText().toString();

        String CONNECTION_URL = "tcp://" + ip + ":" + port;

        boolean error = false;

        if (isEmpty(etIP2))
            error = true;
        if (isEmpty(etPort2))
            error = true;
        if (isEmpty(etTopic2))
            error = true;
        if (isEmpty(etUsername2))
            error = true;
        if (isEmpty(etPassword2))
            error = true;

        if(!error)
        {
            String clientId = MqttClient.generateClientId();
            client = new MqttAndroidClient(this.getApplicationContext(), CONNECTION_URL, clientId);
            MqttConnectOptions options = new MqttConnectOptions();
            options.setUserName(USERNAME);
            options.setPassword(PASSWORD.toCharArray());

            try
            {
                IMqttToken token = client.connect(options);
                token.setActionCallback(new IMqttActionListener()
                {
                    @Override
                    public void onSuccess(IMqttToken asyncActionToken)
                    {
                        //Conectou
                        Toast.makeText(GyroscopeActivity.this, "Successfully connected", Toast.LENGTH_LONG).show();
                        bStart.setEnabled(true);
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception)
                    {
                        //Não conectou
                        Toast.makeText(GyroscopeActivity.this, "Connection wasn't successful", Toast.LENGTH_LONG).show();
                        bStart.setEnabled(false);
                        bStop.setEnabled(false);
                    }
                });
            } catch (MqttException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void onClickManual(View v)
    {
        sendMessages = false;
        Intent openMainScreen = new Intent(this, MainActivity.class);
        openMainScreen.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.startActivity(openMainScreen);
    }

    public void onClickClear(View v)
    {
        bStart.setEnabled(false);
        bStop.setEnabled(false);

        etIP2.setText("");
        etPort2.setText("");
        etTopic2.setText("");
        etUsername2.setText("");
        etPassword2.setText("");
    }

    public void onClickStart(View v)
    {
        Toast.makeText(GyroscopeActivity.this, "Starting", Toast.LENGTH_SHORT).show();
        sendMessages = true;
        bStop.setEnabled(true);
        bStart.setEnabled(false);
    }

    public void onClickStop(View v)
    {
        Toast.makeText(GyroscopeActivity.this, "Stopped", Toast.LENGTH_SHORT).show();
        sendMessages = false;
        bStop.setEnabled(false);
        bStart.setEnabled(true);
    }

    //Publica uma mensagem
    //Têm como parâmetro o tópico onde deve ser publicada a mensagem e a mensagem em si
    public void publishMessage(String topic, String message)
    {
        try
        {
            client.publish(topic, message.getBytes(), 0, false);
        }
        catch(MqttException e)
        {
            e.printStackTrace();
        }
    }

    long lastUpdate = 0;
    @Override
    public void onSensorChanged(SensorEvent sensorEvent)
    {
        long curTime = System.currentTimeMillis();
        if(sendMessages)
        {
            if(curTime - lastUpdate > 400)
            {
                //Valores de X
                //Direita
                if (sensorEvent.values[0] < -5)
                {
                    publishMessage(SUBSCRIPTION, "0000000015000000");
                }
                //Esquerda
                if (sensorEvent.values[0] > 5)
                {
                    publishMessage(SUBSCRIPTION, "0000000000001500");
                }
                //Valores de Y
                //Frente
                if (sensorEvent.values[1] < -5)
                {
                    publishMessage(SUBSCRIPTION, "1500000000000000");
                }
                //Atrás
                if (sensorEvent.values[1] > 5)
                {
                    publishMessage(SUBSCRIPTION, "0000150000000000");
                }
                lastUpdate = curTime;
            }

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i)
    {

    }
}
