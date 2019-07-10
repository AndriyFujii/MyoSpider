package com.univali.myospider;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;

public class MainActivity extends AppCompatActivity
{

    final String CONNECTION_URL = "tcp://10.3.141.1:1883";
    final String SUBSCRIPTION = "test_channel";
    final String USERNAME = "biosinal";
    final String PASSWORD = "notapass";
    MqttAndroidClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String clientId = MqttClient.generateClientId();
        client =  new MqttAndroidClient(this.getApplicationContext(), CONNECTION_URL, clientId);
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
                    Toast.makeText(MainActivity.this, "Successfully connected", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    //Não conectou
                    Toast.makeText(MainActivity.this, "Connection wasn't successfully!", Toast.LENGTH_LONG).show();
                }
            });
        }
        catch (MqttException e)
        {
            e.printStackTrace();
        }
    }

    public void onClickForward(View v)
    {
        String topic = SUBSCRIPTION;
        String message = "1500000000000000";
        try
        {
            client.publish(topic, message.getBytes(), 0, false);
        }
        catch(MqttException e)
        {
            e.printStackTrace();
        }
    }

    public void onClickBackward(View v)
    {
        String topic = SUBSCRIPTION;
        String message = "0000150000000000";
        try
        {
            client.publish(topic, message.getBytes(), 0, false);
        }
        catch(MqttException e)
        {
            e.printStackTrace();
        }
    }

    public void onClickRotRight(View v)
    {
        String topic = SUBSCRIPTION;
        String message = "0000000015000000";
        try
        {
            client.publish(topic, message.getBytes(), 0, false);
        }
        catch(MqttException e)
        {
            e.printStackTrace();
        }
    }

    public void onClickRotLeft(View v)
    {
        String topic = SUBSCRIPTION;
        String message = "0000000000001500";
        try
        {
            client.publish(topic, message.getBytes(), 0, false);
        }
        catch(MqttException e)
        {
            e.printStackTrace();
        }
    }
}
