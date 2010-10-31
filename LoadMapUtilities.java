import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

/**
 * 地図を読み込むユーティリティクラス
 */
public class LoadMapUtilities {
	/**
	 * 点データを読み込みます。
	 * @param file ファイル
	 * @return 点データ
	 * @throws IOException 入出力例外
	 * @throws XMLStreamException XMLストリーム例外
	 */
	public static Map<String, PointData> loadPoints(final File file) throws IOException, XMLStreamException {
		final Map<String, PointData> points = new HashMap<String, PointData>();
		final ZipFile zipFile = new ZipFile(file);
		for (final Enumeration<? extends ZipEntry> enumeration = zipFile.entries(); enumeration.hasMoreElements();) {
			final ZipEntry entry = enumeration.nextElement();
			if (entry.getName().matches("^FG-GML-[0-9]+-(Adm|Comm)Pt(25000)?-[0-9]+-[0-9]+\\.xml$")) {
				final XMLInputFactory factory = XMLInputFactory.newInstance();
				final XMLEventReader reader = factory.createXMLEventReader(new InputStreamReader(zipFile
						.getInputStream(entry), "SJIS"));
				String id = null;
				Point2D point = null;
				String label = null;
				while (reader.hasNext()) {
					final XMLEvent event = reader.nextEvent();
					if (event.isStartElement()) {
						final StartElement element = event.asStartElement();
						if (element.getName().getLocalPart().equals("fid")) {
							if (reader.hasNext()) {
								final XMLEvent event2 = reader.nextEvent();
								if (event2.isCharacters()) {
									id = event2.asCharacters().getData();
								}
							}
						} else if (element.getName().getPrefix().equals("gml")
								&& element.getName().getLocalPart().equals("pos")) {
							if (reader.hasNext()) {
								final XMLEvent event2 = reader.nextEvent();
								if (event2.isCharacters()) {
									final String[] items = event2.asCharacters().getData().split(" ");
									if (items.length == 2) {
										point = new Point2D.Double(Double.valueOf(items[1]), Double.valueOf(items[0]));
									}
								}
							}
						} else if (element.getName().getLocalPart().equals("name")) {
							if (reader.hasNext()) {
								final XMLEvent event2 = reader.nextEvent();
								if (event2.isCharacters()) {
									label = event2.asCharacters().getData();
								}
							}
						}
					} else if (event.isEndElement()) {
						final EndElement element = event.asEndElement();
						if (element.getName().getLocalPart().matches("(Adm|Comm)Pt")) {
							if (id != null && point != null && label != null) {
								points.put(id, new PointData(point, label));
							}
						}
					}
				}
				reader.close();
			}
		}
		return points;
	}

	/**
	 * 面データを読み込みます。
	 * @param file ファイル
	 * @return 面データ
	 * @throws IOException 入出力例外
	 * @throws XMLStreamException XMLストリーム例外
	 */
	public static Map<String, AreaData> loadAreas(final File file) throws IOException, XMLStreamException {
		final Map<String, AreaData> areas = new HashMap<String, AreaData>();
		final ZipFile zipFile = new ZipFile(file);
		for (final Enumeration<? extends ZipEntry> enumeration = zipFile.entries(); enumeration.hasMoreElements();) {
			final ZipEntry entry = enumeration.nextElement();
			//			if (entry.getName().matches("^FG-GML-[0-9]+-(AdmArea|BldA)(25000)?-[0-9]+-[0-9]+\\.xml$")) {
			if (entry.getName().matches("^FG-GML-[0-9]+-AdmArea(25000)?-[0-9]+-[0-9]+\\.xml$")) {
				final XMLInputFactory factory = XMLInputFactory.newInstance();
				final XMLEventReader reader = factory.createXMLEventReader(new InputStreamReader(zipFile
						.getInputStream(entry), "SJIS"));
				String id = null;
				Shape area = null;
				String label = null;
				while (reader.hasNext()) {
					final XMLEvent event = reader.nextEvent();
					if (event.isStartElement()) {
						final StartElement element = event.asStartElement();
						if (element.getName().getLocalPart().equals("fid")) {
							if (reader.hasNext()) {
								final XMLEvent event2 = reader.nextEvent();
								if (event2.isCharacters()) {
									id = event2.asCharacters().getData();
								}
							}
						} else if (element.getName().getPrefix().equals("gml")
								&& element.getName().getLocalPart().equals("posList")) {
							GeneralPath path = null;
							StringBuilder builder = new StringBuilder();
							for (XMLEvent event2 = reader.nextEvent(); reader.hasNext() && event2.isCharacters(); event2 = reader
									.nextEvent()) {
								String data = event2.asCharacters().getData().replace("\n", "");
								builder.append(data);
							}
							final Scanner scanner = new Scanner(builder.toString());
							while (scanner.hasNextDouble()) {
								double y = scanner.nextDouble();
								if (scanner.hasNextDouble()) {
									double x = scanner.nextDouble();
									if (path == null) {
										path = new GeneralPath();
										path.moveTo(x, y);
									} else {
										path.lineTo(x, y);
									}
								}
							}
							scanner.close();
							area = path;
						} else if (element.getName().getLocalPart().equals("name")) {
							label = "";
							if (reader.hasNext()) {
								final XMLEvent event2 = reader.nextEvent();
								if (event2.isCharacters()) {
									label = event2.asCharacters().getData();
								}
							}
						}
					} else if (event.isEndElement()) {
						final EndElement element = event.asEndElement();
						if (element.getName().getLocalPart().matches("AdmArea")) {
							if (id != null && area != null && label != null) {
								areas.put(id, new AreaData(area, label));
								id = null;
								area = null;
								label = null;
							}
						}
					}
				}
			}
		}
		return areas;
	}
}
