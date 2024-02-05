package app;

import mutex.suzuki.SuzukiToken;
import networking.Address;

import java.io.Serializable;
import java.util.*;

/**
 * This is an immutable class that holds all the information for a servent.
 *
 * @author bmilojkovic
 */
public class ServentInfo implements Serializable {

	private static final long serialVersionUID = 5304170042791281555L;
	private final int id;
	private final String ipAddress;
	private final int listenerPort;
	private final Address address;
	private final boolean boostrap;
	private final List<Integer> neighbors;

	// Suzuki Kasami
	private Map<ServentInfo, Integer> requestNumbers = new HashMap<>();
	private Map<ServentInfo, Integer> lastNumbers = new HashMap<>();
	private Queue<ServentInfo> requestQueue = new LinkedList<>();
	private boolean hasToken = false;
	private boolean inCriticalSection = false;
	private volatile SuzukiToken mutexToken = null;

	public ServentInfo(String ipAddress, int id, int listenerPort, List<Integer> neighbors, boolean boostrap) {
		this.ipAddress = ipAddress;
		this.listenerPort = listenerPort;
		this.id = id;
		this.neighbors = neighbors;

		this.boostrap = boostrap;
		this.address = new Address(ipAddress, listenerPort);
	}

	public SuzukiToken getMutexToken() {
		return mutexToken;
	}

	public void setMutexToken(SuzukiToken mutexToken) {
		this.mutexToken = mutexToken;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public int getListenerPort() {
		return listenerPort;
	}

	public int getId() {
		return id;
	}
	
	public List<Integer> getNeighbors() {
		return neighbors;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		ServentInfo that = (ServentInfo) o;
		return id == that.id && Objects.equals(address, that.address);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, address);
	}

	@Override
	public String toString() {
		return "[" + id + "|" + ipAddress + "|" + listenerPort + "]";
	}

	public Address getAddress() {
		return address;
	}

	public boolean isBoostrap() {
		return boostrap;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public Map<ServentInfo, Integer> getRequestNumbers() {
		return requestNumbers;
	}

	public Queue<ServentInfo> getRequestQueue() {
		return requestQueue;
	}

	public Map<ServentInfo, Integer> getLastNumbers() {
		return lastNumbers;
	}

	public void setLastNumbers(Map<ServentInfo, Integer> lastNumbers) {
		this.lastNumbers = lastNumbers;
	}

	public void setHasToken(boolean hasToken) {
		this.hasToken = hasToken;
	}

	public void setInCriticalSection(boolean inCriticalSection) {
		this.inCriticalSection = inCriticalSection;
	}

	public void setRequestNumbers(Map<ServentInfo, Integer> requestNumbers) {
		this.requestNumbers = requestNumbers;
	}

	public void setRequestQueue(Queue<ServentInfo> requestQueue) {
		this.requestQueue = requestQueue;
	}

	public boolean isInCriticalSection() {
		return inCriticalSection;
	}

	public boolean isHasToken() {
		return hasToken;
	}
}
