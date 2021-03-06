package qa.qcri.aidr.analysis.stat;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class TagDataMapRecord extends MapRecord {
	
	private Long firstEntryTime;

	public Long getFirstEntryTime() {
		return firstEntryTime;
	}

	public void setFirstEntryTime(Long firstEntryTime) {
		this.firstEntryTime = firstEntryTime;
	}

	public TagDataMapRecord(List<Long> granularityList) {
		this.count = new ConcurrentHashMap<Long, Integer>();
		for (Long g: granularityList) {
			this.count.put(g, 0);
		}
		this.lastUpdateTime = System.currentTimeMillis();
		this.setFirstEntryTime(System.currentTimeMillis());
	}

	public int getCount(Long g) {
		return count.containsKey(g) ? count.get(g) : 0;
	}

	public void setCount(Long g, int val) {
		synchronized(count) {
			count.put(g, val);
		}
		this.lastUpdateTime = System.currentTimeMillis();
	}
	
	public void incrementCount(Long g) {
		this.setCount(g, count.get(g)+1);
	}

	public void incrementAllCounts() {
		for (Long g: count.keySet()) {
			this.setCount(g, count.get(g)+1);
		}
	}
	
	public void resetCount(Long g) {
		this.setCount(g, 0);
	}
	
	public long getLastUpdateTime() {
		return this.lastUpdateTime;
	}
	
	public void setLastUpdateTime(long lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	
	public boolean isCountZeroForGranularity(Long g) {
		return ((count.get(g) == 0) || (count.get(g) == null));
	}
	
	public boolean isCountZeroForAllGranularity() {
		boolean flag = true;
		for (Long g: count.keySet()) {
			flag &= isCountZeroForGranularity(g);
		}
		return flag;
	}
	
	@Override
	public String toString() {
		return "TagDataMapRecord [count=" + count + "]";
	}
}
