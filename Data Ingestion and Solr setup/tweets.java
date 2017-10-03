package proj1;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDate;
import java.time.LocalDateTime;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import twitter4j.*;
import twitter4j.conf.*;

class tweets {
	private static final String[] EMOTICON_UNICODE_LIST = getUnicodeList();
	private static final String[] KAOMOJI_UNICODE_LIST = getKaomojiCodes();

	void configure(ConfigurationBuilder cb) {
		cb.setJSONStoreEnabled(true);
		cb.setDebugEnabled(true);
		cb.setOAuthConsumerKey("BscPyeQ1xYsC5DCF3YReBt5Jn");
		cb.setOAuthConsumerSecret("2OeWxytvErtwwJ9FC01Za12Z1AJ2BxSHLSwXXVgtpFqojLDbpg");
		cb.setOAuthAccessToken("778021785933582336-m3OCCWpE5YJhtmjYYeOfQLtPiIGArTf");
		cb.setOAuthAccessTokenSecret("A3GovdfpCMGw5gMRieoDSMVfP9tgNiq9lRWupkWFbRe9h");
	}
	
	public static void main(String[] args) throws IOException, TwitterException {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		Scanner scan = new Scanner(System.in);
		tweets F = new tweets();
		F.configure(cb);
		Twitter twitter = new TwitterFactory(cb.build()).getInstance();
		Query query = null;
		int totalNumberOfTweets = 0;
		FileWriter writer = new FileWriter("Statistics_Final.csv", true);
		writer.write("Topic,Language,Date,Count");
		// PrintStream out = new PrintStream(new
		// FileOutputStream("RequirementCount.csv"));
		String lang = "es";
		String DateSince = "2016-09-10";
		String DateTill = "2016-09-19";
		int count = 15000;
		
		/*
		System.out.println("Enter the Search language:");
		String lang = scan.next();
		System.out.println("Enter the Date Since: ");
		String DateSince = scan.next();
		System.out.println("Enter the Date till: ");
		String DateTill = scan.next();
		System.out.println("Enter the max number of tweets req.: ");
		
		int count = scan.nextInt();
		*/
		totalNumberOfTweets = totalNumberOfTweets + F.calculation(query, lang, DateSince, DateTill, count, twitter);
		writer.close();

	}

	int calculation(Query q, String lang, String DateSince, String Datetill, int count, Twitter twitter)
			throws IOException, TwitterException {
		String Topic = "sports";
		//q = new Query("(Syrian War) OR (SyrianWar) OR (freeSyria) OR (Syrian Civil War)  +exclude:retweets");
		//+  " OR (Suriye Savaşı) OR (Suriye) OR (Suriye İç Savaşı) OR (Suriye Saldırı) " + " OR (Guerra Siria) OR (Guerra civil siria) OR (Ataque sirio)
		q = new Query("(US Open) OR (USOpen) OR (usopen) OR (Abrir EE.UU.) OR (Serena Williams) OR (Kerber) OR (Wawrinka) OR (Djokovic) OR (Monfils) OR (Nadal)  +exclude:retweets");
		//q = new Query("(Game of Thrones) OR (Gameofthrones) OR (Jon Snow) OR (George Martin) OR (Tyrion) OR (GoTSeason6)  OR (Hodor) OR (hodor) OR (holdthedoor) OR (Daenerys) OR (Targaryen) OR (Melisandre) OR (Arya Stark) OR (AryaStark) OR (Gregor Clegane) OR (GregorClegane) OR (Cersei Lannister) OR (Cersei) OR (Lannister) OR (Sansa Stark) OR (SansaStark) OR (Margaery Tyrell) OR (ThronesYall) OR (MargaeryTyrell) +exclude:retweets");
		//q = new Query("(iphone7) OR (iphone 7) OR (iOS)  OR (AppleEvent) +exclude:retweets");
		//q = new Query("(US Presidential Elections) OR (MakeAmericaGreatAgain) OR (dumptrump) OR (Hillary) OR (Trump) OR (donaltrump) OR (USAElection2016) OR (donald trump) OR (Hillary Clinton) OR (HillaryClinton) OR (election2016) OR (USelection2016) OR (US election 2016)+exclude:retweets"); // OR (미국 선거 2016) OR (미국 대통령 선거) OR (훌륭한 사람) OR (힐러리) OR (Elección de Estados Unidos 2016) OR (USAElection2016)");
		//q = new Query("Pastor John Hagee Ministries September 2016 - The Final Game of Thrones");
		//q=new Query("USOpen");
		q.lang(lang);
		q.setSince(DateSince);
		q.setUntil(Datetill);
		int size = 0;
		long lastID = Long.MAX_VALUE;
		QueryResult result = null;
		FileWriter jsonFileName = new FileWriter("sports_es.json", true);
		FileWriter jsonFileNameRaw = new FileWriter("sports_es_raw.json", true);
		FileWriter jsonAppendAll =  new FileWriter("AdditionTweets.json", true);
		ArrayList<Status> tweets = new ArrayList<Status>();
		while (tweets.size() < count) {
			if (count - tweets.size() > 100)
				q.setCount(100);
			else
				q.setCount(count - tweets.size());
			size = tweets.size();
			result = twitter.search(q);
			String json = "";
			String hashtag = "", mentions = "", URL = "", emoticons = "", temp = "";
			for (Status s : result.getTweets()) {
				String rawJson = TwitterObjectFactory.getRawJSON(s);
				System.out.println(rawJson);
				hashtag = "";
				mentions = "";
				URL = "";
				emoticons = "";
				temp = "";
				String tweetText = s.getText();
				try{
				for (String emoticon : EMOTICON_UNICODE_LIST) {
					if (tweetText.contains(emoticon) && !emoticon.equals("")) {
						while(tweetText.contains(emoticons)){
							tweetText = tweetText.replaceFirst(emoticon, "");
							emoticons += "\"" + emoticon + "\",";
						}
					}
				}
				for (String kaomoji : KAOMOJI_UNICODE_LIST) {
					if (tweetText.contains(kaomoji) && !kaomoji.equals("")) {
						
							tweetText = tweetText.replaceAll(kaomoji, "");
							emoticons += "\"" + kaomoji + "\",";
					}
				}
				tweetText = tweetText.replaceAll("\n", "").replaceAll("\"", "").replaceAll("\'", "")
						.replaceAll("“", "").replaceAll("”", "").replaceAll("}", "")
						.replaceAll("\\)", "").replaceAll("\\(", "").replaceAll("\\[","").replaceAll("\\]", "")
						.replaceAll("[!\\\"$%&'()*+,;<=>?[\\\\]^`{|}~\\u201C\\u201D\\r\\n]", "") + "        ";

				if(emoticons.length() > 0){
					emoticons = emoticons.substring(0, emoticons.length() - 1);
				}
				if (tweetText.contains("#")) {
					while (tweetText.contains("#")) {
						temp = tweetText.substring(tweetText.indexOf("#"),tweetText.indexOf(" ", tweetText.indexOf("#")));
						for(String temp1 : temp.split("#")){
							if(!temp1.equals(""))
								hashtag += "\"" + temp1 + "\",";
						}
						tweetText = tweetText.replaceAll(temp + " ", "");
					}
					if(hashtag.length() > 0){
						hashtag = hashtag.substring(0, hashtag.length() -1);
					}
				}	
				if (tweetText.contains("@")) {
					while (tweetText.contains("@")) {
						temp = tweetText.substring(tweetText.indexOf("@"),
								tweetText.indexOf(" ", tweetText.indexOf("@")));
						for(String temp1 : temp.split("@")){
							if(!temp1.equals(""))
								mentions += "\"" + temp1 + "\",";
						}
						tweetText = tweetText.replaceAll(temp + " ", "");
					}
					if(mentions.length() > 1){
						mentions = mentions.substring(0, mentions.length() -1);
					}
				}
				if (tweetText.contains("http")) {
					while (tweetText.contains("http")) {
						temp = tweetText.substring(tweetText.indexOf("http"),
								tweetText.indexOf(" ", tweetText.indexOf("http")));
						URL += "\"" + temp + "\",";
						tweetText = tweetText.replaceAll(temp + " ", "");

					}
					if(URL.length() > 1){
						URL = URL.substring(0, URL.length() -1);
					}
				} 
				
				tweetText = tweetText.replaceAll("[!\\\"#$%&'()*+,-./:;<=>?@[\\\\]^_`{|}~\\u201C\\u201D\\r\\n]", "")
						+ "           ";
				String tweetText_raw = s.getText().replaceAll("\n", " ").replaceAll("\"", "").replaceAll("\'", "")
						.replaceAll("“", "").replaceAll("”", "");
				if (s.getLang().compareTo("en") == 0) {
					json = "{\"id\":" + s.getId() + ",\"topic\":\"" + Topic +"\""+ ",\"tweet_text\":\"" + tweetText_raw.trim()
							+ "\",\"tweet_lang\":\"" + s.getLang() + "\",\"text_en\":\"" + tweetText.trim() + "\","
							+ "\"text_es\":\"\"," + "\"text_ko\":\"\"," + "\"text_tr\":\"\",";
					rawJson=rawJson.substring(0,rawJson.length()-1)+",\"topic\":\"" + Topic +"\""+ ",\"tweet_text\":\"" + tweetText_raw.trim()
					+ "\",\"tweet_lang\":\"" + s.getLang() + "\",\"text_en\":\"" + tweetText.trim() + "\","
					+ "\"text_es\":\"\"," + "\"text_ko\":\"\"," + "\"text_tr\":\"\",";
					
				} else if (s.getLang().compareTo("es") == 0) {
					json ="{\"id\":" + s.getId() + ",\"topic\":\"" + Topic +"\"" + ",\"tweet_text\":\"" + tweetText_raw.trim()
							+ "\",\"tweet_lang\":\"" + s.getLang() + "\",\"text_en\":\"\"," + "\"text_es\":\""
							+ tweetText.trim() + "\",\"text_ko\":\"\"," + "\"text_tr\":\"\",";
					rawJson=rawJson.substring(0,rawJson.length()-1)+"\"topic\":\"" + Topic +"\"" + ",\"tweet_text\":\"" + tweetText_raw.trim()
					+ "\",\"tweet_lang\":\"" + s.getLang() + "\",\"text_en\":\"\"," + "\"text_es\":\""
					+ tweetText.trim() + "\",\"text_ko\":\"\"," + "\"text_tr\":\"\",";
					
				} else if (s.getLang().compareTo("ko") == 0) {
					json = "{\"id\":" + s.getId() + ",\"topic\":\"" + Topic +"\"" + ",\"tweet_text\":\"" + tweetText_raw.trim()
							+ "\",\"tweet_lang\":\"" + s.getLang() + "\",\"text_en\":\"\"," + "\"text_es\":\"\","
							+ "\"text_ko\":\"" + tweetText.trim() + "\",\"text_tr\":\"\",";
					rawJson=rawJson.substring(0,rawJson.length()-1)+"\"topic\":\"" + Topic +"\"" + ",\"tweet_text\":\"" + tweetText_raw.trim()
							+ "\",\"tweet_lang\":\"" + s.getLang() + "\",\"text_en\":\"\"," + "\"text_es\":\"\","
							+ "\"text_ko\":\"" + tweetText.trim() + "\",\"text_tr\":\"\",";
					
				} else if (s.getLang().compareTo("tr") == 0) {
					json ="{\"id\":" + s.getId() + ",\"topic\":\"" + Topic +"\"" + ",\"tweet_text\":\"" + tweetText_raw.trim()
							+ "\",\"tweet_lang\":\"" + s.getLang() + "\",\"text_en\":\"\"," + "\"text_es\":\"\","
							+ "\"text_ko\":\"\"," + "\"text_tr\":\"" + tweetText.trim() + "\",";
					rawJson=rawJson.substring(0,rawJson.length()-1)+",\"topic\":\"" + Topic +"\"" + ",\"tweet_text\":\"" + tweetText_raw.trim()
							+ "\",\"tweet_lang\":\"" + s.getLang() + "\",\"text_en\":\"\"," + "\"text_es\":\"\","
							+ "\"text_ko\":\"\"," + "\"text_tr\":\"" + tweetText.trim() + "\",";
					
					
				} else {
					json = "{\"id\":" + s.getId() + ",\"topic\":\"" + Topic +"\"" + ",\"tweet_text\":\"" + tweetText_raw.trim()
							+ "\",\"tweet_lang\":\"" + s.getLang() + "\",\"text_en\":\"\"," + "\"text_es\":\"\","
							+ "\"text_ko\":\"\"," + "\"text_tr\":\"\",";
					rawJson=rawJson.substring(0,rawJson.length()-1)+",\"topic\":\"" + Topic +"\"" + ",\"tweet_text\":\"" + tweetText_raw.trim()
							+ "\",\"tweet_lang\":\"" + s.getLang() + "\",\"text_en\":\"\"," + "\"text_es\":\"\","
							+ "\"text_ko\":\"\"," + "\"text_tr\":\"\",";
					
					
				}
				json += "\"hashtags\":[" + hashtag.trim() + "],\"mentions\":[" + mentions.trim() + "],\"tweet_urls\":["
						+ URL.trim() + "],\"tweet_emoticons\":[" + emoticons.trim() + "],\"tweet_date\":\"" 
						+ dateConvertor(LocalDateTime.ofInstant(s.getCreatedAt().toInstant(),ZoneId.systemDefault())) 
						+ "\",\"tweet_location\":\"";
				
				rawJson+="\"tweet_emoticons\":[" + emoticons.trim() + "],\"tweet_date\":\"" 
						+ dateConvertor(LocalDateTime.ofInstant(s.getCreatedAt().toInstant(),ZoneId.systemDefault())) 
						+ "\",\"tweet_location\":\"";
				
				if(s.getGeoLocation() == null){
					json += "\"}";
					rawJson += "\"}";
					
				}else{
					json += String.valueOf(s.getGeoLocation().getLatitude()) + "," + String.valueOf(s.getGeoLocation().getLongitude()) + "\"}";
					rawJson += String.valueOf(s.getGeoLocation().getLatitude()) + "," + String.valueOf(s.getGeoLocation().getLongitude()) + "\"}";
				}	}
				catch(Exception e){
					
				}
				
				//rawJson=rawJson.subString(0,rawJson.length()-1)+"    "+"}";
				jsonFileName.write(json.toString() + "\n");
				jsonFileNameRaw.write(rawJson + "\n");
				//jsonAppendAll.write(json.toString() + "\n");
				System.out.println(json);
			}
			tweets.addAll(result.getTweets());
			if (tweets.size() == size) {
				break;
			}
			for (Status t : tweets)
				if (t.getId() < lastID)
					lastID = t.getId();
			q.setMaxId(lastID - 1);
		}
		System.out.println("tweets.size() ---->  " + tweets.size());
		jsonFileName.close();
		jsonFileNameRaw.close();
		jsonAppendAll.close();
		
		return tweets.size();
	}

