import java.io.*;
import java.util.*;

public class postTokenizer
{
	static ArrayList<String> dictionary = new ArrayList<String>();
	static String[] morphemes = {"خواهم","خواهی","خواهد","خواهیم","خواهید","خواهند","ب","بی","می","نمی","ها","های","ای","ام","شان","تر","تری","ترین","ایست","است","هایی","هایم","هایت","هایش","هایمان","هایتان","هایشان","هاست","هایم","هاید","هایند","ات","اش","مان","تان","ایم","اید","اند"};
	static boolean[] stickBefore = new boolean[morphemes.length];
	static char[] farsiAlphabet = {'ض','ص','ث','ق','ف','غ','ع','ه','خ','ح','ج','چ','پ','ش','س','ی','ب','ل','ا','آ','ت','ن','م','ک','گ','ظ','ط','ز','ژ','ر','ذ','د','ئ','و'};
	static char[] nonFinalForm = {'ا', 'د', 'ذ', 'ر', 'ز', 'ژ', 'و'};
	static boolean[] ambiguous = new boolean[morphemes.length];
	static ArrayList<ArrayList<String>> tokens = new ArrayList<ArrayList<String>>();
	static ArrayList<String> lexiconWords = new ArrayList<String>();


	public static void main(String[] args)
	{
		for(int i = 10; i < 19; ambiguous[i] = true, i++);
		for(int i = 0; i < 10; stickBefore[i] = true, i++);

		String fileName = "in.txt";
		String line = null;

		// read from lexicon
		try
		{
			FileReader fileReader = new FileReader("Lexicon.txt");
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			while ((line = bufferedReader.readLine()) != null)
			{
				for (String tok : line.split("[A-Z]|[\t]"))
					lexiconWords.add(tok.trim());
			}

		}
		catch(FileNotFoundException ex)
		{
			System.out.println("Unable to open file '"+fileName+"'");
		}
		catch(IOException ex)
		{
			System.out.println("Error reading file '"+ fileName + "'");
		}
		//


		try
		{
			FileReader fileReader = new FileReader(fileName);
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			while((line = bufferedReader.readLine()) != null)
			{
				for(String tok: line.split("[{[( )]},?!;\"]+"))
				{
					ArrayList<String> tmp = new ArrayList<String>();
					tmp.add(tok);
					tokens.add(tmp);
				}

				// check from lexicon
				for(String lexWord: lexiconWords)
				{
					String[] lexTokens = lexWord.split(" ");
					int n = lexTokens.length;

					for(int i = 0; i+n <= tokens.size(); i++)
					{
						String word = "";
						for(int j = i; j < i+n; j++)
							word += tokens.get(j).get(0)+" ";
						word = word.trim();


						String tmp = tokens.get(i).get(0);
						if(word.equals(lexWord))
							for(int j = i+1; j < i+n; j++)
							{
								tokens.get(j).set(0, tmp+" "+tokens.get(j).get(0));
								tmp = tokens.get(j).get(0);
								tokens.remove(j-1);
								j--;
								n--;
							}
					}

				}

				// Check two consecutive tokens
				for(int i = 0; i < tokens.size(); i++)
					if(!lexiconWords.contains(tokens.get(i).get(0)))
					{
						String token_i = tokens.get(i).get(0);

						if (IsMorpheme(token_i) && !ambiguous[getMorphemeIdx(token_i)])
						{
							if(stickBefore[getMorphemeIdx(token_i)])
							{
								if(i+1 >= tokens.size())	continue;

								String jointString = token_i +"~"+ tokens.get(i+1).get(0);
								tokens.get(i+1).set(0, jointString);
								tokens.remove(i);

							}
							else
							{
								if(i-1 < 0)	continue;

								String jointString = tokens.get(i-1).get(0) +"~"+ token_i;
								tokens.get(i).set(0, jointString);
								tokens.remove(i-1);
								i--;
							}
						}
						if (IsMorpheme(token_i) && ambiguous[getMorphemeIdx(token_i)])
						{
							if(stickBefore[getMorphemeIdx(token_i)])
							{
								if(i+1 >= tokens.size())	continue;

								tokens.get(i+1).add(token_i + "~" + tokens.get(i + 1).get(0));
							}
							else
							{
								if(i-1 < 0)	continue;

								tokens.get(i).add(tokens.get(i - 1).get(0) + "~" + token_i);
							}
						}
					}


				for(int i = 0; i < tokens.size(); i++)
					for(int k = 0; k < tokens.get(i).size(); k++)
					{
						String token_i = tokens.get(i).get(k);

						// unknown words
						if (!dictionary.contains(token_i))
						{
							for (int j = 0; j < 7; j++)
								if (token_i.contains(""+nonFinalForm[j]))
								{
									String newWord = (new StringBuffer(token_i).insert(token_i.indexOf(nonFinalForm[j])+1, " ")).toString();
									if(dictionary.contains(newWord))
										tokens.get(i).add(newWord);
								}
						}

						//tokenization rules for Acronyms, Abbreviation, and sentence boundries

						if(IsAcronym(token_i)[1] == 0 && IsAcronym(token_i)[0] == 1 ||
								IsAbbreviation(token_i)[1] == 0 && (IsAbbreviation(token_i)[0] == 1 || IsAbbreviation(token_i)[0] == 4))
						{
							// there is no action to be taken
						}

						if(IsAcronym(token_i)[0] == 1 && IsAcronym(token_i)[1] == 2 ||
								IsAbbreviation(token_i)[1] == 2 && (IsAbbreviation(token_i)[0] == 1 || IsAbbreviation(token_i)[0] == 2 || IsAbbreviation(token_i)[0] == 4))
						{
							//create sentence boundary
							if(token_i.contains("."))
								for(String tok: token_i.split("[.]+"))
									tokens.get(i).add(tok);
						}

						if(IsAcronym(token_i)[0] == 2 && IsAcronym(token_i)[1] == 1)
							for(String tok: token_i.split("[.~]+"))                  // create word token
								if(dictionary.contains(tok))
									tokens.get(i).add(tok);

						if(IsAbbreviation(token_i)[0] == 3)
						{
							for(String tok: token_i.split("[.~]+"))            	// create word token
								if(dictionary.contains(tok))
									tokens.get(i).add(tok);

							//create sentence boundary
							if(token_i.contains("."))
								for(String tok: token_i.split("[.]+"))
									tokens.get(i).add(tok);
						}
					}


				// Check two consecutive tokens again
				for(int i = 0; i < tokens.size(); i++)
					for(int k = 0; k < tokens.get(i).size(); k++)
						if(!lexiconWords.contains(tokens.get(i).get(k)))
						{
							String token_i = tokens.get(i).get(k);

							// check in lexicon
							if(i+1 < tokens.size())
								for(int j = 0; j < tokens.get(i+1).size(); j++)
								{
									String jointString = token_i + "~" + tokens.get(i + 1).get(j);
									if(!lexiconWords.contains(jointString))	continue;

									tokens.get(i).set(k, jointString);
									tokens.get(i+1).remove(j);
									j--;
									if(tokens.get(i+1).isEmpty())
									{
										tokens.remove(i+1);
										i--;
										break;
									}
								}


							if (IsMorpheme(token_i) && !ambiguous[getMorphemeIdx(token_i)])
							{
								if(stickBefore[getMorphemeIdx(token_i)])
								{
									if(i+1 >= tokens.size())	continue;

									for(int j = 0; j < tokens.get(i+1).size(); j++)
									{
										String jointString = token_i + "~" + tokens.get(i + 1).get(j);
										tokens.get(i).set(k, jointString);
										tokens.get(i+1).remove(j);
										j--;
										if(tokens.get(i+1).isEmpty())
										{
											tokens.remove(i+1);
											i--;
											break;
										}
									}
								}
								else
								{
									if(i-1 < 0)	continue;

									for(int j = 0; j < tokens.get(i-1).size(); j++)
									{
										String jointString = tokens.get(i - 1).get(j) + "~" + token_i;
										tokens.get(i).set(k, jointString);
										tokens.get(i - 1).remove(j);
										j--;
										if(tokens.get(i-1).isEmpty())
										{
											tokens.remove(i-1);
											i--;
											break;
										}
									}
								}
							}
							else if (IsMorpheme(token_i) && ambiguous[getMorphemeIdx(token_i)])
							{
								if(stickBefore[getMorphemeIdx(token_i)])
								{
									if(i+1 >= tokens.size())	continue;

									for(int j = 0; j < tokens.get(i+1).size(); j++)
										tokens.get(i+1).add(token_i+"~"+tokens.get(i+1).get(j));
								}
								else
								{
									if(i-1 < 0)	continue;

									for(int j = 0; j < tokens.get(i-1).size(); j++)
										tokens.get(i).add(tokens.get(i-1).get(j)+"~"+token_i);
								}
							}
						}




			}

			bufferedReader.close();
		}
		catch(FileNotFoundException ex)
		{
			System.out.println("Unable to open file '"+fileName+"'");
		}
		catch(IOException ex)
		{
			System.out.println("Error reading file '"+ fileName + "'");
		}



		Write();

	}


