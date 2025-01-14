package co.aospa.sense.camera

import android.graphics.SurfaceTexture
import android.hardware.Camera
import android.os.Handler
import android.os.Message
import android.view.SurfaceHolder
import co.aospa.sense.camera.callables.AddCallbackBufferCallable
import co.aospa.sense.camera.callables.CameraCallable
import co.aospa.sense.camera.callables.CloseCameraCallable
import co.aospa.sense.camera.callables.OpenCameraCallable
import co.aospa.sense.camera.callables.ReadParamsCallable
import co.aospa.sense.camera.callables.SetDisplayOrientationCallback
import co.aospa.sense.camera.callables.SetFaceDetectionCallback
import co.aospa.sense.camera.callables.SetPreviewCallbackCallable
import co.aospa.sense.camera.callables.StartPreviewCallable
import co.aospa.sense.camera.callables.StopPreviewCallable
import co.aospa.sense.camera.callables.WriteParamsCallable
import co.aospa.sense.camera.listeners.CameraEventListener
import co.aospa.sense.camera.listeners.CameraListener

class CameraService private constructor() {

    private val serviceHandler = Handler(
        CameraHandlerThread().apply { start() }.looper
    ) { message: Message ->
        (message.obj as CameraCallable).run()
        true
    }

    private fun addCallable(cameraCallable: CameraCallable) {
        serviceHandler.sendMessage(
            serviceHandler.obtainMessage(DEFAULT_MSG_TYPE, cameraCallable)
        )
    }

    companion object {
        private const val DEFAULT_MSG_TYPE = 1
        private val instance: CameraService by lazy { CameraService() }

        fun openCamera(id: Int, errorListener: CameraEventListener?, listener: CameraListener?) {
            instance.addCallable(OpenCameraCallable(id, errorListener, listener))
        }

        fun closeCamera(listener: CameraListener?) {
            clearQueue()
            instance.addCallable(CloseCameraCallable(listener))
        }

        fun readParameters(eventListener: CameraEventListener?, listener: CameraListener?) {
            instance.addCallable(ReadParamsCallable(eventListener, listener))
        }

        fun writeParameters(listener: CameraListener?) {
            instance.addCallable(WriteParamsCallable(listener))
        }

        fun startPreview(surfaceTexture: SurfaceTexture?, listener: CameraListener?) {
            instance.addCallable(StartPreviewCallable(surfaceTexture, listener))
        }

        fun startPreview(surfaceHolder: SurfaceHolder?, listener: CameraListener?) {
            instance.addCallable(StartPreviewCallable(surfaceHolder, listener))
        }

        fun stopPreview(listener: CameraListener?) {
            instance.addCallable(StopPreviewCallable(listener))
        }

        fun addCallbackBuffer(data: ByteArray?, listener: CameraListener?) {
            instance.addCallable(AddCallbackBufferCallable(data, listener))
        }

        fun setPreviewCallback(
            eventListener: CameraEventListener?,
            withBuffer: Boolean,
            listener: CameraListener?
        ) {
            instance.addCallable(
                SetPreviewCallbackCallable(eventListener, withBuffer, listener)
            )
        }

        fun setFaceDetectionCallback(
            faceDetectionListener: Camera.FaceDetectionListener?,
            listener: CameraListener?
        ) {
            instance.addCallable(
                SetFaceDetectionCallback(faceDetectionListener, listener)
            )
        }

        fun setDisplayOrientationCallback(angle: Int, listener: CameraListener?) {
            instance.addCallable(SetDisplayOrientationCallback(angle, listener))
        }

        fun clearQueue() {
            instance.serviceHandler.removeMessages(DEFAULT_MSG_TYPE)
        }
    }
}