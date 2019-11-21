import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.Reader;

public class TestingSuite {

	private static int testNumLots = 5;
	private static int testNumFew = 3;

	private static int antNumBig = 30;
	private static int antNumSmall = 15;

	private static double alpha = 1;
	private static double betaBig = 5;
	private static double betaSmall = 3;
	private static double rho = 0.1; // evaporation rate
	private static double epsilon = 0.1; // decay coefficient
	private static double eliteNum = antNumBig;
	private static double qNot = 0.9;

	private String twoKTest;
	private String threeKTest;
	private String fourKTest;
	private String fiveKTest;

	/**
	 * Constructor, which instantiates the particular problems we want to test our
	 * algorithm on.
	 */
	public TestingSuite() {

		this.twoKTest = "d2103.tsp";
		this.threeKTest = "pcb3038.tsp";
		this.fourKTest = "fnl4461.tsp";
		this.fiveKTest = "rl5915.tsp";
	}

	/**
	 * Helper method to read in the file and problem information.
	 * 
	 * @param filename The name of the file we want to read in.
	 * @return A list of City objects (i.e. our problem information)
	 */
	public static List<City> getCityList(String filename) {
		String data = null;
		int dimension = 0;
		String edgeWeightType = "";
		List<City> cities = new ArrayList<City>();

		try {
			File initialFile = new File(filename);
			initialFile.createNewFile();

			Reader reader = new FileReader(initialFile);

			BufferedReader buffReader = new BufferedReader(reader);

			boolean first = true;
			int counter = 1;

			while ((data = buffReader.readLine()) != null) {
				City city;
				if (first) {
					if (data.contains("EDGE_WEIGHT_TYPE")) {
						edgeWeightType = data.split(" ")[1];
					} else if (data.contains("DIMENSION")) {
						dimension = Integer.parseInt(data.replace(" ", "").split(":")[1]);
					} else if (data.contains("NODE_COORD_SECTION")) {
						first = false;
					}
				} else {
					if (counter <= dimension) {
						String[] temp = data.trim().replace("  ", " ").replace("  ", " ").split(" ");
						double xCoord = Double.parseDouble(temp[1].trim());
						double yCoord = Double.parseDouble(temp[2].trim());
						city = new City(counter, xCoord, yCoord);
						cities.add(city);
						counter++;
					}
				}
			}
			buffReader.close();
		} catch (IOException e) {
			System.out.println("IO Exception");
		}
		return cities;
	}

	/***********************************
	 * Running on problems of differing size
	 *******************************************/

	// helper method to find the best medians over each run of the algorithm
	public static List<Double> processList(List<List<Double>> doubleList) {

		List<Double> results = new ArrayList<Double>();

		for (int i = 0; i < doubleList.get(0).size(); i++) {
			List<Double> nums = new ArrayList<Double>();
			for (int j = 0; j < doubleList.size(); j++) {
				nums.add(doubleList.get(j).get(i));
			}
			Collections.sort(nums);

			int medianIndex = nums.size() / 2;

			double median = nums.get(medianIndex);
			results.add(median);
		}
		return results;
	}

	// run both ACS and EAS for the 2k problem size
	public void runTwoThousands() {

		List<City> cities = getCityList(this.twoKTest);
		System.out.println(cities.size());
		List<List<Double>> acsList = new ArrayList<List<Double>>();
		List<List<Double>> easList = new ArrayList<List<Double>>();

		for (int i = 0; i < testNumLots; i++) {
			List<Double> bests = new ArrayList<Double>();
			Environment env = new Environment(cities.size(), antNumBig, alpha, betaBig, rho, eliteNum, epsilon, 0.1,
					qNot);
			AntColonySystem acs = new AntColonySystem(env, cities, 50);
			bests = acs.optimize();
			acsList.add(bests);
		}

		for (int i = 0; i < testNumLots; i++) {
			List<Double> bests = new ArrayList<Double>();
			Environment env = new Environment(cities.size(), antNumBig, alpha, betaBig, rho, eliteNum, epsilon, 0.1,
					qNot);
			ElitistAntSystem eas = new ElitistAntSystem(env, cities, 50);
			bests = eas.optimize();
			easList.add(bests);
		}

		List<Double> mediansACS = processList(acsList);
		List<Double> mediansEAS = processList(easList);

		System.out.println("Printing ACS medians for 2k:");
		for (int i = 0; i < mediansACS.size(); i++) {
			System.out.println(mediansACS.get(i));
		}

		System.out.println("Printing EAS medians for 2k");
		for (int i = 0; i < mediansEAS.size(); i++) {
			System.out.println(mediansEAS.get(i));
		}

	}

