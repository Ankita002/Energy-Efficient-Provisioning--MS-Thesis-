package org.cloudbus.cloudsim.power;

import java.util.LinkedList;
import java.util.List;

import org.cloudbus.cloudsim.CloudletScheduler;
import org.cloudbus.cloudsim.Vm;

/**
 * The Class PowerVm.
 */
public class PowerVm extends Vm {

	/** The Constant HISTORY_LENGTH. */
	public static final int HISTORY_LENGTH = 30;

	/** The utilization history. */
	List<Double> utilizationHistory;

	/**
	 * Instantiates a new power vm.
	 *
	 * @param id the id
	 * @param userId the user id
	 * @param mips the mips
	 * @param pesNumber the pes number
	 * @param ram the ram
	 * @param bw the bw
	 * @param size the size
	 * @param priority the priority
	 * @param vmm the vmm
	 * @param cloudletScheduler the cloudlet scheduler
	 */
	public PowerVm(int id, int userId, double mips, int pesNumber, int ram,
			long bw, long size, int priority, String vmm,
			CloudletScheduler cloudletScheduler) {
		super(id, userId, mips, pesNumber, ram, bw, size, vmm, cloudletScheduler);
		setUtilizationHistory(new LinkedList<Double>());
	}

	/**
	 * Updates the processing of cloudlets running on this VM.
	 *
	 * @param currentTime current simulation time
	 * @param mipsShare array with MIPS share of each Pe available to the scheduler
	 *
	 * @return time predicted completion time of the earliest finishing cloudlet, or 0
	 * if there is no next events
	 *
	 * @pre currentTime >= 0
	 * @post $none
	 */
	@Override
	public double updateVmProcessing(double currentTime, List<Double> mipsShare) {
		double time = super.updateVmProcessing(currentTime, mipsShare);
		double utilization = getTotalUtilizationOfCpu(getCloudletScheduler().getPreviousTime());
		if (utilization != 0 || !getUtilizationHistory().isEmpty()) {
			addUtilizationHistoryValue(utilization);
		}
		return time;
	}

	/**
	 * Gets the utilization mean in MIPS.
	 *
	 * @return the utilization mean in MIPS
	 */
	public double getUtilizationMean() {
		double mean = 0;
		if (!getUtilizationHistory().isEmpty()) {
			int n = HISTORY_LENGTH;
			if (HISTORY_LENGTH > getUtilizationHistory().size()) {
				n = getUtilizationHistory().size();
			}
			for (int i = 0; i < n; i++) {
				mean += getUtilizationHistory().get(i);
			}
			mean /= n;
		}
		return mean * getMips();
	}

	/**
	 * Gets the utilization variance in MIPS.
	 *
	 * @return the utilization variance in MIPS
	 */
	public double getUtilizationVariance() {
		double mean = getUtilizationMean();
		double variance = 0;
		if (!getUtilizationHistory().isEmpty()) {
			int n = HISTORY_LENGTH;
			if (HISTORY_LENGTH > getUtilizationHistory().size()) {
				n = getUtilizationHistory().size();
			}
			for (int i = 0; i < n; i++) {
				double tmp = getUtilizationHistory().get(i) * getMips() - mean;
				variance += tmp * tmp;
			}
			variance /= n;
		}
		return variance;
	}

	/**
	 * Adds the utilization history value.
	 *
	 * @param utilization the utilization
	 */
	public void addUtilizationHistoryValue(double utilization) {
		getUtilizationHistory().add(0, utilization);
		if (getUtilizationHistory().size() > HISTORY_LENGTH) {
			getUtilizationHistory().remove(HISTORY_LENGTH);
		}
	}

	/**
	 * Gets the utilization history.
	 *
	 * @return the utilization history
	 */
	protected List<Double> getUtilizationHistory() {
		return utilizationHistory;
	}

	/**
	 * Sets the utilization history.
	 *
	 * @param utilizationHistory the new utilization history
	 */
	protected void setUtilizationHistory(List<Double> utilizationHistory) {
		this.utilizationHistory = utilizationHistory;
	}

}
