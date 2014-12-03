import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Genetic {

	private ArrayList<String> sequences = new ArrayList<>();
	private ArrayList<Individual> population = new ArrayList<>();

	// private int generation = 0;

	public void readFile() {
		try {
			File newFile = new File("p53.fasta");
			FileReader fw = new FileReader(newFile.getAbsoluteFile());
			BufferedReader out = new BufferedReader(fw);
			String line;
			String tempSequence = "";
			while ((line = out.readLine()) != null) {
				if (line.length() > 0) {
					char c = line.charAt(0);
					if (c == 'A' || c == 'C' || c == 'T' || c == 'G') {
						tempSequence += line.trim();
					} else {
						if (!tempSequence.isEmpty()) {
							sequences.add(tempSequence);
							tempSequence = "";
						}
					}
				}
			}
			if (!tempSequence.isEmpty()) {
				sequences.add(tempSequence);
			}
			out.close();

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public void writeToFile(){
		try{
			File newFile = new File("saida.fasta");
			FileWriter fw = new FileWriter(newFile.getAbsoluteFile());
			BufferedWriter out = new BufferedWriter(fw);

			for (Individual ind : getPopulation()) {
				out.write(ind.toString() + 	"\n");
				
			}
			out.close();
		}catch(Exception e){
			
		}
		
	}
	
	public ArrayList<String> getSequences() {
		return sequences;
	}

	public void setSequences(ArrayList<String> sequences) {
		this.sequences = sequences;
	}

	private float find(String motif, String seq) {
		if (motif.length() != seq.length()) {
			return 0;
		}
		float match = 0;
		for (int i = 0; i < motif.length(); i++) {
			if (motif.charAt(i) == seq.charAt(i)) {
				match++;
			}
		}
		return match / motif.length();
	}

	public float findInSequence(Individual ind, String seq, boolean verbose) {
		float match, temp = 0;
		String s = "";
		String subSeq = "";
		String tempSeq = "";
		String motif = ind.getSequence();
		for (int i = 0; i < seq.length() - motif.length(); i++) {
			subSeq = seq.substring(i, i + motif.length());
			match = find(motif, subSeq);
			if (match >= 0.7 && match > temp) {
				temp = match;
				tempSeq = subSeq;
				if (verbose) {
					s += subSeq + " " + i + " - " + (i + motif.length())
							+ " | ";
				}
			}
		}
		if (!s.equals("")) {
			System.out.println(s);
		}
		if (temp > 0) {
			ind.setPresence(ind.getPresence() + 1);
			ind.setFitness(ind.getFitness() + temp);
			ind.getMatches().add(tempSeq);
		}
		return temp;
	}

	public void findInAllSequences(Individual ind, boolean verb) {
		if (ind.getFitness() == 0) {
			for (String seq : sequences) {
				findInSequence(ind, seq, verb);
			}
		}
	}

	public String generateMotif(int size) {
		Random r = new Random();
		String[] nucleotides = { "A", "C", "T", "G" };
		String motif = "";
		for (int i = 0; i < size; i++) {
			motif += nucleotides[r.nextInt(4)];
		}
		return motif;
	}

	public void generatePopulation(int size, int motifSize) {
		for (int i = 0; i < size; i++) {
			Individual ind = new Individual(generateMotif(motifSize));
			population.add(ind);
		}
	}

	public Individual crossOver(Individual ind1, Individual ind2) {
		Random r = new Random();
		int divPoint = r.nextInt(ind1.getSequence().length() - 3) + 1;
		String seq = ind1.getSequence().substring(0, divPoint);
		seq += ind2.getSequence().substring(divPoint);

		if (r.nextFloat() < 0.10) {
			String[] nucleotides = { "A", "C", "T", "G" };
			String n = "ACTG";
			int pos = r.nextInt(ind1.getSequence().length());
			if (ind1.matrix()[n.lastIndexOf(ind1.getSequence().charAt(pos))][pos] != 1
					&& ind2.matrix()[n.lastIndexOf(ind2.getSequence().charAt(
							pos))][pos] != 1) {

				String p1 = seq.substring(0, pos);
				String p2 = seq.substring(pos + 1);
				seq = p1 + nucleotides[r.nextInt(4)] + p2;
			}
		}

		Individual ind = new Individual(seq);

		if (!population.contains(ind)) {
			return ind;
		}
		return new Individual(generateMotif(ind1.getSequence().length()));

	}
	
	public Individual roulletSelection(){
		int totalFitness = 0;
		int temp = 0;
		int selected;
		Random r = new Random();
		for(Individual ind : population){
			totalFitness += ind.getFitness();
		}
		selected = r.nextInt(totalFitness);
		for(Individual ind : population){
			temp += ind.getFitness();
			if(temp > selected){
				return ind;
			}
		}
		
		
		return null;
	}
	
	public void run(int numGen) {
		for (int gen = 0; gen < numGen; gen++) {
			// System.out.println(gen + " " + population.size());
			// while (true) {
			for (Individual ind : population) {
				findInAllSequences(ind, false);
			}
			Collections.sort(population, new CompareIndividual());
			// if (population.get(0).getPresence() >= sequences.size()) {
			// break;
			// }
			// population.get(0).matrix();

			if (gen == numGen - 1) {
				break;
			}

			for (int k = 0; k < 10; k++) {
				System.out.println(population.get(k));
			}
			System.out.println("-------------------------------");
			ArrayList<Individual> newPopulation = new ArrayList<>();
			Random r = new Random();
			float i = 0;
			for (int j = 0; j < population.size(); j += 1) {
				if (i / population.size() <= 0.90) {
					int x = r.nextInt((int) (population.size() * 0.5));
					Individual newInd1 = crossOver(population.get(x),
							population.get(j + 1));
					x = r.nextInt((int) (population.size() * 0.5));
					Individual newInd2 = crossOver(population.get(x),
							population.get(j));
					newPopulation.add(newInd1);
					newPopulation.add(newInd2);

				} else {
					break;
				}
				i += 2;
			}
			for (int j = 0; j < population.size() - i; j++) {
				newPopulation.add(population.get((int) j));
			}
			/*
			 * for(int k=0;k<newPopulation.size();k++){
			 * System.out.println(population.get(k) + " " +
			 * newPopulation.get(k)); }
			 */
			// System.out.println("----------------------------------------------------------------");
			population = (ArrayList<Individual>) newPopulation.clone();
		}
		/*
		 * for (Individual ind : population) { System.out.println(ind); }
		 */
		System.out.println("------------------------------------");

	}

	public ArrayList<Individual> getPopulation() {
		return population;
	}

	public void setPopulation(ArrayList<Individual> population) {
		this.population = population;
	}

	public static void main(String[] args) {
		Genetic g = new Genetic();
		g.readFile();
		System.out.println(g.getSequences().size() + " Sequences");
		for (int k = 0; k < 2; k++) {
			g.setPopulation(new ArrayList<>());
			g.generatePopulation(100, 15);
			g.run(100);

			System.out.println();

			int i = 0;
			System.out.println("finished");
			for (Individual ind : g.getPopulation()) {
				// if (ind.getPresence() == g.getSequences().size()) {
				if (i < 10) {
					// System.out.println(ind + " " + ind.consensus() + " " +
					// ind.getMatches().size());
					ind.writeToFile();
				} else {
					break;
				}
				i++;
			}
			g.writeToFile();
		}
	}
}
