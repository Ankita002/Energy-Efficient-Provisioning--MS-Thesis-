package org.cloudbus.cloudsim;

import org.cloudbus.cloudsim.provisioners.BwProvisioner;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisioner;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;

import sun.security.x509.AVA;


public class HostStabilityFactor {

	private VmScheduler vms;
	private RamProvisioner rps;
	private BwProvisioner bwps;
	private PeProvisionerSimple peps;
	
	public HostStabilityFactor(VmScheduler vms, RamProvisioner ramProvisioner, BwProvisioner bwProvisioner, PeProvisionerSimple peps) {
		
		this.vms = vms;
		this.rps = ramProvisioner;
		this.bwps = bwProvisioner;
		this.peps = peps;
	}

	public boolean isHostSuitableForVmBasedOnStabilityFactor(Vm vm) {
		
		double availableMipsInhost = vms.getAvailableMips();
		double mipsRequestedByVm = vm.getCurrentRequestedTotalMips();
		
		boolean isRamAvailable = rps.isSuitableForVm(vm, vm.getCurrentRequestedRam());
		boolean isBandwidthAvailable = bwps.isSuitableForVm(vm, vm.getCurrentRequestedBw());
		
		if (availableMipsInhost < mipsRequestedByVm && !isRamAvailable && !isBandwidthAvailable ) {
			return false;
		}
		return true; 
		
	}
	
	
	
}
