/*
 *  Copyright 2013 The WebRTC project authors. All Rights Reserved.
 *
 *  Use of this source code is governed by a BSD-style license
 *  that can be found in the LICENSE file in the root of the source
 *  tree. An additional intellectual property rights grant can be found
 *  in the file PATENTS.  All contributing project authors may
 *  be found in the AUTHORS file in the root of the source tree.
 */

package org.webrtc;

import android.content.Context;
import android.os.Process;
import androidx.annotation.Nullable;
import java.util.List;
import org.webrtc.PeerConnection.SdpSemantics;
import org.webrtc.audio.AudioDeviceModule;
import org.webrtc.audio.JavaAudioDeviceModule;

public class PeerConnectionFactory {
  public static final String TRIAL_ENABLED = "Enabled";
  /** @deprecated */
  @Deprecated
  public static final String VIDEO_FRAME_EMIT_TRIAL = "VideoFrameEmit";
  private static final String TAG = "PeerConnectionFactory";
  private static final String VIDEO_CAPTURER_THREAD_NAME = "VideoCapturerThread";
  private static volatile boolean internalTracerInitialized;
  @Nullable
  private static ThreadInfo staticNetworkThread;
  @Nullable
  private static ThreadInfo staticWorkerThread;
  @Nullable
  private static ThreadInfo staticSignalingThread;
  private long nativeFactory;
  @Nullable
  private volatile ThreadInfo networkThread;
  @Nullable
  private volatile ThreadInfo workerThread;
  @Nullable
  private volatile ThreadInfo signalingThread;

  public static Builder builder() {
    return new Builder();
  }

  public static void initialize(InitializationOptions options) {
    ContextUtils.initialize(options.applicationContext);
    NativeLibrary.initialize(options.nativeLibraryLoader, options.nativeLibraryName);
    nativeInitializeAndroidGlobals();
    nativeInitializeFieldTrials(options.fieldTrials);
    if (options.enableInternalTracer && !internalTracerInitialized) {
      initializeInternalTracer();
    }

    if (options.loggable != null) {
      Logging.injectLoggable(options.loggable, options.loggableSeverity);
      nativeInjectLoggable(new JNILogging(options.loggable), options.loggableSeverity.ordinal());
    } else {
      Logging.d("PeerConnectionFactory", "PeerConnectionFactory was initialized without an injected Loggable. Any existing Loggable will be deleted.");
      Logging.deleteInjectedLoggable();
      nativeDeleteLoggable();
    }

  }

  private static void checkInitializeHasBeenCalled() {
    if (!NativeLibrary.isLoaded() || ContextUtils.getApplicationContext() == null) {
      throw new IllegalStateException("PeerConnectionFactory.initialize was not called before creating a PeerConnectionFactory.");
    }
  }

  private static void initializeInternalTracer() {
    internalTracerInitialized = true;
    nativeInitializeInternalTracer();
  }

  public static void shutdownInternalTracer() {
    internalTracerInitialized = false;
    nativeShutdownInternalTracer();
  }

  /** @deprecated */
  @Deprecated
  public static void initializeFieldTrials(String fieldTrialsInitString) {
    nativeInitializeFieldTrials(fieldTrialsInitString);
  }

  public static String fieldTrialsFindFullName(String name) {
    return NativeLibrary.isLoaded() ? nativeFindFieldTrialsFullName(name) : "";
  }

  public static boolean startInternalTracingCapture(String tracingFilename) {
    return nativeStartInternalTracingCapture(tracingFilename);
  }

  public static void stopInternalTracingCapture() {
    nativeStopInternalTracingCapture();
  }

  @CalledByNative
  PeerConnectionFactory(long nativeFactory) {
    checkInitializeHasBeenCalled();
    if (nativeFactory == 0L) {
      throw new RuntimeException("Failed to initialize PeerConnectionFactory!");
    } else {
      this.nativeFactory = nativeFactory;
    }
  }

  @Nullable
  PeerConnection createPeerConnectionInternal(PeerConnection.RTCConfiguration rtcConfig, MediaConstraints constraints, PeerConnection.Observer observer, SSLCertificateVerifier sslCertificateVerifier) {
    this.checkPeerConnectionFactoryExists();
    long nativeObserver = PeerConnection.createNativePeerConnectionObserver(observer);
    if (nativeObserver == 0L) {
      return null;
    } else {
      long nativePeerConnection = nativeCreatePeerConnection(this.nativeFactory, rtcConfig, constraints, nativeObserver, sslCertificateVerifier);
      return nativePeerConnection == 0L ? null : new PeerConnection(nativePeerConnection);
    }
  }

