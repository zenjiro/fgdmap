import java.awt.geom.Rectangle2D;
import java.util.Collection;

/**
 * ユーティリティクラス
 */
public class Utilities {
	/**
	 * @param points 点データ
	 * @return 外接長方形
	 */
	public static Rectangle2D getPointBounds(final Collection<PointData> points) {
		double minX = Double.POSITIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY;
		double maxX = Double.NEGATIVE_INFINITY;
		double maxY = Double.NEGATIVE_INFINITY;
		for (final PointData point : points) {
			minX = Math.min(minX, point.point.getX());
			minY = Math.min(minY, point.point.getY());
			maxX = Math.max(minX, point.point.getX());
			maxY = Math.max(minY, point.point.getY());
		}
		return new Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY);
	}

	/**
	 * @param areas 面データ
	 * @return 外接長方形
	 */
	public static Rectangle2D getAreaBounds(Collection<AreaData> areas) {
		double minX = Double.POSITIVE_INFINITY;
		double minY = Double.POSITIVE_INFINITY;
		double maxX = Double.NEGATIVE_INFINITY;
		double maxY = Double.NEGATIVE_INFINITY;
		for (final AreaData area : areas) {
			minX = Math.min(minX, area.area.getBounds2D().getMinX());
			minY = Math.min(minY, area.area.getBounds2D().getMinY());
			maxX = Math.max(maxX, area.area.getBounds2D().getMaxX());
			maxY = Math.max(maxY, area.area.getBounds2D().getMaxY());
			if (area.area.getBounds2D().getMinX() < 100) {
				System.out.println(area + ", " + area.area.getBounds2D());
			}
		}
		return new Rectangle2D.Double(minX, minY, maxX - minX, maxY - minY);
	}
}
