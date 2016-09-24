package br.com.minegames.arqueiro.domain.level;

public class Level {
	
	private long levelStartTime;
	private int level;
	
	public Level() {
		this.level = 0;
		this.levelStartTime = System.currentTimeMillis();
	}
	
	public Level(Level level) {
		int newLevel = level.getLevel()+1; 
		this.level = newLevel;
		this.levelStartTime = System.currentTimeMillis();
	}
	
	public long getLevelStartTime() {
		return levelStartTime;
	}
	
	public void setLevelStartTime(long levelStartTime) {
		this.levelStartTime = levelStartTime;
	}
	
	public int getLevel() {
		return level;
	}
	
	public void setLevel(int level) {
		this.level = level;
	}
	
	public Long lifeTime() {
		return System.currentTimeMillis() - this.levelStartTime;
	}
	

}