  /** @deprecated */
  @Deprecated
  @Nullable
  public PeerConnection createPeerConnection(PeerConnection.RTCConfiguration rtcConfig, MediaConstraints constraints, PeerConnection.Observer observer) {
    return this.createPeerConnectionInternal(rtcConfig, constraints, observer, (SSLCertificateVerifier)null);
  }

  /** @deprecated */
  @Deprecated
  @Nullable
  public PeerConnection createPeerConnection(List<PeerConnection.IceServer> iceServers, MediaConstraints constraints, PeerConnection.Observer observer) {
    PeerConnection.RTCConfiguration rtcConfig = new PeerConnection.RTCConfiguration(iceServers);
    rtcConfig.sdpSemantics = SdpSemantics.UNIFIED_PLAN;
    return this.createPeerConnection(rtcConfig, constraints, observer);
  }

  @Nullable
  public PeerConnection createPeerConnection(List<PeerConnection.IceServer> iceServers, PeerConnection.Observer observer) {
    PeerConnection.RTCConfiguration rtcConfig = new PeerConnection.RTCConfiguration(iceServers);
    rtcConfig.sdpSemantics = SdpSemantics.UNIFIED_PLAN;
    return this.createPeerConnection(rtcConfig, observer);
  }

  @Nullable
  public PeerConnection createPeerConnection(PeerConnection.RTCConfiguration rtcConfig, PeerConnection.Observer observer) {
    return this.createPeerConnection((PeerConnection.RTCConfiguration)rtcConfig, (MediaConstraints)null, observer);
  }

  @Nullable
  public PeerConnection createPeerConnection(PeerConnection.RTCConfiguration rtcConfig, PeerConnectionDependencies dependencies) {
    return this.createPeerConnectionInternal(rtcConfig, (MediaConstraints)null, dependencies.getObserver(), dependencies.getSSLCertificateVerifier());
  }

  public MediaStream createLocalMediaStream(String label) {
    this.checkPeerConnectionFactoryExists();
    return new MediaStream(nativeCreateLocalMediaStream(this.nativeFactory, label));
  }

  public VideoSource createVideoSource(boolean isScreencast, boolean alignTimestamps) {
    this.checkPeerConnectionFactoryExists();
    return new VideoSource(nativeCreateVideoSource(this.nativeFactory, isScreencast, alignTimestamps));
  }

  public VideoSource createVideoSource(boolean isScreencast) {
    return this.createVideoSource(isScreencast, true);
  }

  public VideoTrack createVideoTrack(String id, VideoSource source) {
    this.checkPeerConnectionFactoryExists();
    return new VideoTrack(nativeCreateVideoTrack(this.nativeFactory, id, source.getNativeVideoTrackSource()));
  }

  public AudioSource createAudioSource(MediaConstraints constraints) {
    this.checkPeerConnectionFactoryExists();
    return new AudioSource(nativeCreateAudioSource(this.nativeFactory, constraints));
  }

  public AudioTrack createAudioTrack(String id, AudioSource source) {
    this.checkPeerConnectionFactoryExists();
    return new AudioTrack(nativeCreateAudioTrack(this.nativeFactory, id, source.getNativeAudioSource()));
  }

  public boolean startAecDump(int file_descriptor, int filesize_limit_bytes) {
    this.checkPeerConnectionFactoryExists();
    return nativeStartAecDump(this.nativeFactory, file_descriptor, filesize_limit_bytes);
  }

  public void stopAecDump() {
    this.checkPeerConnectionFactoryExists();
    nativeStopAecDump(this.nativeFactory);
  }

  public void dispose() {
    this.checkPeerConnectionFactoryExists();
    nativeFreeFactory(this.nativeFactory);
    this.networkThread = null;
    this.workerThread = null;
    this.signalingThread = null;
    this.nativeFactory = 0L;
  }

  public long getNativePeerConnectionFactory() {
    this.checkPeerConnectionFactoryExists();
    return nativeGetNativePeerConnectionFactory(this.nativeFactory);
  }

  public long getNativeOwnedFactoryAndThreads() {
    this.checkPeerConnectionFactoryExists();
    return this.nativeFactory;
  }