	// tokenizer needs to produce 2 kinds of tokens in ambiguous words
	private static int[] IsAbbreviation(String word)     // returns a pair: (format num, ambiguity (1:ambiguous with word , 2:ambiguous with EOS) )
	{
		//in cases 1,2, and 4 tokenizer will produce 2 kinds of tokens: the token is abbrevi, abbrevi with end of sentence

		int[] res = new int[2];
		int n = word.length();

		if(n>=2 && isFinalForm(word.charAt(0)) && word.charAt(1) == '~')
		{
			res[0] = 1;
			if(word.charAt(word.length()-1) == '.')	res[1] = 2;
		}
		else if(isFarsiAlphabetic(word.charAt(0)) && word.charAt(word.length()-1) == '.' && isFinalForm(word.charAt(word.indexOf(".")-1)))
		{
			res[0] = res[1] = 2;
		}
		else if(isFarsiAlphabetic(word.charAt(0)) && word.contains(".") && !isFinalForm(word.charAt(word.indexOf(".")-1)))
		{
			// the token have 3 possible ways to be tokenized: it is an abbreviation, is an abbreviation making the EOS, or is a word with a sentence boundary
			res[0] = 3;
		}

		boolean ok = true;
		String[] tokens = word.split("[ .~]");
		for(String str: tokens)
			if(str.length() != 1 || !isFinalForm(str.charAt(0)))
				ok = false;
		if(ok)
		{
			res[0] = 4;
			if(word.charAt(word.length()-1) == '.')	res[1] = 2;
		}

		return res;
	}

