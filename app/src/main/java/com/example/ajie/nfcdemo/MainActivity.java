package com.example.ajie.nfcdemo;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.ajie.nfcdemo.utils.StringHelper;

public class MainActivity extends AppCompatActivity {
    protected boolean isRunClose = true;

    private NfcAdapter nfcAdapter = null;
    private static PendingIntent mPendingIntent;

    private static IntentFilter[] mFilters;

    private static String[][] mTechLists;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView text_view = findViewById(R.id.tv);

        handler=new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what){
                    case 1:
                        Bundle bundle = msg.getData();
                        String string = bundle.getString("1");
                        Log.d("",string);
                        text_view.setText(string);
                }
                return false;
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        InitNfc(handler);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (nfcAdapter != null) {
            nfcAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters,
                    mTechLists);

        }
    }
    @Override
    protected void onPause() {
        if (nfcAdapter != null) {
            //设置当前程序为优先处理nfc的程序
            nfcAdapter.disableForegroundDispatch(this);
        }
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {

        super.onNewIntent(intent);
        ReadNfcIntent(intent,handler);
    }

    protected void InitNfc(Handler handler) {

        if (nfcAdapter == null) {
            nfcAdapter = NfcAdapter.getDefaultAdapter(getApplicationContext());

            if (nfcAdapter != null) {
                if (nfcAdapter.isEnabled()) {
                    mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this,
                            getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
                    // Setup an intent filter for all MIME based dispatches
                    IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);

                    try {
                        ndef.addDataType("*/*");
                    } catch (IntentFilter.MalformedMimeTypeException e) {
                        throw new RuntimeException("fail", e);
                    }
                    mFilters = new IntentFilter[]{ndef,};

                    // Setup a tech list for all NfcF tags
                    mTechLists = new String[][]{new String[]{MifareClassic.class
                            .getName()}};

                    Intent intent = getIntent();
                    ReadNfcIntent(intent,handler);



                    //SendHandlerMessage(scalehandler, ChildrenHospitalConst.Message_What_Nfc_Device_Init);

                }

            }

        }

    }

    public void ReadNfcIntent(Intent intent,Handler handler) {

        String action = intent.getAction();

        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {

            Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            MifareClassic mfc = MifareClassic.get(tagFromIntent);

            Tag tag = mfc.getTag();

            if (tag != null) {
                byte[] bytes = tag.getId();
                if (bytes != null) {
                    Bundle bundle = new Bundle();
                    String value = StringHelper.Bytes2HexString(bytes);
                    Log.d("NFCread",value);
                    bundle.putString("1", value);
                    SendHandlerMessage(handler,1,bundle);
                    //SendMessage1(HospitalConst.Message_What_Sender_Rfid_Hf_Card_Id, bundle);

                }
            }
        }
    }

    protected void SendHandlerMessage(Handler handler, int what, Bundle bundle) {

        Message message = handler.obtainMessage(what);
        if (bundle != null && bundle.size() > 0) {
            message.setData(bundle);
        }
        handler.sendMessage(message);
    }

}