  private void checkPeerConnectionFactoryExists() {
    if (this.nativeFactory == 0L) {
      throw new IllegalStateException("PeerConnectionFactory has been disposed.");
    }
  }

  private static void printStackTrace(@Nullable ThreadInfo threadInfo, boolean printNativeStackTrace) {
    if (threadInfo != null) {
      String threadName = threadInfo.thread.getName();
      StackTraceElement[] stackTraces = threadInfo.thread.getStackTrace();
      if (stackTraces.length > 0) {
        Logging.w("PeerConnectionFactory", threadName + " stacktrace:");
        StackTraceElement[] var4 = stackTraces;
        int var5 = stackTraces.length;

        for(int var6 = 0; var6 < var5; ++var6) {
          StackTraceElement stackTrace = var4[var6];
          Logging.w("PeerConnectionFactory", stackTrace.toString());
        }
      }

      if (printNativeStackTrace) {
        Logging.w("PeerConnectionFactory", "*** *** *** *** *** *** *** *** *** *** *** *** *** *** *** ***");
        Logging.w("PeerConnectionFactory", "pid: " + Process.myPid() + ", tid: " + threadInfo.tid + ", name: " + threadName + "  >>> WebRTC <<<");
        nativePrintStackTrace(threadInfo.tid);
      }

    }
  }

  /** @deprecated */
  @Deprecated
  public static void printStackTraces() {
    printStackTrace(staticNetworkThread, false);
    printStackTrace(staticWorkerThread, false);
    printStackTrace(staticSignalingThread, false);
  }

  public void printInternalStackTraces(boolean printNativeStackTraces) {
    printStackTrace(this.signalingThread, printNativeStackTraces);
    printStackTrace(this.workerThread, printNativeStackTraces);
    printStackTrace(this.networkThread, printNativeStackTraces);
  }

  @CalledByNative
  private void onNetworkThreadReady() {
    this.networkThread = PeerConnectionFactory.ThreadInfo.getCurrent();
    staticNetworkThread = this.networkThread;
    Logging.d("PeerConnectionFactory", "onNetworkThreadReady");
  }

  @CalledByNative
  private void onWorkerThreadReady() {
    this.workerThread = PeerConnectionFactory.ThreadInfo.getCurrent();
    staticWorkerThread = this.workerThread;
    Logging.d("PeerConnectionFactory", "onWorkerThreadReady");
  }

  @CalledByNative
  private void onSignalingThreadReady() {
    this.signalingThread = PeerConnectionFactory.ThreadInfo.getCurrent();
    staticSignalingThread = this.signalingThread;
    Logging.d("PeerConnectionFactory", "onSignalingThreadReady");
  }

  private static native void nativeInitializeAndroidGlobals();

  private static native void nativeInitializeFieldTrials(String var0);

  private static native String nativeFindFieldTrialsFullName(String var0);

  private static native void nativeInitializeInternalTracer();

  private static native void nativeShutdownInternalTracer();

  private static native boolean nativeStartInternalTracingCapture(String var0);

  private static native void nativeStopInternalTracingCapture();

  private static native PeerConnectionFactory nativeCreatePeerConnectionFactory(Context var0, Options var1, long var2, long var4, long var6, VideoEncoderFactory var8, VideoDecoderFactory var9, long var10, long var12, long var14, long var16, long var18);

  private static native long nativeCreatePeerConnection(long var0, PeerConnection.RTCConfiguration var2, MediaConstraints var3, long var4, SSLCertificateVerifier var6);

  private static native long nativeCreateLocalMediaStream(long var0, String var2);

  private static native long nativeCreateVideoSource(long var0, boolean var2, boolean var3);

  private static native long nativeCreateVideoTrack(long var0, String var2, long var3);

  private static native long nativeCreateAudioSource(long var0, MediaConstraints var2);

  private static native long nativeCreateAudioTrack(long var0, String var2, long var3);

  private static native boolean nativeStartAecDump(long var0, int var2, int var3);

  private static native void nativeStopAecDump(long var0);

  private static native void nativeFreeFactory(long var0);

  private static native long nativeGetNativePeerConnectionFactory(long var0);

  private static native void nativeInjectLoggable(JNILogging var0, int var1);

  private static native void nativeDeleteLoggable();

  private static native void nativePrintStackTrace(int var0);