	private static int[] IsAcronym(String word)  // returns a pair: (format num, ambiguity (1:ambiguous with word , 2:ambiguous with EOS) )
	{
		int[] res = new int[2];
		int n = word.length();

		if(word.contains(".") && (word.indexOf(".")-1>=0 && isFarsiAlphabetic(word.charAt(word.indexOf(".")-1)) || word.indexOf(".")-2>=0 && word.charAt(word.indexOf(".")-1) == '~'  &&
				isFarsiAlphabetic(word.charAt(word.indexOf(".")-2))) && word.indexOf(".")+1<n && isFarsiAlphabetic(word.charAt(word.indexOf(".") + 1)))
		{
			res[0] = 1;
			if(word.charAt(word.length()-1) == '.')
				res[1] = 2;
		}

		else if(word.contains("~") && word.indexOf("~")-1>=0 && isFinalForm(word.charAt(word.indexOf("~")-1)) && word.indexOf("~")+1<n && isFarsiAlphabetic(word.charAt(word.indexOf("~")+1)) && endsWithFinalForm(word.substring(word.indexOf("~")+1)))
		{
			res[0] = 2;
			res[1] = 1;
		}

		return res;
	}

	private static boolean endsWithFinalForm(String str)
	{
		for(int j = 0; j < 7; j++)
			if (str.endsWith(""+nonFinalForm[j]))
				return false;
		return true;
	}

	private static boolean isFinalForm(char ch)
	{
		for(char nff: nonFinalForm)
			if(ch == nff)
				return false;
		return true;

	}

	private static boolean isFarsiAlphabetic(char ch)
	{
		for(char alph: farsiAlphabet)
			if(alph == ch)
				return true;
		return false;
	}

	private static int getMorphemeIdx(String token)
	{
		for(int i = 0; i < morphemes.length; i++)
			if(morphemes[i].equals(token))
				return i;
		return -1;
	}

	private static boolean IsMorpheme(String token)
	{
		for(String str: morphemes)
			if(str.equals(token))
				return true;
		return false;
	}

	private static void Write()
	{
		String fileName = "out.txt";

		try {
			FileWriter fileWriter = new FileWriter(fileName);

			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

			bufferedWriter.write("تمام حالات توکن ها:\n");
			for(int i = 0; i < tokens.size(); i++)
			{
				bufferedWriter.write("-----\n");
				for(String token: tokens.get(i))
					bufferedWriter.write(token+"\n");

			}
			bufferedWriter.close();
		}
		catch(IOException ex) {
			System.out.println("Error writing to file '" + fileName + "'");

		}

	}

}
