import java.awt.geom.Point2D;

/**
 * 点データ
 */
public class PointData {
	/**
	 * 点
	 */
	public Point2D point;
	/**
	 * ラベル
	 */
	public String label;
	/**
	 * 点データを初期化します。
	 * @param point 点
	 * @param label ラベル
	 */
	public PointData(final Point2D point, final String label) {
		this.point = point;
		this.label = label;
	}
	@Override
	public String toString() {
		return this.label + "(" + this.point.getX() + ", " + this.point.getY() + ")";
	}
}