  public static class Builder {
    @Nullable
    private Options options;
    @Nullable
    private AudioDeviceModule audioDeviceModule;
    private AudioEncoderFactoryFactory audioEncoderFactoryFactory;
    private AudioDecoderFactoryFactory audioDecoderFactoryFactory;
    @Nullable
    private VideoEncoderFactory videoEncoderFactory;
    @Nullable
    private VideoDecoderFactory videoDecoderFactory;
    @Nullable
    private AudioProcessingFactory audioProcessingFactory;
    @Nullable
    private FecControllerFactoryFactoryInterface fecControllerFactoryFactory;
    @Nullable
    private NetworkControllerFactoryFactory networkControllerFactoryFactory;
    @Nullable
    private NetworkStatePredictorFactoryFactory networkStatePredictorFactoryFactory;
    @Nullable
    private NetEqFactoryFactory neteqFactoryFactory;

    private Builder() {
      this.audioEncoderFactoryFactory = new BuiltinAudioEncoderFactoryFactory();
      this.audioDecoderFactoryFactory = new BuiltinAudioDecoderFactoryFactory();
    }

    public Builder setOptions(Options options) {
      this.options = options;
      return this;
    }

    public Builder setAudioDeviceModule(AudioDeviceModule audioDeviceModule) {
      this.audioDeviceModule = audioDeviceModule;
      return this;
    }

    public Builder setAudioEncoderFactoryFactory(AudioEncoderFactoryFactory audioEncoderFactoryFactory) {
      if (audioEncoderFactoryFactory == null) {
        throw new IllegalArgumentException("PeerConnectionFactory.Builder does not accept a null AudioEncoderFactoryFactory.");
      } else {
        this.audioEncoderFactoryFactory = audioEncoderFactoryFactory;
        return this;
      }
    }

    public Builder setAudioDecoderFactoryFactory(AudioDecoderFactoryFactory audioDecoderFactoryFactory) {
      if (audioDecoderFactoryFactory == null) {
        throw new IllegalArgumentException("PeerConnectionFactory.Builder does not accept a null AudioDecoderFactoryFactory.");
      } else {
        this.audioDecoderFactoryFactory = audioDecoderFactoryFactory;
        return this;
      }
    }

    public Builder setVideoEncoderFactory(VideoEncoderFactory videoEncoderFactory) {
      this.videoEncoderFactory = videoEncoderFactory;
      return this;
    }

    public Builder setVideoDecoderFactory(VideoDecoderFactory videoDecoderFactory) {
      this.videoDecoderFactory = videoDecoderFactory;
      return this;
    }

    public Builder setAudioProcessingFactory(AudioProcessingFactory audioProcessingFactory) {
      if (audioProcessingFactory == null) {
        throw new NullPointerException("PeerConnectionFactory builder does not accept a null AudioProcessingFactory.");
      } else {
        this.audioProcessingFactory = audioProcessingFactory;
        return this;
      }
    }

    public Builder setFecControllerFactoryFactoryInterface(FecControllerFactoryFactoryInterface fecControllerFactoryFactory) {
      this.fecControllerFactoryFactory = fecControllerFactoryFactory;
      return this;
    }

    public Builder setNetworkControllerFactoryFactory(NetworkControllerFactoryFactory networkControllerFactoryFactory) {
      this.networkControllerFactoryFactory = networkControllerFactoryFactory;
      return this;
    }

    public Builder setNetworkStatePredictorFactoryFactory(NetworkStatePredictorFactoryFactory networkStatePredictorFactoryFactory) {
      this.networkStatePredictorFactoryFactory = networkStatePredictorFactoryFactory;
      return this;
    }

    public Builder setNetEqFactoryFactory(NetEqFactoryFactory neteqFactoryFactory) {
      this.neteqFactoryFactory = neteqFactoryFactory;
      return this;
    }

    public PeerConnectionFactory createPeerConnectionFactory() {
      PeerConnectionFactory.checkInitializeHasBeenCalled();
      if (this.audioDeviceModule == null) {
        this.audioDeviceModule = JavaAudioDeviceModule.builder(ContextUtils.getApplicationContext()).createAudioDeviceModule();
      }

      return PeerConnectionFactory.nativeCreatePeerConnectionFactory(ContextUtils.getApplicationContext(), this.options, this.audioDeviceModule.getNativeAudioDeviceModulePointer(), this.audioEncoderFactoryFactory.createNativeAudioEncoderFactory(), this.audioDecoderFactoryFactory.createNativeAudioDecoderFactory(), this.videoEncoderFactory, this.videoDecoderFactory, this.audioProcessingFactory == null ? 0L : this.audioProcessingFactory.createNative(), this.fecControllerFactoryFactory == null ? 0L : this.fecControllerFactoryFactory.createNative(), this.networkControllerFactoryFactory == null ? 0L : this.networkControllerFactoryFactory.createNativeNetworkControllerFactory(), this.networkStatePredictorFactoryFactory == null ? 0L : this.networkStatePredictorFactoryFactory.createNativeNetworkStatePredictorFactory(), this.neteqFactoryFactory == null ? 0L : this.neteqFactoryFactory.createNativeNetEqFactory());
    }
  }

