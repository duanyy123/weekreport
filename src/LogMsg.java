import java.util.Date;

public class LogMsg implements Comparable<LogMsg>{
		 String commitVersion;
		 String author;
		 Date logTime;
		 String proName;
		 String Merge;
		 String content;
		@Override
		public boolean equals(Object obj) {
			if(obj==null)
				return false;
			if(!(obj instanceof LogMsg))
				return false;
			LogMsg compareObj = (LogMsg) obj;
			
			long nowTime = logTime.getTime();
			long compareTime = compareObj.logTime.getTime();
			return nowTime-compareTime==0;
		}
		@Override
		public int hashCode() {
			return 1;
		}
		@Override
		public int compareTo(LogMsg logMsg) {
			long nowTime = logTime.getTime();
			long compareTime = logMsg.logTime.getTime();
			return nowTime-compareTime>0L?1:(nowTime-compareTime==0L?0:-1);
		}
		 
	}