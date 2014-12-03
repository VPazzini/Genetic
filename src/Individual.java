import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class Individual {
	private float fitness = 0;
	private String sequence;
	private int presence = 0;
	private ArrayList<String> matches = new ArrayList<>();
	String[] nucleotides = { "A", "C", "T", "G" };

	public Individual(float fitness, String sequence) {
		this.fitness = fitness;
		this.sequence = sequence;
	}

	public Individual(String sequence) {
		this.sequence = sequence;
	}

	public float getFitness() {
		return fitness;
	}

	public void setFitness(float fitness) {
		this.fitness = fitness;
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}

	public int getPresence() {
		return presence;
	}

	public void setPresence(int presence) {
		this.presence = presence;
	}

	public ArrayList<String> getMatches() {
		return matches;
	}

	public float[][] matrix() {
		float[][] matrix = new float[4][sequence.length()];
		for (int i = 0; i < sequence.length(); i++) {
			for (int j = 0; j < matches.size(); j++) {
				switch (matches.get(j).charAt(i)) {
				case ('A'):
					matrix[0][i]++;
					break;
				case ('C'):
					matrix[1][i]++;
					break;
				case ('T'):
					matrix[2][i]++;
					break;
				case ('G'):
					matrix[3][i]++;
					break;
				}
			}
		}
		for (int i = 0; i < sequence.length(); i++) {
			for (int j = 0; j < 4; j++) {
				matrix[j][i] /= matches.size();
			}
		}
		return matrix;
	}

	public String consensus() {
		float[][] m = matrix();
		String cons = "";
		float max = 0;
		int pos = 0;
		for (int i = 0; i < sequence.length(); i++) {
			max = 0;
			for (int j = 0; j < 4; j++) {
				if (m[j][i] > max) {
					max = m[j][i];
					pos = j;
				}
			}
			cons += nucleotides[pos];
		}

		return cons;
	}

	@Override
	public String toString() {
		return "Individual [fitness=" + fitness + ", sequence=" + sequence
				+ ", presence=" + presence + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Float.floatToIntBits(fitness);
		result = prime * result
				+ ((sequence == null) ? 0 : sequence.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Individual other = (Individual) obj;
		if (sequence == null) {
			if (other.sequence != null)
				return false;
		} else if (!sequence.equals(other.sequence))
			return false;
		return true;
	}

	public void writeToFile() {
		BufferedWriter output = null;
		try {
			File file = new File("output.txt");
			output = new BufferedWriter(new FileWriter(file,true));
			// writer = new PrintWriter("output.txt", "UTF-8");

			output.write("Motif " + sequence + "\n");
			output.write("Occurences:\n");
			for (String m : matches) {
				output.write(m + "\n");
			}
			float[][] m = matrix();
			for (int j = 0; j < 4; j++) {
				output.write(nucleotides[j]+ " ");
				for (int i = 0; i < sequence.length(); i++) {
					output.write(String.format("%.4f  ",m[j][i]));
				}
				output.write("\n");
			}
			output.write("--------------------------------------------------------\n");
			output.close();
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
