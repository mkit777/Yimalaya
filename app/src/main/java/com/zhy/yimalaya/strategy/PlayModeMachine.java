package com.zhy.yimalaya.strategy;

import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.zhy.yimalaya.R;
import com.zhy.yimalaya.interfaces.IPlayerPresenter;

import java.util.HashMap;

public class PlayModeMachine {

    public static class ModeInfo {
        private int mImg;
        private String mText;
        private XmPlayListControl.PlayMode mPlayMode;

        public ModeInfo(int img, String text, XmPlayListControl.PlayMode playMode) {
            this.mImg = img;
            this.mText = text;
            this.mPlayMode = playMode;
        }


        public int getImg() {
            return mImg;
        }

        public String getText() {
            return mText;
        }

        public XmPlayListControl.PlayMode getPlayMode() {
            return mPlayMode;
        }
    }

    private static final HashMap<XmPlayListControl.PlayMode, XmPlayListControl.PlayMode> TRANSFORM_TABLE = new HashMap<>();
    private static final HashMap<XmPlayListControl.PlayMode, ModeInfo> INFO_MAP = new HashMap<>();

    static {
        TRANSFORM_TABLE.put(XmPlayListControl.PlayMode.PLAY_MODEL_LIST, XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP);
        TRANSFORM_TABLE.put(XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP, XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP);
        TRANSFORM_TABLE.put(XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP, XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM);
        TRANSFORM_TABLE.put(XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM, XmPlayListControl.PlayMode.PLAY_MODEL_LIST);


        INFO_MAP.put(XmPlayListControl.PlayMode.PLAY_MODEL_LIST,
                new ModeInfo(R.drawable.selector_play_mode_list, "顺序播放", XmPlayListControl.PlayMode.PLAY_MODEL_LIST));

        INFO_MAP.put(XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP,
                new ModeInfo(R.drawable.selector_play_mode_loop, "列表循环", XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP));

        INFO_MAP.put(XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP,
                new ModeInfo(R.drawable.selector_play_mode_one, "单曲循环", XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP));

        INFO_MAP.put(XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM,
                new ModeInfo(R.drawable.selector_play_mode_random, "随机播放", XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM));
    }


    public static XmPlayListControl.PlayMode nextMode(XmPlayListControl.PlayMode mode) {
        return TRANSFORM_TABLE.get(mode);
    }

    public static ModeInfo getModeInfo(XmPlayListControl.PlayMode mode) {
        return INFO_MAP.get(mode);
    }
}
