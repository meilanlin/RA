import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

public class addressTrans {
	public static void main(String args[]) throws IOException {
		String lng 			= null;
		String lat 			= null;
		String precise 		= null;
		String confidence 	= null;
		String level 		= null;
		String currentLine 	= "";
		String calLine 		= "";
		int totalLine 		= 0;
		int count 			= 0;

		long startTime 			  = System.currentTimeMillis();
		String pathname 		  = "D:\\2009-2.txt";
		File filename 			  = new File(pathname);
		InputStreamReader reader  = new InputStreamReader(new FileInputStream(filename));
		BufferedReader br 		  = new BufferedReader(reader);
		// Get total lines
		InputStreamReader reader2 = new InputStreamReader(new FileInputStream(filename));
		BufferedReader cal 		  = new BufferedReader(reader2);
		while (calLine != null) {
			calLine = cal.readLine(); // Read one line at a time
			if (calLine == null)
				break;
			totalLine++;
		}
		while (currentLine != null) {
			currentLine = br.readLine(); // Read one line at a time
			if (currentLine == null)
				break;
			String[] info 	= currentLine.split("\\.");
			String index 	= info[0];
			String address 	= info[1];
			Map<String, String> map = addressTrans.getLatitude(address);
			if (null != map) {
				lng 		= map.get("lng");// longitude
				lat 		= map.get("lat");// latitude
				precise 	= map.get("precise");// 0-Fuzzy search 1-Exact search
				confidence 	= map.get("confidence");// >80 means
				level 		= map.get("levelNew");
			}
			try {
				File csv = new File("D:\\output.csv");
				BufferedWriter bw = new BufferedWriter(new FileWriter(csv, true)); // true-not overwrite previous data
				// Add new line
				bw.write(
						index + "," + address + "," + lng + "," + lat + "," + precise + "," + confidence + "," + level);
				bw.newLine();
				bw.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			count++;
			// Progress bar
			System.out.print("current:" + count + "/" + totalLine);
			Double p1 = new Double((double) count);
			Double p2 = new Double((double) totalLine);
			double p3 = p1 / p2;
			NumberFormat nf = NumberFormat.getPercentInstance();
			nf.setMinimumFractionDigits(2);
			System.out.println(" " + nf.format(p3));
		}
		long endTime = System.currentTimeMillis();
		System.out.println("Costs£º " + (endTime - startTime) / 1000 + "s");
		System.out.println("Finish");
	}

	// Return the lng&lat of address
	// Use key
	// public static final String AK = "AqQdnGImovCYwO2E2jeSmtCGDVppW6jF";
	public static final String AK = "SHr3ZmCRL1AIp0wgtjGYbeeBxGXdNwkc";

	public static Map<String, String> getLatitude(String address) {
		try {
			address 		  = URLEncoder.encode(address, "UTF-8"); // Transform address into utf-8 hexadecimal
			URL resjson 	  = new URL("http://api.map.baidu.com/geocoder/v2/?address=" + address + "&output=json&ak=" + AK);
			BufferedReader in = new BufferedReader(new InputStreamReader(resjson.openStream()));
			String res;
			StringBuilder sb  = new StringBuilder("");
			while ((res = in.readLine()) != null) {
				sb.append(res.trim());
			}
			in.close();
			String str = sb.toString();
			Map<String, String> map = null;
			if (str != null) {
				// Use json to get simpler codes
				// But I did'nt use json
				int lngStart 		= str.indexOf("lng\":");
				int lngEnd 			= str.indexOf(",\"lat");
				int latEnd 			= str.indexOf("},\"precise");
				int preciseStart 	= str.indexOf("\"precise\":");
				int preciseEnd 		= str.indexOf(",\"confidence\"");
				int confidenceStart	= str.indexOf("\"confidence\":");
				int confidenceEnd 	= str.indexOf(",\"level\"");
				int levelStart 		= str.indexOf("\"level\":");
				int levelEnd 		= str.indexOf("\"}}");
				if (lngStart > 0 && lngEnd > 0 && latEnd > 0) {
					String lng 			= str.substring(lngStart + 5, lngEnd);
					String lat 			= str.substring(lngEnd + 7, latEnd);
					String precise 		= str.substring(preciseStart + 10, preciseEnd);
					String confidence	= str.substring(confidenceStart + 13, confidenceEnd);
					String level 		= str.substring(levelStart + 9, levelEnd);
					String levelNew 	= new String(level.getBytes("GBK"), "UTF-8");

					map = new HashMap<String, String>();
					map.put("lng", lng);
					map.put("lat", lat);
					map.put("precise", precise);
					map.put("confidence", confidence);
					map.put("levelNew", levelNew);
					return map;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
