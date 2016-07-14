package org.cloudbus.cloudsim;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

/**
 * The Class UtilizationModelPlanetLab.
 */
public class UtilizationModelPlanetLabInMemory implements UtilizationModel {

	/** The data. */
	private double[] data;

	/**
	 * Instantiates a new utilization model PlanetLab.
	 *
	 * @param inputPath the input path
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 * @throws NumberFormatException the number format exception
	 */
	public UtilizationModelPlanetLabInMemory(String inputPath, double simulationLength) throws NumberFormatException, IOException {
		int n = (int) simulationLength;
		if ((int) simulationLength == -1) {
			n = getNumberOfLines(inputPath);
		}
		setData(new double[n]);
		BufferedReader input = new BufferedReader(new FileReader(inputPath));
		String line;
		for (int i = 0; i < n && ((line = input.readLine()) != null); i++) {
			getData()[i] = Double.parseDouble(line);
		}
		input.close();
	}

	/* (non-Javadoc)
	 * @see cloudsim.power.UtilizationModel#getUtilization(double)
	 */
	public double getUtilization(double time) {
		int lineNumber = (int) time;
		if (lineNumber >= getData().length) {
			return 0.0;
		}
		return getData()[lineNumber];
	}

	/**
	 * Count lines.
	 *
	 * @param filename the filename
	 *
	 * @return the int
	 *
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected int getNumberOfLines(String filename) {
	    byte[] c = new byte[1024];
	    int count = 1;
	    int readChars = 0;
		InputStream is = null;
		try {
			is = new BufferedInputStream(new FileInputStream(filename));
		    while ((readChars = is.read(c)) != -1) {
		        for (int i = 0; i < readChars; ++i) {
		            if (c[i] == '\n') {
						++count;
					}
		        }
		    }
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	    return count;
	}

	/**
	 * Gets the data.
	 *
	 * @return the data
	 */
	protected double[] getData() {
		return data;
	}

	/**
	 * Sets the data.
	 *
	 * @param data the new data
	 */
	protected void setData(double[] data) {
		this.data = data;
	}

}