	void Statistics(String topic, String lang, String DateSince, String DateTill, int totalNumberOfTweets)
			throws IOException {
	}

	private static String dateConvertor(LocalDateTime date){
		if(date.getMinute() > 30)
			date = date.plusHours(1);
		date = date.withMinute(0);
		date = date.withSecond(0);
		DateTimeFormatter ldf = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
		return String.valueOf(date.format(ldf));
	}
	
	private static String[] getUnicodeList() {
		String unicodeString = "\ud83c\uddec\ud83c\udde7#\ud83c\uddfa\ud83c\uddf8#\ud83c\udde9\ud83c\uddea#\ud83c\uddea\ud83c\uddf8#"
				+ "\ud83c\uddeb\ud83c\uddf7#\ud83c\udde8\ud83c\uddf3#\ud83c\uddee\ud83c\uddf9#\ud83c\uddef\ud83c\uddf5#"
				+ "\ud83c\uddf0\ud83c\uddf7#\ud83c\uddf7\ud83c\uddfa#\ud83d\udc7a#\ud83c\udd94#\ud83c\udd95#\ud83c\udd96#"
				+ "\ud83c\udd97#\ud83c\udd98#\ud83c\udd99#\ud83c\udd9a#\ud83c\udde6#\ud83c\udde7#\ud83d\udc83#\ud83c\udde8#"
				+ "\ud83c\udccf#\ud83c\udde9#\ud83c\udd70#\ud83c\uddea#\ud83c\udd71#\ud83c\uddeb#\ud83c\udd7e#\ud83c\uddec#"
				+ "\ud83c\udded#\ud83c\uddee#\ud83c\udd8e#\ud83c\uddef#\ud83c\udd91#\ud83c\uddf0#\ud83c\uddf1#\ud83c\uddf2#"
				+ "\ud83c\uddf3#\ud83c\uddf4#\ud83c\uddf5#\ud83c\uddf6#\ud83c\udd92#\ud83c\uddf7#\ud83c\uddf8#\ud83c\uddf9#"
				+ "\ud83c\udd93#\ud83c\uddfa#\ud83c\uddfb#\ud83c\uddfc#\ud83c\uddfd#\ud83c\uddfe#\ud83c\uddff#\ud83c\ude01#"
				+ "\ud83c\ude02#\ud83c\ude32#\ud83c\ude33#\ud83c\ude34#\ud83c\ude35#\ud83c\ude36#\ud83c\ude37#\ud83c\ude38#"
				+ "\ud83c\ude39#\ud83c\ude3a#\ud83c\ude50#\ud83c\ude51#\ud83c\udf00#\ud83c\udf01#\ud83c\udf02#\ud83c\udf03#"
				+ "\ud83c\udf04#\ud83c\udf05#\ud83c\udf06#\ud83c\udf07#\ud83c\udf08#\ud83c\udf09#\ud83c\udf0a#\ud83c\udf0b#"
				+ "\ud83c\udf0c#\ud83c\udf0d#\ud83c\udf0e#\ud83c\udf0f#\ud83c\udf10#\ud83c\udf11#\ud83c\udf12#\ud83c\udf13#"
				+ "\ud83c\udf14#\ud83c\udf15#\ud83c\udf16#\ud83c\udf17#\ud83c\udf18#\ud83c\udf19#\ud83c\udf1a#\ud83c\udf1b#"
				+ "\ud83c\udf1c#\ud83c\udf1d#\ud83c\udf1e#\ud83c\udf1f#\ud83c\udf20#\ud83c\udf30#\ud83c\udf31#\ud83c\udf32#"
				+ "\ud83c\udf33#\ud83c\udf34#\ud83c\udf35#\ud83c\udf37#\ud83c\udf38#\ud83c\udf39#\ud83c\udf3a#\ud83c\udf3b#"
				+ "\ud83c\udf3c#\ud83c\udf3d#\ud83c\udf3e#\ud83c\udf3f#\ud83c\udf40#\ud83c\udf41#\ud83c\udf42#\ud83c\udf43#"
				+ "\ud83c\udf44#\ud83c\udf45#\ud83c\udf46#\ud83c\udf47#\ud83c\udf48#\ud83c\udf49#\ud83c\udf4a#\ud83c\udf4b#"
				+ "\ud83c\udf4c#\ud83c\udf4d#\ud83c\udf4e#\ud83c\udf4f#\ud83c\udf50#\ud83c\udf51#\ud83c\udf52#\ud83c\udf53#"
				+ "\ud83c\udf54#\ud83c\udf55#\ud83c\udf56#\ud83c\udf57#\ud83c\udf58#\ud83c\udf59#\ud83c\udf5a#\ud83c\udf5b#"
				+ "\ud83c\udf5c#\ud83c\udf5d#\ud83c\udf5e#\ud83c\udf5f#\ud83c\udf60#\ud83c\udf61#\ud83c\udf62#\ud83c\udf63#"
				+ "\ud83c\udf64#\ud83c\udf65#\ud83c\udf66#\ud83c\udf67#\ud83c\udf68#\ud83c\udf69#\ud83c\udf6a#\ud83c\udf6b#"
				+ "\ud83c\udf6c#\ud83c\udf6d#\ud83c\udf6e#\ud83c\udf6f#\ud83c\udf70#\ud83c\udf71#\ud83c\udf72#\ud83c\udf73#"
				+ "\ud83c\udf74#\ud83c\udf75#\ud83c\udf76#\ud83c\udf77#\ud83c\udf78#\ud83c\udf79#\ud83c\udf7a#\ud83c\udf7b#"
				+ "\ud83c\udf7c#\ud83c\udf80#\ud83c\udf81#\ud83c\udf82#\ud83c\udf83#\ud83c\udf84#\ud83c\udf85#\ud83c\udf86#"
				+ "\ud83c\udf87#\ud83c\udf88#\ud83c\udf89#\ud83c\udf8a#\ud83c\udf8b#\ud83c\udf8c#\ud83c\udf8d#\ud83c\udf8e#"
				+ "\ud83c\udf8f#\ud83c\udf90#\ud83c\udf91#\ud83c\udf92#\ud83c\udf93#\ud83c\udfa0#\ud83c\udfa1#\ud83c\udfa2#"
				+ "\ud83c\udfa3#\ud83c\udfa4#\ud83c\udfa5#\ud83c\udfa6#\ud83c\udfa7#\ud83c\udfa8#\ud83c\udfa9#\ud83c\udfaa#"
				+ "\ud83c\udfab#\ud83c\udfac#\ud83c\udfad#\ud83c\udfae#\ud83c\udfaf#\ud83c\udfb0#\ud83c\udfb1#\ud83c\udfb2#"
				+ "\ud83c\udfb3#\ud83c\udfb4#\ud83c\udfb5#\ud83c\udfb6#\ud83c\udfb7#\ud83c\udfb8#\ud83c\udfb9#\ud83c\udfba#"
				+ "\ud83c\udfbb#\ud83c\udfbc#\ud83c\udfbd#\ud83c\udfbe#\ud83c\udfbf#\ud83c\udfc0#\ud83c\udfc1#\ud83c\udfc2#"
				+ "\ud83c\udfc3#\ud83c\udfc4#\ud83c\udfc6#\ud83c\udfc7#\ud83c\udfc8#\ud83c\udfc9#\ud83c\udfca#\ud83c\udfe0#"
				+ "\ud83c\udfe1#\ud83c\udfe2#\ud83c\udfe3#\ud83c\udfe4#\ud83c\udfe5#\ud83c\udfe6#\ud83c\udfe7#\ud83c\udfe8#"
				+ "\ud83c\udfe9#\ud83c\udfea#\ud83c\udfeb#\ud83c\udfec#\ud83c\udfed#\ud83c\udfee#\ud83c\udfef#\ud83c\udff0#"
				+ "\ud83d\udc00#\ud83d\udc01#\ud83d\udc02#\ud83d\udc03#\ud83d\udc04#\ud83d\udc05#\ud83d\udc06#\ud83d\udc07#"
				+ "\ud83d\udc08#\ud83d\udc09#\ud83d\udc0a#\ud83d\udc0b#\ud83d\udc0c#\ud83d\udc0d#\ud83d\udc0e#\ud83d\udc0f#"
				+ "\ud83d\udc10#\ud83d\udc11#\ud83d\udc12#\ud83d\udc13#\ud83d\udc14#\ud83d\udc15#\ud83d\udc16#\ud83d\udc17#"
				+ "\ud83d\udc18#\ud83d\udc19#\ud83d\udc1a#\ud83d\udc1b#\ud83d\udc1c#\ud83d\udc1d#\ud83d\udc1e#\ud83d\udc1f#"
				+ "\ud83d\udc20#\ud83d\udc21#\ud83d\udc22#\ud83d\udc23#\ud83d\udc24#\ud83d\udc25#\ud83d\udc26#\ud83d\udc27#"
				+ "\ud83d\udc28#\ud83d\udc29#\ud83d\udc2a#\ud83d\udc2b#\ud83d\udc2c#\ud83d\udc2d#\ud83d\udc2e#\ud83d\udc2f#"
				+ "\ud83d\udc30#\ud83d\udc31#\ud83d\udc32#\ud83d\udc33#\ud83d\udc34#\ud83d\udc35#\ud83d\udc36#\ud83d\udc37#"
				+ "\ud83d\udc38#\ud83d\udc39#\ud83d\udc3a#\ud83d\udc3b#\ud83d\udc3c#\ud83d\udc3d#\ud83d\udc3e#\ud83d\udc40#"
				+ "\ud83d\udc42#\ud83d\udc43#\ud83d\udc44#\ud83d\udc45#\ud83d\udc46#\ud83d\udc47#\ud83d\udc48#\ud83d\udc49#"
				+ "\ud83d\udc4a#\ud83d\udc4b#\ud83d\udc4c#\ud83d\udc4d#\ud83d\udc4e#\ud83d\udc4f#\ud83d\udc50#\ud83d\udc51#"
				+ "\ud83d\udc52#\ud83d\udc53#\ud83d\udc54#\ud83d\udc55#\ud83d\udc56#\ud83d\udc57#\ud83d\udc58#\ud83d\udc59#"
				+ "\ud83d\udc5a#\ud83d\udc5b#\ud83d\udc5c#\ud83d\udc5d#\ud83d\udc5e#\ud83d\udc5f#\ud83d\udc60#\ud83d\udc61#"
				+ "\ud83d\udc62#\ud83d\udc63#\ud83d\udc64#\ud83d\udc65#\ud83d\udc66#\ud83d\udc67#\ud83d\udc68#\ud83d\udc69#"
				+ "\ud83d\udc6a#\ud83d\udc6b#\ud83d\udc6c#\ud83d\udc6d#\ud83d\udc6e#\ud83d\udc6f#\ud83d\udc70#\ud83d\udc71#"
				+ "\ud83d\udc72#\ud83d\udc73#\ud83d\udc74#\ud83d\udc75#\ud83d\udc76#\ud83d\udc77#\ud83d\udc78#\ud83d\udc79#"
				+ "\ud83d\udc7b#\ud83d\udc7c#\ud83d\udc7d#\ud83d\udc7e#\ud83d\udc7f#\ud83d\udc80#\ud83d\udc81#\ud83d\udc82#"
				+ "\ud83d\udc84#\ud83d\udc85#\ud83d\udc86#\ud83d\udc87#\ud83d\udc88#\ud83d\udc89#\ud83d\udc8a#\ud83d\udc8b#"
				+ "\ud83d\udc8c#\ud83d\udc8d#\ud83d\udc8e#\ud83d\udc8f#\ud83d\udc90#\ud83d\udc91#\ud83d\udc92#\ud83d\udc93#"
				+ "\ud83d\udc94#\ud83d\udc95#\ud83d\udc96#\ud83d\udc97#\ud83d\udc98#\ud83d\udc99#\ud83d\udc9a#\ud83d\udc9b#"
				+ "\ud83d\udc9c#\ud83d\udc9d#\ud83d\udc9e#\ud83d\udc9f#\ud83d\udca0#\ud83d\udca1#\ud83d\udca2#\ud83d\udca3#"
				+ "\ud83d\udca4#\ud83d\udca5#\ud83d\udca6#\ud83d\udca7#\ud83d\udca8#\ud83d\udca9#\ud83d\udcaa#\ud83d\udcab#"
				+ "\ud83d\udcac#\ud83d\udcad#\ud83d\udcae#\ud83d\udcaf#\ud83d\udcb0#\ud83d\udcb1#\ud83d\udcb2#\ud83d\udcb3#"
				+ "\ud83d\udcb4#\ud83d\udcb5#\ud83d\udcb6#\ud83d\udcb7#\ud83d\udcb8#\ud83d\udcb9#\ud83d\udcba#\ud83d\udcbb#"
				+ "\ud83d\udcbc#\ud83d\udcbd#\ud83d\udcbe#\ud83d\udcbf#\ud83d\udcc0#\ud83d\udcc1#\ud83d\udcc2#\ud83d\udcc3#"
				+ "\ud83d\udcc4#\ud83d\udcc5#\ud83d\udcc6#\ud83d\udcc7#\ud83d\udcc8#\ud83d\udcc9#\ud83d\udcca#\ud83d\udccb#"
				+ "\ud83d\udccc#\ud83d\udccd#\ud83d\udcce#\ud83d\udccf#\ud83d\udcd0#\ud83d\udcd1#\ud83d\udcd2#\ud83d\udcd3#"
				+ "\ud83d\udcd4#\ud83d\udcd5#\ud83d\udcd6#\ud83d\udcd7#\ud83d\udcd8#\ud83d\udcd9#\ud83d\udcda#\ud83d\udcdb#"
				+ "\ud83d\udcdc#\ud83d\udcdd#\ud83d\udcde#\ud83d\udcdf#\ud83d\udce0#\ud83d\udce1#\ud83d\udce2#\ud83d\udce3#"
				+ "\ud83d\udce4#\ud83d\udce5#\ud83d\udce6#\ud83d\udce7#\ud83d\udce8#\ud83d\udce9#\ud83d\udcea#\ud83d\udceb#"
				+ "\ud83d\udcec#\ud83d\udced#\ud83d\udcee#\ud83d\udcef#\ud83d\udcf0#\ud83d\udcf1#\ud83d\udcf2#\ud83d\udcf3#"
				+ "\ud83d\udcf4#\ud83d\udcf5#\ud83d\udcf6#\ud83d\udcf7#\ud83d\udcf9#\ud83d\udcfa#\ud83d\udcfb#\ud83d\udcfc#"
				+ "\ud83d\udd00#\ud83d\udd01#\ud83d\udd02#\ud83d\udd03#\ud83d\udd04#\ud83d\udd05#\ud83d\udd06#\ud83d\udd07#"
				+ "\ud83d\udd08#\ud83d\udd09#\ud83d\udd0a#\ud83d\udd0b#\ud83d\udd0c#\ud83d\udd0d#\ud83d\udd0e#\ud83d\udd0f#"
				+ "\ud83d\udd10#\ud83d\udd11#\ud83d\udd12#\ud83d\udd13#\ud83d\udd14#\ud83d\udd15#\ud83d\udd16#\ud83d\udd17#"
				+ "\ud83d\udd18#\ud83d\udd19#\ud83d\udd1a#\ud83d\udd1b#\ud83d\udd1c#\ud83d\udd1d#\ud83d\udd1e#\ud83d\udd1f#"
				+ "\ud83d\udd20#\ud83d\udd21#\ud83d\udd22#\ud83d\udd23#\ud83d\udd24#\ud83d\udd25#\ud83d\udd26#\ud83d\udd27#"
				+ "\ud83d\udd28#\ud83d\udd29#\ud83d\udd2a#\ud83d\udd2b#\ud83d\udd2c#\ud83d\udd2d#\ud83d\udd2e#\ud83d\udd2f#"
				+ "\ud83d\udd30#\ud83d\udd31#\ud83d\udd32#\ud83d\udd33#\ud83d\udd34#\ud83d\udd35#\ud83d\udd36#\ud83d\udd37#"
				+ "\ud83d\udd38#\ud83d\udd39#\ud83d\udd3a#\ud83d\udd3b#\ud83d\udd3c#\ud83d\udd3d#\ud83d\udd50#\ud83d\udd51#"
				+ "\ud83d\udd52#\ud83d\udd53#\ud83d\udd54#\ud83d\udd55#\ud83d\udd56#\ud83d\udd57#\ud83d\udd58#\ud83d\udd59#"
				+ "\ud83d\udd5a#\ud83d\udd5b#\ud83d\udd5c#\ud83d\udd5d#\ud83d\udd5e#\ud83d\udd5f#\ud83d\udd60#\ud83d\udd61#"
				+ "\ud83d\udd62#\ud83d\udd63#\ud83d\udd64#\ud83d\udd65#\ud83d\udd66#\ud83d\udd67#\ud83d\uddfb#\ud83d\uddfc#"
				+ "\ud83d\uddfd#\ud83d\uddfe#\ud83d\uddff#\ud83d\ude00#\ud83d\ude01#\ud83d\ude02#\ud83d\ude03#\ud83d\ude04#"
				+ "\ud83d\ude05#\ud83d\ude06#\ud83d\ude07#\ud83d\ude08#\ud83d\ude09#\ud83d\ude0a#\ud83d\ude0b#\ud83d\ude0c#"
				+ "\ud83d\ude0d#\ud83d\ude0e#\ud83d\ude0f#\ud83d\ude10#\ud83d\ude11#\ud83d\ude12#\ud83d\ude13#\ud83d\ude14#"
				+ "\ud83d\ude15#\ud83d\ude16#\ud83d\ude17#\ud83d\ude18#\ud83d\ude19#\ud83d\ude1a#\ud83d\ude1b#\ud83d\ude1c#"
				+ "\ud83d\ude1d#\ud83d\ude1e#\ud83d\ude1f#\ud83d\ude20#\ud83d\ude21#\ud83d\ude22#\ud83d\ude23#\ud83d\ude24#"
				+ "\ud83d\ude25#\ud83d\ude26#\ud83d\ude27#\ud83d\ude28#\ud83d\ude29#\ud83d\ude2a#\ud83d\ude2b#\ud83d\ude2c#"
				+ "\ud83d\ude2d#\ud83d\ude2e#\ud83d\ude2f#\ud83d\ude30#\ud83d\ude31#\ud83d\ude32#\ud83d\ude33#\ud83d\ude34#"
				+ "\ud83d\ude35#\ud83d\ude36#\ud83d\ude37#\ud83d\ude38#\ud83d\ude39#\ud83d\ude3a#\ud83d\ude3b#\ud83d\ude3c#"
				+ "\ud83d\ude3d#\ud83d\ude3e#\ud83d\ude3f#\ud83d\ude40#\ud83d\ude45#\ud83d\ude46#\ud83d\ude47#\ud83d\ude48#"
				+ "\ud83d\ude49#\ud83d\ude4a#\ud83d\ude4b#\ud83d\ude4c#\ud83d\ude4d#\ud83d\ude4e#\ud83d\ude4f#\ud83d\ude80#"
				+ "\ud83d\ude81#\ud83d\ude82#\ud83d\ude83#\ud83d\ude84#\ud83d\ude85#\ud83d\ude86#\ud83d\ude87#\ud83d\ude88#"
				+ "\ud83d\ude89#\ud83d\ude8a#\ud83d\ude8b#\ud83d\ude8c#\ud83d\ude8d#\ud83d\ude8e#\ud83d\ude8f#\ud83d\ude90#"
				+ "\ud83d\ude91#\ud83d\ude92#\ud83d\ude93#\ud83d\ude94#\ud83d\ude95#\ud83d\ude96#\ud83d\ude97#\ud83d\ude98#"
				+ "\ud83d\ude99#\ud83d\ude9a#\ud83d\ude9b#\ud83d\ude9c#\ud83d\ude9d#\ud83d\ude9e#\ud83d\ude9f#\ud83d\udea0#"
				+ "\ud83d\udea1#\ud83d\udea2#\ud83d\udea3#\ud83d\udea4#\ud83d\udea5#\ud83d\udea6#\ud83d\udea7#\ud83d\udea8#"
				+ "\ud83d\udea9#\ud83d\udeaa#\ud83d\udeab#\ud83d\udeac#\ud83d\udead#\ud83d\udeae#\ud83d\udeaf#\ud83d\udeb0#"
				+ "\ud83d\udeb1#\ud83d\udeb2#\ud83d\udeb3#\ud83d\udeb4#\ud83d\udeb5#\ud83d\udeb6#\ud83d\udeb7#\ud83d\udeb8#"
				+ "\ud83d\udeb9#\ud83d\udeba#\ud83d\udebb#\ud83d\udebc#\ud83d\udebd#\ud83d\udebe#\ud83d\udebf#\ud83d\udec0#"
				+ "\ud83d\udec1#\ud83d\udec2#\ud83d\udec3#\ud83d\udec4#\ud83d\udec5#\u0023\u20e3#\u0030\u20e3#\u0031\u20e3#"
				+ "\u0032\u20e3#\u0033\u20e3#\u0034\u20e3#\u0035\u20e3#\u0036\u20e3#\u0037\u20e3#\u0038\u20e3#\u0039\u20e3#"
				+ "\u3030#\u2705#\u2728#\u2122#\u23e9#\u23ea#\u23eb#\u23ec#\u23f0#\u23f3#\u26ce#\u270a#\u270b#\u274c#\u274e#"
				+ "\u27b0#\u27bf#\u2753#\u2754#\u2755#\u2795#\u2796#\u2797#\u00a9#\u00ae#\ue50a#"
				+ "\ud83c\udd7f#\ud83c\ude1a#\ud83c\ude2f#\ud83c\udc04#\u2935#\u3297#\u3299#\u2049#\u2139#\u2194#\u2195#"
				+ "\u2196#\u2197#\u2198#\u2199#\u2600#\u2601#\u2611#\u2614#\u2615#\u2648#\u2649#\u2650#\u2651#\u2652#\u2653#"
				+ "\u2660#\u2663#\u2665#\u2666#\u2668#\u2693#\u2702#\u2708#\u2709#\u2712#\u2714#\u2716#\u2733#\u2734#\u203c#"
				+ "\u21a9#\u21aa#\u2744#\u231a#\u231b#\u24c2#\u25aa#\u25ab#\u25b6#\u25c0#\u25fb#\u25fc#\u25fd#\u25fe#\u260e#"
				+ "\u261d#\u263a#\u264a#\u264b#\u264c#\u264d#\u264e#\u264f#\u267b#\u267f#\u26a0#\u26a1#\u26aa#\u26ab#\u26bd#"
				+ "\u26be#\u26c4#\u26c5#\u26d4#\u26ea#\u26f2#\u26f3#\u26f5#\u26fa#\u26fd#\u270c#\u270f#\u27a1#\u2b05#\u2b06#"
				+ "\u2b07#\u2b1b#\u2b1c#\u2b50#\u2b55#\u2747#\u303d#\u2757#\u2764#\u2934#([\ufe0e\ufe0f]?)";
		return unicodeString.split("#");
	}
	
