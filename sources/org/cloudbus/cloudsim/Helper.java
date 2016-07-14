package org.cloudbus.cloudsim;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;

import org.cloudbus.cloudsim.power.PowerDatacenter;
import org.cloudbus.cloudsim.power.PowerHost;
//import org.cloudbus.cloudsim.power.PowerHostDynamicThreshold;
import org.cloudbus.cloudsim.power.PowerPe;
import org.cloudbus.cloudsim.power.PowerVm;
import org.cloudbus.cloudsim.power.models.PowerModelLinear;
import org.cloudbus.cloudsim.provisioners.BwProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.PeProvisionerSimple;
import org.cloudbus.cloudsim.provisioners.RamProvisionerSimple;


/**
 * The Class Helper.
 */
public class Helper {

	/**
	 * Creates the cloudlet list.
	 *
	 * @param brokerId the broker id
	 * @param cloudletsNumber the cloudlets number
	 *
	 * @return the list< cloudlet>
	 */
	public static List<Cloudlet> createCloudletList(int brokerId, int cloudletsNumber) {
		List<Cloudlet> list = new ArrayList<Cloudlet>();

		long length = 150000; // 10 min on 250 MIPS
		int pesNumber = 1;
		long fileSize = 300;
		long outputSize = 300;

		for (int i = 0; i < cloudletsNumber; i++) {
			Cloudlet cloudlet = new Cloudlet(i, length, pesNumber, fileSize, outputSize, new UtilizationModelStochastic(), new UtilizationModelStochastic(), new UtilizationModelStochastic());
			cloudlet.setUserId(brokerId);
			cloudlet.setVmId(i);
			list.add(cloudlet);
		}

		return list;
	}

