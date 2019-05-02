package eu.su.mas.dedaleEtu.mas.behaviours;

import java.util.List;

import message.Case;

public interface ExploAgent {
	public void maj(List<Case> e1, List<Case> e2);

	public boolean explore();

	public void moveRandom();

	public boolean isDroping();

	public void dropped();

}
