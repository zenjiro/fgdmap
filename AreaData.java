import java.awt.Shape;

/**
 * 面データ
 */
public class AreaData {
	/**
	 * 面
	 */
	public Shape area;
	/**
	 * ラベル
	 */
	public String label;
	/**
	 * 点データを初期化します。
	 * @param area 面
	 * @param label ラベル
	 */
	public AreaData(final Shape area, final String label) {
		this.area = area;
		this.label = label;
	}
	@Override
	public String toString() {
		return this.label;
	}
}