	/**
	 * Creates the cloudlet list with PlanetLab utilization models.
	 *
	 * @param brokerId the broker id
	 * @param cloudletsNumber the cloudlets number
	 *
	 * @return the list< cloudlet>
	 * @throws FileNotFoundException
	 */
	public static List<Cloudlet> createCloudletListPlanetLab(int brokerId, String inputFolderName, double simulationLength, int cloudletsNumber) throws FileNotFoundException {
		List<Cloudlet> list = new ArrayList<Cloudlet>();

		long length = 302400000; // 24 hours on 3500 MIPS
		int pesNumber = 1;
		long fileSize = 300;
		long outputSize = 300;

		File inputFolder = new File(inputFolderName);
		File[] files = inputFolder.listFiles(new FileFilter() {
		    public boolean accept(File file) {
		        return file.isFile();
		    }
		});

		int n = cloudletsNumber;
		if (cloudletsNumber == -1) {
			n = files.length;
		}

		for (int i = 0; i < n; i++) {
			Cloudlet cloudlet = null;
			try {
				cloudlet = new Cloudlet(i, length, pesNumber, fileSize, outputSize, new UtilizationModelPlanetLabInMemory(files[i].getAbsolutePath(), simulationLength), new UtilizationModelStochastic(), new UtilizationModelStochastic());
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			cloudlet.setUserId(brokerId);
			cloudlet.setVmId(i);
			list.add(cloudlet);
		}

		return list;
	}

	/**
	 * Creates the vm list.
	 *
	 * @param brokerId the broker id
	 * @param vmsNumber the vms number
	 *
	 * @return the list< vm>
	 */
	public static List<Vm> createVmList(int brokerId, int vmsNumber) {
		List<Vm> vms = new ArrayList<Vm>();

		int[] mips = { 1000, 2000, 2500, 3250 }; // MIPSRating according to EC2 instance types
		int pesNumber = 1; // number of cpus
		int ram = 1024; // vm memory (MB)
		long bw = 100000; // bandwidth Kb/s
		long size = 2500; // image size (MB)
		int priority = 1; // image size (MB)
		String vmm = "Xen"; // VMM name

		for (int i = 0; i < vmsNumber; i++) {
			vms.add(
				new PowerVm(i, brokerId, mips[i % mips.length], pesNumber, ram, bw, size, priority, vmm, new CloudletSchedulerDynamicWorkload(mips[i % mips.length], pesNumber))
			);
		}

		return vms;
	}

	/**
	 * Creates the host list.
	 *
	 * @param hostsNumber the hosts number
	 *
	 * @return the list< power host>
	 */
	public static List<PowerHost> createHostList(int hostsNumber) {
		List<PowerHost> hostList = new ArrayList<PowerHost>();

		double maxPower = 250; // 250W
		double idlePowerPercentage = 0.7; // 70%

		int[] mips = { 2000, 3000, 4500, 6000 };
		int ram = 16384; // host memory (MB)
		long storage = 1000000; // host storage
		int bw = 10000000; // 10 Gb/s (Kb/s)

		for (int i = 0; i < hostsNumber; i++) {
			List<PowerPe> peList = new ArrayList<PowerPe>();
			peList.add(new PowerPe(0, new PeProvisionerSimple(mips[i % mips.length]), new PowerModelLinear(maxPower, idlePowerPercentage)));

			hostList.add(
				new PowerHost(
					i,
					new RamProvisionerSimple(ram),
					new BwProvisionerSimple(bw),
					storage,
					peList,
					new VmSchedulerTimeShared(peList)
				)
			);
		}

		return hostList;
	}

	/**
	 * Creates the host list.
	 *
	 * @param hostsNumber the hosts number
	 *
	 * @return the list< power host>
	 */
/*	public static List<PowerHost> createHostListDynamicThreshold(int hostsNumber, String probabilityLower, String probabilityUpper) {
		List<PowerHost> hostList = new ArrayList<PowerHost>();

		double maxPower = 250; // 250W
		double idlePowerPercentage = 0.7; // 70%

		int[] mips = { 2000, 2500, 3000, 3500 };
		int ram = 16384; // host memory (MB)
		long storage = 1000000; // host storage
		int bw = 10000000; // 10 Gb/s (Kb/s)

		for (int i = 0; i < hostsNumber; i++) {
			List<PowerPe> peList = new ArrayList<PowerPe>();
			peList.add(new PowerPe(0, new PeProvisionerSimple(mips[i % mips.length]), new PowerModelLinear(maxPower, idlePowerPercentage)));

			hostList.add(
				new PowerHostDynamicThreshold(
					i,
					new RamProvisionerSimple(ram),
					new BwProvisionerSimple(bw),
					storage,
					peList,
					new VmSchedulerTimeShared(peList),
					probabilityLower,
					probabilityUpper
				)
			);
		}

		return hostList;
	}
*/

	/**
	 * Creates the broker.
	 *
	 * @return the datacenter broker
	 */
	public static DatacenterBroker createBroker() {
		DatacenterBroker broker = null;
		try {
			broker = new DatacenterBroker("Broker");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return broker;
	}

	/**
	 * Creates the datacenter.
	 *
	 * @param name the name
	 * @param datacenterClass the datacenter class
	 * @param hostList the host list
	 * @param vmAllocationPolicy the vm allocation policy
	 * @param simulationLength
	 *
	 * @return the power datacenter
	 *
	 * @throws Exception the exception
	 */
	public static Datacenter createDatacenter(String name, Class<? extends Datacenter> datacenterClass, List<PowerHost> hostList, VmAllocationPolicy vmAllocationPolicy, double simulationLength) throws Exception {
		String arch = "x86"; // system architecture
		String os = "Linux"; // operating system
		String vmm = "Xen";
		double time_zone = 10.0; // time zone this resource located
		double cost = 3.0; // the cost of using processing in this resource
		double costPerMem = 0.05; // the cost of using memory in this resource
		double costPerStorage = 0.001; // the cost of using storage in this resource
		double costPerBw = 0.0; // the cost of using bw in this resource
		double schedulingInterval = 60;

		DatacenterCharacteristics characteristics = new DatacenterCharacteristics(arch, os, vmm, hostList, time_zone, cost, costPerMem, costPerStorage, costPerBw);

		Datacenter datacenter = null;
		try {
			if (simulationLength == -1) {
				datacenter = datacenterClass.getConstructor(
					String.class,
					DatacenterCharacteristics.class,
					VmAllocationPolicy.class,
					List.class,
					Double.TYPE).newInstance(
							name,
							characteristics,
							vmAllocationPolicy,
							new LinkedList<Storage>(),
							schedulingInterval);
			} else {
				datacenter = datacenterClass.getConstructor(
						String.class,
						DatacenterCharacteristics.class,
						VmAllocationPolicy.class,
						List.class,
						Double.TYPE,
						Double.TYPE).newInstance(
								name,
								characteristics,
								vmAllocationPolicy,
								new LinkedList<Storage>(),
								schedulingInterval,
								simulationLength);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return datacenter;
	}

	/**
	 * Gets the average.
	 *
	 * @param list the list
	 *
	 * @return the average
	 */
	public static double getAverage(List<? extends Number> list) {
		Double total = 0.0;
		for (Number item : list) {
			total += item.doubleValue();
		}
		return total / list.size();
	}

	/**
	 * Prints the results.
	 *
	 * @param datacenter the datacenter
	 * @param lastClock the last clock
	 * @param outputInCsv
	 */
	public static void printResults(PowerDatacenter datacenter, double lastClock, String experimentName, boolean outputInCsv) {
		double totalTotalRequested = 0;
		double totalTotalAllocated = 0;
	    ArrayList<Double> sla = new ArrayList<Double>();
	    int numberOfAllocations = 0;
		for (Entry<String, List<List<Double>>> entry : datacenter.getUnderAllocatedMips().entrySet()) {
		    List<List<Double>> underAllocatedMips = entry.getValue();
		    double totalRequested = 0;
		    double totalAllocated = 0;
		    for (List<Double> mips : underAllocatedMips) {
		    	if (mips.get(0) != 0) {
		    		numberOfAllocations++;
		    		totalRequested += mips.get(0);
		    		totalAllocated += mips.get(1);
		    		double _sla = (mips.get(0) - mips.get(1)) / mips.get(0) * 100;
		    		if (_sla > 0) {
		    			sla.add(_sla);
		    		}
		    	}
			}
		    totalTotalRequested += totalRequested;
		    totalTotalAllocated += totalAllocated;
		}

		double averageSla = 0;
		if (sla.size() > 0) {
		    double totalSla = 0;
		    for (Double _sla : sla) {
		    	totalSla += _sla;
			}
		    averageSla = totalSla / sla.size();
		}

		Log.setDisabled(false);

		if (outputInCsv) {
			Log.print(experimentName + "\t");

			Scanner scanner = new Scanner(experimentName);
			scanner.useDelimiter("_");
			for (int i = 0; i < 10; i++) {
				if (scanner.hasNext()) {
					Log.print(scanner.next() + "\t");
				} else {
					Log.print("\t");
				}
			}
			scanner.close();

			Log.print(String.format("%.3f", datacenter.getPower() / (3600 * 1000)) + "\t"); // energy consumption kWh
			Log.print(String.format("%d", datacenter.getMigrationCount()) + "\t"); // number of VM mirgations
			Log.print(String.format("%.5f", (totalTotalRequested - totalTotalAllocated) / totalTotalRequested) + "\t"); // overall SLA violation
			Log.print(String.format("%.5f", (double) sla.size() / numberOfAllocations) + "\t"); // SLA violation percentage
			Log.print(String.format("%.5f", averageSla) + "\t"); // average SLA
			Log.print(String.format("%.2f", lastClock)); // simulation time
			Log.printLine();
		} else {
			Log.printLine();
			Log.printLine(String.format("Total simulation time: %.2f sec", lastClock));
			Log.printLine(String.format("Energy consumption is: %.2f kWh", datacenter.getPower() / (3600 * 1000)));
			Log.printLine(String.format("Number of VM migrations: %d", datacenter.getMigrationCount()));
			//Log.printLine(String.format("Number of SLA violations: %d", sla.size()));
			//Log.printLine(String.format("SLA violation percentage: %.2f%%", (double) sla.size() * 100 / numberOfAllocations));
			//Log.printLine(String.format("Average SLA violation: %.2f%%", averageSla));
			Log.printLine(String.format("Overall SLA violation: %.2f%%", 100 * (totalTotalRequested - totalTotalAllocated) / totalTotalRequested));
			Log.printLine();
		}

		Log.setDisabled(true);

	}

	/**
	 * Prints the Cloudlet objects.
	 *
	 * @param list list of Cloudlets
	 */
	public static void printCloudletList(List<Cloudlet> list) {
		int size = list.size();
		Cloudlet cloudlet;

		String indent = "\t";
		Log.printLine();
		Log.printLine("========== OUTPUT ==========");
		Log.printLine("Cloudlet ID" + indent + "STATUS" + indent
				+ "Resource ID" + indent + "VM ID" + indent + "Time" + indent
				+ "Start Time" + indent + "Finish Time");

		DecimalFormat dft = new DecimalFormat("###.##");
		for (int i = 0; i < size; i++) {
			cloudlet = list.get(i);
			Log.print(indent + cloudlet.getCloudletId());

			if (cloudlet.getCloudletStatus() == Cloudlet.SUCCESS) {
				Log.printLine(indent + "SUCCESS"
					+ indent + indent + cloudlet.getResourceId()
					+ indent + cloudlet.getVmId()
					+ indent + dft.format(cloudlet.getActualCPUTime())
					+ indent + dft.format(cloudlet.getExecStartTime())
					+ indent + indent + dft.format(cloudlet.getFinishTime())
				);
			}
		}
	}

}
