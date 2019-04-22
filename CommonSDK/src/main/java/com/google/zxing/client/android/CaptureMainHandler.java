/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.zxing.client.android;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;

import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;

import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.Timer;

/**
 * This class handles all the messaging which comprises the state machine for capture.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 */
public final class CaptureMainHandler extends Handler {
    public static final int msg_decode_succeeded = 0x10;
    public static final int msg_decode_failed = 0x11;
    public static final int msg_initial_camera = 0x20;
    private HandlerThread mDecodeHandlerThread;
    private DecodeHandler mDecodeHandler;
    private State state;
    private final SoftReference<CodeCaptor> mCodeCaptor;
    private final Map<DecodeHintType, ?> baseHints;

    private enum State {
        PREVIEW,
        SUCCESS,
        DONE
    }

    CaptureMainHandler(CodeCaptor mCodeCaptor, Map<DecodeHintType, ?> baseHints) {
        this.baseHints = baseHints;
        state = State.SUCCESS;
        this.mCodeCaptor = new SoftReference<>(mCodeCaptor);
        // Start ourselves capturing previews and decoding.

    }

    /**
     * 开启解码线程
     */
    private void startDecodeThread() {
        if (mDecodeHandler == null) {
            mDecodeHandlerThread = new HandlerThread("DecodeHandlerThread");
            mDecodeHandlerThread.start();
            mDecodeHandler = new DecodeHandler(mDecodeHandlerThread.getLooper(), mCodeCaptor.get());
            mDecodeHandler.setDecodeHintType(baseHints);
        }
    }

    /**
     * 关闭解码线程
     */
    private void stopDecodeThread() {
        if (mDecodeHandler != null) {
            mDecodeHandler.removeMessages(DecodeHandler.msg_decode);
            Message quit = Message.obtain(mDecodeHandler, DecodeHandler.msg_quit);
            quit.sendToTarget();
            try {
//            // Wait at most half a second; should be enough time, and pause() will timeout quickly
                mDecodeHandlerThread.join(500L);
            } catch (InterruptedException e) {
                // continue
            }
            mDecodeHandler = null;
            mDecodeHandlerThread = null;
        }
    }

    @Override
    public void handleMessage(Message message) {
        if (state == State.DONE)
            return;
        switch (message.what) {
            case msg_decode_succeeded:
                state = State.SUCCESS;
                //true 开始下次解析
                if (mCodeCaptor.get().handleDecode((Result) message.obj)) {
                    sendEmptyMessageDelayed(msg_decode_failed, mCodeCaptor.get().getAutoRestartTime());
                }
                break;
            case msg_decode_failed:
                // We're decoding as fast as possible, so when one decode fails, start another.
                if (mDecodeHandler != null) {
                    state = State.PREVIEW;
                    mCodeCaptor.get().getCameraManager().requestPreviewFrame(mDecodeHandler,
                            DecodeHandler.msg_decode);
                }
                break;
            case msg_initial_camera:
                if (mCodeCaptor.get().initCameraInternal()) {
                    resume();
                }
                break;
        }
    }

    void resume() {
        mCodeCaptor.get().getCameraManager().startPreview();
        startDecodeThread();
        sendEmptyMessage(msg_decode_failed);
    }

    void pause() {
        mCodeCaptor.get().getCameraManager().stopPreview();
        stopDecodeThread();
    }

    /**
     * 测地退出
     */
    void quitSynchronously() {
        state = State.DONE;
        mCodeCaptor.clear();
        // Be absolutely sure we don't send any queued up messages
        removeMessages(msg_decode_succeeded);
        removeMessages(msg_decode_failed);
        stopDecodeThread();

//        try {
//            // Wait at most half a second; should be enough time, and pause() will timeout quickly
//            mDecodeThread.join(500L);
//        } catch (InterruptedException e) {
//            // continue
//        }


    }

    protected void restartPreviewAndDecode() {
        if (state == State.SUCCESS && mDecodeHandler != null) {
            state = State.PREVIEW;
            mCodeCaptor.get().getCameraManager().requestPreviewFrame(mDecodeHandler,
                    DecodeHandler.msg_decode);
//            activity.drawViewfinder();
        }
    }

}
