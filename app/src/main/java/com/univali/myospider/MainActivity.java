package com.univali.myospider;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
    Button bWave;
    Button bWave2;

    EditText etIP;
    EditText etPort;
    EditText etTopic;
    EditText etUsername;
    EditText etPassword;

    public void disableButtons()
    {
        bForward.setEnabled(false);
        bBackward.setEnabled(false);
        bRotRight.setEnabled(false);
        bRotLeft.setEnabled(false);
        bWave.setEnabled(false);
        bWave2.setEnabled(false);
    }

    public void enableButtons()
    {
        bForward.setEnabled(true);
        bBackward.setEnabled(true);
        bRotRight.setEnabled(true);
        bRotLeft.setEnabled(true);
        bWave.setEnabled(true);
        bWave2.setEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bForward = findViewById(R.id.bForward);
        bBackward = findViewById(R.id.bBackward);
        bRotRight = findViewById(R.id.bRotRight);
        bRotLeft = findViewById(R.id.bRotLeft);
        bWave = findViewById(R.id.bWave);
        bWave2 = findViewById(R.id.bWave2);
        etIP = findViewById(R.id.etIP);
        etPort = findViewById(R.id.etPort);
        etTopic = findViewById(R.id.etTopic);
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);

        disableButtons();
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
                        enableButtons();
                    }

                    @Override
                    public void onFailure(IMqttToken asyncActionToken, Throwable exception)
                    {
                        //Não conectou
                        Toast.makeText(MainActivity.this, "Connection wasn't successful", Toast.LENGTH_LONG).show();
                        enableButtons();
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
        disableButtons();

        etIP.setText("");
        etPort.setText("");
        etTopic.setText("");
        etUsername.setText("");
        etPassword.setText("");
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

    public void onClickGyroscope(View v)
    {
        Intent intentOpenGyroscopeAtivity = new Intent(this.getApplicationContext(), GyroscopeActivity.class);
        this.startActivity(intentOpenGyroscopeAtivity);
    }

    public void onClickForward(View v)
    {
        publishMessage(SUBSCRIPTION,"1500000000000000");
    }

    public void onClickBackward(View v)
    {
        publishMessage(SUBSCRIPTION,"0000150000000000");
    }

    public void onClickRotRight(View v)
    {
        publishMessage(SUBSCRIPTION,"0000000015000000");
    }

    public void onClickRotLeft(View v)
    {
        publishMessage(SUBSCRIPTION,"0000000000001500");
    }

    public void onClickWave(View v)
    {
        publishMessage(SUBSCRIPTION,"Wave1");
    }

    public void onClickWave2(View v)
    {
        publishMessage(SUBSCRIPTION,"Wave2");
    }
}
