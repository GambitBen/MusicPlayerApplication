package com.musicplayer.yorai.musicplayerapplication.Logic;

import android.content.Context;
import android.view.KeyEvent;
import android.widget.MediaController;

import com.musicplayer.yorai.musicplayerapplication.MainActivity.MainActivity;


public class MusicController extends MediaController {

    Context context;

    public MusicController(Context context) {
        super(context);
        this.context = context;
    }

    public void hide(){

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if(keyCode == KeyEvent.KEYCODE_BACK){
            ((MainActivity)context).onBackPressed();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }


}