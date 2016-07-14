package org.cloudbus.cloudsim;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import org.cloudbus.cloudsim.core.CloudSim;
import org.cloudbus.cloudsim.power.PowerDatacenter;
import org.cloudbus.cloudsim.power.PowerDatacenterSimulationLimit;
import org.cloudbus.cloudsim.power.PowerHost;
import org.cloudbus.cloudsim.power.PowerVmAllocationPolicyDoubleThresholdMinimizationOfMigrations;
import org.cloudbus.cloudsim.JFreeSeriesLineChart;
public class DoubleThresholdMinimizationOfMigrations {

	/**
	 * Creates main() to run this example.
	 *
	 * @param args the args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		String inputFolderName = "abc";
		String outputFolderName = "def";
		String experimentName = "exp";

		boolean enableOutput = true;
	/*	if (args[3].equals("enableOutput")) {
			enableOutput = true;
		}
	*/
		boolean outputToFile = false;
	/*	if (args[4].equals("outputToFile")) {
			outputToFile = true;
		}
	*/
		boolean outputInCsv = false;
	/*	if (args[5].equals("csv")) {
			outputInCsv = true;
		}
	*/
		int cloudletsNumber = 45;
		int hostsNumber = 100;
		double simulationLength = 20000.0;
		double utilizationThresholdLower = 30.0;
		double utilizationThresholdUpper = 70.0;

		if (outputToFile) {
			File outpuFile = new File(outputFolderName + "/" + experimentName + ".txt");
			outpuFile.delete();
			outpuFile.createNewFile();
			Log.setOutput(new FileOutputStream(outpuFile));
		}

		Log.setDisabled(!enableOutput);
		Log.printLine("Starting DoubleThresholdMinimizationOfMigrations simulation - " + experimentName);

		try {
			CloudSim.init(1, Calendar.getInstance(), false);

			DatacenterBroker broker = Helper.createBroker();
			int brokerId = broker.getId();

			//List<Cloudlet> cloudletList = Helper.createCloudletListPlanetLab(brokerId, inputFolderName, simulationLength, cloudletsNumber);
			List<Cloudlet> cloudletList = Helper.createCloudletList(brokerId, cloudletsNumber);
			List<Vm> vmList = Helper.createVmList(brokerId, cloudletList.size());
			List<PowerHost> hostList = Helper.createHostList(hostsNumber);

			VmAllocationPolicy vmAllocationPolicy = new PowerVmAllocationPolicyDoubleThresholdMinimizationOfMigrations(
					hostList, utilizationThresholdLower, utilizationThresholdUpper);

			PowerDatacenter datacenter = null;
			if (simulationLength != -1) {
				datacenter = (PowerDatacenter) Helper.createDatacenter(
					"Datacenter",
					PowerDatacenterSimulationLimit.class,
					hostList,
					vmAllocationPolicy,
					simulationLength);
			} else {
				datacenter = (PowerDatacenter) Helper.createDatacenter(
					"Datacenter",
					PowerDatacenter.class,
					hostList,
					vmAllocationPolicy,
					simulationLength);				
			}

			broker.submitVmList(vmList);
			broker.submitCloudletList(cloudletList);

			double lastClock = CloudSim.startSimulation();
			
			
			List<Cloudlet> newList = broker.getCloudletReceivedList();
			Log.printLine("Received " + cloudletList.size() + " cloudlets");

			CloudSim.stopSimulation();

			Helper.printResults(datacenter, lastClock, experimentName, outputInCsv);

		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("Unwanted errors happen");
		}
		JFreeSeriesLineChart lc = new JFreeSeriesLineChart("My line chart");
		lc.main(null);
	}

}

	/**
	public static void main(String[] args) throws IOException {
		String inputFolderName = args[0];
		String outputFolderName = args[1];
		String experimentName = args[2];
		boolean enableOutput = false;
		if (args[3].equals("enableOutput")) {
			enableOutput = true;
		}
		boolean outputToFile = false;
		if (args[4].equals("outputToFile")) {
			outputToFile = true;
		}
		boolean outputInCsv = false;
		if (args[5].equals("csv")) {
			outputInCsv = true;
		}
		int cloudletsNumber = Integer.parseInt(args[6]);
		int hostsNumber = Integer.parseInt(args[7]);
		double simulationLength = Double.parseDouble(args[8]);
		double utilizationThresholdLower = Double.parseDouble(args[9]);
		double utilizationThresholdUpper = Double.parseDouble(args[10]);

		if (outputToFile) {
			File outpuFile = new File(outputFolderName + "/" + experimentName + ".txt");
			outpuFile.delete();
			outpuFile.createNewFile();
			Log.setOutput(new FileOutputStream(outpuFile));
		}

		Log.setDisabled(!enableOutput);
		Log.printLine("Starting DoubleThresholdMinimizationOfMigrations simulation - " + experimentName);

		try {
			CloudSim.init(1, Calendar.getInstance(), false);

			DatacenterBroker broker = Helper.createBroker();
			int brokerId = broker.getId();

			List<Cloudlet> cloudletList = Helper.createCloudletListPlanetLab(brokerId, inputFolderName, simulationLength, cloudletsNumber);
			//List<Cloudlet> cloudletList = Helper.createCloudletList(brokerId, cloudletsNumber);
			List<Vm> vmList = Helper.createVmList(brokerId, cloudletList.size());
			List<PowerHost> hostList = Helper.createHostList(hostsNumber);

			VmAllocationPolicy vmAllocationPolicy = new PowerVmAllocationPolicyDoubleThresholdMinimizationOfMigrations(
					hostList, utilizationThresholdLower, utilizationThresholdUpper);

			PowerDatacenter datacenter = null;
			if (simulationLength != -1) {
				datacenter = (PowerDatacenter) Helper.createDatacenter(
					"Datacenter",
					PowerDatacenterSimulationLimit.class,
					hostList,
					vmAllocationPolicy,
					simulationLength);
			} else {
				datacenter = (PowerDatacenter) Helper.createDatacenter(
					"Datacenter",
					PowerDatacenter.class,
					hostList,
					vmAllocationPolicy,
					simulationLength);				
			}

			broker.submitVmList(vmList);
			broker.submitCloudletList(cloudletList);

			double lastClock = CloudSim.startSimulation();

			List<Cloudlet> newList = broker.getCloudletReceivedList();
			Log.printLine("Received " + newList.size() + " cloudlets");

			CloudSim.stopSimulation();

			Helper.printResults(datacenter, lastClock, experimentName, outputInCsv);

		} catch (Exception e) {
			e.printStackTrace();
			Log.printLine("Unwanted errors happen");
		}

		Log.printLine("DoubleThresholdMinimizationOfMigrations simulation finished!");
	}

}
*/

