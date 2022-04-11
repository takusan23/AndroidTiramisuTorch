package io.github.takusan23.androidtiramisutorch

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi

class MainActivity : AppCompatActivity() {

    private val cameraManager by lazy { getSystemService(Context.CAMERA_SERVICE) as CameraManager }

    /** シークバー */
    private val lightLevelSeekBar by lazy { findViewById<SeekBar>(R.id.activity_main_seekbar) }

    /** 明るさレベル表示用TextView */
    private val lightLevelTextView by lazy { findViewById<TextView>(R.id.activity_main_textview) }

    // 警告が出るので黙らせる
    @RequiresApi(33)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val list = cameraManager.cameraIdList
        // 多分アウトカメラ
        val outCameraId = list[0]
        // アウトカメラ情報
        val cameraCharacteristics = cameraManager.getCameraCharacteristics(outCameraId)
        // 懐中電灯最大の明るさレベル
        val torchMaxLevel = cameraCharacteristics[CameraCharacteristics.FLASH_INFO_STRENGTH_MAXIMUM_LEVEL] ?: -1
        // 未対応
        if (torchMaxLevel < 0) {
            Toast.makeText(this, "明るさ調整に未対応です", Toast.LENGTH_SHORT).show()
        } else {
            // 最大値
            lightLevelSeekBar.max = torchMaxLevel
            lightLevelSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    // シークバー操作時に呼ばれる
                    cameraManager.turnOnTorchWithStrengthLevel(outCameraId, p1)
                    lightLevelTextView.text = "明るさレベル ${p1}"
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {

                }

                override fun onStopTrackingTouch(p0: SeekBar?) {

                }
            })
        }
    }

    /** アプリ終了時に消す */
    override fun onDestroy() {
        super.onDestroy()
        val list = cameraManager.cameraIdList
        val outCameraId = list[0]
        cameraManager.setTorchMode(outCameraId, false)
    }
}