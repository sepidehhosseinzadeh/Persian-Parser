import java.util.*;

import static java.lang.Math.*;

public class CKYparse
{
	public static void main(String[] args)
	{
		// to do with table of parse
		Scanner scanner = new Scanner(System.in);

		int n = scanner.nextInt();    // number of words
		int m = scanner.nextInt();   // number of grammars

		String[] words = new String[n];
		String[] grammars = new String[m];

		CKYParse(words, grammars);


	}

	/*function CKY-PARSE(words, grammar) returns table
		for j←from 1 to LENGTH(words) do
			table[ j−1, j]←{A | A → words[j] ∈ grammar }
			for i←from j−2 downto 0 do
				for k←i+1 to j−1 do
					table[i,j]←table[i,j] ∪ {A | A → BC ∈ grammar, B ∈ table[i, k], C ∈ table[k, j] }*/

	private static ArrayList<String>[][] CKYParse(String[] words, String[] grammars)
	{
		int n = words.length;
		ArrayList<String>[][] table = new ArrayList[n+1][n+1];

		for(int j = 1; j <= n; j++)
		{
			ArrayList<String> res = getGrammars(grammars, words[j]);
			for(String s: res)
				table[j-1][j].add(s);

			for(int i = j-2; i >= 0; i--)
				for(int k = i+1; k <= j-1; k++)
				{
					res = getComplexGrammars(grammars, table);
					for(String s: res)
						table[i][j].add(s);
				}
		}

		return table;

	}

	/*{A | A → BC ∈ grammar, B ∈ table[i, k], C ∈ table[k, j] }*/
	private static ArrayList<String> getComplexGrammars(String[] grammars, ArrayList<String>[][] table)
	{
		int n = table[0].length;
		ArrayList<String> res = new ArrayList<String>();

		for(String grammar: grammars)
		{
			String left = grammar.split(">")[0];
			String right = grammar.split(">")[1];

			for(int idx = 0; idx < right.length(); idx++)
				for(int i = 0; i < n; i++)
					for(int j = 0; j < n; j++)
						for(int k = 0; k < n; k++)
							if(table[i][k].contains(right.substring(0, idx)) && table[k][j].contains(right.substring(idx)))
								res.add(left);

		}
		return res;
	}


	/*{A | A → words[j] ∈ grammar }*/
	private static ArrayList<String> getGrammars(String[] grammars, String word)
	{
		ArrayList<String> res = new ArrayList<String>();

		for(String grammar: grammars)
			if(grammar.split(">")[1].equals(word))  // suppose that grammars is like: A > a , so, split give us [A,a]
				res.add(grammar.split(">")[0]);

		return res;

	}


}
