import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.xml.stream.XMLStreamException;

/**
 * 基盤地図情報（縮尺レベル25000）の行政区画の境界線及び代表点データを読み込んでみます。
 * FG-GML-28-05-Z001.zip
 * FG-GML-28102-ALL-Z001.zip
 */
public class Main {
	/**
	 * メインクラス
	 * @param args コマンドライン引数
	 * @throws XMLStreamException XMLストリーム例外
	 * @throws IOException IO例外
	 */
	public static void main(final String[] args) throws XMLStreamException, IOException {
		final Map<String, AreaData> areas = LoadMapUtilities.loadAreas(new File("FG-GML-28102-ALL-Z001.zip"));
		final Rectangle2D bounds = Utilities.getAreaBounds(areas.values());
		final JFrame frame = new JFrame("FGDMap 0.1.0");
		JFrame.setDefaultLookAndFeelDecorated(true);
		frame.setLocationByPlatform(true);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setSize(640, 480);
		frame.setBackground(Color.YELLOW);
		final JPanel panel = new JPanel() {
			@Override
			protected void paintComponent(final Graphics g) {
				super.paintComponent(g);
				final Graphics2D g2 = (Graphics2D) g;
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setFont(new Font("メイリオ", Font.PLAIN, g2.getFont().getSize()));
				double scale = Math.min(getWidth() / bounds.getWidth(), getHeight() / bounds.getHeight());
				AffineTransform transform = new AffineTransform();
				transform.scale(scale, -scale);
				transform.translate(-bounds.getMinX(), -bounds.getMaxY());
				final Random random = new Random(100);
				for (final AreaData area : areas.values()) {
					g2.setColor(Color.getHSBColor(random.nextFloat(), .1f, 1));
					g2.fill(transform.createTransformedShape(area.area));
				}
				g2.setColor(Color.BLACK);
				for (final AreaData area : areas.values()) {
					g2.draw(transform.createTransformedShape(area.area));
					g2.drawString(area.label.replaceFirst(".+ ", ""),
							(float) ((area.area.getBounds2D().getCenterX() - bounds.getMinX()) * scale),
							(float) (-(area.area.getBounds2D().getCenterY() - bounds.getMaxY()) * scale));
				}
			}
		};
		panel.setBackground(Color.WHITE);
		frame.add(panel);
		frame.setVisible(true);
	}
}
