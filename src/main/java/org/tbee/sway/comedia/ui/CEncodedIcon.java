package org.tbee.sway.comedia.ui;

import javax.swing.*;
import java.awt.*;


/**
 * Copy from CoMedia cbeans
 */
public class CEncodedIcon implements Icon {
    public static final CEncodedIcon FIRST_SIGN = new CEncodedIcon(new int[]{193, 195, 199, 207, 199, 195, 193}, 8, 7);
    public static final CEncodedIcon PRIOR_SIGN = new CEncodedIcon(new int[]{16, 48, 112, 240, 112, 48, 16}, 4, 7);
    public static final CEncodedIcon LAST_SIGN = new CEncodedIcon(new int[]{131, 195, 227, 243, 227, 195, 131}, 8, 7);
    public static final CEncodedIcon LAST_NEW_SIGN = new CEncodedIcon(new int[]{128, 64, 193, 80, 224, 224, 243, 248, 224, 224, 193, 80, 128, 64}, 13, 7);
    public static final CEncodedIcon NEXT_SIGN = new CEncodedIcon(new int[]{128, 192, 224, 240, 224, 192, 128}, 4, 7);
    public static final CEncodedIcon BIG_RIGHT_ARROW = new CEncodedIcon(new int[]{128, 192, 224, 240, 248, 240, 224, 192, 128}, 5, 9);
    public static final CEncodedIcon SMALL_DOWN_ARROW = new CEncodedIcon(new int[]{124, 56, 16}, 7, 3);
    public static final CEncodedIcon SMALL_UP_ARROW = new CEncodedIcon(new int[]{16, 56, 124}, 7, 3);
    public static final CEncodedIcon STAR_SIGN = new CEncodedIcon(new int[]{16, 84, 56, 254, 56, 84, 16}, 7, 7);
    public static final CEncodedIcon PEN_SIGN = new CEncodedIcon(new int[]{1, 192, 1, 32, 2, 192, 2, 64, 4, 128, 4, 128, 9, 0, 9, 0, 14, 0, 12, 0, 168, 0}, 11, 11);
    public static final CEncodedIcon SMALL_LEFT_ARROW = new CEncodedIcon(new int[]{24, 56, 120, 248, 120, 56, 24}, 5, 7);
    public static final CEncodedIcon SMALL_RIGHT_ARROW = new CEncodedIcon(new int[]{192, 224, 240, 248, 240, 224, 192}, 5, 7);
    public static final CEncodedIcon ONE_SIGN = new CEncodedIcon(new int[]{96, 224, 96, 96, 96, 240}, 4, 6);
    public static final CEncodedIcon MANY_SIGN = new CEncodedIcon(new int[]{121, 224, 198, 48, 198, 48, 198, 48, 121, 224}, 12, 5);
    private int[] image = null;
    private int width = 0;
    private int height = 0;
    private Color color;
    private boolean enabled;

    public CEncodedIcon(int[] var1, int var2, int var3) {
        this.color = Color.black;
        this.enabled = true;
        this.image = var1;
        this.width = var2;
        this.height = var3;
    }

    public void paintIcon(Component var1, Graphics var2, int var3, int var4) {
        if (this.image != null) {
            if (this.enabled) {
                var2.setColor(this.color);
                this.paint(var2, var3, var4);
            } else {
                var2.setColor(Color.white);
                this.paint(var2, var3 + 1, var4 + 1);
                var2.setColor(Color.gray);
                this.paint(var2, var3, var4);
            }
        }

    }

    private void paint(Graphics var1, int var2, int var3) {
        int var4 = this.width / 8 + (this.width % 8 != 0 ? 1 : 0);

        for(int var5 = 0; var5 < var4; ++var5) {
            for(int var6 = 0; var6 < this.height; ++var6) {
                int var7 = this.image[var5 + var6 * var4];

                for(int var8 = 0; var8 < 8; ++var8) {
                    if ((var7 << var8 & 128) != 0) {
                        var1.drawLine(var2 + var5 * 8 + var8, var3 + var6, var2 + var5 * 8 + var8, var3 + var6);
                    }
                }
            }
        }

    }

    public int[] getImageArray() {
        return this.image;
    }

    public int getIconWidth() {
        return this.width;
    }

    public int getIconHeight() {
        return this.height;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color var1) {
        this.color = var1;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(boolean var1) {
        this.enabled = var1;
    }

    public static void main(String[] var0) {
        JFrame var1 = new JFrame("Comedia CEncodedIcon Test");
        JPanel var2 = new JPanel();
        var2.setLayout(new GridLayout(3, 4));
        var2.add(new JButton(SMALL_UP_ARROW));
        var2.add(new JButton(BIG_RIGHT_ARROW));
        var2.add(new JButton(SMALL_DOWN_ARROW));
        var2.add(new JButton(MANY_SIGN));
        var2.add(new JButton(ONE_SIGN));
        var2.add(new JButton(PEN_SIGN));
        var2.add(new JButton(SMALL_LEFT_ARROW));
        var2.add(new JButton(SMALL_RIGHT_ARROW));
        var2.add(new JButton(STAR_SIGN));
        var2.add(new JButton(FIRST_SIGN));
        var2.add(new JButton(PRIOR_SIGN));
        var2.add(new JButton(NEXT_SIGN));
        var2.add(new JButton(LAST_SIGN));
        var2.add(new JButton(LAST_NEW_SIGN));
        var1.getContentPane().add(var2, "Center");
        var1.setLocation(100, 100);
        var1.setSize(300, 300);
        var1.setDefaultCloseOperation(3);
        var1.show();
    }
}
