import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class addressTrans {
	public static void main(String args[]) throws IOException {
		String lng 		= null;
		String lat 		= null;
		String status 		= null;
		String locationType 	= null;
		
		String currentLine 	= "";
		String calLine 		= "";
		int totalLine 		= 0;
		int count 		= 0;
		
		long startTime 	= System.currentTimeMillis();
		String pathname = "D:\\test.txt";
		File filename 	= new File(pathname);
		InputStreamReader reader  = new InputStreamReader(new FileInputStream(filename));
		BufferedReader br 	  = new BufferedReader(reader);
		// Get total lines
		InputStreamReader reader2 = new InputStreamReader(new FileInputStream(filename));
		BufferedReader cal 	  = new BufferedReader(reader2);
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
			//String[] info = currentLine.split("\\.");
			String[] info 	= currentLine.split("!");
			String index 	= info[0];
			String address 	= info[1];
			Map<String, String> map = addressTrans.getLatitude(address);
			if (null != map) {
				lng 		= map.get("lng");// longitude
				lat 		= map.get("lat");// latitude
				locationType 	= map.get("locationType");
				status 		= map.get("status");// OK - success, ZERO_RESULTS - no info found
			}
			try {
				File csv = new File("D:\\output.csv");
				BufferedWriter bw = new BufferedWriter(new FileWriter(csv, true)); // true-not overwrite previous data
				// Add new line
				bw.write(
						index + "," + address + "," + lng + "," + lat + "," + locationType + "," + status);
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
		System.out.println("Costs： " + (endTime - startTime) / 1000 + "s");
		System.out.println("Finish");
	}

	// Return the lng&lat of address
	// Use key
	//public static final String AK = "AIzaSyAki6Ot57vnAzVG9YJRRpsteCwDgRXr2PM";
	public static final String AK = "AIzaSyDkccjjBnD7gre_7Oli5YKTXWanUUSqbCc";
	
	public static Map<String, String> getLatitude(String address) {
		try {
			//"https://maps.googleapis.com/maps/api/geocode/json?address=1600+Amphitheatre+Parkway,+Mountain+View,+CA&key=YOUR_API_KEY"
			address = address.replace(" ","+");
			address = address.replace(",,",",");
			address = address.replace(",",",+");
			
			URL resjson 	  = new URL("https://maps.googleapis.com/maps/api/geocode/json?address=" + address + "&key=" + AK);
			BufferedReader in = new BufferedReader(new InputStreamReader(resjson.openStream()));
			String res;
			StringBuilder sb  = new StringBuilder("");
			while ((res = in.readLine()) != null) {
				sb.append(res.trim());
			}
			in.close();
			String str = sb.toString();
			//解析这段json字符串，首先取得一个JSONObject,如果只有一层数据直接使用getString("名称")就可以，
			//含有集合的话就使用getJSONArray("名称");先得到集合如下所示
			JSONObject obj = new JSONObject(str.toString()); 
			String status  = obj.getString("status");
			System.out.println(status);
			JSONArray objArray = obj.getJSONArray("results");
			String lat 	    = null;
			String lng 	    = null;
			String locationType = null;
			if(objArray.length() != 0) {
				JSONObject location = objArray.getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
				lat 	     = Double.toString(location.getDouble("lat"));
				lng 	     = Double.toString(location.getDouble("lng"));
				locationType = objArray.getJSONObject(0).getJSONObject("geometry").getString("location_type");
			}else {
				lat = "null";
				lng = "null";
				locationType = "null";
			}
			//System.out.println("locationType"+locationType);
			Map<String, String> map = null;
			if (str != null) {
					map = new HashMap<String, String>();
					map.put("lng", lng);
					map.put("lat", lat);
					map.put("status", status);
					map.put("locationType", locationType);
					return map;
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