	// run both ACS and EAS for the 3k problem size
	public void runThreeThousands() {
		List<City> cities = getCityList(this.threeKTest);
		List<List<Double>> acsList = new ArrayList<List<Double>>();
		List<List<Double>> easList = new ArrayList<List<Double>>();

		for (int i = 0; i < testNumLots; i++) {
			List<Double> bests = new ArrayList<Double>();
			Environment env = new Environment(cities.size(), antNumBig, alpha, betaBig, rho, eliteNum, epsilon, 0.1,
					qNot);
			AntColonySystem acs = new AntColonySystem(env, cities, 50);
			bests = acs.optimize();
			acsList.add(bests);
		}

		for (int i = 0; i < testNumLots; i++) {
			List<Double> bests = new ArrayList<Double>();
			Environment env = new Environment(cities.size(), antNumBig, alpha, betaBig, rho, eliteNum, epsilon, 0.1,
					qNot);
			ElitistAntSystem eas = new ElitistAntSystem(env, cities, 50);
			bests = eas.optimize();
			easList.add(bests);
		}

		List<Double> mediansACS = processList(acsList);
		List<Double> mediansEAS = processList(easList);

		System.out.println("Printing ACS medians for 3k:");
		for (int i = 0; i < mediansACS.size(); i++) {
			System.out.println(mediansACS.get(i));
		}

		System.out.println("Printing EAS medians for 3k");
		for (int i = 0; i < mediansEAS.size(); i++) {
			System.out.println(mediansEAS.get(i));
		}

	}

	// run both ACS and EAS for the 4k problem size
	public void runFourThousands() {
		List<City> cities = getCityList(this.fourKTest);
		List<List<Double>> acsList = new ArrayList<List<Double>>();
		List<List<Double>> easList = new ArrayList<List<Double>>();

		for (int i = 0; i < testNumFew; i++) {
			List<Double> bests = new ArrayList<Double>();
			Environment env = new Environment(cities.size(), antNumBig, alpha, betaBig, rho, eliteNum, epsilon, 0.1,
					qNot);
			AntColonySystem acs = new AntColonySystem(env, cities, 50);
			bests = acs.optimize();
			acsList.add(bests);
		}

		for (int i = 0; i < testNumFew; i++) {
			List<Double> bests = new ArrayList<Double>();
			Environment env = new Environment(cities.size(), antNumBig, alpha, betaBig, rho, eliteNum, epsilon, 0.1,
					qNot);
			ElitistAntSystem eas = new ElitistAntSystem(env, cities, 50);
			bests = eas.optimize();
			easList.add(bests);
		}

		List<Double> mediansACS = processList(acsList);
		List<Double> mediansEAS = processList(easList);

		System.out.println("Printing ACS medians for 4k:");
		for (int i = 0; i < mediansACS.size(); i++) {
			System.out.println(mediansACS.get(i));
		}

		System.out.println("Printing EAS medians for 4k");
		for (int i = 0; i < mediansEAS.size(); i++) {
			System.out.println(mediansEAS.get(i));
		}

	}