	private static String[] getKaomojiCodes(){
		return new String("\u00AF\\_(\u30C4)_/\u00AF55\\*-*/55\\ \u02D9\u25BF\uFE0E\u02D9 /55\\^0^/55(*^\u03C9^)55(o^\u25BD^o)55\u30FD(\u30FB\u2200\u30FB)\uFF8955(o\uFF65\u03C9\uFF65o)55(^\u4EBA^)55(\u00B4\u03C9\uFF40)55(\u00B4\u2022 \u03C9 \u2022`)55\u2570(\u2594\u2200\u2594)\u256F55(\u272F\u25E1\u272F)55(\u2312\u203F\u2312)55(*\uFF9F\u25BD\uFF9F*)55(\u00B4\uFF61\u2022 \u1D55 \u2022\uFF61`)55\u30FD(>\u2200<\u2606)\u30CE55\uFF3C(\uFFE3\u25BD\uFFE3)\uFF0F55(o\u02D8\u25E1\u02D8o)55(\u256F\u2727\u25BD\u2727)\u256F55(\u00B4\u2200\uFF40*)55(\u2312\u25BD\u2312)\u260655(\u00B4\uFF61\u2022 \u03C9 \u2022\uFF61`)55(\uFF20\uFF3E\uFF0D\uFF3E)55(o\u00B4\u25BD`o)55(((o(*\uFF9F\u25BD\uFF9F*)o)))55(\uFF3E\u25BD\uFF3E)55(\u2500\u203F\u203F\u2500)55(\u25D5\u203F\u25D5)55\uFF3C(\u2267\u25BD\u2266)\uFF0F55(\u2727\u2200\u2727)55( \u00B4 \u25BD ` )55o(\u2267\u25BD\u2266)o55(*\u00AF\uFE36\u00AF*)55\\(\u2605\u03C9\u2605)/55o(>\u03C9<)o55(-\u203F\u203F-)55<(\uFFE3\uFE36\uFFE3)>55(\uFFE3\u03C9\uFFE3)55\u30FD(*\u30FB\u03C9\u30FB)\uFF8955(*\u00B4\u25BD`*)55(\u2267\u25E1\u2266)55(\u2312\u03C9\u2312)55(*^\u203F^*)55(*\u2267\u03C9\u2266*)55\u2312(o\uFF3E\u25BD\uFF3Eo)\u30CE55(\u2727\u03C9\u2727)55(\uFFE3\u25BD\uFFE3)55(\u2606\u03C9\u2606)55\uFF3C(\uFF3E\u25BD\uFF3E)\uFF0F55\\(^\u30EE^)/55o( \u275B\u1D17\u275B )o55\u2606*:.\uFF61.o(\u2267\u25BD\u2266)o.\uFF61.:*\u260655\u3002.:\u2606*:\uFF65'(*\u2312\u2015\u2312*)))55\uFF40;:\u309B;\uFF40;\uFF65(\u309C\u03B5\u309C )55(o_ _)\uFF89\u5F61\u260655\uFF61\uFF9F( \uFF9F^\u2200^\uFF9F)\uFF9F\uFF6155(o\u00B4\u2200\uFF40o)55\u2211d(\uFF9F\u2200\uFF9Fd)55\u30FD(o^\u2015^o)\uFF8955(\u2606\u25BD\u2606)55\u2606 \uFF5E('\u25BD^\u4EBA)55\u30FD(*\u2312\u25BD\u2312*)\uFF8955\u2570(*\u00B4\uFE36`*)\u256F55(\u3063\u02D8\u03C9\u02D8\u03C2 )55\u0669(\u25D5\u203F\u25D5)\u06F655(\u3003\uFF3E\u25BD\uFF3E\u3003)55\uFF61\uFF9F(T\u30EET)\uFF9F\uFF6155(\uFF89\u00B4\u0437\uFF40)\u30CE55(\u2661-_-\u2661)55(\u2500\u203F\u203F\u2500)\u266155(\u00B4\u03C9\uFF40\u2661)55(\u10E6\u02D8\u2323\u02D8\u10E6)55(\u00B4\u2022 \u03C9 \u2022`) \u266155\u2570(*\u00B4\uFE36`*)\u256F\u266155(\u2267\u25E1\u2266) \u266155\u2661 (\u02D8\u25BD\u02D8>\u0505( \u02D8\u2323\u02D8)55\u03C3(\u2267\u03B5\u2266\u03C3) \u266155(\u02D8\u2200\u02D8)/(\u03BC\u203F\u03BC) \u276455(\u2661\u03BC_\u03BC)55(\uFFE3\u03B5\uFFE3\uFF20)55(\u00B4\uFF61\u2022 \u1D55 \u2022\uFF61`) \u266155( \u25E1\u203F\u25E1 \u2661)55(\u2661\uFF9F\u25BD\uFF9F\u2661)55(\u00B4\u03B5\uFF40 )\u266155(*\u02D8\uFE36\u02D8*).\uFF61.:*\u266155(\u2312\u25BD\u2312)\u266155( \u02D8\u2323\u02D8)\u2661(\u02D8\u2323\u02D8 )55\u2661 (\u21C0 3 \u21BC)55\u2764 (\u0254\u02C6\u0437(\u02C6\u2323\u02C6c)55(*^^*)\u266155\u30FD(\u2661\u203F\u2661)\u30CE55(*\u2661\u2200\u2661)55(\u25D5\u203F\u25D5)\u266155\u2661(\u3002-\u03C9-)55(\u00B4\uFF61\u2022 \u03C9 \u2022\uFF61`) \u266155(\u2661\u02D9\uFE36\u02D9\u2661)55(*\u00AF \u00B3\u00AF*)\u266155(/^-^(^ ^*)/ \u266155\u2661 (\uFFE3\u0417\uFFE3)55(\u00B4\u2661\u203F\u2661`)55\u2606\u2312\u30FD(*'\uFF64^*)chu55( \u00B4\u2200\uFF40)\u30CE\uFF5E \u266155(\uFF61\u30FB//\u03B5//\u30FB\uFF61)55(/\u25BD\uFF3C*)\uFF61o\u25CB\u266155\u2661 \uFF5E('\u25BD^\u4EBA)55( \u00B4 \u25BD ` ).\uFF61\uFF4F\u266155\u2661\uFF3C(\uFFE3\u25BD\uFFE3)\uFF0F\u266155(\u3063\u02D8\u0437(\u02D8\u2323\u02D8 ) \u266155\u0669(\u2661\u03B5\u2661)\u06F655(\u2764\u03C9\u2764)55(\u00B0\u25E1\u00B0\u2661)55(\u2312_\u2312;)55(*/_\uFF3C)55( \u25E1\u203F\u25E1 *)55(//\u03C9//)55(\uFFE3\u25BD\uFFE3*)\u309E55(o^ ^o)55(*\uFF89\u03C9\uFF89)55(\u1D54.\u1D54)55(\u30CE*\uFF9F\u25BD\uFF9F*)55(\u2044 \u2044\u2022\u2044\u03C9\u2044\u2022\u2044 \u2044)55(*/\u03C9\uFF3C)55(o-_-o)55(*\uFF89\u2200`*)55(*^.^*)55(*/\u25BD\uFF3C*)55(*/\u3002\uFF3C)55(*\u03BC_\u03BC)55(//\u25BD//)55(*\uFF89\u25BD\uFF89)55(\u2044 \u2044>\u2044 \u25BD \u2044<\u2044 \u2044)55(\u30CE_<\u3002)\u30FE(\u00B4\u25BD\uFF40)55\u30FD(\uFFE3\u03C9\uFFE3(\u3002\u3002 )\u309D55(\uFF89\uFF3F\uFF1B)\u30FE(\u00B4\u2200\uFF40)55(\u00B4-\u03C9-`( _ _ )55\uFF61\uFF65\uFF9F\uFF65(\uFF89\u0414`)\u30FD(\uFFE3\u03C9\uFFE3 )55(*\u00B4I\uFF40)\uFF89\uFF9F(\uFF89\u0414\uFF40\uFF9F)\uFF9F\uFF6155(\uFF1B\u03C9\uFF1B )\u30FE(\u00B4\u2200\uFF40* )55(\u3063\u00B4\u03C9\uFF40)\uFF89(\u2565\u03C9\u2565)55\u03C1(-\u03C9-\u3001)\u30FE(\uFFE3\u03C9\uFFE3; )55\u30FD(~_~(\u30FB_\u30FB )\u309D55(*\u00B4\u30FC)\uFF89(\u30CE\u0434`)55(\uFF4F\u30FB_\u30FB)\u30CE\u201D(\u30CE_<\u3001)55(\uFF03\uFF1E\uFF1C)55(\uFF1B\uFFE3\u0414\uFFE3)55(\uFFE2_\uFFE2;)55(\uFF3E\uFF3E\uFF03)55(\uFFE3\uFE3F\uFFE3)55\u30FE( \uFFE3O\uFFE3)\u30C455(\u15D2\u15E3\u15D5)\u055E55(\uFF1B\u2323\u0300_\u2323\u0301)55(\uFFE3\u25A1\uFFE3\u300D)55(\uFF1E\uFF4D\uFF1C)55(\uFE36\uFE39\uFE3A)55(\uFF1E\uFE4F\uFF1C)55(\u21C0\u2038\u21BC\u2036)55(\uB208_\uB208)55\u2606\uFF4F(\uFF1E\uFF1C\uFF1B)\u25CB55(\uFF03\uFFE30\uFFE3)55(\u300D\u309C\u30ED\u309C)\u300D55(\uFFE3\u30D8\uFFE3)55(--_--)55o(>< )o55(\uFFE3 \uFFE3|||)55(\uFF03\uFFE3\u03C9\uFFE3)55(\u3003\uFF1E\uFF3F\uFF1C;\u3003)55<(\uFFE3 \uFE4C \uFFE3)>55\u51F8(\uFFE3\u30D8\uFFE3)55(\u300D\uFF1E\uFF1C)\u300D55(\uFF03`\u0414\u00B4)55(\u30FB\uFF40\u03C9\u00B4\u30FB)55(\uFF40\u03B5\u00B4)55(\uFF92\uFF40\uFF9B\u00B4)55\u03A3(\u25BC\u25A1\u25BC\u30E1)55(\u0482 `\u0437\u00B4 )55\u0669(\u256C\u0298\u76CA\u0298\u256C)\u06F655\u2191_(\u03A6w\u03A6)\u03A855(\uFF40\u76BF\u00B4\uFF03)55(\uFF40\u30FC\u00B4)55\u03C8(\uFF40\u2207\u00B4)\u03C855(\u256C\uFF40\u76CA\u00B4)55(\u00B0\u3142\u00B0\u256C)55(\u2021\u25BC\u76CA\u25BC)55(\u256C \u00D2\uFE4F\u00D3)55\u2190~(\u03A8\u25BC\uFF70\u25BC)\u220855(\uFF40\u03C9\u00B4)55\u30FD(\uFF40\u2312\u00B4\u30E1)\u30CE55\u30FE(\uFF40\u30D8\u00B4)\uFF89\uFF9E55\u250C\u2229\u2510(\u25E3_\u25E2)\u250C\u2229\u251055\u03C8(\u25BC\u3078\u25BC\u30E1)\uFF5E\u219255(\u0482\uFF40\uFF9B\u00B4)\u51F855\uFF3C\uFF3C\u0669(\u0E51`^\u00B4\u0E51)\u06F6\uFF0F\uFF0F55\u0B67((#\u03A6\u76CA\u03A6#))\u0B6855\u30FD( `\u0434\u00B4*)\u30CE55\u51F8(\uFF40\u25B3\u00B4\uFF03)55\u30FD(\u2035\uFE4F\u2032)\u30CE55\u51F8(\uFF40\uFF9B\u00B4)\u51F855(\u30CE\u00B0\u76CA\u00B0)\u30CE55((\u256C\u25E3\uFE4F\u25E2))55(\u51F8\u0CA0\u76CA\u0CA0)\u51F855\u0669(\u0C20\u76CA\u0C20)\u06F655(\u30CE_<\u3002)55(\u03BC_\u03BC)55o(T\u30D8To)55( \uFF9F\uFF0C_\u309D\uFF40)55( \u2565\u03C9\u2565 )55(\uFF0F\u02CD\u30FB\u3001)55(\u3064\u03C9`*)55(T_T)55o(\u3012\uFE4F\u3012)o55(*-_-)55(\uFF89\u0414`)55(\uFF1B\u03C9\uFF1B)55(\u4E2A_\u4E2A)55(\u256F_\u2570)55(\u30CE_<\u3001)55(\uFF61T \u03C9 T\uFF61)55(>_<)55(\uFF61\u2022\u0301\uFE3F\u2022\u0300\uFF61)55(\u00B4-\u03C9-\uFF40)55(-\u03C9-\u3001)55(\uFF61\u256F3\u2570\uFF61)55(\u256F\uFE35\u2570,)55(\u2565_\u2565)55(\u2565\uFE4F\u2565)55(\uFF89\u03C9\uFF65\uFF64)55(\uFF34\u25BD\uFF34)55(\u0CA5\uFE4F\u0CA5)55.\uFF65\uFF9F\uFF9F\uFF65(\uFF0F\u03C9\uFF3C)\uFF65\uFF9F\uFF9F\uFF65.55\u3002\u309C\u309C(\u00B4\uFF2F\uFF40)\u00B0\u309C\u300255\uFF61\uFF65\uFF9F\uFF9F*(>\u0434<)*\uFF9F\uFF9F\uFF65\uFF6155\uFF61\uFF65\uFF9F(\uFF9F><\uFF9F)\uFF9F\uFF65\uFF6155.\uFF61\uFF65\uFF9F\uFF9F\uFF65(\uFF1E_\uFF1C)\uFF65\uFF9F\uFF9F\uFF65\uFF61.55\uFF61\uFF9F(\uFF61\uFF89\u03C9\u30FD\uFF61)\uFF9F\uFF6155\uFF65\uFF9F\uFF65(\uFF61>\u03C9<\uFF61)\uFF65\uFF9F\uFF6555\uFF61\uFF9F\uFF65 (>\uFE4F<) \uFF65\uFF9F\uFF6155(\u30CE\u03C9\u30FD)55(\u2033\u30ED\u309B)55(/\u03C9\uFF3C)55(((\uFF1E\uFF1C)))55(\uFF0F\u3002\uFF3C)55(;;;*_*)55(/_\uFF3C)55{{ (>_<) }}55(\uFF89_\u30FD)55(\u30FB\u4EBA\u30FB)55\u301C(\uFF1E\uFF1C)\u301C55\uFF3C(\u00BA \u25A1 \u00BA l|l)/55..\u30FB\u30FE(\u3002\uFF1E\uFF1C)\u30B755\uFF3C(\u3007_\uFF4F)\uFF0F55\u03A3(\u00B0\u25B3\u00B0|||)\uFE3455\u3023( \u00BA\u0394\u00BA )\u302355\u30FD(\u30FC_\u30FC )\u30CE55\u2510(\uFFE3\u30D8\uFFE3)\u250C55\u2510(\uFFE3\uFF5E\uFFE3)\u250C55\u2510(\u00B4\u0434\uFF40)\u250C55\u256E(\uFE36\u25BD\uFE36)\u256D55\u30FD(\u00B4\u30FC\uFF40)\u250C55\u30FD(\uFFE3\uFF5E\uFFE3\u3000)\u30CE55\u2510(\uFE36\u25BD\uFE36)\u250C55\u256E(\uFE36\uFE3F\uFE36)\u256D55\u256E( \u02D8 \uFF64 \u02D8 )\u256D55\u2510(\u2018\uFF5E` )\u250C55\u256E(\uFFE3_\uFFE3)\u256D55\u256E(\uFFE3\uFF5E\uFFE3)\u256D55\u2510(\uFFE3\u2200\uFFE3)\u250C55\u2510( \u02D8_\u02D8 )\u250C55\u30FD(\u3000\uFFE3\u0434\uFFE3)\u30CE55\u30FD(\u02C7\u30D8\u02C7)\u30CE55\u00AF\\_(\u30C4)_/\u00AF55\u2510( \u02D8 \uFF64 \u02D8 )\u250C55\u256E( \u02D8_\u02D8 )\u256D55(\uFFE3\u03C9\uFFE3;)55\u2510('\uFF5E`;)\u250C55(\u30FB_\u30FB;)55(\uFF20_\uFF20)55(\u2022\u0E34_\u2022\u0E34)?55\u03C3(\uFFE3\u3001\uFFE3\u3003)55(\u30FB_\u30FB\u30FE55(\uFFE3_\uFFE3)\u30FB\u30FB\u30FB55(\u30FB\u30FB;)\u309E55(\u25CE \u25CE)\u309E55(\uFFE3\uFF5E\uFFE3;)55(\u3003\uFFE3\u03C9\uFFE3\u3003\u309E55\u256E(\uFFE3\u03C9\uFFE3;)\u256D55\u03A3(\uFFE3\u3002\uFFE3\uFF89)55(\u30FC\u30FC;)55(-_-;)\u30FB\u30FB\u30FB55\u2510(\uFFE3\u30D8\uFFE3;)\u250C55(\uFFE3.\uFFE3;)55(\u30FB\u30FB ) ?55\u10DA(\u0CA0_\u0CA0 \u10DA)55(\uFFE2_\uFFE2)55(\u00AC_\u00AC )55(\u21BC_\u21BC)55(\u2192_\u2192)55(\u2190_\u2190)55(\u21C0_\u21C0)55(\uFFE2 \uFFE2)55(\u00AC \u00AC )55(\uFFE2\u203F\uFFE2 )55(\u00AC\u203F\u00AC )55w(\uFF9F\uFF4F\uFF9F)w55(\u2299_\u2299)55(\uFF9F\u30ED\uFF9F) !55\u2211(O_O;)55\u30FD(\uFF9F\u3007\uFF9F)\uFF8955(o_O)55(o_O) !55\u03A3(O_O)55(O_O;)55(\u25A1_\u25A1)55\u03A3(\uFF9F\u30ED\uFF9F)55(O.O)55\u03A3(\u25A1_\u25A1)55(*\u30FB\u03C9\u30FB)\uFF8955(^-^*)/55\u30FE(*'\u25BD'*)55(^\uFF10^)\u30CE55(*\uFF9F\uFF70\uFF9F)\uFF8955(\uFFE3\u03C9\uFFE3)/55(\u2267\u25BD\u2266)/55(\uFFE3\u25BD\uFFE3)\u30CE55(\uFF20\u00B4\u30FC`)\uFF89\uFF9E55\uFF3C(\u2312\u25BD\u2312)55~\u30FE(\u30FB\u03C9\u30FB)55(\u30FB_\u30FB)\u30CE55(\u00B4\u03C9\uFF40)\u30CE\uFF9E55(\u2727\u2200\u2727)/55(\uFF9F\u25BD\uFF9F)/55(\u00B4\u2022 \u03C9 \u2022`)\uFF8955\u30FE(\u2606\u25BD\u2606)55(\u30FB\u2200\u30FB)\u30CE55(o\u00B4\u03C9`o)\uFF8955(\u2312\u03C9\u2312)\uFF8955(o\u00B4\u25BD`o)\uFF8955(*\u00B4\u2200\uFF40)\uFF8955(\uFF9F\u2200\uFF9F)\uFF89\uFF9E55( \u00B4 \u25BD ` )\uFF8955\u30FE(^\u03C9^*)55\u30FE(\u2606'\u2200'\u2606)55(o^ ^o)/55(\uFFE3\u25BD\uFFE3)/55(^_~)55(>\u03C9^)55(^_<)\u301C\u260655(^_<)55( \uFF9F\uFF4F\u2312)55(~\u4EBA^)55(^\u4EBA<)\u301C\u260655(^_\u2212)\u260655(^_-)\u2261\u260655(^_-)55\u2606\u2312(\u2267\u25BD\u200B\u00B0 )55(\uFF65\u03C9<)\u260655(^\u03C9~)55( -_\u30FB)55\u2606\u2312(\u309D\u3002\u2202)55m(_ _)m55\u4EBA(_ _*)55(\u30B7. .)\u30B755(\u30B7_ _)\u30B755(*_ _)\u4EBA55m(. .)m55m(_ _;m)55<(_ _)>55(m;_ _)m55(*\uFFE3ii\uFFE3)55(\uFF3E\u3003\uFF3E)55(\uFFE3\uFF8A\uFFE3*)55(\uFFE3 \u00A8\u30FD\uFFE3)55\\(\uFFE3\uFF8A\uFFE3)55(\uFFE3 ;\uFFE3)55(\uFF3E\u0F0B\u0F0D\uFF3E)55(\uFFE3 ;;\uFFE3)55|\uFF65\u03C9\uFF65)55|\u0434\uFF65)55\u252C\u2534\u252C\u2534\u2524\uFF65\u03C9\uFF65)\uFF8955|\uFF65\u0434\uFF65)\uFF8955\uFF8D(\uFF65_|55|_\uFFE3))55\u252C\u2534\u252C\u2534\u2524( \u0361\u00B0 \u035C\u0296\u251C\u252C\u2534\u252C\u253455|\u0298\u203F\u0298)\u256F55|\u03C9\uFF65)\uFF8955|\u25BD//)55\u252C\u2534\u252C\u2534\u2524(\uFF65_\u251C\u252C\u2534\u252C\u253455\u30FE(\uFF65|55\u252C\u2534\u252C\u2534\u2524(\uFF65_\u251C\u252C\u2534\u252C\u253455|_\u30FB)55__\u03C6(\uFF0E\uFF0E)55\u30FE( `\u30FC\u00B4)\u30B7\u03C6__55( ^\u25BD^)\u03C8__55( \uFFE3\u30FC\uFFE3)\u03C6__55__\u3006(\uFFE3\u30FC\uFFE3 )55....\u03C6(\uFE36\u25BD\uFE36)\u03C6....55__\u03C6(\u3002\u3002)55....\u03C6(\u30FB\u2200\u30FB*)55( . .)\u03C6__55__\u03C6(\uFF0E\uFF0E;)55___\u3006(\u30FB\u2200\u30FB)55__\u03C6(\u25CE\u25CE\u30D8)55\u2606\uFF90(o*\uFF65\u03C9\uFF65)\uFF8955\u03B5=\u03B5=\u03B5=\u03B5=\u250C(;\uFFE3\u25BD\uFFE3)\u251855\u03B5===(\u3063\u2267\u03C9\u2266)\u306355C= C= C= C= C=\u250C(;\u30FB\u03C9\u30FB)\u251855\u03B5=\u03B5=\u250C( >_<)\u251855\u30FD(\uFFE3\u0434\uFFE3;)\u30CE=3=3=355\u2500=\u2261\u03A3((( \u3064\uFF1E\uFF1C)\u306455C= C= C= C=\u250C(\uFF40\u30FC\u00B4)\u251855\u3002\u3002\u3002\u30DF\u30FD(\u3002\uFF1E\uFF1C)\u30CE55[(\uFF0D\uFF0D)]..zzZ55(\uFFE3o\uFFE3) zzZZzzZZ55(\uFF3F \uFF3F*) Z z z55(\uFF0D_\uFF0D) zzZ55(( _ _ ))..zzzZZ55(x . x) ~~zzZ55(\u222A\uFF61\u222A)\uFF61\uFF61\uFF61zzZ55(\uFFE3\u03C1\uFFE3)..zzZZ55(\uFF0D\u03C9\uFF0D) zzZ55(\uFF0D.\uFF0D)...zzz55(=^\uFF65\u03C9\uFF65^=)55(=\uFF1B\uFF6A\uFF1B=)55(=\u2312\u203F\u203F\u2312=)55(\uFF3E\u2022 \u03C9 \u2022\uFF3E)55\u0B32(\u24DB \u03C9 \u24DB)\u0B3255(=^\uFF65\uFF6A\uFF65^=)55(=\uFF40\u03C9\u00B4=)55(=^ \u25E1 ^=)55(/ =\u03C9=)/55(=\u2460\u03C9\u2460=)55(=^\u2025^=)55(=^-\u03C9-^=)55\u0E05(\u2022\u3145\u2022\u2740)\u0E0555( =\u03C9=)..nyaa55( =\u30CE\u03C9\u30FD=)55\u30FE(=\uFF40\u03C9\u00B4=)\u30CE\u201D55\u0E05(\u2022 \u026A \u2022)\u0E0555(\u00B4(\uFF74)\uFF40)55(\uFFE3(\uFF74)\uFFE3)55\u2282(\u00B4(\u30A7)\u02CB)\u228355\u0295 \u2022\u1D25\u2022 \u029455(*\uFFE3(\uFF74)\uFFE3*)55\u30FD(\uFF40(\uFF74)\u00B4)\uFF8955(/-(\uFF74)-\uFF3C)55\u0295 \u2022\u0300 \u03C9 \u2022\u0301 \u029455\u30FD(\uFFE3(\uFF74)\uFFE3)\uFF8955\u2282(\uFFE3(\uFF74)\uFFE3)\u228355(/\u00B0(\uFF74)\u00B0)/55\u0295 \u2022\u0300 o \u2022\u0301 \u029455(\uFF0F\uFFE3(\uFF74)\uFFE3)\uFF0F55(\uFF0F(\uFF74)\uFF3C)55\u0295 \u1D54\u1D25\u1D54 \u029455\u222A\uFF3E\u30A7\uFF3E\u222A55\uFF35^\u76BF^\uFF3555\u222A\uFF65\u03C9\uFF65\u222A55\uFF35\uFF34\uFF6A\uFF34\uFF3555\u222A\uFFE3-\uFFE3\u222A55U^\uFF6A^U55\u222A\uFF65\uFF6A\uFF65\u222A55V\u25CF\u1D25\u25CFV55\uFF0F(\u2267 x \u2266)\uFF3C55\uFF0F(=\uFF65 x \uFF65=)\uFF3C55\uFF0F(\uFF65 \u00D7 \uFF65)\uFF3C55\uFF0F(^ \u00D7 ^)\uFF3C55\uFF0F(=\u00B4x`=)\uFF3C55\uFF0F(\uFF1E\u00D7\uFF1C)\uFF3C55\uFF0F(^ x ^)\uFF3C55\uFF0F(\u02C3\u11BA\u02C2)\uFF3C55(\u00B4(00)\uFF40)55\uFF3C(\uFFE3(oo)\uFFE3)\uFF0F55(\uFFE3(\u03C9)\uFFE3)55\uFF61\uFF9F(\uFF9F\u00B4(00)`\uFF9F)\uFF9F\uFF6155\u30FD(\uFF40(00)\u00B4)\u30CE55(\uFFE3(00)\uFFE3)55(\u00B4(oo)\uFF40)55(\u02C6(oo)\u02C6)55(\uFFE3\u0398\uFFE3)55\uFF3C(\uFF40\u0398\u00B4)\uFF0F55(`\uFF65\u0398\uFF65\u00B4)55(\uFF65\u03B8\uFF65)55(\uFF40\u0398\u00B4)55(\u30FB\u0398\u30FB)55(\u25C9\u0398\u25C9)55\u30FE(\uFFE3\u25C7\uFFE3)\u30CE\u300355(\u00B0)#))<<55(\u00B0))<<55<\u30FB )))><<55>^)))<\uFF5E\uFF5E55\u03B6\u00B0)))\u5F6155\u2267(\uFF9F \uFF9F)\u226655>\u00B0))))\u5F6155/\u2572/\\\u256D(\u0C20\u0C20\u76CA\u0C20\u0C20)\u256E/\\\u2571\\55/\u2572/\\\u256D[ \u1D3C\u1D3C \u0C6A \u1D3C\u1D3C]\u256E/\\\u2571\\55/\u2572/\\\u256D(\u0CB0\u0CB0\u2313\u0CB0\u0CB0)\u256E/\\\u2571\\55/\u2572/\\( \u2022\u0300 \u03C9 \u2022\u0301 )/\\\u2571\\55/\u2572/\\\u256D\u0F3C \u00BA\u00BA\u0644\u035F\u00BA\u00BA \u0F3D\u256E/\\\u2571\\55/\u2572/\\\u256D[\u2609\uFE4F\u2609]\u256E/\\\u2571\\55/\u2572/\\\u256D( \u0361\u00B0\u0361\u00B0 \u035C\u0296 \u0361\u00B0\u0361\u00B0)\u256E/\\\u2571\\55\u30FE(\u30FB\u03C9\u30FB)\u30E1(\u30FB\u03C9\u30FB)\u30CE55(*^\u03C9^)\u516B(\u2312\u25BD\u2312)\u516B(-\u203F\u203F- )\u30FD55\u30FD( \u2312\u03C9\u2312)\u4EBA(=^\u2025^= )\uFF8955\uFF61*:\u2606(\u30FB\u03C9\u30FB\u4EBA\u30FB\u03C9\u30FB)\uFF61:\u309C\u2606\uFF6155(\uFF9F(\uFF9F\u03C9(\uFF9F\u03C9\uFF9F(\u2606\u03C9\u2606)\uFF9F\u03C9\uFF9F)\u03C9\uFF9F)\uFF9F)55(\u3063\u02D8\u25BD\u02D8)(\u02D8\u25BD\u02D8)\u02D8\u25BD\u02D8\u03C2)55(*\uFF3E\u03C9\uFF3E)\u4EBA(\uFF3E\u03C9\uFF3E*)55\uFF3C(\u25BD\uFFE3 \\ (\uFFE3\u25BD\uFFE3) / \uFFE3\u25BD)\uFF0F55\u30FD(\u2200\u309C )\u4EBA( \u309C\u2200)\u30CE55\uFF3C(\uFF3E\u2200\uFF3E)\u30E1(\uFF3E\u2200\uFF3E)\u30CE55\u30FD(\u2267\u25E1\u2266)\u516B(o^ ^o)\u30CE55o(^^o)(o^^o)(o^^o)(o^^)o55\u30FE(\u30FB\u03C9\u30FB\uFF40)\u30CE\u30FE(\u00B4\u30FB\u03C9\u30FB)\u30CE\u309B55(((*\u00B0\u25BD\u00B0*)\u516B(*\u00B0\u25BD\u00B0*)))55\u0669(\u0E51\uFF65\u0E34\u1D17\uFF65\u0E34)\u06F6\u0669(\uFF65\u0E34\u1D17\uFF65\u0E34\u0E51)\u06F655\u30FD( \u2312o\u2312)\u4EBA(\u2312-\u2312 )\uFF8955\u30FE(\uFFE3\u30FC\uFFE3(\u2267\u03C9\u2266*)\u309D55(*\u30FB\u2200\u30FB)\u723B(\u30FB\u2200\u30FB*)55(((\uFFE3(\uFFE3(\uFFE3\u25BD\uFFE3)\uFFE3)\uFFE3)))55\u03A8( \uFF40\u2200)(\u2200\u00B4 )\u03A855\u2606\u30FE(*\u00B4\u30FB\u2200\u30FB)\uFF89\u30FE(\u30FB\u2200\u30FB`*)\uFF89\u260655(\u261E\uFF9F\u30EE\uFF9F)\u261E \u261C(\uFF9F\u30EE\uFF9F\u261C)55\u30FD( \uFF65\u2200\uFF65)\uFF89_\u03B8\u5F61\u2606\u03A3(\u30CE `\u0414\u00B4)\u30CE55(*\uFF400\u00B4)\u03B8\u2606(\u30E1\u309C\u76BF\u309C)\uFF8955(; -_-)\u2015\u2015\u2015\u2015\u2015\u2015C<\u2015_-)55\u30FD(>_<\u30FD) \u2015\u2282|=0\u30D8(^\u203F^ )55(\u0482\uFF40\uFF9B\u00B4)\uFE3B\u30C7\u2550\u4E00 \uFF3C(\u00BA \u25A1 \u00BA l|l)/55/( .\u25A1.)\uFF3C \uFE35\u2570(\u00B0\u76CA\u00B0)\u256F\uFE35 /(.\u25A1. /)55(\uFF40\u2312*)O-(\uFF40\u2312\u00B4Q)55(*\u00B4\u2207\uFF40)\u250C\u03B8\u2606(\uFF89>_<)\uFF8955(o\u00AC\u203F\u00ACo )...\u2606\uFF90(*x_x)55\uFF1C( \uFFE3\uFE3F\uFFE3)\uFE35\u03B8\uFE35\u03B8\uFE35\u2606(\uFF1E\u53E3\uFF1C\uFF0D)55\u30D8(>_<\u30D8) \uFFE2o(\uFFE3\u203F\uFFE3\uFF92)55(\u256F\u00B0\u0414\u00B0)\u256F\uFE35 /(.\u25A1 . \uFF3C)55(\uFF89-.-)\uFF89\u2026.((((((((((((\u25CF~* ( >_<)55(((\u0E07\u2019\u03C9\u2019)\u0648\u4E09 \u0E07\u2019\u03C9\u2019)\u06A1\u2261\u3000\u2606\u2312\uFF90((x_x)55( \uFFE3\u03C9\uFFE3)\u30CE\uFF9E\u2312\u2606\uFF90(o _ _)o55(\u256C\uFFE3\u76BF\uFFE3)=\u25CB\uFF03(\uFFE3#)\uFF13\uFFE3)55(\uFFE3\u03B5(#\uFFE3)\u2606\u2570\u256Eo(\uFFE3\u25BD\uFFE3///)55,,((( \uFFE3\u25A1)_\uFF0F \uFF3C_(\u25CB\uFFE3 ))),,55(\u00AC_\u00AC'')\u0505(\uFFE3\u03B5\uFFE3\u0505)55!!(\uFF92\uFFE3 \uFFE3)_\u03B8\u2606\uFF9F0\uFF9F)/55(\u0E07\u0CA0_\u0CA0)\u0E07\u3000\u03C3( \u2022\u0300 \u03C9 \u2022\u0301 \u03C3)55( \u30FB\u2200\u30FB)\u30FB\u30FB\u30FB--------\u260655( -\u03C9-)\uFF0F\u5360~~~~~55\u25CB\u221E\u221E\u221E\u221E\u30FD(^\u30FC^ )55(*\uFF3E\uFF3E)/~~~~~~~~~~\u25CE55((( \uFFE3\u25A1)_\uFF0F55(\uFF92\uFFE3\u25BD\uFFE3)\uFE3B\u2533\u2550\u4E0055(/-_\u30FB)/D\u30FB\u30FB\u30FB\u30FB\u30FB------ \u219255(/\u30FB\u30FB)\u30CE\u3000\u3000 (( \u304F ((\u307855(; \u30FB_\u30FB)\u2015\u2015\u2015\u2015C55\uFFE2o(\uFFE3-\uFFE3\uFF92)55(\uFF92\uFF40\uFF9B\u00B4)\uFE3B\u30C7\u2550\u4E0055Q(\uFF40\u2312\u00B4Q)55(^\u03C9^)\u30CE\uFF9E(((((((((\u25CF\uFF5E*55\u2015\u2282|=0\u30D8(^^ )55(\u0CA0 o \u0CA0)\u00A4=[]:::::>55\u2015(T_T)\u219255( \u00B4-\u03C9\uFF65)\uFE3B\u253B\u2533\u2550\u2550\u2501\u4E0055(\u30CE \u02D8_\u02D8)\u30CE\u3000\u03B6|||\u03B6\u3000\u03B6|||\u03B6\u3000\u03B6|||\u03B655(\u30CE\uFF9F\u2200\uFF9F)\u30CE\u2312\uFF65*:.\uFF61. .\uFF61.:*\uFF65\u309C\uFF9F\uFF65*\u260655(\u2283\uFF61\u2022\u0301\u203F\u2022\u0300\uFF61)\u2283\u2501\u273F\u273F\u273F\u273F\u273F\u273F55(\u2229\uFF40\uFF9B\u00B4)\u2283\u2501\u708E\u708E\u708E\u708E\u708E55(\uFF89\u2267\u2200\u2266)\uFF89 \u2025\u2026\u2501\u2501\u2501\u260555\u2570( \u0361\u00B0 \u035C\u0296 \u0361\u00B0 )\u3064\u2500\u2500\u2606*:\u30FB\uFF9F55(\u2229\u1111_\u1111)\u2283\u2501\u2606\uFF9F*\uFF65\uFF61*\uFF65:\u2261( \u03B5:)55(\uFF89>\u03C9<)\uFF89 :\uFF61\uFF65:*:\uFF65\uFF9F\u2019\u2605,\uFF61\uFF65:*:\uFF65\uFF9F\u2019\u260655(\uFF03\uFFE3\u25A1\uFFE3)o\u2501\u2208\u30FB\u30FB\u2501\u2501\u2501\u2501\u260655(/\uFFE3\u30FC\uFFE3)/~~\u2606\u2019.\uFF65.\uFF65:\u2605\u2019.\uFF65.\uFF65:\u260655(*\u00B4\u30FC\u00B4)\u65E6 \u65E6(\uFFE3\u03C9\uFFE3*)55( \uFFE3\u25BD\uFFE3)[] [](\u2267\u25BD\u2266 )55(*\uFFE3\u25BD\uFFE3)\u65E6 \u4E14(\u00B4\u2200\uFF40*)55(*\u00B4\u0437\uFF40)\u53E3\uFF9F\uFF61\uFF9F\u53E3(\u30FB\u2200\u30FB )55( *^^)o\u2200*\u2200o(^^* )55(\u3000\u2019\u03C9\u2019)\u65E6~~\u250F\u2501\u251355( o^ ^o)\u4E14 \u4E14(\u00B4\u03C9\uFF40*)55( ^^)_\u65E6~~\u3000 ~~U_(^^ )55\u30FE(\u00B4\u3007\uFF40)\uFF89\u266A\u266A\u266A55\u30FD(o\u00B4\u2200`)\uFF89\u266A\u266C55\u266A\u266C((d\u2312\u03C9\u2312b))\u266C\u266A55\u2514(\uFF3E\uFF3E)\u251055(\uFFE3\u25BD\uFFE3)/\u266B\u2022*\u00A8*\u2022.\u00B8\u00B8\u266A55\u30FE(\u2310\u25A0_\u25A0)\u30CE\u266A55\u4E41( \u2022 \u03C9 \u2022\u4E41)55\u266C\u266B\u266A\u25D6(\u25CF o \u25CF)\u25D7\u266A\u266B\u266C55\u30D8(\uFFE3\u03C9\uFFE3\u30D8)55(\uFF89\u2267\u2200\u2266)\uFF8955\u2514(\uFFE3-\uFFE3\u2514))55\u250C(\uFF3E\uFF3E)\u251855(^_^\u266A)55(\u301C\uFFE3\u25B3\uFFE3)\u301C55(\uFF62\u2022 \u03C9 \u2022)\uFF6255( \u02D8 \u025C\u02D8) \u266C\u266A\u266B55(\u301C\uFFE3\u25BD\uFFE3)\u301C55\u266A\u30FD(^^\u30FD)\u266A55((\u2518\uFFE3\u03C9\uFFE3)\u251855\uFF3C(\uFFE3\u25BD\uFFE3)\uFF3C55(~\u02D8\u25BD\u02D8)~55(~\u203E\u25BD\u203E)~55\u207D\u207D\u25DD( \u2022 \u03C9 \u2022 )\u25DC\u207E\u207E55\u266A\u266A\u266A \u30FD(\u02C7\u2200\u02C7 )\u309E55\u301C(\uFFE3\u25BD\uFFE3\u301C)55\u266A(/_ _ )/\u266A55\u221A(\uFFE3\u2025\uFFE3\u221A)55\uFF0F(\uFFE3\u25BD\uFFE3)\uFF0F55~(\u02D8\u25BD\u02D8~)55~(\u02D8\u25BD\u02D8)~55\u273A\u25DF( \u2022 \u03C9 \u2022 )\u25DE\u273A55( ^^)p_____|_o____q(^^ )55\u30FD(^o^)\u03C1\u2533\u253B\u2533\u00B0\u03C3(^o^)\u30CE55( \u30CE-_-)\u30CE\uFF9E_\u25A1 VS \u25A1_\u30FE(^-^\u30FD)55\u042E\u3000\u25CB\u4E09\u3000\uFF3C(\uFFE3^\uFFE3\uFF3C)55(\uFF0Fo^)/ \u00B0\u22A5 \uFF3C(^o\uFF3C)55(\uFF0F_^)\uFF0F\u3000\u3000\u25CF\u3000\uFF3C(^_\uFF3C)55\u30FD(\uFF1B^ ^)\u30CE\uFF9E \uFF0E\uFF0E\uFF0E...___\u300755!(;\uFF9Fo\uFF9F)o/\uFFE3\uFFE3\uFFE3\uFFE3\uFFE3\uFFE3\uFFE3~ >\uFF9F))))\u5F6155\"( (\u2261|\u2261))_\uFF0F \uFF3C_((\u2261|\u2261) )\"55(=O*_*)=O Q(*_*Q)55\u0669(\u02CA\u3007\u02CB*)\u064855(\uFFE3^\uFFE3)\u309E55(\uFF0D\u2038\u10DA)55(\u256F\u00B0\u76CA\u00B0)\u256F\u5F61\u253B\u2501\u253B55(\u256E\u00B0-\u00B0)\u256E\u2533\u2501\u2501\u2533 ( \u256F\u00B0\u25A1\u00B0)\u256F \u253B\u2501\u2501\u253B55\u252C\u2500\u252C\u30CE( \u00BA _ \u00BA\u30CE)55(oT-T)\u5C3855( \u0361\u00B0 \u035C\u0296 \u0361\u00B0)55[\u0332\u0305$\u0332\u0305(\u0332\u0305 \u0361\u00B0 \u035C\u0296 \u0361\u00B0\u0332\u0305)\u0332\u0305$\u0332\u0305]55(\u0CA0_\u0CA0)55\u25EF\uFF10o\u3002(\u30FC\u3002\u30FC)y~~55(\uFFE3\uFE43\uFFE3)55( \u02D8\u25BD\u02D8)\u3063\u266855(\u3063\u02D8\u06A1\u02D8\u03C2)55(x(x_(x_x(O_o)x_x)_x)x)55(\u3000\uFF65\u03C9\uFF65)\u261E55(\u2310\u25A0_\u25A0)55(\u25D5\u203F\u25D5\u273F)55(\u3000\uFFE3.)o-\u3000\u3000\u3010\u3000TV\u3000\u301155\uFF40\u3001\u30FD\uFF40\u30FD\uFF40\u3001\u30FD(\u30CE\uFF1E\uFF1C)\u30CE \uFF40\u3001\u30FD\uFF40\u2602\u30FD\uFF40\u3001\u30FD55( \u2022 )( \u2022 )\u0505(\u2256\u203F\u2256\u0505)55( \uFF3E\u25BD\uFF3E)\u3063\u2702\u2570\u22C3\u256F").split("55");
	}}