package org.cloudbus.cloudsim.power;

import java.util.List;

import org.cloudbus.cloudsim.DatacenterCharacteristics;
import org.cloudbus.cloudsim.Storage;
import org.cloudbus.cloudsim.VmAllocationPolicy;
import org.cloudbus.cloudsim.core.CloudSim;

/**
 * The Class PowerDatacenterSimulationLimit.
 */
public class PowerDatacenterSimulationLimit extends PowerDatacenter {

	/** The simulation length. */
	private double simulationLength;

	/**
	 * Instantiates a new power datacenter simulation limit.
	 *
	 * @param name the name
	 * @param characteristics the characteristics
	 * @param vmAllocationPolicy the vm allocation policy
	 * @param storageList the storage list
	 * @param schedulingInterval the scheduling interval
	 * @param simulationLength the simulation length
	 *
	 * @throws Exception the exception
	 */
	public PowerDatacenterSimulationLimit(String name,
			DatacenterCharacteristics characteristics,
			VmAllocationPolicy vmAllocationPolicy,
			List<Storage> storageList,
			double schedulingInterval,
			double simulationLength) throws Exception {
		super(name, characteristics, vmAllocationPolicy, storageList, schedulingInterval);
		setSimulationLength(simulationLength);
	}

	/* (non-Javadoc)
	 * @see cloudsim.power.PowerDatacenter#updateCloudletProcessing()
	 */
	@Override
	protected void updateCloudletProcessing() {
		if (CloudSim.clock() >= simulationLength) {
			CloudSim.cancelAll(getId(), CloudSim.SIM_ANY);
			setLastProcessTime(CloudSim.clock());
			return;
		}
		super.updateCloudletProcessing();
	}

	/**
	 * Sets the simulation length.
	 *
	 * @param simulationLength the new simulation length
	 */
	public void setSimulationLength(double simulationLength) {
		this.simulationLength = simulationLength;
	}

	/**
	 * Gets the simulation length.
	 *
	 * @return the simulation length
	 */
	public double getSimulationLength() {
		return simulationLength;
	}

}
