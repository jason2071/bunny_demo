package com.example.bunny.api;

import android.content.Context;
import android.os.RemoteException;
import android.os.SystemClock;

import com.example.bunny.ArkeSdkDemoApplication;
import com.example.bunny.R;
import com.example.bunny.manager.Contextor;
import com.example.bunny.util.BytesUtil;
import com.example.bunny.util.LogUtil;
import com.google.gson.Gson;
import com.usdk.apiservice.aidl.serialport.SerialPortError;
import com.usdk.apiservice.aidl.serialport.USerialPort;

import java.util.Hashtable;
import java.util.Map;

/**
 * SerialPort API.
 */

public class SerialPort {

    /**
     * Serial port object.
     */
    private USerialPort serialPort;

    /**
     * Context.
     */
    private Context context = Contextor.getInstance().getContext();

    /**
     * Open.
     */
    public boolean open(String deviceName) throws RemoteException {
        serialPort = ArkeSdkDemoApplication.getDeviceService().getSerialPort(deviceName);
        int ret = serialPort.open();
        if (ret != SerialPortError.SUCCESS) {
            throw new RemoteException(context.getString(getErrorId(ret)));
        }
        return true;
    }

    /**
     * Init.
     */
    public boolean init(int baudRate, int parityBit, int dataBit) throws RemoteException {
        int ret = serialPort.init(baudRate, parityBit, dataBit);
        if (ret != SerialPortError.SUCCESS) {
            throw new RemoteException(context.getString(getErrorId(ret)));
        }
        return true;
    }

    /**
     * Write.
     */
    public int write(byte[] data, int timeout) throws RemoteException {
        int ret = serialPort.write(data, timeout);
        if (ret == -1) {
            throw new RemoteException(context.getString(R.string.write_fail));
        } else {
            return ret;
        }
    }

    /**
     * Read.
     */
    public byte[] read(byte[] data, int timeout) throws RemoteException {
        int ret = serialPort.read(data, timeout);
        if (ret == -1) {
            throw new RemoteException(context.getString(R.string.read_fail));
        } else {
            return data;
        }
    }

    public byte[] read2(byte[] buffer, int timeout) throws RemoteException {
        int readLength = 0;
        int i = 0;
        do {

            i++;

            LogUtil.INSTANCE.log("i: " + i);

            SystemClock.sleep(1000);

            readLength = serialPort.read(buffer, timeout);
            if (readLength == 0) {
                continue;
            }

            if (readLength < 0) {
                throw new RemoteException("read data fail!");
            }
            break;
        } while (true);

        return buffer;
    }

    /**
     * Is buffer empty.
     */
    public boolean isBufferEmpty(boolean input) throws RemoteException {
        return serialPort.isBufferEmpty(input);
    }

    /**
     * Clear input buffer.
     */
    public void clearInputBuffer() throws RemoteException {
        int ret = serialPort.clearInputBuffer();
        if (ret != SerialPortError.SUCCESS) {
            throw new RemoteException(context.getString(getErrorId(ret)));
        }
    }

    /**
     * Close.
     */
    public boolean close() throws RemoteException {
        int ret = serialPort.close();
        if (ret != SerialPortError.SUCCESS) {
            throw new RemoteException(context.getString(getErrorId(ret)));
        }
        return true;
    }

    /**
     * Creator.
     */
    private static class Creator {
        private static final SerialPort INSTANCE = new SerialPort();
    }

    /**
     * Get serial port instance.
     */
    public static SerialPort getInstance() {
        return Creator.INSTANCE;
    }

    /**
     * Constructor.
     */
    private SerialPort() {

    }

    /**
     * Error code.
     */
    private static Map<Integer, Integer> errorCodes;

    static {
        errorCodes = new Hashtable<>();
        errorCodes.put(SerialPortError.SUCCESS, R.string.succeed);
        errorCodes.put(SerialPortError.SERVICE_CRASH, R.string.service_crash);
        errorCodes.put(SerialPortError.REQUEST_EXCEPTION, R.string.request_exception);
        errorCodes.put(SerialPortError.ERROR_DEVICE_DISABLE, R.string.device_disable);
//        errorCodes.put(SerialPortError.DEVICE_USED, R.string.device_used);
        errorCodes.put(SerialPortError.ERROR_OTHERERR, R.string.other_error);
        errorCodes.put(SerialPortError.ERROR_PARAMERR, R.string.param_error);
        errorCodes.put(SerialPortError.ERROR_TIMEOUT, R.string.timeout);
    }

    /**
     * Get error id.
     */
    private static int getErrorId(int errorCode) {
        if (errorCodes.containsKey(errorCode)) {
            return errorCodes.get(errorCode);
        }

        return R.string.other_error;
    }

    public String getState() throws RemoteException {
        int state = serialPort.getBaseConnState();
        switch (state) {
            case SerialPortError.ERROR_BTSERVICE_DISCONN:
                return "Bluetooth service disconnect";
            case SerialPortError.ERROR_BT_DISENABLE:
                return "Bluetooth disable";
            case SerialPortError.ERROR_BTBASE_UNPAIRED:
                return "Bluetooth base unpaired";
            case SerialPortError.ERROR_BTBASE_DISCONN:
                return "Bluetooth base disconnect";
            case SerialPortError.SUCCESS:
                return "Bluetooth base connect success";
            default:
                return "Unknown state";
        }
    }
}
