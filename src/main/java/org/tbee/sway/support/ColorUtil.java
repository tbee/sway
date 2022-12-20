/*
 * Copyright: (c) TBEE.ORG
 */
package org.tbee.sway.support;

import java.awt.*;

/**
 * Some Color Utils methods a.o. convert a HTML Color string to Color and vice versa.
 */
public class ColorUtil
{
    /**
     * Creates a new <code>Color</code> that is a scalar version of this <code>Color</code>.
     */
    static public Color change(Color color, double factor) {
		int red = color.getRed();
		int green = color.getGreen();
		int blue = color.getBlue();

		red = (int) Math.round(red + (255 * factor));
		green = (int) Math.round(green + (255 * factor));
		blue = (int) Math.round(blue + (255 * factor));

		if (red < 0) red = 0;
		if (red > 255) red = 255;
		if (green < 0) green = 0;
		if (green > 255) green = 255;
		if (blue < 0) blue = 0;
		if (blue > 255) blue = 255;

		return new Color(red, green, blue, color.getAlpha());
	}

    /**
     * Creates a new <code>Color</code> that is a brighter version of this
     * <code>Color</code>.
     * <p>
     * This method applies an arbitrary scale factor to each of the three RGB 
     * components of this <code>Color</code> to create a brighter version
     * of this <code>Color</code>. Although <code>brighter</code> and
     * <code>darker</code> are inverse operations, the results of a
     * series of invocations of these two methods might be inconsistent
     * because of rounding errors. 
     * @return     a new <code>Color</code> object that is a brighter version of this <code>Color</code>.
     * @see        Color#brighter
     */
    static public Color brighter(Color color, double percentage) {
    	while (percentage < -1 || percentage > 1) percentage /= 100;
    	while (percentage < -1) percentage += 1.0;
    	return change(color, percentage);
	}

    /**
	 * Creates a new <code>Color</code> that is a darker version of this
	 * <code>Color</code>.
	 * <p>
	 * This method applies an arbitrary scale factor to each of the three RGB
	 * components of this <code>Color</code> to create a darker version of
	 * this <code>Color</code>. Although <code>brighter</code> and
	 * <code>darker</code> are inverse operations, the results of a series of
	 * invocations of these two methods might be inconsistent because of
	 * rounding errors.
	 * 
	 * @return a new <code>Color</code> object that is a darker version of
	 *         this <code>Color</code>.
	 * @see Color#darker
	 */
	public static Color darker(Color color, double percentage) {
    	while (percentage < -1 || percentage > 1) percentage /= 100;
    	while (percentage < -1) percentage += 1.0;
    	return change(color, -1 * percentage);
	}
    
	/**
	 * Make a darker color brighter and vice versa
	 * @param color
	 * @param percentage
	 * @return
	 */
	public static Color brighterOrDarker(Color color, double percentage) {
		return ColorUtil.isDark(color) ? ColorUtil.brighter(color, percentage) : ColorUtil.darker(color, percentage);
	}
	
	/**
	 * Blend two colors.
	 * 
	 * @param color1  First color to blend.
	 * @param color2  Second color to blend.
	 * @param ratio   Blend ratio. 0.5 will give even blend, 1.0 will return
	 *                color1, 0.0 will return color2 and so on.
	 * @return        Blended color.
	 */
	public static Color blend(Color color1, Color color2, double ratio) {
		float r = (float) ratio;
		float ir = (float) 1.0 - r;

		float rgb1[] = new float[3];
		float rgb2[] = new float[3];

		color1.getColorComponents(rgb1);
		color2.getColorComponents(rgb2);

		Color color = new Color(rgb1[0] * r + rgb2[0] * ir, rgb1[1] * r + rgb2[1] * ir, rgb1[2] * r + rgb2[2] * ir);

		return color;
	}

	/**
	 * Make an even blend between two colors.
	 * 
	 * @param color1 First color to blend.
	 * @param color2 Second color to blend.
	 * @return       Blended color.
	 */
	public static Color blend(Color color1, Color color2) {
		return ColorUtil.blend(color1, color2, 0.5);
	}

