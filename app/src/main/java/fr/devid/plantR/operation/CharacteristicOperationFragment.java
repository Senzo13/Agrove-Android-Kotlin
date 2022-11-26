package fr.devid.plantR.operation;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothGattCharacteristic;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import java.util.ArrayList;
import java.util.List;
import fr.devid.plantR.BleManager;
import fr.devid.plantR.R;
import fr.devid.plantR.callback.BleIndicateCallback;
import fr.devid.plantR.callback.BleReadCallback;
import fr.devid.plantR.callback.BleWriteCallback;
import fr.devid.plantR.data.BleDevice;
import fr.devid.plantR.exception.BleException;
import fr.devid.plantR.services.Singleton;
import fr.devid.plantR.ui.stateGardener.PlantrFragment;
import fr.devid.plantR.utils.HexUtil;
import timber.log.Timber;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class CharacteristicOperationFragment extends Fragment {
    //
    //DU WRITE & NOTIFY
    public static final int PROPERTY_READ = 1;
    public static final int PROPERTY_WRITE = 2;
    public static final int PROPERTY_WRITE_NO_RESPONSE = 3;
    public static final int PROPERTY_NOTIFY = 4;
    public static final int PROPERTY_INDICATE = 5;
    public static Integer xo = 1;
    public Integer nb = 0;
    private LinearLayout layout_container;
    private List<String> childList = new ArrayList<>();
    Singleton singleton = Singleton.Companion.getInstance();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_characteric_operation, null);
        initView(v);
        return v;
    }

    private void initView(View v) {
        layout_container = (LinearLayout) v.findViewById(R.id.layout_container);
    }

    public static String hexToStr(String hex) {
        if (hex == "" || hex.length() == 0) {
            return "";
        } else {
            String digital = "0123456789ABCDEF";
            char[] hex2char = hex.toCharArray();
            byte[] bytes = new byte[hex.length() / 2];
            int temp;
            for (int i = 0; i < bytes.length; i++) {
                temp = digital.indexOf(hex2char[2 * i]) * 16;
                temp += digital.indexOf(hex2char[2 * i + 1]);
                bytes[i] = (byte) (temp & 0xff);
            }
            return new String(bytes);
        }
    }

    public static String str2Hex(String bin) {
        char[] digital = "0123456789ABCDEF".toCharArray();
        StringBuffer sb = new StringBuffer("");
        byte[] bs = bin.getBytes();
        int bit;
        for (int i = 0; i < bs.length; i++) {
            bit = (bs[i] & 0x0f0) >> 4;
            sb.append(digital[bit]);
            bit = bs[i] & 0x0f;
            sb.append(digital[bit]);
        }
        return sb.toString();
    }

