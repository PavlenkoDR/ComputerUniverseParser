import org.jsoup.*;
import org.jsoup.nodes.*;
import org.jsoup.select.*;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Main {
	public static String getHTML(String urlToRead) throws Exception {
		StringBuilder result = new StringBuilder();
		URL url = new URL(urlToRead);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		BufferedReader rd = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
		String line;
		while ((line = rd.readLine()) != null) {
			result.append(line);
		}
		rd.close();
		return result.toString();
	}

	public static Document getURLrequerst(String url) {
		Document doc = null;
		boolean flag = false;
		while (!flag)
			try {
				System.out.print("Try get HTML... ");
				doc = Jsoup.connect(url).get();
				System.out.println("Ok!");
				flag = true;
			} catch (Exception e) {
				System.err
						.print("URL FAIL! Try after 5 seconds");
				try {
					for (int i = 0; i < 5; i++)
					{
						System.err.print(".");
						TimeUnit.SECONDS.sleep(1);
					}
					System.err.println();
				} catch (InterruptedException e1) {
					System.err.println("Time delay error");
				}
			}
		return doc;
	}

	public static void getHTMLrequest(String request) {
		boolean flag = false;
		while (!flag)
			try {
				System.out.print("Try send message... ");
				getHTML(request);
				System.out.println("Ok!");
				flag = true;
			} catch (Exception e) {
				System.err
						.print("VK Api FAIL! Try after 5 seconds");
				try {
					for (int i = 0; i < 5; i++)
					{
						System.err.print(".");
						TimeUnit.SECONDS.sleep(1);
					}
					System.err.println();
				} catch (InterruptedException e1) {
					System.err.println("Time delay error");
				}
			}
	}

	public static void main(String[] args) throws IOException {
		Path file;
		List<String> lines = null;
		file = new File("properties.txt").toPath();
		lines = Files.readAllLines(file, StandardCharsets.UTF_8);
		String sid = null, stoken = null, sdelay = null, surl = null;
		for (String line : lines) {
			if (line.indexOf("id=") != -1)
				sid = line.replace("id=", "");
			if (line.indexOf("url=") != -1)
				surl = line.replace("url=", "");
			if (line.indexOf("token=") != -1)
				stoken = line.replace("token=", "");
			if (line.indexOf("delay=") != -1)
				sdelay = line.replace("delay=", "");
		}
		String message = "", finded = "", prewfinded;
		// String params = "user_id="+sid+"&message=";//to message
		String params = "owner_id=-" + sid + "&from_group=1&message=";// wall
																		// post
		String token = "access_token=" + stoken;
		// String method = "messages.send"; //to message
		String method = "wall.post"; // wall post
		String request;
		String url = surl;
		Document doc = null;
		doc = getURLrequerst(url);
		Elements masthead = doc.select("div[class=\"productsTableRow\"]");
		while (true) {
			message = "";
			finded = "";
			message += "====================%0A";
			int k = 0;
			for (int i = 0; i < masthead.size(); i++)
				if (!masthead.get(i).select("strong[style=\"color:#009d2c;\"]")
						.text().equals("")) {
					k++;
					message += masthead.get(i).select("a[href]").get(1).text()
							+ "%0A";
					message += masthead.get(i)
							.select("font[class=\"listCurrency2\"]").text()
							+ "%0A";
					message += masthead.get(i).select("a[href]").get(1)
							.attr("abs:href")
							+ "%0A" + "%0A";
					finded += masthead.get(i).select("a[href]").get(1)
							.attr("abs:href")
							+ "%0A" + "%0A";
				}
			System.out.println(k + " finded");
			message += Integer.toString(k) + "%20finded%0A";
			message += "====================";
			message = message.replace(" ", "%20");
			message = message.replace(":", "%3A");
			message = message.replace("/", "%2F");
			// request = "https://api.vk.com/method/" + method + "?" + params +
			// message + "&" + token;//to message
			request = "https://api.vk.com/method/" + method + "?" + params
					+ message + "&" + token;// wall post
			getHTMLrequest(request);
			int i = 0, j = new Integer(sdelay);
			if (j == 0)
				j = -1;
			while (i != j) {
				System.out.print("Check new... " + (i + 1) + "/"
						+ (j >= 0 ? j : 0) + ". ");
				doc = getURLrequerst(url);
				masthead = doc.select("div[class=\"productsTableRow\"]");
				prewfinded = finded;
				finded = "";
				for (int ii = 0; ii < masthead.size(); ii++)
					if (!masthead.get(ii)
							.select("strong[style=\"color:#009d2c;\"]").text()
							.equals(""))
						finded += masthead.get(ii).select("a[href]").get(1)
								.attr("abs:href")
								+ "%0A" + "%0A";
				if (!prewfinded.equals(finded))
					break;
				try {
					int time = 5 * 60, nowtime = 0;
					while (nowtime != time) {
						TimeUnit.SECONDS.sleep(1);
						nowtime++;
					}
				} catch (InterruptedException e) {
					System.err.println("Time delay error");
				}
				i++;
			}
		}
	}
}
