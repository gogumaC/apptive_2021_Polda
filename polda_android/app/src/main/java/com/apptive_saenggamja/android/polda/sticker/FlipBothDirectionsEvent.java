package com.apptive_saenggamja.android.polda.sticker;

/**
 * @author wupanjie
 */

public class FlipBothDirectionsEvent extends AbstractFlipEvent {

  @Override @StickerView.Flip protected int getFlipDirection() {
    return StickerView.FLIP_VERTICALLY | StickerView.FLIP_HORIZONTALLY;
  }
}
