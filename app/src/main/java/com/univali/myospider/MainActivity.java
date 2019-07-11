package com.univali.myospider;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
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

public class MainActivity extends AppCompatActivity
{
    String SUBSCRIPTION;
    MqttAndroidClient client;

    Button bForward;
    Button bBackward;
    Button bRotRight;
    Button bRotLeft;

    EditText etIP;
    EditText etPort;
    EditText etTopic;
    EditText etUsername;
    EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bForward = findViewById(R.id.bForward);
        bBackward = findViewById(R.id.bBackward);
        bRotRight = findViewById(R.id.bRotRight);
        bRotLeft = findViewById(R.id.bRotLeft);
        etIP = findViewById(R.id.etIP);
        etPort = findViewById(R.id.etPort);
        etTopic = findViewById(R.id.etTopic);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);

        bForward.setEnabled(false);
        bBackward.setEnabled(false);
        bRotRight.setEnabled(false);
        bRotLeft.setEnabled(false);
    }

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
        String ip = etIP.getText().toString();
        String port = etPort.getText().toString();
        SUBSCRIPTION = etTopic.getText().toString();
        String USERNAME = etUsername.getText().toString();
        String PASSWORD = etPassword.getText().toString();

        String CONNECTION_URL = "tcp://" + ip + ":" + port;

        boolean error = false;

        if (isEmpty(etIP))
            error = true;
        if (isEmpty(etPort))
            error = true;
        if (isEmpty(etTopic))
            error = true;
        if (isEmpty(etUsername))
            error = true;
        if (isEmpty(etPassword))
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
                        Toast.makeText(MainActivity.this, "Successfully connected", Toast.LENGTH_LONG).show();
                        bForward.setEnabled(true);
                        bBackward.setEnabled(true);
                        bRotRight.setEnabled(true);
                        bRotLeft.setEnabled(true);
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception)
                    {
                        //Não conectou
                        Toast.makeText(MainActivity.this, "Connection wasn't successful", Toast.LENGTH_LONG).show();
                        bForward.setEnabled(false);
                        bBackward.setEnabled(false);
                        bRotRight.setEnabled(false);
                        bRotLeft.setEnabled(false);
                    }
                });
            } catch (MqttException e)
            {
                e.printStackTrace();
            }
        }
    }

    public void onClickClear(View v)
    {
        bForward.setEnabled(false);
        bBackward.setEnabled(false);
        bRotRight.setEnabled(false);
        bRotLeft.setEnabled(false);

        etIP.setText("");
        etPort.setText("");
        etTopic.setText("");
        etUsername.setText("");
        etPassword.setText("");
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