  public static class Options {
    static final int ADAPTER_TYPE_UNKNOWN = 0;
    static final int ADAPTER_TYPE_ETHERNET = 1;
    static final int ADAPTER_TYPE_WIFI = 2;
    static final int ADAPTER_TYPE_CELLULAR = 4;
    static final int ADAPTER_TYPE_VPN = 8;
    static final int ADAPTER_TYPE_LOOPBACK = 16;
    static final int ADAPTER_TYPE_ANY = 32;
    public int networkIgnoreMask;
    public boolean disableEncryption;
    public boolean disableNetworkMonitor;

    public Options() {
    }

    @CalledByNative("Options")
    int getNetworkIgnoreMask() {
      return this.networkIgnoreMask;
    }

    @CalledByNative("Options")
    boolean getDisableEncryption() {
      return this.disableEncryption;
    }

    @CalledByNative("Options")
    boolean getDisableNetworkMonitor() {
      return this.disableNetworkMonitor;
    }
  }

  public static class InitializationOptions {
    final Context applicationContext;
    final String fieldTrials;
    final boolean enableInternalTracer;
    final NativeLibraryLoader nativeLibraryLoader;
    final String nativeLibraryName;
    @Nullable
    Loggable loggable;
    @Nullable
    Logging.Severity loggableSeverity;

    private InitializationOptions(Context applicationContext, String fieldTrials, boolean enableInternalTracer, NativeLibraryLoader nativeLibraryLoader, String nativeLibraryName, @Nullable Loggable loggable, @Nullable Logging.Severity loggableSeverity) {
      this.applicationContext = applicationContext;
      this.fieldTrials = fieldTrials;
      this.enableInternalTracer = enableInternalTracer;
      this.nativeLibraryLoader = nativeLibraryLoader;
      this.nativeLibraryName = nativeLibraryName;
      this.loggable = loggable;
      this.loggableSeverity = loggableSeverity;
    }

    public static Builder builder(Context applicationContext) {
      return new Builder(applicationContext);
    }

    public static class Builder {
      private final Context applicationContext;
      private String fieldTrials = "";
      private boolean enableInternalTracer;
      private NativeLibraryLoader nativeLibraryLoader = new NativeLibrary.DefaultLoader();
      private String nativeLibraryName = "jingle_peerconnection_so";
      @Nullable
      private Loggable loggable;
      @Nullable
      private Logging.Severity loggableSeverity;

      Builder(Context applicationContext) {
        this.applicationContext = applicationContext;
      }

      public Builder setFieldTrials(String fieldTrials) {
        this.fieldTrials = fieldTrials;
        return this;
      }

      public Builder setEnableInternalTracer(boolean enableInternalTracer) {
        this.enableInternalTracer = enableInternalTracer;
        return this;
      }

      public Builder setNativeLibraryLoader(NativeLibraryLoader nativeLibraryLoader) {
        this.nativeLibraryLoader = nativeLibraryLoader;
        return this;
      }

      public Builder setNativeLibraryName(String nativeLibraryName) {
        this.nativeLibraryName = nativeLibraryName;
        return this;
      }

      public Builder setInjectableLogger(Loggable loggable, Logging.Severity severity) {
        this.loggable = loggable;
        this.loggableSeverity = severity;
        return this;
      }

      public InitializationOptions createInitializationOptions() {
        return new InitializationOptions(this.applicationContext, this.fieldTrials, this.enableInternalTracer, this.nativeLibraryLoader, this.nativeLibraryName, this.loggable, this.loggableSeverity);
      }
    }
  }

  private static class ThreadInfo {
    final Thread thread;
    final int tid;

    public static ThreadInfo getCurrent() {
      return new ThreadInfo(Thread.currentThread(), Process.myTid());
    }

    private ThreadInfo(Thread thread, int tid) {
      this.thread = thread;
      this.tid = tid;
    }
  }
}
