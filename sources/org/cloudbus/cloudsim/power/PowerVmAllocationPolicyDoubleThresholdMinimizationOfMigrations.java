/*
 * Title:        CloudSim Toolkit
 * Description:  CloudSim (Cloud Simulation) Toolkit for Modeling and Simulation
 *               of Parallel and Distributed Systems such as Clusters and Clouds
 * Licence:      GPL - http://www.gnu.org/copyleft/gpl.html
 *
 * Copyright (c) 2008, The University of Melbourne, Australia
 */

package org.cloudbus.cloudsim.power;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cloudbus.cloudsim.Log;
import org.cloudbus.cloudsim.Vm;
import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.power.lists.PowerVmList;

/**
 * The Class PowerVmAllocationPolicyDoubleThresholdMinimizationOfMigrations.
 */
public class PowerVmAllocationPolicyDoubleThresholdMinimizationOfMigrations extends PowerVmAllocationPolicyDoubleThresholdAbstract {

	/**
	 * Instantiates a new vM provisioner mpp.
	 *
	 * @param list the list
	 * @param utilizationThresholdLower the utilization threshold lower
	 * @param utilizationThresholdUpper the utilization threshold upper
	 */
	public PowerVmAllocationPolicyDoubleThresholdMinimizationOfMigrations(List<? extends PowerHost> list, double utilizationThresholdLower, double utilizationThresholdUpper) {
		super(list, utilizationThresholdLower, utilizationThresholdUpper);
	}

	/**
	 * Optimize allocation of the VMs according to current utilization.
	 *
	 * @param vmList the vm list
	 *
	 * @return the array list< hash map< string, object>>
	 */
	@Override
	public List<Map<String, Object>> optimizeAllocation(List<? extends Vm> vmList) {
		List<Map<String, Object>> migrationMap = new ArrayList<Map<String, Object>>();
		if (vmList.isEmpty()) {
			return migrationMap;
		}
		saveAllocation(vmList);
		List<Vm> vmsToRestore = new ArrayList<Vm>();
		vmsToRestore.addAll(vmList);

		List<Vm> vmsToMigrate = new ArrayList<Vm>();

		for (PowerHost host : this.<PowerHost>getHostList()) {
			host.reallocateMigratingVms();
		}

		// check min and max utilization bounds for each host
		for (PowerHost host : this.<PowerHost>getHostList()) {
			double utilization = host.getUtilizationOfCpuMips();
			double utilizationThresholdLower = getUtilizationThresholdLower() * host.getTotalMips();
			double utilizationThresholdUpper = getUtilizationThresholdUpper() * host.getTotalMips();

			if (utilization < utilizationThresholdLower) {
				// migrate all VMs to turn off this host
				List<Vm> tmpList = new ArrayList<Vm>();
				tmpList.addAll(host.getVmList());
				for (Vm vm : tmpList) {
					if (vm.isRecentlyCreated() || vm.isInMigration()) {
						continue;
					}
					vmsToMigrate.add(vm);
					host.vmDestroy(vm);
				}
			} else if (utilization > utilizationThresholdUpper) {
				// migrate necessary amount of VMs to reduce utilization below max bound

				List<Vm> tmpList = new ArrayList<Vm>();
				tmpList.addAll(host.getVmList());

				while (utilization > utilizationThresholdUpper) {
					Vm bestFitVm = null;
					double bestFitUtilizationUpper = Integer.MAX_VALUE;
					double bestFitUtilizationLower = Integer.MAX_VALUE;
					for (Vm vm : tmpList) {
						if (vm.isRecentlyCreated() || vm.isInMigration()) {
							continue;
						}
						double vmUtilization = vm.getTotalUtilizationOfCpu(CloudSim.clock()) * vm.getMips();
						//Log.printLine("utilization: " + vmUtilization);
						//Log.printLine("utilization diff: " + (utilization - utilizationThresholdUpper));
						if (vmUtilization > utilization - utilizationThresholdUpper) {
							if (vmUtilization - (utilization - utilizationThresholdUpper) < bestFitUtilizationUpper) {
								bestFitUtilizationUpper = vmUtilization - (utilization - utilizationThresholdUpper);
								bestFitVm = vm;
								//Log.printLine("Chosen higher: " + bestFitUtilizationHigher);
							}
						} else if (bestFitUtilizationUpper == Integer.MAX_VALUE) { // priority to VMs that have utilization higher than difference
							if ((utilization - utilizationThresholdUpper) - vmUtilization < bestFitUtilizationLower) {
								bestFitUtilizationLower = (utilization - utilizationThresholdUpper) - vmUtilization;
								bestFitVm = vm;
								//Log.printLine("Chosen lower: " + bestFitUtilizationLower);
							}
						}
					}

					if (bestFitVm == null) {
						break;
					}

					utilization -= bestFitVm.getTotalUtilizationOfCpu(CloudSim.clock()) * bestFitVm.getMips();
					vmsToMigrate.add(bestFitVm);
					host.vmDestroy(bestFitVm);
					tmpList.remove(bestFitVm);
				}
			}
		}

		PowerVmList.sortByCpuUtilization(vmsToMigrate);

		restoreAllocation(vmsToRestore, getHostList());
		for (PowerHost host : this.<PowerHost>getHostList()) {
			host.reallocateMigratingVms();
		}

		for (Vm vm : vmsToMigrate) {
			//Log.printLine("VM #" + vm.getId() + " utilization: " + String.format("%.2f;", vm.getTotalUtilization(CloudSim.clock()) * 100));
			PowerHost oldHost = (PowerHost) getVmTable().get(vm.getUid());
			//PowerHost oldHost = vm.getHost();
			PowerHost allocatedHost = findHostForVm(vm);
			if (allocatedHost != null && allocatedHost.getId() != oldHost.getId()) {
				allocatedHost.vmCreate(vm);
				Log.printLine("VM #" + vm.getId() + " allocated to host #" + allocatedHost.getId());

				Map<String, Object> migrate = new HashMap<String, Object>();
				migrate.put("vm", vm);
				migrate.put("host", allocatedHost);
				migrationMap.add(migrate);
				//getVmTable().put(vm.getUid(), allocatedHost);

				//vmsToRestore.remove(vm);
				//Log.printLine("Skip restore of VM #" + vm.getId());
			}
		}

		restoreAllocation(vmsToRestore, getHostList());

		return migrationMap;
	}

}
