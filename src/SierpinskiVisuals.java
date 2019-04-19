package util;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Line;
/**
 * 
 * @author Brian Guidarini
 * 
 * This is the result of messing around with the slick API. 
 *
 */
public class SierpinskiVisuals extends BasicGame {

	//Window width
	private final static int WIDTH = 1024;
	//Window height
	private final static int HEIGHT = 1024;
	//Maximum extension of the side circular fractals. Changing this value impacts the colors!
	private final static int WAVE_MAX = 512;
	//Number of frames until a spectacular burst of colors
	private final static int BURST_FREQ = 2048;

	//arbitrary iteration value. It's used for the visuals
	private int i = 1;
	//The rate that i increases
	private int itrRate = 4;
	//This tracks the number of big red Sierpinski triangles
	private int curTriangle = 1;
	//Length of the current big red Sierpinski triangle
	private int curTriangleLength = 1;
	//Current state of the "wave" of the visuals. 
	private int waveVal = 0;
	//Number of times waveVal has changed. It's used for computing the color of each pixel.
	private int waveChangeCount = 0;
	//If false, the wave is decreasing. Otherwise, it's increasing in size
	private boolean waveUp = false;

	private static AppGameContainer app;


	public static void main(String[] args) throws SlickException {
		app = new AppGameContainer(new SierpinskiVisuals("Fractal"));
		app.setDisplayMode(WIDTH, HEIGHT, false);
		app.setFullscreen(false);
		app.setTargetFrameRate(250);
		app.setAlwaysRender(true);
		app.setUpdateOnlyWhenVisible(false);
		app.setTargetFrameRate(30);
		app.setShowFPS(true);
		app.start();

	}

	public SierpinskiVisuals(String title) {
		super(title);
	}

	/**
	 * This method renders the Sierpinski triangles to the screen. 
	 * 
	 * @param gc
	 * Game container for this window
	 * @param g
	 * Graphics context
	 * @throws SlickException
	 * Thrown if the method fails for any reason
	 */
	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException {
		i += itrRate;

		if (waveVal == 0) {
			waveChangeCount++;
			waveUp = false;
			waveVal++;
		}
		if (waveVal == WAVE_MAX) {
			waveChangeCount++;
			waveUp = true;
			waveVal--;
		}
		if (!waveUp) {
			waveVal++;
		} else {
			waveVal--;
		}
		if (i > curTriangleLength) {
			curTriangle++;
			curTriangleLength = (int) Math.pow(2, curTriangle);
		}
		//Function that draws each pixel
		for (int x = 1; x < WIDTH; x += 2) {
			for (int y = 1; y < HEIGHT; y += 3) {
				if ((i + x & y + i) == 0) {
					Color c = new Color(16711680);
					g.setColor(c);
					//draws outward sierpinski triangle
					g.drawLine(x+ (int)Math.sqrt(waveVal*y) , y + (int)Math.sqrt(waveVal*x), x+ (int)Math.sqrt(waveVal*y), y + (int)Math.sqrt( waveVal*x));
					//draws inward sierpinski triangle
					g.drawLine(y+ (int)Math.sqrt(waveVal*y) , x + (int)Math.sqrt(waveVal*x), y+ (int)Math.sqrt(waveVal*y), x + (int)Math.sqrt( waveVal*x));
				}
				if (((x ^ y)) == x+y) {
					//Choose an interesting background color
					Color c = new Color(((((i % BURST_FREQ * waveChangeCount) * x ^ (i % BURST_FREQ * waveChangeCount) * y)) * 438
							* waveChangeCount));
					g.setColor(c);
					//draw the background
					g.drawLine(WIDTH - x, y, WIDTH - x, y);
					g.drawLine(x, HEIGHT - y, x, HEIGHT - y);
					g.drawLine(WIDTH - x, HEIGHT - y, WIDTH - x, HEIGHT - y);
					g.drawLine(x, y, x, y);
					//Top left circular sierpinski triangle
					g.drawLine(WIDTH - ((int) ((int) Math.sqrt(waveVal * x))), ((int) Math.sqrt(waveVal * y)), WIDTH - ((int) ((int) Math.sqrt(waveVal * x))), ((int) Math.sqrt(waveVal * y)));
					//Bottom left circular sierpinski triangle
					g.drawLine(((int) Math.sqrt(waveVal * x)), HEIGHT - ((int) ((int) Math.sqrt(waveVal * y))), ((int) Math.sqrt(waveVal * x)),
							HEIGHT - ((int) ((int) Math.sqrt(waveVal * y))));
					//Bottom right circular sierpinski triangle
					g.drawLine(WIDTH - ((int) Math.sqrt(waveVal * x)), HEIGHT - ((int) Math.sqrt(waveVal * y )), WIDTH - ((int) Math.sqrt(waveVal * x)),
							HEIGHT - ((int) Math.sqrt(waveVal * y)));
					//Top left circular sierpinski triangle
					g.drawLine(((int) ((int) Math.sqrt(waveVal * x))), ((int) Math.sqrt(waveVal * y)), (int) ((int) Math.sqrt(waveVal * x)), ((int) Math.sqrt(waveVal * y)));
				}
			}
		}

		g.setColor(Color.black);
		int remainingItrs = (curTriangleLength - i);
		g.setColor(Color.white);
		g.drawString("Iteration " + i + " Increment Rate: " + itrRate + " Current Triangle: #" + curTriangle, 100, 10);
		g.drawString("Iterations until next Triangle: " + remainingItrs + " " + "", 100, 21);

	}

	/**
	 * 
	 * @param x
	 * x component on the board
	 * @param y
	 * y component on the board
	 * @param type
	 * Must be 0, 1, 2
	 * @return
	 * Returns an array of interesting lines to render. This is unused by default.
	 */
	public Line[] lineSets(int x, int y, int type) {
		if (type == 0) {
			Line a = new Line(WIDTH - ((int) Math.sqrt(x * y + x * y)), y, WIDTH - ((int) Math.sqrt(x * y + x * y)), y);
			Line b = new Line(x, HEIGHT - ((int) Math.sqrt(x * y + y * x)), x,
					HEIGHT - ((int) Math.sqrt(y * x + x * y)));
			Line c = new Line(WIDTH - x, HEIGHT - ((int) Math.sqrt(y * x + y * x)), WIDTH - x,
					HEIGHT - ((int) Math.sqrt(x * y + y * x)));
			Line d = new Line(((int) Math.sqrt(x * y + x * y)), y, (int) Math.sqrt(x * y + y * x), y);
			return new Line[] { a, b, c, d };
		} else if (type == 1) {
			Line a = new Line(x + (int) ((double) y * Math.sin(x * y)), y + (int) ((double) waveVal * Math.cos(x * x)),
					x + (int) ((double) y * Math.sin(x * y)), y + (int) ((double) waveVal * Math.cos(x * x)));
			return new Line[] { a };
		} else if (type == 2) {
			Line a = new Line(x + (int) ((double) y * Math.sin(x * x)), y + (int) ((double) waveVal * Math.tan(x * x)),
					x + (int) ((double) y * Math.sin(x * x)), y + (int) ((double) waveVal * Math.tan(x * x)));
			return new Line[] { a };
		}

		System.err.println("Bad type");
		return null;
	}
	
	@Override
	public void init(GameContainer arg0) throws SlickException {}

	@Override
	public void update(GameContainer arg0, int arg1) throws SlickException {}

}