	// run both ACS and EAS for the 5k problem size
	public void runFiveThousands() {
		List<City> cities = getCityList(this.fiveKTest);
		List<List<Double>> acsList = new ArrayList<List<Double>>();
		List<List<Double>> easList = new ArrayList<List<Double>>();

		for (int i = 0; i < testNumFew; i++) {
			List<Double> bests = new ArrayList<Double>();
			Environment env = new Environment(cities.size(), antNumBig, alpha, betaBig, rho, eliteNum, epsilon, 0.1,
					qNot);
			AntColonySystem acs = new AntColonySystem(env, cities, 50);
			bests = acs.optimize();
			acsList.add(bests);
		}

		for (int i = 0; i < testNumFew; i++) {
			List<Double> bests = new ArrayList<Double>();
			Environment env = new Environment(cities.size(), antNumBig, alpha, betaBig, rho, eliteNum, epsilon, 0.1,
					qNot);
			ElitistAntSystem eas = new ElitistAntSystem(env, cities, 50);
			bests = eas.optimize();
			easList.add(bests);
		}

		List<Double> mediansACS = processList(acsList);
		List<Double> mediansEAS = processList(easList);

		System.out.println("Printing ACS medians for 5k:");
		for (int i = 0; i < mediansACS.size(); i++) {
			System.out.println(mediansACS.get(i));
		}

		System.out.println("Printing EAS medians for 5k");
		for (int i = 0; i < mediansEAS.size(); i++) {
			System.out.println(mediansEAS.get(i));
		}
	}

	/***************************
	 * Tests that adjust for differing evaporation factors (rho)
	 ********************************/

	// runs test for specific rho
	public void runRho(double specificRho) {
		List<City> cities = getCityList(this.twoKTest);
		List<List<Double>> acsList = new ArrayList<List<Double>>();
		List<List<Double>> easList = new ArrayList<List<Double>>();

		for (int i = 0; i < testNumFew; i++) {
			List<Double> bests = new ArrayList<Double>();
			Environment env = new Environment(cities.size(), antNumBig, alpha, betaBig, specificRho, eliteNum, epsilon,
					0.1, qNot);
			AntColonySystem acs = new AntColonySystem(env, cities, 50);
			bests = acs.optimize();
			acsList.add(bests);
		}

		for (int i = 0; i < testNumFew; i++) {
			List<Double> bests = new ArrayList<Double>();
			Environment env = new Environment(cities.size(), antNumBig, alpha, betaBig, specificRho, eliteNum, epsilon,
					0.1, qNot);
			ElitistAntSystem eas = new ElitistAntSystem(env, cities, 50);
			bests = eas.optimize();
			easList.add(bests);
		}

		List<Double> mediansACS = processList(acsList);
		List<Double> mediansEAS = processList(easList);

		System.out.println("Printing ACS medians for 2k evaporation changes: rho = " + specificRho);
		for (int i = 0; i < mediansACS.size(); i++) {
			System.out.println(mediansACS.get(i));
		}

		System.out.println("Printing EAS medians for 2k evaporation changes: rho = " + specificRho);
		for (int i = 0; i < mediansEAS.size(); i++) {
			System.out.println(mediansEAS.get(i));
		}
	}

	// tests for 4 different values of rho that are not 0.1 for both ACS and EAS
	public void runEvaporationChanges() {
		System.out.println("Running tests for changes in rho -> evaporation rate");

		System.out.println("TESTING FOR RHO = 0.01");
		this.runRho(0.01);
		System.out.println("TESTING FOR RHO = 0.05");
		this.runRho(0.05);
		System.out.println("TESTING FOR RHO = 0.3");
		this.runRho(0.3);
		System.out.println("TESTING FOR RHO = 0.5");
		this.runRho(0.5);

	}

	public static void main(String args[]) {

		TestingSuite ts = new TestingSuite();

		System.out.println("TWO THOUSANDS TEST");
		ts.runTwoThousands();

		System.out.println("THREE THOUSANDS TEST");
		ts.runThreeThousands();

		System.out.println("FOUR THOUSANDS TEST");
		ts.runFourThousands();

		System.out.println("FIVE THOUSANDS TEST");
		ts.runFiveThousands();

		System.out.println("RHO TEST");
		ts.runEvaporationChanges();
	}
}