	/**
	 * Return the hex name of a specified color.
	 * 
	 * @param color  Color to get hex name of.
	 * @return       Hex name of color: "rrggbb".
	 */
	public static String getHexName(Color color) {
		int r = color.getRed();
		int g = color.getGreen();
		int b = color.getBlue();

		String rHex = Integer.toString(r, 16);
		String gHex = Integer.toString(g, 16);
		String bHex = Integer.toString(b, 16);

		return (rHex.length() == 2 ? "" + rHex : "0" + rHex) + (gHex.length() == 2 ? "" + gHex : "0" + gHex) + (bHex.length() == 2 ? "" + bHex : "0" + bHex);
	}

	/**
	 * Return the "distance" between two colors. The rgb entries are taken
	 * to be coordinates in a 3D space [0.0-1.0], and this method returnes
	 * the distance between the coordinates for the first and second color.
	 * 
	 * @param   r1, g1, b1  First color.
	 * @param   r2, g2, b2  Second color.
	 * @return  Distance bwetween colors.
	 */
	public static double colorDistance(double r1, double g1, double b1, double r2, double g2, double b2) {
		double a = r2 - r1;
		double b = g2 - g1;
		double c = b2 - b1;

		return Math.sqrt(a * a + b * b + c * c);
	}

	/**
	 * Return the "distance" between two colors.
	 * 
	 * @param color1  First color [r,g,b].
	 * @param color2  Second color [r,g,b].
	 * @return        Distance bwetween colors.
	 */
	public static double colorDistance(double[] color1, double[] color2) {
		return ColorUtil.colorDistance(color1[0], color1[1], color1[2], color2[0], color2[1], color2[2]);
	}

	/**
	 * Return the "distance" between two colors.
	 * 
	 * @param color1  First color.
	 * @param color2  Second color.
	 * @return        Distance between colors.
	 */
	public static double colorDistance(Color color1, Color color2) {
		float rgb1[] = new float[3];
		float rgb2[] = new float[3];

		color1.getColorComponents(rgb1);
		color2.getColorComponents(rgb2);

		return ColorUtil.colorDistance(rgb1[0], rgb1[1], rgb1[2], rgb2[0], rgb2[1], rgb2[2]);
	}

	/**
	 * Check if a color is more dark than light. Useful if an entity of
	 * this color is to be labeled: Use white label on a "dark" color and
	 * black label on a "light" color.
	 *
	 * @param r,g,b  Color to check.
	 * @return       True if this is a "dark" color, false otherwise.
	 */
	public static boolean isDark(double r, double g, double b) {
		// Measure distance to white and black respectively
		double dWhite = ColorUtil.colorDistance(r, g, b, 1.0, 1.0, 1.0);
		double dBlack = ColorUtil.colorDistance(r, g, b, 0.0, 0.0, 0.0);

		return dBlack < dWhite;
	}

	/**
	 * Check if a color is more dark than light. Useful if an entity of
	 * this color is to be labeled: Use white label on a "dark" color and
	 * black label on a "light" color.
	 *
	 * @param color  Color to check.
	 * @return       True if this is a "dark" color, false otherwise.
	 */
	public static boolean isDark(Color color) {
		float r = color.getRed() / 255.0f;
		float g = color.getGreen() / 255.0f;
		float b = color.getBlue() / 255.0f;

		return isDark(r, g, b);
	}
	

	/**
	 * Make a new version of the color but change the alpha component
	 * 
	 * @param color
	 * @param alpha
	 * @return
	 */
	static public Color alterAlpha(Color color, int alpha) {
		return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
	}
	
	/**
	 * Make a new version of the color but change the alpha component
	 * 
	 * @param color
	 * @param alpha
	 * @return
	 */
	static public Color alterAlpha(Color color, float alpha) {
		float rgb[] = color.getRGBColorComponents(null);
		return new Color(rgb[0], rgb[1], rgb[2], alpha);
	}
	
	/**
	 * Determine the opposite color but with the same alpha.
	 * 
	 * @param color
	 * @return
	 */
	public static Color getOppositeColor(Color color) {
		return new Color( 255 ^ color.getRed(), 255 ^ color.getGreen(), 255 ^ color.getBlue(), color.getAlpha());
	}
	
	
	/**
	 * Return the same color but with an altered transparency
	 * 
	 * @param color
	 * @return
	 */
	public static Color alterTransparency(Color color, int alpha) {
		return new Color( color.getRed(), color.getGreen(), color.getBlue(), alpha);
	}
}
