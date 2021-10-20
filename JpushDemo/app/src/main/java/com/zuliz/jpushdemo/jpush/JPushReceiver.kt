package com.zuliz.jpushdemo.jpush

import android.content.Context
import cn.jpush.android.service.JPushMessageReceiver
import cn.jpush.android.api.JPushMessage
import cn.jpush.android.api.CmdMessage
import android.content.Intent
import cn.jpush.android.api.NotificationMessage
import cn.jpush.android.api.JPushInterface
import android.os.Bundle
import android.util.Log
import cn.jpush.android.api.CustomMessage
import com.zuliz.jpushdemo.MainActivity

class JPushReceiver : JPushMessageReceiver() {
    companion object {
        private const val TAG = "JPushReceiver"
    }

    override fun onMessage(context: Context, customMessage: CustomMessage) {
        Log.e(TAG, "[onMessage] $customMessage")
        /*val intent = Intent("com.jiguang.demo.message")
        intent.putExtra("msg", customMessage.message)
        context.sendBroadcast(intent)*/
    }

    override fun onNotifyMessageOpened(context: Context, message: NotificationMessage) {
        Log.e(TAG, "[onNotifyMessageOpened] $message")
        /*try {
            //打开自定义的Activity
            val i = Intent(context, TestActivity::class.java)
            val bundle = Bundle()
            bundle.putString(JPushInterface.EXTRA_NOTIFICATION_TITLE, message.notificationTitle)
            bundle.putString(JPushInterface.EXTRA_ALERT, message.notificationContent)
            i.putExtras(bundle)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            context.startActivity(i)
        } catch (throwable: Throwable) {
        }*/
    }

    override fun onMultiActionClicked(context: Context?, intent: Intent) {
        Log.e(TAG, "[onMultiActionClicked] 用户点击了通知栏按钮")
        val nActionExtra = intent.extras!!.getString(JPushInterface.EXTRA_NOTIFICATION_ACTION_EXTRA)

        //开发者根据不同 Action 携带的 extra 字段来分配不同的动作。
        /*if (nActionExtra == null) {
            Log.d(TAG, "ACTION_NOTIFICATION_CLICK_ACTION nActionExtra is null")
            return
        }
        if (nActionExtra == "my_extra1") {
            Log.e(TAG, "[onMultiActionClicked] 用户点击通知栏按钮一")
        } else if (nActionExtra == "my_extra2") {
            Log.e(TAG, "[onMultiActionClicked] 用户点击通知栏按钮二")
        } else if (nActionExtra == "my_extra3") {
            Log.e(TAG, "[onMultiActionClicked] 用户点击通知栏按钮三")
        } else {
            Log.e(TAG, "[onMultiActionClicked] 用户点击通知栏按钮未定义")
        }*/
    }

    override fun onNotifyMessageArrived(context: Context?, message: NotificationMessage) {
        Log.e(TAG, "[onNotifyMessageArrived] $message")
    }

    override fun onNotifyMessageDismiss(context: Context?, message: NotificationMessage) {
        Log.e(TAG, "[onNotifyMessageDismiss] $message")
    }

    override fun onRegister(context: Context, registrationId: String) {
        Log.e(TAG, "[onRegister] $registrationId")
        /*val intent = Intent("com.jiguang.demo.register")
        context.sendBroadcast(intent)*/
    }

    override fun onConnected(context: Context?, isConnected: Boolean) {
        Log.e(TAG, "[onConnected] $isConnected")
        if (isConnected) {
            Log.i(
                TAG,
                "JPushInterface.getRegistrationID(context)=${JPushInterface.getRegistrationID(context)}"
            )
        }
    }

    override fun onCommandResult(context: Context?, cmdMessage: CmdMessage) {
        Log.e(TAG, "[onCommandResult] $cmdMessage")
    }

    override fun onTagOperatorResult(context: Context?, jPushMessage: JPushMessage?) {
        Log.e(TAG, "[onTagOperatorResult] $jPushMessage")
        /*TagAliasOperatorHelper.getInstance().onTagOperatorResult(context, jPushMessage)*/
        super.onTagOperatorResult(context, jPushMessage)
    }

    override fun onCheckTagOperatorResult(context: Context?, jPushMessage: JPushMessage?) {
        Log.e(TAG, "[onCheckTagOperatorResult] $jPushMessage")
        /*TagAliasOperatorHelper.getInstance().onCheckTagOperatorResult(context, jPushMessage)*/
        super.onCheckTagOperatorResult(context, jPushMessage)
    }

    override fun onAliasOperatorResult(context: Context?, jPushMessage: JPushMessage?) {
        Log.e(TAG, "[onAliasOperatorResult] $jPushMessage")
        /*TagAliasOperatorHelper.getInstance().onAliasOperatorResult(context, jPushMessage)*/
        super.onAliasOperatorResult(context, jPushMessage)
    }

    override fun onMobileNumberOperatorResult(context: Context?, jPushMessage: JPushMessage?) {
        Log.e(TAG, "[onMobileNumberOperatorResult] $jPushMessage")
        /*TagAliasOperatorHelper.getInstance().onMobileNumberOperatorResult(context, jPushMessage)*/
        super.onMobileNumberOperatorResult(context, jPushMessage)
    }

    override fun onNotificationSettingsCheck(context: Context?, isOn: Boolean, source: Int) {
        super.onNotificationSettingsCheck(context, isOn, source)
        Log.e(TAG, "[onNotificationSettingsCheck] isOn:$isOn,source:$source")
    }

}