//    public void keyboard() {
//        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(windowToken, 0)
//    }
public static String hexToString(String hex)
{
    StringBuilder sb = new StringBuilder();

    for (int count = 0; count < hex.length() - 1; count += 2)
    {

        try{
            String output = hex.substring(count, (count + 2));    //grab the hex in pairs

            int decimal = Integer.parseInt(output, 16);//convert hex to decimal
            sb.append((char)decimal);    //convert the decimal to character

        } catch(NumberFormatException exception) {
            Timber.d("Bonjour probleme exception de nombre " + exception);
        }

    }

    return sb.toString();
}

    public void showData() {
        final BleDevice bleDevice = Singleton.Companion.getInstance().getBleDevice();
        final BluetoothGattCharacteristic characteristic = Singleton.Companion.getInstance().getCharacteristic();
        final int charaProp = Singleton.Companion.getInstance().getCharaProp();
        String child = characteristic.getUuid().toString() + String.valueOf(charaProp);

        for (int i = 0; i < layout_container.getChildCount(); i++) {
            layout_container.getChildAt(i).setVisibility(View.GONE);
        }
        if (childList.contains(child)) {
            layout_container.findViewWithTag(bleDevice.getKey() + characteristic.getUuid().toString() + charaProp).setVisibility(View.VISIBLE);
        } else {
            childList.add(child);

            View view = LayoutInflater.from(getActivity()).inflate(R.layout.layout_characteric_operation, null);
            view.setTag(bleDevice.getKey() + characteristic.getUuid().toString() + charaProp);
            LinearLayout layout_add = (LinearLayout) view.findViewById(R.id.layout_add);
            final TextView txt_title = (TextView) view.findViewById(R.id.txt_title);
            txt_title.setText(String.valueOf(characteristic.getUuid().toString() + "\n" + getActivity().getString(R.string.data_changed)));
            final TextView txt = (TextView) view.findViewById(R.id.txt);
            txt.setMovementMethod(ScrollingMovementMethod.getInstance());

            switch (charaProp) {
                case PROPERTY_READ: {
                    View view_add = LayoutInflater.from(getActivity()).inflate(R.layout.layout_characteric_operation_button, null);
                    Button btn = (Button) view_add.findViewById(R.id.btn);
                    btn.setText(getActivity().getString(R.string.read));
                    //final TextView response = view_add.findViewById(R.id.tv_response);
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            BleManager.getInstance().read(
                                    bleDevice,
                                    characteristic.getService().getUuid().toString(),
                                    characteristic.getUuid().toString(),
                                    new BleReadCallback() {

                                        @Override
                                        public void onReadSuccess(final byte[] data) {
                                            runOnUiThread(() -> {
                                                addText(txt, "Ligne : " + xo + "\nHexa :\n"+HexUtil.formatHexString(data, true)+"\n\nJson :\n" + hexToString(HexUtil.formatHexString(data, false)) + "\n");
                                                String monText = HexUtil.formatHexString(data, false);
                                                Log.d("CONTENU ", monText);
                                                Timber.d("Mon gros test " + hexToStr(monText));
                                                Log.d("OO" ,  hexToStr(monText));
                                                xo += 1;
                                            });
                                        }

                                        @Override
                                        public void onReadFailure(final BleException exception) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    addText(txt, exception.toString());
                                                }
                                            });
                                        }
                                    });
                        }
                    });
                    layout_add.addView(view_add);
                }
                break;

                case PROPERTY_WRITE:

                case PROPERTY_WRITE_NO_RESPONSE: {
                    View view_add = LayoutInflater.from(getActivity()).inflate(R.layout.layout_characteric_operation_et, null);
                    final EditText et = (EditText) view_add.findViewById(R.id.et);
                    et.getText().clear(); //Vider le contenu
                    final TextView et1 = view_add.findViewById(R.id.et_1);
                    final TextView et2 = view_add.findViewById(R.id.et_2);
                    Button btn = (Button) view_add.findViewById(R.id.btn);
                    Button btn1 = (Button) view_add.findViewById(R.id.btn_send);
                    Button btn2 = (Button) view_add.findViewById(R.id.btn_send1);
                    Button btn3 = (Button) view_add.findViewById(R.id.btn_clear);
                    Button btn4 = (Button) view_add.findViewById(R.id.btn_get_data);
                    Button btn5 = (Button) view_add.findViewById(R.id.btn_exec_data);
                    Button btn6 = (Button) view_add.findViewById(R.id.btn_stop_get_data);
                    //btn.setText(getActivity().getString(R.string.write));
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String hex = et.getText().toString();
                            String hexTest = str2Hex(et.getText().toString());
                            Log.d("IMPORTANT" , "MON STRING TRADUIT EN HEX EST : " + hexTest);
                            if (TextUtils.isEmpty(hexTest)) {
                                return;
                            }
                            BleManager.getInstance().write(
                                    bleDevice,
                                    HexUtil.hexStringToBytes(hexTest),
                                    new BleWriteCallback() {

                                        @Override
                                        public void onWriteSuccess(final int current, final int total, final byte[] justWrite) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    addText(txt, "write success !\ncurrent: " + current
                                                            + " total: " + total
                                                            + " justWrite: " + et.getText().toString());
                                                }
                                            });
                                            et.getText().clear(); //Vider le contenu

                                        }

                                        @Override
                                        public void onWriteFailure(final BleException exception) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    addText(txt, exception.toString());
                                                }
                                            });
                                        }
                                    });
                        }
                    });
                    btn1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String hex = "36303b39353b39393b38372c313030303b3130303b39363b36323b3b";

                            if (TextUtils.isEmpty(hex)) {
                                return;
                            }
                            BleManager.getInstance().write(
                                    bleDevice,
                                    HexUtil.hexStringToBytes(hex),
                                    new BleWriteCallback() {

                                        @Override
                                        public void onWriteSuccess(final int current, final int total, final byte[] justWrite) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    addText(txt, "write success, current: " + current
                                                            + " total: " + total
                                                            + " justWrite: " + HexUtil.formatHexString(justWrite, true));
                                                }
                                            });
                                        }

                                        @Override
                                        public void onWriteFailure(final BleException exception) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    addText(txt, exception.toString());
                                                }
                                            });
                                        }
                                    });
                        }
                    });

                    btn2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String hex = "36313b37342c302c363b37313b36323b3b";
                            if (TextUtils.isEmpty(hex)) {
                                return;
                            }
                            BleManager.getInstance().write(
                                    bleDevice,
                                    HexUtil.hexStringToBytes(hex),
                                    new BleWriteCallback() {

                                        @Override
                                        public void onWriteSuccess(final int current, final int total, final byte[] justWrite) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    addText(txt, "write success, current: " + current
                                                            + " total: " + total
                                                            + " justWrite: " + HexUtil.formatHexString(justWrite, true));
                                                }
                                            });
                                        }

                                        @Override
                                        public void onWriteFailure(final BleException exception) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    addText(txt, exception.toString());
                                                }
                                            });
                                        }
                                    });
                        }
                    });
                    btn3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String hex = "36313b36333b36323b3b";

                            if (TextUtils.isEmpty(hex)) {
                                return;
                            }
                            BleManager.getInstance().write(
                                    bleDevice,

                                    HexUtil.hexStringToBytes(hex),
                                    new BleWriteCallback() {

                                        @Override
                                        public void onWriteSuccess(final int current, final int total, final byte[] justWrite) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    addText(txt, "write success, current: " + current
                                                            + " total: " + total
                                                            + " justWrite: " + HexUtil.formatHexString(justWrite, true));
                                                }
                                            });
                                        }

                                        @Override
                                        public void onWriteFailure(final BleException exception) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    addText(txt, exception.toString());
                                                }
                                            });
                                        }
                                    });
                        }
                    });
                    btn4.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String hex = "36303b39393b3130333b3132303b3132313b3132333b3132353b3130343b3130303b36323b3b";

                            if (TextUtils.isEmpty(hex)) {
                                return;
                            }
                            BleManager.getInstance().write(
                                    bleDevice,

                                    HexUtil.hexStringToBytes(hex),
                                    new BleWriteCallback() {

                                        @Override
                                        public void onWriteSuccess(final int current, final int total, final byte[] justWrite) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    addText(txt, "écriture réussis, current: " + current
                                                            + " total: " + total
                                                            + " justWrite: " + HexUtil.formatHexString(justWrite, true));
                                                }
                                            });
                                        }

                                        @Override
                                        public void onWriteFailure(final BleException exception) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    addText(txt, exception.toString());
                                                }
                                            });
                                        }
                                    });
                        }
                    });
                    btn5.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String hex = "36313b37382c302c332c303b37353b36323b3b";

                            if (TextUtils.isEmpty(hex)) {
                                return;
                            }
                            BleManager.getInstance().write(
                                    bleDevice,

                                    HexUtil.hexStringToBytes(hex),
                                    new BleWriteCallback() {

                                        @Override
                                        public void onWriteSuccess(final int current, final int total, final byte[] justWrite) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    addText(txt, "écriture réussis, current: " + current
                                                            + " total: " + total
                                                            + " justWrite: " + HexUtil.formatHexString(justWrite, true));
                                                }
                                            });
                                        }

                                        @Override
                                        public void onWriteFailure(final BleException exception) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    addText(txt, exception.toString());
                                                }
                                            });
                                        }
                                    });
                        }
                    });
                    btn6.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String hex = "36313b37363b36323b3b";

                            if (TextUtils.isEmpty(hex)) {
                                return;
                            }
                            BleManager.getInstance().write(
                                    bleDevice,
                                    HexUtil.hexStringToBytes(hex),
                                    new BleWriteCallback() {

                                        @Override
                                        public void onWriteSuccess(final int current, final int total, final byte[] justWrite) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    addText(txt, "écriture réussis, current: " + current
                                                            + " total: " + total
                                                            + " justWrite: " + HexUtil.formatHexString(justWrite, true));
                                                }
                                            });
                                        }

                                        @Override
                                        public void onWriteFailure(final BleException exception) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    addText(txt, exception.toString());
                                                }
                                            });
                                        }
                                    });
                        }
                    });
                    layout_add.addView(view_add);
                }
                break;

                case PROPERTY_NOTIFY: {
                    View view_add = LayoutInflater.from(getActivity()).inflate(R.layout.layout_characteric_operation_button, null);
                    final Button btn = (Button) view_add.findViewById(R.id.btn);
                    btn.setText(getActivity().getString(R.string.open_notification));
//                            if (btn.getText().toString().equals(getActivity().getString(R.string.open_notification))) {
                                btn.setText(getActivity().getString(R.string.close_notification));
                          //      singleton.startNotification(bleDevice, characteristic);

//                            } else {
//                                btn.setText(getActivity().getString(R.string.open_notification));
                             //   BleManager.getInstance().stopNotify(
//                                        bleDevice,
//                                        characteristic.getService().getUuid().toString(),
//                                        characteristic.getUuid().toString());
//                            }

                    layout_add.addView(view_add);
                }
                break;

                case PROPERTY_INDICATE: {
                    View view_add = LayoutInflater.from(getActivity()).inflate(R.layout.layout_characteric_operation_button, null);
                    final Button btn = (Button) view_add.findViewById(R.id.btn);
                    btn.setText(getActivity().getString(R.string.open_notification));
                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (btn.getText().toString().equals(getActivity().getString(R.string.open_notification))) {
                                btn.setText(getActivity().getString(R.string.close_notification));
                                BleManager.getInstance().indicate(
                                        bleDevice,
                                        characteristic.getService().getUuid().toString(),
                                        characteristic.getUuid().toString(),
                                        new BleIndicateCallback() {

                                            @Override
                                            public void onIndicateSuccess() {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        addText(txt, "indicate success");
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onIndicateFailure(final BleException exception) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        addText(txt, exception.toString());
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onCharacteristicChanged(byte[] data) {
                                                runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        addText(txt, hexToString(HexUtil.formatHexString(data, false)));
                                                    }
                                                });
                                            }
                                        });
                            } else {
                                btn.setText(getActivity().getString(R.string.open_notification));
                                BleManager.getInstance().stopIndicate(
                                        bleDevice,
                                        characteristic.getService().getUuid().toString(),
                                        characteristic.getUuid().toString());
                            }
                        }
                    });
                    layout_add.addView(view_add);
                }
                break;
            }

            layout_container.addView(view);
        }
    }

    private void runOnUiThread(Runnable runnable) {
        if (isAdded() && getActivity() != null)
            getActivity().runOnUiThread(runnable);
    }

    private void addText(TextView textView, String content) {
        textView.append(content);
        textView.append("\n");
        int offset = textView.getLineCount() * textView.getLineHeight();
        if (offset > textView.getHeight()) {
            textView.scrollTo(0, offset - textView.getHeight());
        }
    }


}
