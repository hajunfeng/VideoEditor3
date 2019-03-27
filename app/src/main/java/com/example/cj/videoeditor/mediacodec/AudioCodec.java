package com.example.cj.videoeditor.mediacodec;

import android.media.MediaCodec;
import android.media.MediaCodecInfo;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.media.MediaMuxer;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.example.cj.videoeditor.Constants;
import com.example.cj.videoeditor.jni.AudioJniUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * 音频相关的操作类
 */

public class AudioCodec {
    final static int TIMEOUT_USEC = 0;
    private static Handler handler = new Handler(Looper.getMainLooper());
    /**
     * 从视频文件中分离出音频，并保存到本地
     * */
    public static void getAudioFromVideo(String videoPath, final String audioSavePath, final AudioDecodeListener listener){
        final MediaExtractor extractor = new MediaExtractor();
        int audioTrack = -1;
        boolean hasAudio = false;
        try {
            extractor.setDataSource(videoPath);
            for (int i = 0; i < extractor.getTrackCount(); i++) {
                MediaFormat trackFormat = extractor.getTrackFormat(i);
                String mime = trackFormat.getString(MediaFormat.KEY_MIME);
                if (mime.startsWith("audio/")) {
                    audioTrack = i;
                    hasAudio = true;
                    break;
                }
            }
            if (hasAudio) {
                extractor.selectTrack(audioTrack);
                final int finalAudioTrack = audioTrack;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try{
                            MediaMuxer mediaMuxer = new MediaMuxer(audioSavePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4);
                            MediaFormat trackFormat = extractor.getTrackFormat(finalAudioTrack);
                            int writeAudioIndex = mediaMuxer.addTrack(trackFormat);
                            mediaMuxer.start();
                            ByteBuffer byteBuffer = ByteBuffer.allocate(trackFormat.getInteger(MediaFormat.KEY_MAX_INPUT_SIZE));
                            MediaCodec.BufferInfo bufferInfo = new MediaCodec.BufferInfo();

                            extractor.readSampleData(byteBuffer, 0);
                            if (extractor.getSampleFlags() == MediaExtractor.SAMPLE_FLAG_SYNC) {
                                extractor.advance();
                            }
                            while (true) {
                                int readSampleSize = extractor.readSampleData(byteBuffer, 0);
                                Log.e("hero","---读取音频数据，当前读取到的大小-----：：："+readSampleSize);
                                if (readSampleSize < 0) {
                                    break;
                                }

                                bufferInfo.size = readSampleSize;
                                bufferInfo.flags = extractor.getSampleFlags();
                                bufferInfo.offset = 0;
                                bufferInfo.presentationTimeUs = extractor.getSampleTime();
                                Log.e("hero","----写入音频数据---当前的时间戳：：："+extractor.getSampleTime());

                                mediaMuxer.writeSampleData(writeAudioIndex, byteBuffer, bufferInfo);
                                extractor.advance();//移动到下一帧
                            }
                            mediaMuxer.release();
                            extractor.release();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (listener != null){
                                        listener.decodeOver();
                                    }
                                }
                            });
                        }catch (Exception e){
                            e.printStackTrace();
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (listener != null){
                                        listener.decodeFail();
                                    }
                                }
                            });
                        }
                    }
                }).start();
            }else {
                Log.e("hero", " extractor failed !!!! 没有音频信道");
                if (listener != null){
                    listener.decodeFail();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.e("hero", " extractor failed !!!!");
            if (listener != null){
                listener.decodeFail();
            }
        }
    }

    /**
     * 写入ADTS头部数据
     * */
    public static void addADTStoPacket(byte[] packet, int packetLen) {
        int profile = 2; // AAC LC
        int freqIdx = 4; // 44.1KHz
        int chanCfg = 2; // CPE

        packet[0] = (byte) 0xFF;
        packet[1] = (byte) 0xF9;
        packet[2] = (byte) (((profile - 1) << 6) + (freqIdx << 2) + (chanCfg >> 2));
        packet[3] = (byte) (((chanCfg & 3) << 6) + (packetLen >> 11));
        packet[4] = (byte) ((packetLen & 0x7FF) >> 3);
        packet[5] = (byte) (((packetLen & 7) << 5) + 0x1F);
        packet[6] = (byte) 0xFC;
    }


    interface DecodeOverListener {
        void decodeIsOver();

        void decodeFail();
    }

    public interface AudioDecodeListener{
        void decodeOver();
        void decodeFail();
    }
}
